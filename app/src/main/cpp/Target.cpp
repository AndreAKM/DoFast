//
// Created by oem on 21/10/18.
//

#include "Target.h"

#include <algorithm>

void SameSequentialTarget::nextTask() {
    targSize = minSize + (std::rand() % (maxSize - minSize));
    targID = 1 + (std::rand() % (idTotal ));
    count = (idTotal / 2) * (1 + targSize - minSize);
    done = false;
    finish = false;
}
int SameSequentialTarget::targetId(){
    return targID;
}
int SameSequentialTarget::targetSize(){
    return targSize;
}

int SameSequentialTarget::taskCount() {
    return count;
}
int SameSequentialTarget::taskResult() {
    return (isDone() && count != -1) ? targSize : 1;
}
bool SameSequentialTarget::isDone() {
    return done;
}
bool SameSequentialTarget::isFinish() {
    return finish;
}

void SameSequentialTarget::iter() {
    if(count > -1) --count;
}
void SameSequentialTarget::regChange(int id, int size) {
    if(count < 0) {
        return;
    }
    if (targID == id && targSize <= size) {
        done = true;
    }
    if(count == 0 || done) {
        flash();
    }
}

void SameSequentialTarget::flash() {
    targID = -1;
    targSize = -1;
    finish = true;
}