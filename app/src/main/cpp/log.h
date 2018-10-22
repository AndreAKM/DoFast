//
// Created by oem on 20/10/18.
//

#ifndef DOFAST_LOG_H
#define DOFAST_LOG_H
#include <android/log.h>

#define LOG_TAG "dofast"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
//#define LOG (TAG, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, ##__VA_ARGS__)
inline const char* to_sting(bool d) {return (d)?"true":"false";}
#endif //DOFAST_LOG_H
