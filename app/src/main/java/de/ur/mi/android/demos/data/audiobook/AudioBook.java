package de.ur.mi.android.demos.data.audiobook;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Objekte dieser Klasse repräsentieren einzelene Hörbücher.
 * In den Eigenschaften der Klasse werden die relevanten Informationen über die Hörbücher, so wie
 * Links zur Audiodatei und zum Cover gespeichert.
 * Über Getter-Methoden werden diese Eigenschaften nach außen verfügbargemacht.
 */
public class AudioBook implements Serializable {

    private final String id, // Eindeutige ID dieses Hörbuchs
            title, // Titel des Hörbuchs
            description, // Beschreibung des Hörbuchs
            author, // Autor*in des (Hör-) Buchs
            wallpaperURLString, // Link zum Cover
            audioURLString; // Link zur Audiodatei
    private final int duration; // Länge des Hörbuchs in Sekunden

    // Über den Konstruktor werden die Eigenschaften des Hörbuchs gesetzt. Diese sind dann unveränderlich (Immutable).
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


    /**
     * Mit Hilfe dieser statischen Methode kann aus einer JSON-formatierten Antwort der API eine
     * Liste von AudioBook Objekten erzeugt werden, die dann zurückgegeben wird.
     *
     * @param jsonString JSON-formatierte Antwort der API
     * @return Liste von AudioBook Objekten, die aus dem JSON String erzeugt wurden.
     */
    public static ArrayList<AudioBook> fromJSONString(String jsonString) {
        ArrayList<AudioBook> audioBooks = new ArrayList<>();
        JsonArray jsonArray = new Gson().fromJson(jsonString, JsonArray.class);
        jsonArray.forEach(jsonElement -> {
            audioBooks.add(new Gson().fromJson(jsonElement.getAsJsonObject().toString(), AudioBook.class));
        });
        return audioBooks;
    }

    // Getter Methoden:

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
