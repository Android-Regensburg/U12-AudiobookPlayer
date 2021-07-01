package de.ur.mi.android.demos.data.audiobook;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;

public class AudioBook implements Serializable {

    private final String id,
            title,
            description,
            author,
            wallpaperURLString,
            audioURLString;
    private final int duration;

    public AudioBook(String id, String title,
                     String description,
                     String author,
                     String wallpaperURLString,
                     String audioURLString,
                     int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.wallpaperURLString =
                wallpaperURLString;
        this.audioURLString = audioURLString;
        this.duration = duration;
    }

    public static ArrayList<AudioBook> fromJSONString(String jsonString) {
        ArrayList<AudioBook> audioBooks = new ArrayList<>();
        JsonArray jsonArray = new Gson().fromJson(jsonString, JsonArray.class);
        jsonArray.forEach(jsonElement -> {
            audioBooks.add(new Gson().fromJson(jsonElement.getAsJsonObject().toString(), AudioBook.class));
        });
        return audioBooks;
    }

    public String getId() {
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
