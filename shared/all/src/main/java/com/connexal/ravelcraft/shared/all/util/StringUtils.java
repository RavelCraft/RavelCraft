package com.connexal.ravelcraft.shared.all.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
    public static String formatDate(long timestamp) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(timestamp)) + " UTC";
    }
}
