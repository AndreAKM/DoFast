//
// Created by oem on 2018-10-14.
//

#ifndef GAME_EQUALWINER_H
#define GAME_EQUALWINER_H

#include <tuple>
#include <list>
#include "Field.h"

class EqualWiner {
    Field& field_;
    const int fSizeX_;
    const int fSizeY_;
    const int sizeSequence_;
    std::list<std::tuple<int, int, int, int>> changeRange;
public:
    EqualWiner(Field& field, int fSizeX, int fSizeY, int sizeSequence):
            field_(field), fSizeX_(fSizeX), fSizeY_(fSizeY), sizeSequence_(sizeSequence){
    }
    std::list<std::tuple<int, int, int, int>> move();
    std::tuple<int, int, int, int> change_range();
    bool hasEmptyBlocks() {
        return changeRange.size() != 0;
    }
    void refrashe();
    int checkEl(int x, int y);
};

#endif //GAME_EQUALWINER_H
