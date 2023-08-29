package com.connexal.ravelcraft.proxy.java.util;

public class XMLParser {
    public static String getValue(String path, String xml) {
        String out = xml;

        String[] parts;
        if (path.contains(".")) {
            parts = path.split("\\.");
        } else {
            parts = new String[1];
            parts[0] = path;
        }

        for (String part : parts) {
            String tag = "<" + part + ">";
            int index = out.indexOf(tag);
            if (index == -1) {
                return null;
            } else {
                out = out.substring(index + tag.length());
            }
        }
        String tag = "</" + parts[parts.length - 1] + ">";
        int index = out.indexOf(tag);
        if (index == -1) {
            return null;
        } else {
            out = out.substring(0, out.indexOf(tag));
        }

        if (out.startsWith("\n")) {
            out = out.substring(1);
        }
        if (out.endsWith("\n")) {
            out = out.substring(0, out.length() - 1);
        }

        return out;
    }

    public static String getValue(String path, String xml, String defaultValue) {
        String out = getValue(path, xml);
        if (out == null) {
            return defaultValue;
        } else {
            return out;
        }
    }
}
