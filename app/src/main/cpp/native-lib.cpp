/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

#include <jni.h>
#include <string>

#include "Engine.h"
#include "EqualWiner.h"
#include "Field.h"


using ENGENE = Engine<EqualWiner>; //!< Engine instance type

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_Engine_destry(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
    ENGENE* en = (ENGENE*) T;
        delete(en);
    }

}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_oem_dofast_Engine_createEngine(JNIEnv *env, jobject instance, jint fieldSize,
                                            jint elCount, jint sequencialSize) {

    ENGENE* en = new ENGENE(fieldSize, elCount, sequencialSize);
    return reinterpret_cast<jlong>(en);

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_oem_dofast_Engine_getElvalue(JNIEnv *env, jobject instance, jlong T, jint x, jint y) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        return en->getValue(x, y);
    }
    return Field::defaultValue;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_Engine_swap__JIIII(JNIEnv *env, jobject instance, jlong T, jint x1, jint y1,
                                    jint x2, jint y2) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->action(x1, y1, x2, y2);
    }

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_oem_dofast_Engine_isChange(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        return en->isCange();
    }
    return false;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_Engine_startReading__J(JNIEnv *env, jobject instance, jlong T) {

    if (T != 0) {
        ENGENE *en = (ENGENE *) T;
        en->startReading();
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_Engine_endReading__J(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->endReading();
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_oem_dofast_Engine_getCount__J(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        return en->count();
    }
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_Engine_setElvalue(JNIEnv *env, jobject instance, jlong T, jint x, jint y,
                                          jint value) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->setValue(x, y, value);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_Engine_startChanging(JNIEnv *env, jobject instance, jlong T) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->startChanging();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_Engine_endChanging(JNIEnv *env, jobject instance, jlong T) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->endChanging();
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_oem_dofast_Engine_getNewTask(JNIEnv *env, jobject instance, jlong T) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
    }
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_oem_dofast_Engine_getTaskValue(JNIEnv *env, jobject instance, jlong T, jint id) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
    }
    return 0;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_oem_dofast_Engine_isDone(JNIEnv *env, jobject instance, jlong T) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
    }
    return false;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_oem_dofast_Engine_isFinish(JNIEnv *env, jobject instance, jlong T) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
    }
    return false;
}