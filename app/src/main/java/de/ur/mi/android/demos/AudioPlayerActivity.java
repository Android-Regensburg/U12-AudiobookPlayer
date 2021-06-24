package de.ur.mi.android.demos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;

import de.ur.mi.android.demos.audio.AudioBook;
import de.ur.mi.android.demos.service.AudioPlayerService;
import de.ur.mi.android.demos.utils.TimeFormatter;

public class AudioPlayerActivity extends AppCompatActivity implements AudioPlayerService.PlaybackListener {

    private AudioBook currentAudioBook;
    private AudioPlayerService audioPlayerService;
    private boolean isBound = false;

    private ImageButton btnPlay;
    private boolean isPlaying = false;
    private SeekBar seekBar;
    private TextView txtCurrentTime;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.AudioPlayerBinder binder = (AudioPlayerService.AudioPlayerBinder) service;
            audioPlayerService = binder.getService();
            audioPlayerService.registerPlaybackListener(AudioPlayerActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
    private TextView txtTotalDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchExtras();
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        isBound = false;
    }

    private void fetchExtras() {
        Intent intent = getIntent();
        currentAudioBook = (AudioBook) intent.getSerializableExtra(MainActivity.AUDIOBOOK_EXTRA_KEY);
    }

    private void initUI() {
        setContentView(R.layout.activity_player);
        TextView txtTitle = findViewById(R.id.txt_title);
        TextView txtDescription = findViewById(R.id.txt_description);
        txtTotalDuration = findViewById(R.id.txt_total_duration);
        txtCurrentTime = findViewById(R.id.txt_current_time);
        txtTitle.setText(currentAudioBook.getTitle());
        txtDescription.setText(currentAudioBook.getDescription());
        txtCurrentTime.setText(TimeFormatter.formatSecondsToDurationString(0));


        ImageView imageView = findViewById(R.id.img_wallpaper);
        Glide.with(this)
                .load(currentAudioBook.getWallpaperURLString())
                .centerCrop()
                .into(imageView);
        seekBar = findViewById(R.id.seekbar_progess);
        seekBar.setPadding(0, 0, 0, 0);
        seekBar.setMax(currentAudioBook.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioPlayerService.setAudioTimer(progress * 1000);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPlay = findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    stopAudio();
                } else {
                    playAudio();
                }
            }
        });
    }

    private void playAudio() {
        if (isBound) {
            audioPlayerService.playAudio();
        }
    }

    private void stopAudio() {
        if (isBound) {
            audioPlayerService.pauseAudio();
        }
    }

    private void initService() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(AudioPlayerService.AUDIOBOOK_KEY, currentAudioBook);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPlaybackReady(int milliseconds) {
        txtTotalDuration.setText(TimeFormatter.formatSecondsToDurationString(milliseconds / 1000));
        seekBar.setMax(milliseconds / 1000);
    }

    @Override
    public void onPlaybackStarted() {
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_pause));
        isPlaying = true;
    }

    @Override
    public void onPlaybackPaused() {
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play));
        isPlaying = false;
    }

    @Override
    public void onRemainingTimeUpdated(int milliseconds) {
        seekBar.setProgress(milliseconds / 1000);
        txtCurrentTime.setText(TimeFormatter.formatSecondsToDurationString(milliseconds / 1000));
    }

    @Override
    public void onPlaybackEnded() {
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play));
        isPlaying = false;
        seekBar.setProgress(0);
    }

    @Override
    public void onPlaybackError() {
        Toast.makeText(this, "Something went wrong with Audioplayback", Toast.LENGTH_SHORT).show();
    }
}
