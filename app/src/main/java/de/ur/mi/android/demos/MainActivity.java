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
        manager.addAudioBook(new AudioBook(
                "The Adventures of Tom Sawyer",
                "The Adventures of Tom Sawyer (published 1876) is a very well-known and popular story concerning American youth. Mark Twain's lively tale of the scrapes and adventures of boyhood is set in St. Petersburg, Missouri, where Tom Sawyer and his friend Huckleberry Finn have the kinds of adventures many boys can imagine: racing bugs during class, impressing girls, especially Becky Thatcher, with fights and stunts in the schoolyard, getting lost in a cave, and playing pirates on the Mississippi River.",
                "Mark Twain",
                1598,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Tom_Sawyer_1876_frontispiece.jpg/220px-Tom_Sawyer_1876_frontispiece.jpg",
                "https://ia802607.us.archive.org/31/items/tom_sawyer_librivox/TSawyer_01-02_twain.mp3"
        ));
        manager.addAudioBook(new AudioBook(
                "Treasure Island",
                "Treasure Island is an adventure novel, a thrilling tale of buccaneers and buried gold. Traditionally considered a coming of age story, it is an adventure tale of superb atmosphere, character and action, and also a wry commentary on the ambiguity of morality—as seen in Long John Silver—unusual for children's literature then and now.",
                "Robert Louis Stevenson",
                1520,
                "https://images-na.ssl-images-amazon.com/images/I/71jX9SL21BL.jpg",
                "https://ia802608.us.archive.org/23/items/treasureisland_librivox/treasure_island_01-02_stevenson.mp3"
        ));
        manager.addAudioBook(new AudioBook(
                "Peter Pan",
                "Peter Pan is the well-loved story of three children and their adventures in Neverland with the boy who refuses to grow up. Swashbuckling, fairy dust, and flight; mermaid lagoons, ticking crocodiles, and Princess Tiger Lily; second to the right and then straight on till morning. You know the story... and if you don't, please start listening immediately! (summary by Meredith Hughes)",
                "J.M. Barrie",
                1070,
                "https://i.harperapps.com/hcanz/covers/9780062362223/y648.jpg",
                "https://ia800200.us.archive.org/4/items/peter_pan_0707_librivox/peterpan_01_barrie.mp3"
        ));
        manager.addAudioBook(new AudioBook(
                "Gulliver's Travels",
                "Gulliver's Travels (1726, amended 1735), officially Travels into Several Remote Nations of the World, is a novel by Jonathan Swift that is both a satire on human nature and a parody of the \"travelers' tales\" literary sub-genre. It is widely considered Swift's magnum opus and is his most celebrated work, as well as one of the indisputable classics of English literature. ",
                "Jonathan Swift",
                757,
                "https://m.media-amazon.com/images/I/51tZFussw5L.jpg",
                "https://ia800503.us.archive.org/17/items/gulliver_ld_librivox/gulliverstravels_00_swift.mp3"
        ));
        manager.addAudioBook(new AudioBook(
                "Alice's Adventures in Wonderland",
                "In this children's classic, a girl named Alice follows falls down a rabbit-hole into a fantasy realm full of talking creatures. She attends a never-ending tea party and plays croquet at the court of the anthropomorphic playing cards.",
                "Lewis Carroll",
                640,
                "https://images-na.ssl-images-amazon.com/images/I/71su1O1yFJL.jpg",
                "http://ia800503.us.archive.org/download/alice_in_wonderland_librivox/wonderland_ch_01.mp3"
        ));
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