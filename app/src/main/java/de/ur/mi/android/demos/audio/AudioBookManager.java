package de.ur.mi.android.demos.audio;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class AudioBookManager {

    private static AudioBookManager instance;
    private final ArrayList<AudioBook> audioBooks;

    private AudioBookManager() {
        audioBooks = new ArrayList<>();
    }

    public static AudioBookManager getInstance () {
        if (instance == null) {
            instance = new AudioBookManager();
        }
        return instance;
    }

    public void addAudioBook(AudioBook audioBook) {
        audioBooks.add(audioBook);
    }

    public AudioBook getAudioBook(int position) {
        return audioBooks.get(position);
    }

    public AudioBook getAudioBookForId(int id) {
        for (AudioBook audioBook : audioBooks) {
            if (audioBook.getId() == id) {
                return audioBook;
            }
        }
        return null;
    }

    public AudioBook getNext(AudioBook audioBook) {
        Log.d("manager", String.valueOf(audioBooks.size()));
        int index = audioBooks.indexOf(audioBook);
        Log.d("manager", String.valueOf(index));
        if (index < 0 || index == audioBooks.size() - 1) return null;
        return audioBooks.get(index + 1);
    }

    public AudioBook getPrevious(AudioBook audioBook) {
        int index = audioBooks.indexOf(audioBook);
        if (index <= 0) return null;
        return audioBooks.get(index - 1);
    }

    public ArrayList<AudioBook> getCurrentAudioBooks() {
        return new ArrayList<>(audioBooks);
    }
}
