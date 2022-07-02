package de.ur.mi.android.demos;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
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

public class AudioPlayerActivity extends Activity implements AudioPlayerService.AudioPlayerServiceListener {

    /**
     * Verweis auf den gebundenen Service, über den die Wiedergabe gesteuert wird.
     */
    private AudioPlayerService audioPlayerService;
    private boolean isBound = false;

    /**
     * Instanzvariablen zum Zugriff auf das AudioBookManager-Singleton und das aktuelle AudioBook.
     */
    private AudioBookManager manager;
    private AudioBook currentAudioBook;
    private boolean isPlaying = false;

    /**
     * Instanzvariablen für UI-Elemente
     */
    private ImageButton btnPlay,
            btnPrevious,
            btnNext;
    private ImageView imgWallpaper;
    private SeekBar seekBar;
    private TextView txtTitle,
            txtAuthor,
            txtCurrentTime,
            txtTotalDuration;


    /**
     * Diese Instanzvariable ist eine anonyme Klasse die vom ServiceConnection-Interface erbt.
     * Die hier definierten Callback-Methoden werden aufgerufen, wenn der Service erfolgreich gebunden bzw. wieder getrennt wurde.
     * Durch den IBinder kann über die getService-Methode auf die gebundene Instanz von AudioPlayerService zugegriffen werden
     * und zur Steuerung der Wiedergabe in einer Instanzvariable abgelegt werden.
     */
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
        if (!isBound) {
            initService();
        }
    }

    /**
     * In dieser Methode wird das Binden des Service angestoßen. Sobald der Vorgang erfolgreich durchgeführt wurde,
     * wird die onServiceConnected-Methode der ServiceConnection aufgerufen.
     * Das abzuspielende AudioBook kann über einen Intent an den Service gegeben werden.
     */
    private void initService() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(AudioPlayerService.AUDIOBOOK_KEY, currentAudioBook);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Der Service muss bei Beenden der App entbunden werden.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAudioBook(manager.getPrevious(manager.getAudioBookForId(currentAudioBook.getId())));
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAudioBook(manager.getNext(manager.getAudioBookForId(currentAudioBook.getId())));
            }
        });

        /*
            Der OnSeekBarChangeListener ermöglicht die Steuerung der Wiedergabeposition durch anfassen der SeekBar.
         */
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

    /**
     * In dieser Methode werden die Meta-Daten eines übergebenen AudioBooks in die UI-Elemente eingetragen,
     * sowie mit Hilfe des AudioPlayerService das abzuspielende AudioBook aktualisiert.
     * Da dieses zunächst vorbereitet werden muss, wird eine Ladeanimation gestartet, bis die PlaybackReady-Methode aufgerufen wurde.
     *
     * @param audioBook: Das abzuspielende AudioBook
     */
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

        if (audioPlayerService != null) {
            audioPlayerService.changeTitle(audioBook);
            startLoadingAnimation();
        }
    }

    private void startLoadingAnimation() {
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        rotate.setRepeatMode(Animation.INFINITE);
        btnPlay.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_loading));
        btnPlay.startAnimation(rotate);
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
