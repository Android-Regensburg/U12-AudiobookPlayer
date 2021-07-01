package de.ur.mi.android.demos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import de.ur.mi.android.demos.adapter.AudioBookAdapter;
import de.ur.mi.android.demos.data.audiobook.AudioBook;
import de.ur.mi.android.demos.data.AudioBookManager;

public class MainActivity extends AppCompatActivity {

    public static final String AUDIOBOOK_EXTRA_KEY = "audiobook";

    private AudioBookManager manager;
    private AudioBookAdapter adapter;
    private RecyclerView recyclerAudioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initManager();
        initUI();
        initAdapter();
    }

    private void initManager() {
        manager = AudioBookManager.getInstance();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        recyclerAudioList = findViewById(R.id.recycler_audiobooks);
        recyclerAudioList.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerAudioList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

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

    private void startPlayerActivity(AudioBook audioBook) {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(AUDIOBOOK_EXTRA_KEY, audioBook);
        startActivity(intent);
    }
}