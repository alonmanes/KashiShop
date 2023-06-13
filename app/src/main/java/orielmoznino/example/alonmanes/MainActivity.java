package orielmoznino.example.alonmanes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import orielmoznino.example.alonmanes.model.Youth;

public class MainActivity extends AppCompatActivity {

    public static Youth CURRENT_USER;

    public static MusicService MUSIC_SERVICE; // מפעיל ומפסיק את המוסיקה ומכיל מידע נוסף בקשר למוסיקה
    public static Intent PLAY_INTENT; // גישה ל MusicService
    public static Intent MUSIC_LIST;
    public static boolean IS_PLAYING = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    @Override
    public void onDestroy() {
        //סוגר את המוסיקה
        Log.e("OUT_MAIN", "get out from main");
        stopService(PLAY_INTENT);
        MUSIC_SERVICE = null;
        super.onDestroy();
        System.exit(0); // לצאת מהאפלקציה סופית
    }
}