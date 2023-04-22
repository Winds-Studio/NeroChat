package me.softik.nerochat.utils;

import me.softik.nerochat.NeroChat;

import java.util.logging.Level;

public class LogUtils {

    public static void moduleLog(Level logLevel, String moduleName, String logMessage) {
        NeroChat.getLog().log(logLevel, "<" + moduleName + "> " + logMessage);
    }

}

