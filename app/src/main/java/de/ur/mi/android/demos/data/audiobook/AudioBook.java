package de.ur.mi.android.demos.data.audiobook;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;

public class AudioBook implements Serializable {

    /**
     * Eigenschaften eines Audiobooks. Diese sind konstant einer Instanz zugeordnet, daher als final deklariert.
     */
    private final String id,
            title,
            description,
            author,
            wallpaperURLString,
            audioURLString;
    private final int duration;

    public AudioBook(String id,
                     String title,
                     String description,
                     String author,
                     String wallpaperURLString,
                     String audioURLString,
                     int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.wallpaperURLString = wallpaperURLString;
        this.audioURLString = audioURLString;
        this.duration = duration;
    }

    /**
     * In dieser Methode wird die Gson-Library genutzt, um auf einfache Art und Weise Instanzen einer Klasse aus einem JSONArray zu parsen.
     * Das funktioniert hier deshalb mit lediglich 5 Zeilen Code, weil die Felder innerhalb des JSON genauso benannt sind, wie die Member der AudioBook-Klasse.
     * Bei unterschiedlicher Benennung der Felder, kann mit einer "@SerializedName"-Annotation gearbeitet werden.
     *
     * @param jsonString: Daten der Response eines API Zugriffs, in diesem Fall ein JSONArray aus Audiobooks
     * @return Eine ArrayList aus AudioBooks, die aus dem JSON geparsed wurden
     */
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
