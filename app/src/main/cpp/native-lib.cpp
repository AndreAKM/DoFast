#include <jni.h>
#include <string>

#include "Engine.h"
#include "EqualWiner.h"
#include "Field.h"


using ENGENE = Engine<Field, EqualWiner<Field>>;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_game_game_destry(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
    ENGENE* en = (ENGENE*) T;
        delete(en);
    }

}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_oem_game_game_createEngine(JNIEnv *env, jobject instance, jint fieldSize,
                                            jint elCount) {

    ENGENE* en = new ENGENE(fieldSize, elCount);
    return reinterpret_cast<jlong>(en);

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_oem_game_game_getElvalue(JNIEnv *env, jobject instance, jlong T, jint x, jint y) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        return en->getValue(x, y);
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oem_game_game_swap(JNIEnv *env, jobject instance, jlong T, jint x1, jint y1,
                                    jint x2, jint y2) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->swap(x1, y1, x2, y2);
    }

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_oem_game_game_isChange(JNIEnv *env, jobject instance, jlong T) {

    if(T != 0) {
        ENGENE* en = (ENGENE*) T;
        en->isCange();
    }

}