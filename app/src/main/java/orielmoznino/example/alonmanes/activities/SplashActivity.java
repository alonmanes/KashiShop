package orielmoznino.example.alonmanes.activities;

import static orielmoznino.example.alonmanes.MainActivity.PLAY_INTENT;
import static orielmoznino.example.alonmanes.MainActivity.MUSIC_SERVICE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import orielmoznino.example.alonmanes.MusicService;
import orielmoznino.example.alonmanes.R;

public class SplashActivity extends AppCompatActivity {

    Animation animFadeIn;
    ImageView logo;

    private final ServiceConnection musicConnection = new ServiceConnection() {
        //להתחבר למוזיקה
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            MUSIC_SERVICE = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.logoApp);
        animFadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        logo.startAnimation(animFadeIn);
        countDownTimer.start();

        connectMusic();
    }

    public void connectMusic() {
        if (PLAY_INTENT == null) {
            MUSIC_SERVICE = new MusicService();
            PLAY_INTENT = new Intent(this, MusicService.class);
            bindService(PLAY_INTENT, musicConnection,
                    Context.BIND_AUTO_CREATE);
            startService(PLAY_INTENT);
        }
    }

    CountDownTimer countDownTimer = new CountDownTimer(5000, 2000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            startActivity(intent);
        }
    };
}
