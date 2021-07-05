package de.ur.mi.android.demos.data;

import android.content.Context;
import java.util.ArrayList;

import de.ur.mi.android.demos.data.audiobook.AudioBook;

/**
 * Der AudioBookManager soll eine Liste von AudioBooks verwalten und brauchbare Methode für diesen Kontext z.B. getNext bereitstellen.
 * Zusätzlich wird hier der APIRequest gesteuert.
 *
 * Diese Klasse ist als Singleton angelegt, das heißt innerhalb einer Anwendung kann nur eine Instanz von ihr erzeugt werden.
 * Dies ist für diese Aufgabe sehr praktisch, da sowohl in der MainActivity, als auch in der AudioPlayerActivity auf den AudioBookManager
 * zugegriffen werden kann, ohne einen Verweis auf eine entsprechende Instanz übergeben zu müssen.
 * Dadurch wird ein Weiterschalten zwischen den Hörbüchern in der Player-Ansicht ermöglicht.
 *
 * https://en.wikipedia.org/wiki/Singleton_pattern
 */
public class AudioBookManager {

    /**
     * In der statischen instance-Variable wird eine Instanz des AudioBookManagers gehalten.
     * Die Variable wird instantiiert, sobald innerhalb einer anderen Klasse der AudioBookManager angefordert wird.
     * Beim ersten Aufruf der getInstance-Methode, wird eine neue Instanz erstellt un in instance abgelegt.
     * Bei weiteren Aufrufen wird die bereits erstellte Instanz aus dem ersten Aufruf zurückgegeben.
     */
    private static AudioBookManager instance;
    private final ArrayList<AudioBook> audioBooks;

    /**
     * Ein privater Konstruktor ist für ein Singleton zu empfehlen, da das unkontrollierte Erstellen von Instanzen verhindert werden soll.
     */
    private AudioBookManager() {
        audioBooks = new ArrayList<>();
    }

    /**
     * Siehe Kommentar zum Konstruktor:
     * Durch den null-Check wird nur dann eine neue Instanz erstellt, wenn noch keine Referenz in der instance-Variable abgelegt ist.
     * Das passiert nur beim ersten Zugriff auf die AudioBookManager-Klasse, bzw. deren getInstance-Methode.
     *
     * @return Aktuelle Instanz
     */
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

    /**
     * @param audioBook: Das AudioBook von dessen Position das vorherige ermittelt werden soll
     * @return Das Audiobook das an der vorherigen Position zum Übergegeben steht
     */
    public AudioBook getNext(AudioBook audioBook) {
        int index = audioBooks.indexOf(audioBook);
        if (index < 0 || index == audioBooks.size() - 1) return null;
        return audioBooks.get(index + 1);
    }

    /**
     * @param audioBook: Das AudioBook von dessen Position das nachfolgende ermittelt werden soll
     * @return Das Audiobook das an der nachfolgenden Position zum Übergegeben steht
     */
    public AudioBook getPrevious(AudioBook audioBook) {
        int index = audioBooks.indexOf(audioBook);
        if (index <= 0) return null;
        return audioBooks.get(index - 1);
    }

    public void requestAudioBooks(Context context, AudioBookDataAvailableListener listener) {
        if (audioBooks.isEmpty()) {
            fetchAudioBookData(context, listener);
            return;
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
