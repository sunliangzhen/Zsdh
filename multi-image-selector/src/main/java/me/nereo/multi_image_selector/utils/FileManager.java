package me.nereo.multi_image_selector.utils;

import android.os.Environment;


import java.io.File;

/**
 * @author Zero
 * @date 2016/4/24 18:10
 */
public class FileManager {
    public static final String SDCARD_FOLDER_NAME = "TooCMS";
    public static final String DISK_CACHE_DIR_NAME = "TooCMS_Bitmap_Cache";
    public static final String THUMB_CACHE = "TooCMS_Bitmap_Cache_Thumb";

    public FileManager() {
    }

    public static String getSaveFilePath() {
        return getRootFilePath() + "TooCMS" + File.separator + "picture_cache" + File.separator;
    }

    public static String getCompressFilePath() {
        return getRootFilePath() + "TooCMS" + File.separator + "compress_cache" + File.separator;
    }

    public static String getCrashLogFilePath() {
        return getRootFilePath() + "TooCMS" + File.separator + "crash_log" + File.separator;
    }

    public static String getVoiceFilePath() {
        return getRootFilePath() + "TooCMS" + File.separator + "voice" + File.separator;
    }

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals("mounted");
    }

    public static String getRootFilePath() {
        return hasSDCard()? Environment.getExternalStorageDirectory().getAbsolutePath() + "/": Environment.getDataDirectory().getAbsolutePath() + "/data/";
    }

}