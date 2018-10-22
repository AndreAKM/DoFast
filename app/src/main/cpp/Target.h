//
// Created by oem on 21/10/18.
//

#ifndef DOFAST_TARGET_H
#define DOFAST_TARGET_H

#include <memory>
#include "DataProcessor.h"
/**
 * @class definition play target
 */
class SameSequentialTarget {
    const int maxSize;
    const int minSize;
    const char idTotal;
    char targID = -1;
    char targSize = -1;
    int count = -1;
    bool done = false;
    bool finish = true;
    std::shared_ptr<DataProcessor> processor;

public:
    SameSequentialTarget(int minSize, int maxSize, int idTotal, std::shared_ptr<DataProcessor> processor) :
            maxSize(maxSize), minSize(minSize), idTotal(idTotal), processor(processor){}
    /**
     * return target value id
     */
    int targetId();
    /**
     * return size of sequence
     */
    int targetSize();
    /**
     * create new task
     */
    void nextTask();
    /**
     * return current task counter
     */
    int taskCount();
    /**
     * retunr task result koeffecient. It used as prize for player.
     */
    int taskResult();
    /**
     * return true if the task was success finished
     */
    bool isDone();
    /**
     * return true if now player does not have a task
     */
    bool isFinish();
    /**
     * increase task counter
     */
    void iter();
    /**
     * register result of changed field
     */
    void regChange(int id, int size);

private:
    void flash();
};


#endif //DOFAST_TARGET_H
