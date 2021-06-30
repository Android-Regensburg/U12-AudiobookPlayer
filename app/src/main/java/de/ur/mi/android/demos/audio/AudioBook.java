package de.ur.mi.android.demos.audio;

import java.io.Serializable;

public class AudioBook implements Serializable {

    private static int counter = 0;

    private final int id;
    private final String title,
            description,
            author,
            wallpaperURLString,
            audioURLString;
    private final int duration;

    public AudioBook(String title,
                     String description, String author, int duration, String wallpaperURLString, String audioURLString) {
        this.id = counter++;
        this.title = title;
        this.description = description;
        this.author = author;
        this.duration = duration;
        this.wallpaperURLString =
                wallpaperURLString;
        this.audioURLString = audioURLString;
    }

    public int getId() {
        return id;
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
