package de.ur.mi.android.demos.audio;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.ur.mi.android.demos.data.audiobook.AudioBook;

/**
 * Der Audioplayer kapselt die Steuerung und Wiedergabe eines Hörbuchs nach außen und bietet einfach
 * zu verwendende öffentliche Methoden dafür an.
 */
public class AudioPlayer {

    private final PlaybackListener listener;
    private MediaPlayer mediaPlayer;

    /**
     * Verweis auf geplanten Hintergrundthread, in diesem Fall zum Aktualisieren der aktuellen Position innerhalb des AudioBooks
     * bzw. der Anzeige in der SeekBar. Wird als Instanzvariable benötigt, da der Thread gecancelled werden soll, wenn nicht abgespielt wird.
     */
    private ScheduledFuture scheduledFuture;

    public AudioPlayer(PlaybackListener listener) {
        this.listener = listener;
    }

    /**
     * In dieser Methode wird der Vorbereitungsschritt des MediaPlayers durchgeführt.
     * Dazu muss diesem die Datenquelle übergeben werden.
     * Da diese auf einem externen Server gespeichert wird, wird der Inhalt gestreamed und muss vorgeladen werden.
     * Dieser Vorgang ist zeitlich vorab nicht zu bestimmen, daher ist der prepareAync()-Aufruf notwendig.
     * Mit setOnPreparedListener kann eine anonyme Klasse bzw. Callback definiert werden, der aufgerufen wird, sobald das Hörbuch vorgeladen ist.
     *
     * @param audioBook das abgespielt werden soll
     */
    public void prepare(AudioBook audioBook) {
        if (audioBook == null) return;
        MediaPlayer mediaPlayer = new MediaPlayer();
        /*
            Festlegen der Eigenschaften zur Optimierung der Wiedergabe. In diesem Fall Sprache.
         */
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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    listener.onPlaybackEnded();
                }
            });
        } catch (IOException e) {
            listener.onPlaybackError();
            e.printStackTrace();
        }
    }

    /**
     * Innerhalb dieser Methode wird der Listener über den Zustand, also die abgeschlossene Vorbereitung, informiert.
     * Hier kann z.B. eine Ladeanimation beendet werden.
     *
     * @param mp: Die fertig vorbereitete Instanz des MediaPlayers
     */
    private void handleIsPrepared(MediaPlayer mp) {
        this.mediaPlayer = mp;
        listener.onPlaybackReady();
    }

    /**
     * Der MediaPlayer sollte freigegeben werden, sobald er nicht mehr benötigt wird. Aufruf z.B. in onDestroy
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    /**
     * In der play-Methode wird, neben dem Start des Abspielens, ein Hintergrundthread zum Mitzählen der aktuellen Spielzeit gestartet.
     * Dieser Thread soll abgebrochen werden, sobald das Playback pausiert oder beendet wird.
     */
    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            listener.onPlaybackStarted();
            scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    listener.onRemainingTimeUpdated(mediaPlayer.getCurrentPosition());
                }
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Neben dem Pausieren wird der in play gestartete Hintergrundthread hier beendet.
     */
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            scheduledFuture.cancel(true);
            mediaPlayer.pause();
            listener.onPlaybackPaused();
        }
    }

    /**
     * Diese Methode ermöglicht es, zu bestimmten Stellen innerhalb des Hörbuchs zu springen.
     * Da beim Streamen der Datei aus dem Netz nur ein Teil der Daten vorgeladen wird, kann durch einen
     * Seek-Befehl, ein erneutes Laden notwendig sein. Deshalb wird ein OnSeekCompleteListener benötigt, bevor die Wiedergabe fortgesetzt wird.
     *
     * @param milliseconds: Der Zeitstempel innerhalb des Hörbuchs, zu dem gesprungen werden soll
     */
    public void seek(int milliseconds) {
        if (mediaPlayer == null) return;
        pause();
        mediaPlayer.seekTo(milliseconds);
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                play();
            }
        });
    }

    /**
     * Diese Methode ermöglicht das Wechseln zu einem anderen Hörbuch.
     * Dafür ist ein erneutes Vorbereiten notwendig, da wieder eine Audio-Datei gestreamt werden soll.
     * Eine Instanz des MediaPlayers sollte immer nur für eine Datenquelle verwendet werden, d.h. die bisher verwendete Instanz sollte freigegeben werden.
     *
     * Wichtig ist hier der korrekte Umgang mit den Zustände des MediaPlayers.
     * Die Wiedergabe sollte beendet werden, bevor die Instanz freigegeben wird.
     *
     * @param audioBook
     */
    public void changeTitle(AudioBook audioBook) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        prepare(audioBook);
        listener.onPlaybackEnded();
    }


    /**
     * Interface zur Kommunikation mit dem AudioPlayer
     */
    public interface PlaybackListener {
        void onPlaybackReady();
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackEnded();
        void onRemainingTimeUpdated(int milliseconds);
        void onPlaybackError();
    }
}
