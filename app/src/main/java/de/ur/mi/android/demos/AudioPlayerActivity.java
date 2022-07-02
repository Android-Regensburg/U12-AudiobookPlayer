package de.ur.mi.android.demos;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * In dieser Activity werden Details zu einem Hörbuch angezeigt und das Hörbuch abgespielt.
 * Über die Knöpfe und die Seekbar kann die Wiedergabe gesteuert werden.
 */
public class AudioPlayerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }

}
