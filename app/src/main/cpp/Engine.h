/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

#ifndef GAME_ENGINE_H
#define GAME_ENGINE_H

#include <vector>
#include <mutex>
#include <condition_variable>
#include <atomic>
#include <memory>
#include "Field.h"
#include "DataProcessor.h"

/**
 * @class Engine - implementation of game engine.
 * The class is tamplate from strategy which defines of the game rules. The strategy should
 * implemented the following methods ( move(), hasEmptyBlocks, refrashe, checkEl).
 * Please fined them description in @class EqualWiner.
 * The Engine encapsulate State machine of a game. It implements methods to process user actions,
 * to access game field elements, to count result of player actions. The methods is thread save.
 */

 /* TODO This is no priority list.
  * 1) reimplement field filing as a strategy;
  * 2) think about implementing of WinCondition as a interface base hierarchy to avoid template;
  * 3) move SequenceSize into WinCondition, add opportunity set the value from java;
  * 4) change tipe of Field to unique_ptr, and send it int WinCondition as weak_ptr;
  * 5) add int constructor factory methods to create WinCondition and Field;
  * 6) add supporting of difirence size for widht anf height.
  */
template<class WinCondition, class Target>
class Engine {
    const int SequenceSize; //!< number of element sequence to win step

    /**
     * thread driving values
     */
    std::atomic_bool changed;
    std::atomic_bool reading;
    std::atomic_bool processing;
    std::condition_variable changeState;
    std::mutex mut;

    /**
     * other data
     */
    long countVoit = 0; //!<  counte how mach blocs was wined from previous reading

    std::shared_ptr<Field> field; //!< gane field
    std::shared_ptr<DataProcessor> dataProcessor;
    std::shared_ptr<WinCondition> win; //!< game rule startegy
    std::shared_ptr<Target> task;
public:

    /**
     * constructor
     * @param fieldSize - count of the game blocs in rows and columns
     * @param elCount - count of value variants except default value
     */
    Engine(int fieldSize, int elCount, int SequenceSize);

    /**
     * getter for a block value
     * @param x - x coordinate of block
     * @param y - y coordinate of block
     * @return of id value
     */
    int getValue(int x, int y)const;

    /**
     * setter for a block value
     * @param x - x coordinate of block
     * @param y - y coordinate of block
     * @param value- a new id value for block
     */
    void setValue(int x, int y, int value) {field->getValue(x, y) = value;}

    /**
     * checks of chenging
     * @return true if the enging collect some change of play field which have not read yet.
     */
    bool isCange() const{ return changed;}

    /**
     * lock game field for reading
     */
    void startReading();

    /**
     * unlock the game field
     */
    void endReading();

    /**
     * lock the game field for data loading
     */
    void startChanging();

    /**
     * unlock the data field
     */
    void endChanging();

    /**
     * processing user action
     * @param x1 x - coordinate of block first
     * @param y1 y - coordinate of block first
     * @param x2 x - coordinate of block second
     * @param y2 y - coordinate of block second
     */
    void action(int x1, int y1, int x2, int y2);

    /**
     * getter of conter
     * @return count of blocks which value was chenged from last reading
     */
    int count()const {
        return countVoit;
    }

    void nextTask() {
        task->nextTask();
    }
    int taskCount() {
        return task->taskCount();
    }
    bool isDone() {
        return task->isDone();
    }
    bool isFinish(){
        return task->isFinish();
    }
    int targetId() {
        return task->targetId();
    }
    int targetSize(){
        return task->targetSize();
    }

private:
    bool swap(int x1, int y1, int x2, int y2);
    void swap(std::tuple<int, int, int, int> coor);
};

#include <cstdlib>
#include <unordered_set>
#include <algorithm>
#include <cstring>
template < class WinCondition, class Target>
Engine<WinCondition, Target>::Engine(int fieldSize, int elCount, int SequenceSize):
        SequenceSize(SequenceSize),
        reading(false),
        processing(false),
        field(new Field(fieldSize, fieldSize)),
        dataProcessor(new DataProcessor(field, elCount, SequenceSize)),
        win(new WinCondition(field, fieldSize, fieldSize, SequenceSize)),
        task(new Target(SequenceSize+1, SequenceSize * 2, elCount))
{
    dataProcessor->full(0, 0, fieldSize, fieldSize);
    changed = true;
}
template < class WinCondition, class Target>
void Engine<WinCondition, Target>::action(int x1, int y1, int x2, int y2) {
    if(field->getValue(x1, y1) == Field::defaultValue || field->getValue(x2, y2) == Field::defaultValue)
        return;
    if(swap(x1,y1,x2,y2) == false) {
        std::swap( field->getValue(x2,y2), field->getValue(x1,y1));
    }
    task->iter();
}
template < class WinCondition, class Target>
bool Engine<WinCondition, Target>::swap(int x1, int y1, int x2, int y2) {
    std::unique_lock<std::mutex>lk (mut);
    changeState.wait(lk, [&] {return reading == false;});
    auto check = [&](int x, int y){ return win->checkEl(x, y);};
    auto [first, second]= dataProcessor->swap(x1, y1, x2, y2, check);
    task->regChange(first.id, first.count);
    task->regChange(second.id, second.count);
    countVoit += (first.count + second.count) * task->taskResult();
    changed = countVoit != 0;
    return changed;
}
template < class WinCondition, class Target>
int Engine<WinCondition, Target>::getValue(int x, int y) const{
    return field->getValue(x,y);
}
template < class WinCondition, class Target>
void Engine<WinCondition, Target>::startChanging(){
    std::unique_lock<std::mutex>lk (mut);
    changeState.wait(lk, [&] {return processing == false && reading == false;});
    processing = true;
}
template < class WinCondition, class Target>
void Engine<WinCondition, Target>::endChanging(){
    processing = false;
    changed = true;
    changeState.notify_all();
}

template < class WinCondition, class Target>
void Engine<WinCondition, Target>::startReading(){
    std::unique_lock<std::mutex>lk (mut);
    changeState.wait(lk, [&] {return processing == false;});
    reading = true;
}
template < class WinCondition, class Target>
void Engine<WinCondition, Target>::endReading(){
    reading = false;
    changed = false;
    changeState.notify_all();
    countVoit = 0;
    auto res = win->move();
    if(res.size() == 0){
        if(win->hasEmptyBlocks()){
            dataProcessor->full(0, 0, field->widht(), field->height());
            changed = true;
        }
        return;
    }
    processing = true;
    win->refrashe();
    for (auto& r: res) {
        swap(r);
    }
    if(changed == false) {
        dataProcessor->full(0, 0, field->widht(), field->height());
    }
    processing = false;
    changed = true;
    changeState.notify_all();
}
template < class WinCondition, class Target>
void Engine<WinCondition, Target>::swap(std::tuple<int, int, int, int> coor){
    int x1, y1, x2, y2;
    std::tie(x1, y1, x2, y2) = coor;
    swap(x1, y1, x2, y2);
}

#endif //GAME_ENGINE_H
