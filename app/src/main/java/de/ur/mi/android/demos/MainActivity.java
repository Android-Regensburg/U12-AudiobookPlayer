package de.ur.mi.android.demos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import de.ur.mi.android.demos.adapter.AudioBookAdapter;
import de.ur.mi.android.demos.data.audiobook.AudioBook;
import de.ur.mi.android.demos.data.AudioBookManager;

/**
 * Diese Activity ist der Einstiegspunkt in die Anwendung. Hier wird eine Liste der verfügbaren
 * Hörbücher angezeigt, die dann selektiert und so in der DetailAnsicht geöffnet werden können.
 */
public class MainActivity extends Activity {

    // Unter diesem Schlüssel wird das selektierte Hörbuch an die AudioPlayerActivity weitergegeben.
    public static final String AUDIOBOOK_EXTRA_KEY = "audiobook";

    // Der AudioBookManager verwaltet die interne Repräsentation der Liste an Hörbüchern
    private AudioBookManager manager;
    // Der AudioBookAdapter kümmert sich um die Darstellung der Liste der Hörbücher im UI.
    private AudioBookAdapter adapter;
    private RecyclerView recyclerAudioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initManager();
        initUI();
        initAdapter();
    }

    /**
     * Erstellen der Instanz des AudioBookManagers. Da dieser als Singleton fungiert, hier kein direkter Konstruktoraufruf
     * https://en.wikipedia.org/wiki/Singleton_pattern
     */
    private void initManager() {
        manager = AudioBookManager.getInstance();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        recyclerAudioList = findViewById(R.id.recycler_audiobooks);
        recyclerAudioList.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerAudioList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    // Der Adapter wird initialisiert und auf dem RecyclerView registriert. Dann wird die Liste an Büchern angefragt und dem Adapter übergeben.
    private void initAdapter() {
        adapter = new AudioBookAdapter(this, this::startPlayerActivity);
        recyclerAudioList.setAdapter(adapter);
        manager.requestAudioBooks(this, new AudioBookManager.AudioBookDataAvailableListener() {
            @Override
            public void onAudioBookDataAvailable(ArrayList<AudioBook> audioBooks) {
                adapter.updateData(audioBooks);
            }
        });
    }

    /**
     * Beim Klick auf ein Hörbuch wird die AudioPlayerAcitity gestartet.
     * In dieser können Details eingesehen und die Wiedergabe gesteuert werden.
     * Das selektierte Hörbuch wird dem Intent als Extra mitgegeben.
     *
     * @param audioBook das geklickte Hörbuch.
     */
    private void startPlayerActivity(AudioBook audioBook) {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(AUDIOBOOK_EXTRA_KEY, audioBook);
        startActivity(intent);
    }
}