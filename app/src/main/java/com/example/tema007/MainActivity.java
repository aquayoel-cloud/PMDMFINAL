package com.example.tema007;


import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private Button btnPlay, btnPausa;
    private SeekBar seekBar;
    private TextView tvActual, tvTotal;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private SoundPool soundPool;
    private final int[] sonidos = new int[4];
    private int cargados = 0;

    private final Runnable actualizar = new Runnable() {
        @Override
        public void run() {
            long pos = player.getCurrentPosition();
            seekBar.setProgress((int) pos);
            tvActual.setText(formatear(pos));
            handler.postDelayed(this, 500);
        }
    };

    private String formatear(long ms) {
        long totalSeg = ms / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d", totalSeg / 60, totalSeg % 60);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnPlay = findViewById(R.id.btnPlay);
        btnPausa = findViewById(R.id.btnPausa);
        seekBar = findViewById(R.id.seekBar);
        tvActual = findViewById(R.id.tvActual);
        tvTotal = findViewById(R.id.tvTotal);

        player = new ExoPlayer.Builder(this).build();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cancion);
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();

        btnPlay.setOnClickListener(v -> {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
            }
            player.play();
        });

        btnPausa.setOnClickListener(v -> player.pause());


        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    long dur = player.getDuration();
                    seekBar.setMax((int) dur);
                    tvTotal.setText(formatear(dur));
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser) tvActual.setText(formatear(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) { }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                player.seekTo(sb.getProgress());
            }
        });

        Button btnEf1 = findViewById(R.id.btnEf1);
        Button btnEf2 = findViewById(R.id.btnEf2);
        Button btnEf3 = findViewById(R.id.btnEf3);
        Button btnEf4 = findViewById(R.id.btnEf4);
        Button[] botones = { btnEf1, btnEf2, btnEf3, btnEf4 };

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attrs)
                .build();


        soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
            if (status == 0) {
                cargados++;
                if (cargados == 4) {
                    for (Button b : botones) b.setEnabled(true);
                }
            }
        });

        sonidos[0] = soundPool.load(this, R.raw.aplauso, 1);
        sonidos[1] = soundPool.load(this, R.raw.campana, 1);
        sonidos[2] = soundPool.load(this, R.raw.laser, 1);
        sonidos[3] = soundPool.load(this, R.raw.moneda, 1);

        for (int i = 0; i < 4; i++) {
            final int id = sonidos[i];
            botones[i].setOnClickListener(v -> soundPool.play(id, 1f, 1f, 1, 0, 1f));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.removeCallbacks(actualizar);
        handler.post(actualizar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.pause();
        handler.removeCallbacks(actualizar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        soundPool.release();
    }
}