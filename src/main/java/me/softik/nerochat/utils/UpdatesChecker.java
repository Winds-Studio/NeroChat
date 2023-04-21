package me.softik.nerochat.utils;

// Credits to LimboAuth: https://github.com/Elytrium/LimboAuth

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class UpdatesChecker {

    /**
     * Checks the difference between the current version and the version from the declared url.
     *
     * <p>If the version is a SNAPSHOT, then this version turns smaller to differ correctly. (e.g. 1.0.4-SNAPSHOT = 1.0.3)
     *
     * @param url            A URL containing the version in the text form. (e.g. 1.0.1)
     * @param currentVersion The current version string. (e.g. 1.0.3-SNAPSHOT)
     * @return True if the currentVersion is newer or equal to latest.
     */
    public static boolean checkVersionByURL(@NonNull String url, @NonNull String currentVersion) {
        try {
            URLConnection connection = new URL(url).openConnection();
            int timeout = (int) TimeUnit.SECONDS.toMillis(5L);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            return checkVersion(new Scanner(connection.getInputStream(), "UTF-8").nextLine().trim(), currentVersion);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to check for updates.", e);
        }
    }

    /**
     * Checks the difference between the declared current and latest versions.
     *
     * <p>If the version is a SNAPSHOT, then this version turns smaller to differ correctly (e.g. 1.0.4-SNAPSHOT = 1.0.3).
     *
     * @param latestVersion  The latest version string (e.g. 1.0.1).
     * @param currentVersion The current version string (e.g. 1.0.3-SNAPSHOT).
     * @return True if the currentVersion is newer or equal to latest.
     */
    public static boolean checkVersion(String latestVersion, String currentVersion) {
        String[] latestParts = latestVersion.split("-")[0].split("\\.");
        String[] currentParts = currentVersion.split("-")[0].split("\\.");

        String latest = latestVersion.replaceAll("\\D+", "");
        String current = currentVersion.replaceAll("\\D+", "");

        StringBuilder sbLatest =
                new StringBuilder(String.format("%0" + latest.length() + "d", Long.parseLong(latest) - (latestVersion.contains("-") ? 1 : 0)));
        StringBuilder sbCurrent =
                new StringBuilder(String.format("%0" + current.length() + "d", Long.parseLong(current) - (currentVersion.contains("-") ? 1 : 0)));

        int padding = 0;

        for (int i = 0; i < Math.max(latestParts.length, currentParts.length); i++) {
            String latestPart = i < latestParts.length ? latestParts[i] : "";
            String currentPart = i < currentParts.length ? currentParts[i] : "";
            int maxLength = Math.max(latestPart.length(), currentPart.length());
            int toAddLatest = maxLength - latestPart.length();
            int toAddCurrent = maxLength - currentPart.length();

            sbLatest.insert(padding, new String(new char[toAddLatest]).replace('\0', '0'));
            sbCurrent.insert(padding, new String(new char[toAddCurrent]).replace('\0', '0'));

            padding += maxLength;
        }

        long latestId = Long.parseLong(sbLatest.toString());
        long currentId = Long.parseLong(sbCurrent.toString());

        return currentId >= latestId;
    }
}
