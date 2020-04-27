package com.lgh.test;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    private static final String SETTING = "com_guide_sdk_demo";

    private static final String PALETTE_KEY = "palette";
    private static final String SCALE_KEY = "scale";
    private static final String ROTATE_KEY = "rotate";
    private static final String IMAGE_ALGO_KEY = "image_algo";
    private static final String SWITCH_KEY = "switch";
    private static final String PERIOD_KEY = "period";
    private static final String DELAY_KEY = "delay";

    private static void putStringValue(Context context, String key, String value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putString(key, value);
        sp.commit();
    }

    private static String getStringValue(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        String value = sp.getString(key, defValue);
        return value;
    }

    public static String getPalette(Context context) {
        return getStringValue(context, PALETTE_KEY, "10");
    }

    public static void setPalette(Context context, String value) {
        putStringValue(context, PALETTE_KEY, value);
    }

    public static String getScale(Context context) {
        return getStringValue(context, SCALE_KEY, "3");
    }

    public static void setScale(Context context, String value) {
        putStringValue(context, SCALE_KEY, value);
    }

    public static String getRotate(Context context) {
        return getStringValue(context, ROTATE_KEY, "1");
    }

    public static void setRotate(Context context, String value) {
        putStringValue(context, ROTATE_KEY, value);
    }

    public static String getImageAlgo(Context context) {
        return getStringValue(context, IMAGE_ALGO_KEY, "开");
    }

    public static void setImageAlgo(Context context, String value) {
        putStringValue(context, IMAGE_ALGO_KEY, value);
    }

    public static String getSwitch(Context context) {
        return getStringValue(context, SWITCH_KEY, "开");
    }

    public static void setSwitch(Context context, String value) {
        putStringValue(context, SWITCH_KEY, value);
    }

    public static String getPeriod(Context context) {
        return getStringValue(context, PERIOD_KEY, String.valueOf(30 * 1000));
    }

    public static void setPeriod(Context context, String value) {
        putStringValue(context, PERIOD_KEY, value);
    }

    public static String getDelay(Context context) {
        return getStringValue(context, DELAY_KEY, String.valueOf(60 * 1000));
    }

    public static void setDelay(Context context, String value) {
        putStringValue(context, DELAY_KEY, value);
    }

}
