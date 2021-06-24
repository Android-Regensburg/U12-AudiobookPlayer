package de.ur.mi.android.demos.audio;

import java.io.Serializable;

public class AudioBook implements Serializable {

    private final String title;
    private final String description;
    private final String author;
    private final int duration;
    private final String wallpaperURLString;
    private final String audioURLString;

    public AudioBook(String title,
                     String description, String author, int duration, String wallpaperURLString, String audioURLString) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.duration = duration;
        this.wallpaperURLString =
                wallpaperURLString;
        this.audioURLString = audioURLString;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public int getDuration() {
        return duration;
    }

    public String getWallpaperURLString() {
        return wallpaperURLString;
    }

    public String getAudioURLString() {
        return audioURLString;
    }
}
