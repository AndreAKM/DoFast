//
// Created by oem on 20/10/18.
//

#ifndef DOFAST_DATAPROCESSOR_H
#define DOFAST_DATAPROCESSOR_H

#include <algorithm>
#include <memory>
#include "Field.h"

/**
 * @class DataProcessor encapsulate operatinh whit game data
 */
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
    /**
     * return value id which is the most on the field
     */
    int maxValueId();
    /**
     * full game field
     */
    void full(int tx, int ty, int bx, int by);
    /**
     * defenition value for a block
     */
    int defValue(int x, int y);
    /**
     * structure save result of move a block in new place.
     */
    struct SwapResult {
        int id;
        int count;
    };
    /**
     * swap block values
     */
    template < class Check>
    std::tuple<SwapResult, SwapResult> swap(int x1, int y1, int x2, int y2, Check& check);

    int elements_total() { return elemetTotal;}
};

template < class Check>
std::tuple<DataProcessor::SwapResult, DataProcessor::SwapResult> DataProcessor::swap(int x1, int y1, int x2, int y2, Check& check) {
    std::swap(field->getValue(x1, y1), field->getValue(x2, y2));
    SwapResult first, second;
    first.id =field->getValue(x1, y1);
    second.id =field->getValue(x2, y2);

    first.count = check(x1, y1);
    second.count = check(x2, y2);
    return {first, second};
}

#endif //DOFAST_DATAPROCESSOR_H
