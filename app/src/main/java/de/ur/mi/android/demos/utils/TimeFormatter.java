package de.ur.mi.android.demos.utils;

import java.util.Locale;

public class TimeFormatter {

    /**
     * @param duration: Die Dauer eines Hörbuchs
     * @return String in Format "Stunden:Minuten:Sekunden" ausgehend von Dauer
     */
    public static String formatSecondsToDurationString(int duration) {
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;

        return String.format(Locale.GERMAN,"%02d:%02d:%02d", hours, minutes, seconds);
    }
}
