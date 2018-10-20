//
// Created by oem on 20/10/18.
//

#include "DataProcessor.h"
#include <vector>
#include "log.h"
void DataProcessor::full(int tx, int ty, int bx, int by) {

    for (int x = tx; x < bx; ++x) {
        for (int y = ty; y < by; ++y) {
            if( field->getValue(x,y) == Field::defaultValue) {
                field->getValue(x,y) = defValue(x,y);
                int id = field->getValue(x,y);
                __android_log_print(ANDROID_LOG_DEBUG, "native:DataProcessor", "(%d, %d) = %d", x, y, id);
            }
        }
    }

}

int DataProcessor::defValue(int x, int y) {
    std::vector<char> el(elemetTotal, 0);
    int startx = std::max(x - SequenceSize, 0);
    int maxx = std::min(x + SequenceSize + 1, field->widht());
    int starty = std::max(y - SequenceSize, 0);
    int maxy = std::min(y + SequenceSize + 1, field->height());
    for (int i = startx; i < maxx; ++i) {
        ++el[ field->getValue(i,y)];
    }
    for (int i = starty; i < maxy; ++i) {
        ++el[ field->getValue(x,i)];
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
