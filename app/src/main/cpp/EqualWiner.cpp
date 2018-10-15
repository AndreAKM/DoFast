//
// Created by oem on 2018-10-14.
//

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

std::tuple<int, int, int, int> EqualWiner::change_range() {
    if(changeRange.size() == 0)
        return std::make_tuple(0,0,0,0);
    int tx = fSizeX_, ty = fSizeY_, bx = 0, by = 0;
    for(auto& ch: changeRange) {
        int tcx, tcy, bcx, bcy;
        std::tie(tcx,tcy, bcx, bcy) = ch;
        tx = std::min(tcx, tx);
        ty = std::min(tcy, ty);
        bx = std::max(bcx, bx);
        by = std::max(bcy, by);
    }
    return std::make_tuple(tx, ty, bx, by);
};

void EqualWiner::refrashe(){
    changeRange.clear();
}

int EqualWiner::checkEl(int x, int y) {

    if(field_.getValue(x,y) == 0) return 0;
    int startx = 0;//std::max(x - sizeSequence_, 0);
    int maxx = fSizeX_;//std::min(x + sizeSequence_ + 1, fSizeX_);
    int starty = 0;//std::max(y - sizeSequence_, 0);
    int maxy = fSizeY_;//std::min(y + sizeSequence_ + 1, fSizeY_);
    int xCount = 1;
    int i = x, j = x;
    for ( ; i > startx ; --i) {
        if(field_.getValue(i,y) != field_.getValue(i - 1,y)) break;
        ++xCount;
    }
    for ( ; j < maxx - 1; ++j) {
        if(field_.getValue(j,y) != field_.getValue(j + 1,y)) break;
        ++xCount;
    }
    if (xCount >= sizeSequence_) {
        int bx = std::max(i, startx);
        int ex = std::min(j + 1, maxx);
        for(int c = bx; c != ex; ++c) {
            field_.claenValue(c, y);
        }
        changeRange.push_back(std::make_tuple(bx, y, ex, y +1));
        return xCount;
    }
    int yCount = 1;
    i = y, j = y;
    for ( ; i > starty; --i) {
        if(field_.getValue(x,i) != field_.getValue(x,i - 1)) break;
        ++yCount;
    }
    for ( ; j < maxx - 1 ; ++j) {
        if(field_.getValue(x,j) != field_.getValue(x,j + 1)) break;
        ++yCount;
    }
    if (yCount >= sizeSequence_) {
        int by = std::max(i, starty);
        int ey = std::min(j + 1, maxy);
        for(int c = by; c != ey; ++c) {
            field_.claenValue(x, c);
        }
        changeRange.push_back(std::make_tuple(x, by, x + 1, ey));
        return yCount;
    }
    return 0;

}