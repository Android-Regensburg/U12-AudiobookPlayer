package de.ur.mi.android.demos.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.ur.mi.android.demos.audio.AudioBook;

public class AudioPlayerService extends Service {

    public static final String AUDIOBOOK_KEY = "audiobook";
    private MediaPlayer mediaPlayer;
    private PlaybackListener listener;
    private ScheduledFuture scheduledFuture;

    private final IBinder binder = new AudioPlayerBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        prepareMediaPlayer(intent);
        return binder;
    }

    private void prepareMediaPlayer(Intent intent) {
        AudioBook audioBook = (AudioBook) intent.getSerializableExtra(AUDIOBOOK_KEY);
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
        } catch (IOException e) {
            e.printStackTrace();
            listener.onPlaybackError();
        }
    }

    public void registerPlaybackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    private void handleIsPrepared(MediaPlayer mp) {
        this.mediaPlayer = mp;
        listener.onPlaybackReady(mp.getDuration());
    }

    public class AudioPlayerBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    public void playAudio() {
        Log.d("myaudioplayservice", "Now Playing");
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            listener.onPlaybackStarted();
            scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    listener.onRemainingTimeUpdated(mediaPlayer.getCurrentPosition());
                }
            }, 1000, 1000, TimeUnit.MILLISECONDS);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    listener.onPlaybackEnded();
                }
            });
        }
    }

    public void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            scheduledFuture.cancel(true);
            mediaPlayer.pause();
            listener.onPlaybackPaused();
        }
    }

    public void setAudioTimer(int milliseconds) {
        pauseAudio();
        mediaPlayer.seekTo(milliseconds);
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                playAudio();
            }
        });
    }

    public interface PlaybackListener {
        void onPlaybackReady(int milliseconds);
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackEnded();
        void onRemainingTimeUpdated(int milliseconds);
        void onPlaybackError();
    }
}