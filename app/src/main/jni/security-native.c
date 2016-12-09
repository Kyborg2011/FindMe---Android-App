#include <string.h>
#include <jni.h>

const char* API_SECRET = "GT0RnVmyGFDT37ps8pydQXBkjLIsme6CTfKsPc6";
const char* API_KEY = "findme-android-client";

JNIEXPORT jstring JNICALL
Java_com_luceolab_me_Utils_getApiSecret( JNIEnv* env,
                                                  jobject thiz ) {
    return (*env)->NewStringUTF(env, API_SECRET);
}

JNIEXPORT jstring JNICALL
Java_com_luceolab_me_Utils_getApiKey( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env, API_KEY);
}
