package de.ur.mi.android.demos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;

import de.ur.mi.android.demos.data.audiobook.AudioBook;
import de.ur.mi.android.demos.data.AudioBookManager;
import de.ur.mi.android.demos.service.AudioPlayerService;
import de.ur.mi.android.demos.utils.TimeFormatter;

public class AudioPlayerActivity extends AppCompatActivity implements AudioPlayerService.AudioPlayerServiceListener {

    private AudioPlayerService audioPlayerService;
    private AudioBook currentAudioBook;
    private AudioBookManager manager;
    private boolean isBound = false;
    private boolean isPlaying = false;

    private ImageButton btnPlay,
            btnPrevious,
            btnNext;

    private ImageView imgWallpaper;
    private SeekBar seekBar;
    private TextView txtTitle,
            txtAuthor,
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
        initManager();
        initUI();
        setEventListeners();
        setAudioBook((AudioBook) getIntent().getSerializableExtra(MainActivity.AUDIOBOOK_EXTRA_KEY));
    }

    private void initManager() {
        manager = AudioBookManager.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
    }

    private void initService() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(AudioPlayerService.AUDIOBOOK_KEY, currentAudioBook);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
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
        txtAuthor = findViewById(R.id.txt_author);
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
            Log.d("player_here", "TEST");
            setAudioBook(manager.getPrevious(manager.getAudioBookForId(currentAudioBook.getId())));
        });
        btnNext.setOnClickListener(v -> {
            // TODO: Switch to Next Audiobook
            Log.d("player_there", "TEST");
            setAudioBook(manager.getNext(manager.getAudioBookForId(currentAudioBook.getId())));
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
        if (audioBook == null) return;
        currentAudioBook = audioBook;
        txtTitle.setText(audioBook.getTitle());
        txtAuthor.setText(audioBook.getAuthor());
        txtCurrentTime.setText(TimeFormatter.formatSecondsToDurationString(0));
        txtTotalDuration.setText(TimeFormatter.formatSecondsToDurationString(audioBook.getDuration()));
        seekBar.setMax(audioBook.getDuration());

        Glide.with(this)
                .load(audioBook.getWallpaperURLString())
                .centerCrop()
                .into(imgWallpaper);

        seekBar.setMax(audioBook.getDuration());

        if (audioPlayerService != null) {
            audioPlayerService.changeTitle(audioBook);
        }

        startLoadingAnimation();
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

    @Override
    public void onPlaybackReady() {
        stopLoadingAnimation();
    }

    private void startLoadingAnimation() {
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        rotate.setRepeatMode(Animation.INFINITE);
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_loading));
        btnPlay.startAnimation(rotate);
    }

    private void stopLoadingAnimation() {
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_play));
        btnPlay.clearAnimation();
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
