/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

#ifndef GAME_EQUALWINER_H
#define GAME_EQUALWINER_H

#include <tuple>
#include <list>
#include "Field.h"
#include <memory>
/**
 * @class EqualWiner definition game rule then player win if he can collect sizeSequence blocks
 *  with the same value in a row and/or column.
 */
class EqualWiner {

    /**
     * general settings
     */
    std::shared_ptr<Field> field; //! reference to game field
    const int fSizeX_;
    const int fSizeY_;
    const int sizeSequence_;

    std::list<std::tuple<int, int, int, int>> changeRange;//!< colection of changed regions

public:

    /**
     * Constructor
     * @param field - reference to game field
     * @param fSizeX - widht of game field
     * @param fSizeY - height of game field
     * @param sizeSequence - count of tha same block which need to win
     */
    EqualWiner(std::shared_ptr<Field> field, int fSizeX, int fSizeY, int sizeSequence):
            field(field), fSizeX_(fSizeX), fSizeY_(fSizeY), sizeSequence_(sizeSequence){
    }

    /**
     * define behavior when some blocks was disappeared
     * @return list of blocks which should be swapped
     */
    std::list<std::tuple<int, int, int, int>> move();

    /**
     * chack that there are winned blocks
     * @return true if collection of disappeared regions is not empty
     */
    bool hasEmptyBlocks() {
        return changeRange.size() != 0;
    }

    /**
     * clean collection of disappeared regions
     */
    void refrashe();

    /**
     * check a block for win condition
     * @param x - x coordinate of the block
     * @param y - y coordinate of the block
     * @return count of changed blocks
     */
    int checkEl(int x, int y);
};

#endif //GAME_EQUALWINER_H
