package de.ur.mi.android.demos.audio;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.ur.mi.android.demos.data.audiobook.AudioBook;

public class AudioPlayer {

    private final PlaybackListener listener;
    private MediaPlayer mediaPlayer;
    private ScheduledFuture scheduledFuture;

    public AudioPlayer(PlaybackListener listener) {
        this.listener = listener;
    }

    public void prepare(AudioBook audioBook) {
        if (audioBook == null) return;
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(audioBook.getAudioURLString());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    handleIsPrepared(mp);
                }
            });
            mediaPlayer.setOnCompletionListener(mp -> listener.onPlaybackEnded());
        } catch (IOException e) {
            listener.onPlaybackError();
            e.printStackTrace();
        }
    }

    private void handleIsPrepared(MediaPlayer mp) {
        this.mediaPlayer = mp;
        listener.onPlaybackReady();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            listener.onPlaybackStarted();
            scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->
                    listener.onRemainingTimeUpdated(mediaPlayer.getCurrentPosition()), 0, 1000, TimeUnit.MILLISECONDS);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            scheduledFuture.cancel(true);
            mediaPlayer.pause();
            listener.onPlaybackPaused();
        }
    }

    public void seek(int milliseconds) {
        if (mediaPlayer == null) return;
        pause();
        mediaPlayer.seekTo(milliseconds);
        mediaPlayer.setOnSeekCompleteListener(mp -> play());
    }

    public void changeTitle(AudioBook audioBook) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        prepare(audioBook);
        listener.onPlaybackEnded();
    }

    public interface PlaybackListener {
        void onPlaybackReady();
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackEnded();
        void onRemainingTimeUpdated(int milliseconds);
        void onPlaybackError();
    }
}
