//
// Created by oem on 2018-10-14.
//

#ifndef GAME_FIELD_H
#define GAME_FIELD_H


#include <cstring>
class Field {
    int XSize_;
    int YSize_;
    char field[8][8];

public:
    const int defaultValue_ = 0;
    Field(int XSize, int YSize, int defaultValue): XSize_(XSize), YSize_(YSize), defaultValue_(defaultValue){
        std::memset(field, defaultValue_, sizeof(field));
    }
    int getValue(int x, int y)const {return field[x][y];}
    char& getValue(int x, int y) {return field[x][y];}
    int claenValue(int x, int y) { field[x][y] = -1;}
};


#endif //GAME_FIELD_H
