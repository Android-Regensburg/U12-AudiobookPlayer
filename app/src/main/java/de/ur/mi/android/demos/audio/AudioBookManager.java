package de.ur.mi.android.demos.audio;

import java.util.ArrayList;
import java.util.Collections;

public class AudioBookManager {

    private static AudioBookManager instance;
    private ArrayList<AudioBook> audioBooks;

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

    public AudioBook getNext(AudioBook audioBook) {
        int index = audioBooks.indexOf(audioBook);
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
