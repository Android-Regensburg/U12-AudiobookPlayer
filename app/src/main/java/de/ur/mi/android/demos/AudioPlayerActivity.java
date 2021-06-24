package de.ur.mi.android.demos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

    private AudioPlayerService audioPlayerService;
    private AudioBook currentAudioBook;
    private boolean isBound = false;
    private boolean isPlaying = false;

    private ImageButton btnPlay,
            btnPrevious,
            btnNext;

    private ImageView imgWallpaper;
    private SeekBar seekBar;
    private TextView txtTitle,
            txtDescription,
            txtCurrentTime,
            txtTotalDuration;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        setEventListeners();
        setAudioBook((AudioBook) getIntent().getSerializableExtra(MainActivity.AUDIOBOOK_EXTRA_KEY));
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

    private void initUI() {
        setContentView(R.layout.activity_player);
        txtTitle = findViewById(R.id.txt_title);
        txtDescription = findViewById(R.id.txt_description);
        txtTotalDuration = findViewById(R.id.txt_total_duration);
        txtCurrentTime = findViewById(R.id.txt_current_time);
        imgWallpaper = findViewById(R.id.img_wallpaper);
        btnPlay = findViewById(R.id.btn_play);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        seekBar = findViewById(R.id.seekbar_progess);
        seekBar.setPadding(0, 0, 0, 0);
    }

    private void setEventListeners() {
        btnPlay.setOnClickListener(v -> {
            if (isPlaying) {
                stopAudio();
            } else {
                playAudio();
            }
        });
        btnPrevious.setOnClickListener(v -> {
            // TODO: Switch to Previous AudioBook
        });
        btnNext.setOnClickListener(v -> {
            // TODO: Switch to Next Audiobook
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioPlayerService.seekAudio(progress * 1000);
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
    }

    private void setAudioBook(AudioBook audioBook) {
        currentAudioBook = audioBook;
        txtTitle.setText(audioBook.getTitle());
        txtDescription.setText(audioBook.getDescription());
        txtCurrentTime.setText(TimeFormatter.formatSecondsToDurationString(0));

        Glide.with(this)
                .load(audioBook.getWallpaperURLString())
                .centerCrop()
                .into(imgWallpaper);

        seekBar.setMax(audioBook.getDuration());
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
        int durationInSeconds = milliseconds / 1000;
        txtTotalDuration.setText(TimeFormatter.formatSecondsToDurationString(durationInSeconds));
        seekBar.setMax(durationInSeconds);
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
        int durationInSeconds = milliseconds / 1000;
        seekBar.setProgress(durationInSeconds);
        txtCurrentTime.setText(TimeFormatter.formatSecondsToDurationString(durationInSeconds));
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
