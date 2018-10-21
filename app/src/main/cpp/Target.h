//
// Created by oem on 21/10/18.
//

#ifndef DOFAST_TARGET_H
#define DOFAST_TARGET_H


class SameSequentialTarget {
    const int maxSize;
    const int minSize;
    const char idTotal;
    char targID = -1;
    char targSize = -1;
    int count = -1;
    bool done = false;
    bool finish = true;
public:
    SameSequentialTarget(int minSize, int maxSize, int idTotal) :
            maxSize(maxSize), minSize(minSize), idTotal(idTotal){}
    int targetId();
    int targetSize();
    void nextTask();
    int taskCount();
    int taskResult();
    bool isDone();
    bool isFinish();
    void iter();
    void regChange(int id, int size);

private:
    void flash();
};


#endif //DOFAST_TARGET_H
