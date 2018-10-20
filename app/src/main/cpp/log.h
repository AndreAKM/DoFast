//
// Created by oem on 20/10/18.
//

#ifndef DOFAST_LOG_H
#define DOFAST_LOG_H
#include <android/log.h>

#define APPNAME "MyApp"

#define LOG (...) __android_log_print(ANDROID_LOG_DEBUG, __VA_ARGS__)
//#define LOG (TAG, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, ##__VA_ARGS__)

#endif //DOFAST_LOG_H
