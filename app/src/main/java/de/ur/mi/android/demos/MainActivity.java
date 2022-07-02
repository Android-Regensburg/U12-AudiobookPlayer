package de.ur.mi.android.demos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;

/**
 * Diese Activity ist der Einstiegspunkt in die Anwendung.
 * Hier soll eine Liste verfügbarer Hörbücher angezeigt werden, die dann angeklickt und so in der
 * Detail- und Abspielansicht geöffnet werden können.
 */
public class MainActivity extends Activity {

    private RecyclerView recyclerAudioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        recyclerAudioList = findViewById(R.id.recycler_audiobooks);
        recyclerAudioList.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerAudioList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        // TODO: Implementieren Sie hier die Liste an Hörbüchern und die Interaktion mit dieser.
    }
}