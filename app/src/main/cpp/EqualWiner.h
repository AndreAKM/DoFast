//
// Created by oem on 2018-10-14.
//

#ifndef GAME_EQUALWINER_H
#define GAME_EQUALWINER_H

#include <tuple>
#include <list>
template <class Field>
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
    void refrashe();
    int check(int x, int y);

private:
    void update_x_range(int b, int e, std::tuple<int, int, int, int>& range);
    void update_y_range(int b, int e, std::tuple<int, int, int, int>& range);
};

#include "EqualWiner.h"
#include <algorithm>
#include <tuple>
template <class Field>
std::list<std::tuple<int, int, int, int>> EqualWiner<Field>::move() {
    std::list<std::tuple<int, int, int, int>> res;
    int tx, ty, bx, by;
    for(auto& ch: changeRange) {
        std::tie(tx,ty, bx, by) = ch;
        for(int x = tx; x < bx ; ++x){
            for(int y = ty ; y > 0; --y) {
                res.push_back({x, y - 1, x, by - y });
            }
        }
    }

};
template <class Field>
std::tuple<int, int, int, int> EqualWiner<Field>::change_range() {
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
template <class Field>
void EqualWiner<Field>::refrashe(){
    changeRange.clear();
}
template <class Field>
int EqualWiner<Field>::check(int x, int y) {
    int startx = std::max(x - sizeSequence_, 0);
    int maxx = std::min(x + sizeSequence_ + 1, fSizeX_);
    int starty = std::max(y - sizeSequence_, 0);
    int maxy = std::min(y + sizeSequence_ + 1, fSizeY_);
    int xCount = 0;
    int i = x, j = x;
    for ( ; i > startx; --i) {
        if(field_.getValue(i,y) != field_.getValue(i - 1,y)) break;
        ++xCount;
    }
    for ( ; j < maxx - 1; ++j) {
        if(field_.getValue(j,y) != field_.getValue(j + 1,y)) break;
        ++xCount;
    }
    if (xCount >= sizeSequence_) {
        int bx = std::max(i, startx);
        int ex = std::min(j, maxx);
        for(int c = bx; c != ex; ++c) {
            field_.claenValue(x, c);
        }
        changeRange.push_back(std::make_tuple(bx, y, ex, y +1));
        return xCount;
    }
    int yCount = 0;
    i = y, j = y;
    for ( ; i > starty; --i) {
        if(field_.getValue(x,i) != field_.getValue(x,i - 1)) break;
        ++yCount;
    }
    for ( ; j < maxx - 1 ; ++j) {
        if(field_.getValue(x,j) != field_.getValue(x,j + 1)) break;
        ++yCount;
    }
    if (xCount >= sizeSequence_) {
        int by = std::max(i, starty);
        int ey = std::min(j, maxy);
        for(int c = by; c != ey; ++c) {
            field_.claenValue(x, c);
        }
        changeRange.push_back(std::make_tuple(x, by, x + 1, ey));
        return yCount;
    }
    return 0;

}
template <class Field>
void EqualWiner<Field>::update_x_range(int b, int e, std::tuple<int, int, int, int>& range){
    auto& xmin = std::get<0>(range);
    xmin = (xmin == -1)? b: std::min(xmin, b);
    auto& xmax = std::get<2>(range);
    xmax = (xmax == -1)? b: std::max(xmax, e);
}
template <class Field>
void EqualWiner<Field>::update_y_range(int b, int e, std::tuple<int, int, int, int>& range){
    auto& ymin = std::get<1>(range);
    ymin = (ymin == -1)? b: std::min(ymin, b);
    auto& ymax = std::get<2>(range);
    ymax = (ymax == -1)? b: std::max(ymax, e);
}
#endif //GAME_EQUALWINER_H
