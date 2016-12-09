package com.luceolab.me;

import android.app.Activity;
import android.util.ArrayMap;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
    // Format phone number. Template: "(111) 111-11-11"
    public static String formatPhoneNumber(String phone) {
        String result;

        if (phone == null)
            return null;

        if (phone.length() == 10) {
            result = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" +
                    phone.substring(6, 8) + "-" + phone.substring(8);
            return result;
        } else {
            return phone;
        }
    }

    // Round Double number
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Get current working top activity
    public static Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");

            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Working with time interval
    public static long printTimeDifference(DateTime dateTime) {
        DateTime currentDateTime = new DateTime();
        Interval interval = new Interval(dateTime, currentDateTime);
        Duration duration = interval.toDuration();

        return duration.getStandardSeconds();
    }

    static {
        System.loadLibrary("security-native");
    }

    public static native String getApiSecret();
    public static native String getApiKey();
}

