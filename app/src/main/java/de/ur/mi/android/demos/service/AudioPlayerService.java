package de.ur.mi.android.demos.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import de.ur.mi.android.demos.audio.AudioPlayer;
import de.ur.mi.android.demos.data.audiobook.AudioBook;

/**
 * Der AudioPlayerService dient dazu, dass die Wiedergabe forgesetzt werden kann, auch dann wenn die App selbst nicht im Vordergrund geöffnet ist.
 * In dieser Lösung wird ein gebundener Service verwendet.
 */
public class AudioPlayerService extends Service implements AudioPlayer.PlaybackListener {


    public static final String AUDIOBOOK_KEY = "audiobook";
    private AudioPlayer audioPlayer;
    private AudioPlayerServiceListener listener;

    /**
     * Der AudioPlayerBinder ermöglicht es einer Activity, die den Service bindet, auf eine entsprechende Instanz von diesem zuzugreifen.
     * In diesem Fall ist dies notwendig, da über den Service die Wiedergabe gesteuert werden muss.
     */
    private final IBinder binder = new AudioPlayerBinder();
    public class AudioPlayerBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    /**
     * Diese Callback-Methode wird aufgerufen, wenn der Service erfolgreich gebunden wurde.
     * Über den übergebenen Intent kann auf Extras zugegriffen werden, die die AudioPlayerActivity übergeben möchte.
     * In diesem Fall also das abzuspielende AudioBook.
     *
     * @param intent: Intent mit dem der Service gestartet wurde. Über diesen kann auf Extras zugegriffen werden
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        audioPlayer = new AudioPlayer(this);
        audioPlayer.prepare((AudioBook) intent.getSerializableExtra(AUDIOBOOK_KEY));
        return binder;
    }

    /**
     * Der MediaPlayer muss freigegeben werden, sobald der Service nicht mehr benötigt wird.
     * Dies geschieht über die AudioPlayer-Instanz.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        audioPlayer.release();
    }

    public void registerPlaybackListener(AudioPlayerServiceListener listener) {
        this.listener = listener;
    }


    /**
     * Die nachfolgenden Methoden steuern die Wiedergabe über den AudioPlayer.
     * Bei erfolgreicher Änderung des Zustands, wird das Ereignis über den Listener kommuniziert.
     */

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


    /**
     * Die nachfolgenden Methoden sind die Callback-Methoden für den AudioPlayerServiceListener.
     * Innerhalb dieser Lösung wäre ein eigenes Interface unabhängig von Coding-Style prinzipiell nicht notwendig,
     * da dieses die Events des AudioPlayers unverändert an seinen eigenen Listener weitergibt.
     * Dennoch wäre es eine sinnvolle Erweiterung für einen AudioBookPlayer die Anzeige einer Vordergrund-Benachrichtigung, wenn die Wiedergabe aktiv ist.
     * Zur Steuerung dieser Benachrichtigung wäre das zusätzliche Interface notwendig, um z.B. eine Anzeigedauer zu aktualisieren.
     */

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

    public interface AudioPlayerServiceListener {
        void onPlaybackReady();
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackEnded();
        void onRemainingTimeUpdated(int milliseconds);
        void onPlaybackError();
    }
}
