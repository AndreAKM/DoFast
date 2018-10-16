//
// Created by oem on 2018-10-14.
//

#ifndef GAME_ENGINE_H
#define GAME_ENGINE_H

#include <vector>
#include <mutex>
#include <condition_variable>
#include <atomic>
#include "Field.h"
template<class WinCondition>
class Engine {
    const int fieldSize;
    static constexpr int SequenceSize = 3;
    int elemetTotal;
    std::atomic_bool changed;
    std::atomic_bool reading;
    std::atomic_bool processing;
    std::condition_variable changeState;
    std::mutex mut;
    long countVoit = 0;
    Field field;
    WinCondition win;

public:
    static const int defaultValue = 0;
    Engine(int fieldSize, int elCount);
    int getValue(int x, int y)const;
    void setValue(int x, int y, int value) {field.getValue(x, y) = value;}
    bool isCange() const{ return changed;}
    void startReading();
    void endReading();
    void startChanging();
    void endChanging();
    void action(int x1, int y1, int x2, int y2);
    int count()const { return countVoit;}

private:
    bool swap(int x1, int y1, int x2, int y2);
    void full(int tx, int ty, int bx, int by);
    int defValue(int x, int y);
    void swap(std::tuple<int, int, int, int> coor);
};



#include <cstdlib>
#include <unordered_set>
#include <algorithm>
#include <cstring>
template < class WinCondition>
Engine<WinCondition>::Engine(int fieldSize, int elCount):
        fieldSize(fieldSize),
        elemetTotal(elCount + 1),
        reading(false),
        processing(false),
        field(Field(fieldSize, fieldSize, defaultValue)),
        win(field, fieldSize, fieldSize, SequenceSize)
{
    full(0, 0, fieldSize, fieldSize);
    changed = true;
}
template < class WinCondition>
void Engine<WinCondition>::action(int x1, int y1, int x2, int y2) {
    if(field.getValue(x1, y1) == defaultValue || field.getValue(x2, y2) == defaultValue)
        return;
    if(swap(x1,y1,x2,y2) == false) {
        std::swap( field.getValue(x2,y2), field.getValue(x1,y1));
    }
}
template < class WinCondition>
bool Engine<WinCondition>::swap(int x1, int y1, int x2, int y2) {
    std::unique_lock<std::mutex>lk (mut);
    changeState.wait(lk, [&] {return reading == false;});
    std::swap( field.getValue(x1,y1), field.getValue(x2,y2));
    auto cleanSize = win.checkEl(x1, y1);
    cleanSize += win.checkEl(x2, y2);
    changed = changed || (cleanSize != 0);
    countVoit += cleanSize;
    return changed;
}
template < class WinCondition>
int Engine<WinCondition>::getValue(int x, int y) const{
    return field.getValue(x,y);
}
template < class WinCondition>
void Engine<WinCondition>::startChanging(){
    std::unique_lock<std::mutex>lk (mut);
    changeState.wait(lk, [&] {return processing == false && reading == false;});
    processing = true;
}
template < class WinCondition>
void Engine<WinCondition>::endChanging(){
    processing = false;
    changed = true;
    changeState.notify_all();
}

template < class WinCondition>
void Engine<WinCondition>::startReading(){
    std::unique_lock<std::mutex>lk (mut);
    changeState.wait(lk, [&] {return processing == false;});
    reading = true;
}
template < class WinCondition>
void Engine<WinCondition>::endReading(){
    reading = false;
    changed = false;
    changeState.notify_all();
    countVoit = 0;
    auto res = win.move();
    if(res.size() == 0){
        if(win.hasEmptyBlocks()){
            full(0, 0, fieldSize, fieldSize);
            changed == true;
        }
        return;
    }
    processing = true;
    win.refrashe();
    for (auto& r: res) {
        swap(r);
    }
    if(changed == false) {
        full(0, 0, fieldSize, fieldSize);
    }
    processing = false;
    changed = true;
    changeState.notify_all();
}
template < class WinCondition>
void Engine<WinCondition>::swap(std::tuple<int, int, int, int> coor){
    int x1, y1, x2, y2;
    std::tie(x1, y1, x2, y2) = coor;
    swap(x1, y1, x2, y2);
}
template < class WinCondition>
void Engine<WinCondition>::full(int tx, int ty, int bx, int by) {
    for (int x = tx; x < bx; ++x) {
        for (int y = ty; y < by; ++y) {
            if( field.getValue(x,y) == defaultValue) {
                changed = true;
                field.getValue(x,y) = defValue(x,y);
            }
        }
    }
}
template <class WinCondition>
int Engine<WinCondition>::defValue(int x, int y) {
    char el[6];
    memset(el, 0, sizeof(el));
    int startx = std::max(x - SequenceSize, 0);
    int maxx = std::min(x + SequenceSize + 1, fieldSize);
    int starty = std::max(y - SequenceSize, 0);
    int maxy = std::min(y + SequenceSize + 1, fieldSize);
    for (int i = startx; i < maxx; ++i) {
        ++el[ field.getValue(i,y)];
    }
    for (int i = starty; i < maxy; ++i) {
        ++el[ field.getValue(x,i)];
    }
    int res = 1 + (std::rand() % (elemetTotal - 1));
    for (int i = res ; i != elemetTotal; ++i) {
        if (el[i] < SequenceSize - 1) return i;
    }
    for (int i = 1; i != res; ++i) {
        if (el[i] < SequenceSize -1) return i;
    }
    return res;
}

#endif //GAME_ENGINE_H
