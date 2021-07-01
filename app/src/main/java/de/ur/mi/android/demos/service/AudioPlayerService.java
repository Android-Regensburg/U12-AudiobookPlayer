package de.ur.mi.android.demos.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import de.ur.mi.android.demos.audio.AudioPlayer;
import de.ur.mi.android.demos.data.audiobook.AudioBook;

public class AudioPlayerService extends Service implements AudioPlayer.PlaybackListener {

    public static final String AUDIOBOOK_KEY = "audiobook";
    private AudioPlayer audioPlayer;
    private AudioPlayerServiceListener listener;

    private final IBinder binder = new AudioPlayerBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioPlayer.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        audioPlayer = new AudioPlayer(this);
        audioPlayer.prepare((AudioBook) intent.getSerializableExtra(AUDIOBOOK_KEY));
        return binder;
    }

    public void registerPlaybackListener(AudioPlayerServiceListener listener) {
        this.listener = listener;
    }

    public void playAudio() {
        audioPlayer.play();
    }

    public void pauseAudio() {
        audioPlayer.pause();
    }

    public void seekAudio(int milliseconds) {
        audioPlayer.seek(milliseconds);
    }

    public void changeTitle(AudioBook audioBook) {
        audioPlayer.changeTitle(audioBook);
    }

    @Override
    public void onPlaybackReady() {
        if (listener != null) {
            listener.onPlaybackReady();
        }
    }

    @Override
    public void onPlaybackStarted() {
        if (listener != null) {
            listener.onPlaybackStarted();
        }
    }

    @Override
    public void onPlaybackPaused() {
        if (listener != null) {
            listener.onPlaybackPaused();
        }
    }

    @Override
    public void onPlaybackEnded() {
        if (listener != null) {
            listener.onPlaybackEnded();
        }
    }

    @Override
    public void onRemainingTimeUpdated(int milliseconds) {
        if (listener != null) {
            listener.onRemainingTimeUpdated(milliseconds);
        }
    }

    @Override
    public void onPlaybackError() {
        if (listener != null) {
            listener.onPlaybackError();
        }
    }


    public class AudioPlayerBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    public interface AudioPlayerServiceListener {
        void onPlaybackReady();
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackEnded();
        void onRemainingTimeUpdated(int milliseconds);
        void onPlaybackError();
    }
}
