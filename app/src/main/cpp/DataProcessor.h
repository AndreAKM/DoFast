//
// Created by oem on 20/10/18.
//

#ifndef DOFAST_DATAPROCESSOR_H
#define DOFAST_DATAPROCESSOR_H

#include <algorithm>
#include <memory>
#include "Field.h"

class DataProcessor {
    /**
     * General the enging settings
     */
    int elemetTotal; //!< count of field value variant
    const int SequenceSize;
    std::shared_ptr<Field> field;

public:
    DataProcessor(std::shared_ptr<Field> field, int elCount, int SequenceSize):
            elemetTotal(elCount + 1),
            SequenceSize(SequenceSize),
            field(field){}
    void full(int tx, int ty, int bx, int by);
    int defValue(int x, int y);
    template < class Check>
    int swap(int x1, int y1, int x2, int y2, Check& check);

    int elements_total() { return elemetTotal;}
};

template < class Check>
int DataProcessor::swap(int x1, int y1, int x2, int y2, Check& check) {
    std::swap(field->getValue(x1, y1), field->getValue(x2, y2));
    auto cleanSize = check(x1, y1);
    cleanSize += check(x2, y2);
    return cleanSize;
}

#endif //DOFAST_DATAPROCESSOR_H
