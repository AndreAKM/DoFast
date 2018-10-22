/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

#ifndef GAME_FIELD_H
#define GAME_FIELD_H

#include <cstring>
#include <memory>
#include "log.h"
/**
 * @class Field encapsulate game field data
 */
class Field {
    int XSize_;
    int YSize_;
    std::unique_ptr<char[]> field; //!< contain of blocks value id

public:
    static const int defaultValue = 0;

    /**
     * constructor
     * @param XSize - count of blocks in row
     * @param YSize - count of blocks in column
     * @param defaultValue - id of default value
     */
    Field(int XSize, int YSize): XSize_(XSize), YSize_(YSize), field(new char[XSize * YSize]){
        std::memset(field.get(), defaultValue, XSize * YSize);
    }

    /**
     * const getter of a block value
     * @param x - coordinate in row
     * @param y - coorodinate in column
     * @return id of block value
     */
    char getValue(int x, int y)const {
        char id = field[x * YSize_ + y];
        __android_log_print(ANDROID_LOG_DEBUG, "native:FIELD", "(%d, %d) -> %d = %d", x, y, x * YSize_ + y, id);
        return id;
    }

    /**
     * getter of a block value
     * @param x - coordinate in row
     * @param y - coorodinate in column
     * @return id of block value
     */
    char& getValue(int x, int y) {
        char id = field[x * YSize_ + y];
        __android_log_print(ANDROID_LOG_DEBUG, "native:FIELD",  "(%d, %d) -> %d = %d", x, y, x * YSize_ + y, id);

        return field[x * YSize_ + y];
    }
    /**
    * clean a block value
    * @param x - coordinate in row
    * @param y - coorodinate in column
    */
    void claenValue(int x, int y) { field[x * YSize_ + y] = defaultValue;}

    int widht() const {
        return XSize_;
    }
    int height() const {
        return YSize_;
    }
};


#endif //GAME_FIELD_H
