package de.ur.mi.android.demos.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import de.ur.mi.android.demos.data.audiobook.AudioBook;

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

    public AudioBook getAudioBookForId(String id) {
        for (AudioBook audioBook : audioBooks) {
            if (audioBook.getId().equals(id)) {
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

    public void requestAudioBooks(Context context, AudioBookDataAvailableListener listener) {
        if (audioBooks.isEmpty()) {
            fetchAudioBookData(context, listener);
        }
        listener.onAudioBookDataAvailable(new ArrayList<>(audioBooks));
    }


    private void fetchAudioBookData(Context context, AudioBookDataAvailableListener listener) {
        APIRequest apiRequest = new APIRequest(APIRequest.Route.AUDIOBOOK_DATA, context);
        apiRequest.send(new APIRequest.ResponseListener() {
            @Override
            public void onResponse(String response) {
                audioBooks.addAll(AudioBook.fromJSONString(response));
                listener.onAudioBookDataAvailable(audioBooks);
            }

            @Override
            public void onError() {

            }
        });
    }

    public interface AudioBookDataAvailableListener {
        void onAudioBookDataAvailable(ArrayList<AudioBook> audioBooks);
    }
}
