#include <jni.h>
#include <string>

#include "Engine.h"
#include "EqualWiner.h"
#include "Field.h"


using ENGENE = Engine<EqualWiner>;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_DoFast_destry(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
    ENGENE* en = (ENGENE*) T;
        delete(en);
    }

}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_oem_dofast_DoFast_createEngine(JNIEnv *env, jobject instance, jint fieldSize,
                                            jint elCount) {

    ENGENE* en = new ENGENE(fieldSize, elCount);
    return reinterpret_cast<jlong>(en);

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_oem_dofast_DoFast_getElvalue(JNIEnv *env, jobject instance, jlong T, jint x, jint y) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        return en->getValue(x, y);
    }
    return ENGENE::defaultValue;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_DoFast_swap__JIIII(JNIEnv *env, jobject instance, jlong T, jint x1, jint y1,
                                    jint x2, jint y2) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->action(x1, y1, x2, y2);
    }

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_oem_dofast_DoFast_isChange(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        return en->isCange();
    }
    return false;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_DoFast_startReading__J(JNIEnv *env, jobject instance, jlong T) {

    if (T != 0) {
        ENGENE *en = (ENGENE *) T;
        en->startReading();
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_DoFast_endReading__J(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->endReading();
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_oem_dofast_DoFast_getCount__J(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        return en->count();
    }
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_DoFast_setElvalue(JNIEnv *env, jobject instance, jlong T, jint x, jint y,
                                          jint value) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->setValue(x, y, value);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_DoFast_startChanging(JNIEnv *env, jobject instance, jlong T) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->startChanging();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_dofast_DoFast_endChanging(JNIEnv *env, jobject instance, jlong T) {
    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->endChanging();
    }
}

