package de.ur.mi.android.demos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.ur.mi.android.demos.adapter.AudioBookAdapter;
import de.ur.mi.android.demos.audio.AudioBook;

public class MainActivity extends AppCompatActivity {

    public static final String AUDIOBOOK_EXTRA_KEY = "audiobook";

    private AudioBookAdapter adapter;
    private RecyclerView recyclerAudioList;

    private ArrayList<AudioBook> sampleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSampleData();
        initUI();
        initAdapter();
    }

    private void initSampleData() {
        sampleData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sampleData.add(new AudioBook(
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
        adapter = new AudioBookAdapter(this, new AudioBookAdapter.OnAudioBookAdapterItemClickedListener() {
            @Override
            public void onItemClicked(AudioBook audioBook) {
                startPlayerActivity(audioBook);
            }
        });
        recyclerAudioList.setAdapter(adapter);
        adapter.updateData(sampleData);
    }

    private void startPlayerActivity(AudioBook audioBook) {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(AUDIOBOOK_EXTRA_KEY, audioBook);
        startActivity(intent);
    }
}