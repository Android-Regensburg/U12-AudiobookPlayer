package de.ur.mi.android.demos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import de.ur.mi.android.demos.adapter.AudioBookAdapter;
import de.ur.mi.android.demos.audio.AudioBook;
import de.ur.mi.android.demos.audio.AudioBookManager;

public class MainActivity extends AppCompatActivity {

    public static final String AUDIOBOOK_EXTRA_KEY = "audiobook";

    private AudioBookManager manager;
    private AudioBookAdapter adapter;
    private RecyclerView recyclerAudioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initManager();
        initSampleData();
        initUI();
        initAdapter();
    }

    private void initManager() {
        manager = AudioBookManager.getInstance();
    }

    private void initSampleData() {
        for (int i = 0; i < 10; i++) {
            manager.addAudioBook(new AudioBook(
                    "Game of Thrones",
                    "A Song of Ice and Fire",
                    "George R.R. Martin",
                    3000,
                    "https://wallpaperaccess.com/full/2787330.jpg",
                    "https://ia601505.us.archive.org/12/items/theadventuresofoldmrtoad_2106_librivox/theadventuresofoldmrtoad_01_burgess_64kb.mp3"
            ));
        }
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
        adapter.updateData(manager.getCurrentAudioBooks());
    }

    private void startPlayerActivity(AudioBook audioBook) {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(AUDIOBOOK_EXTRA_KEY, audioBook);
        startActivity(intent);
    }
}