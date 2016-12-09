LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := security-native
LOCAL_SRC_FILES := security-native.c

include $(BUILD_SHARED_LIBRARY)
