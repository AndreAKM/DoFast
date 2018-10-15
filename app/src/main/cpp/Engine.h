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
    void swap(int x1, int y1, int x2, int y2);
    int getValue(int x, int y)const;
    bool isCange() const{ return changed;}
    void startReading();
    void endReading();

private:
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
        win(field, fieldSize, fieldSize, 3)
{
    full(0, 0, fieldSize, fieldSize);
    changed = true;
}
template < class WinCondition>
void Engine<WinCondition>::swap(int x1, int y1, int x2, int y2) {
    if(reading) return;
    std::swap( field.getValue(x1,y1), field.getValue(x2,y2));
    auto cleanSize = win.checkEl(x1, y1);
    cleanSize += win.checkEl(x2, y2);
    changed = changed || (cleanSize != 0);
    countVoit += cleanSize;
}
template < class WinCondition>
int Engine<WinCondition>::getValue(int x, int y) const{
    return field.getValue(x,y);
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
    memset(el, defaultValue, sizeof(el));
    if (x > 0) el[ field.getValue(x - 1,y)]=1;
    if (y < 0) el[ field.getValue(x,y - 1)]=1;
    if (x < fieldSize - 1) el[ field.getValue(x + 1,y)]=1;
    if (x < fieldSize - 1) el[ field.getValue(x,y+1)]=1;
    int res = 1 + (std::rand() % (elemetTotal - 1));
    for (int i = res ; i != elemetTotal; ++i) {
        if (el[i] == defaultValue) return i;
    }
    for (int i = 0; i != res; ++i) {
        if (el[i] == defaultValue) return i;
    }
    return res;
}

#endif //GAME_ENGINE_H
