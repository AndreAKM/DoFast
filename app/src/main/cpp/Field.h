/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

#ifndef GAME_FIELD_H
#define GAME_FIELD_H

#include <cstring>

/**
 * @class Field encapsulate game field data
 */
class Field {
    int XSize_;
    int YSize_;
    char field[8][8]; //!< contain of blocks value id

public:
    const int defaultValue_ = 0;

    /**
     * constructor
     * @param XSize - count of blocks in row
     * @param YSize - count of blocks in column
     * @param defaultValue - id of default value
     */
    Field(int XSize, int YSize, int defaultValue): XSize_(XSize), YSize_(YSize), defaultValue_(defaultValue){
        std::memset(field, defaultValue_, sizeof(field));
    }

    /**
     * const getter of a block value
     * @param x - coordinate in row
     * @param y - coorodinate in column
     * @return id of block value
     */
    int getValue(int x, int y)const {return field[x][y];}

    /**
     * getter of a block value
     * @param x - coordinate in row
     * @param y - coorodinate in column
     * @return id of block value
     */
    char& getValue(int x, int y) {return field[x][y];}
    /**
    * clean a block value
    * @param x - coordinate in row
    * @param y - coorodinate in column
    */
    void claenValue(int x, int y) { field[x][y] = defaultValue_;}
};


#endif //GAME_FIELD_H
