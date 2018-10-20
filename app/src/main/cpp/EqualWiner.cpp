/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

#include "EqualWiner.h"
#include <algorithm>
#include <tuple>

std::list<std::tuple<int, int, int, int>> EqualWiner::move() {
    std::list<std::tuple<int, int, int, int>> res;
    int tx, ty, bx, by;
    for(auto& ch: changeRange) {
        std::tie(tx,ty, bx, by) = ch;
        for(int x = tx; x < bx ; ++x){
            for(int y = ty, d = by -1 ; y > 0; --y, --d) {
                res.push_back({x, y - 1, x, d });
            }
        }
    }
    return res;

};

void EqualWiner::refrashe(){
    changeRange.clear();
}

int EqualWiner::checkEl(int x, int y) {


    if(field->getValue(x,y) == 0) return 0;
    int startx = 0;//std::max(x - sizeSequence_, 0);
    int maxx = fSizeX_;//std::min(x + sizeSequence_ + 1, fSizeX_);
    int starty = 0;//std::max(y - sizeSequence_, 0);
    int maxy = fSizeY_;//std::min(y + sizeSequence_ + 1, fSizeY_);
    int xCount = 1;
    int i = x, j = x;
    for ( ; i > startx ; --i) {
        if(field->getValue(i,y) != field->getValue(i - 1,y)) break;
        ++xCount;
    }
    for ( ; j < maxx - 1; ++j) {
        if(field->getValue(j,y) != field->getValue(j + 1,y)) break;
        ++xCount;
    }

    int yCount = 1;
    int iy = y, jy = y;
    for ( ; iy > starty; --iy) {
        if(field->getValue(x,iy) != field->getValue(x,iy - 1)) break;
        ++yCount;
    }
    for ( ; jy < maxy - 1 ; ++jy) {
        if(field->getValue(x,jy) != field->getValue(x,jy + 1)) break;
        ++yCount;
    }
    if (xCount >= sizeSequence_) {
        int bx = std::max(i, startx);
        int ex = std::min(j + 1, maxx);
        for(int c = bx; c != ex; ++c) {
            field->claenValue(c, y);
        }
        changeRange.push_back(std::make_tuple(bx, y, ex, y +1));
    }
    else {
        xCount = 0;
    }
    if (yCount >= sizeSequence_) {
        int by = std::max(iy, starty);
        int ey = std::min(jy + 1, maxy);
        for(int c = by; c != ey; ++c) {
            field->claenValue(x, c);
        }
        changeRange.push_back(std::make_tuple(x, by, x + 1, ey));
    } else {
        yCount = 0;
    }

    return yCount + xCount;

}