package orielmoznino.example.alonmanes;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    private static MediaPlayer player; //media player דרכו אפשר להפעעיל את המוזיקה בטלפון
    private ArrayList<Song> valuesList; //songs List רשימת השירים
    private int songPosition;  //current position // באיזה שיר ברשימה משתמש בה נמצא בה
    private final IBinder musicBind = new MusicBinder(); // כורך את הservice להבדיל בינו לבין סרביסים אחרים

    @Override
    public void onCreate() {
        super.onCreate();

        songPosition = 0;
        player = new MediaPlayer();
        valuesList = new ArrayList<>();

        //default music
        player = MediaPlayer.create(this, R.raw.piano);
        player.setLooping(true);
        player.start();

        initMusicPlayer();

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

        getSongs();

    }

    public void getSongs() {
        //להכניס את השירים שיש בטלפון לרשימה
        ContentResolver cr = getContentResolver();       //--allows access to the the phone
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;      //--songUri is the address to the music files in the phone
        Cursor songs = cr.query(songUri, null, null, null, null);

        // עובר על כל קבצי המוסיקה
        if (songs != null && songs.moveToFirst()) {
            int songTitle = songs.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songID = songs.getColumnIndex(MediaStore.Audio.Media._ID);

            Song song;

            while (songs.moveToNext()) {
                long longSongID = songs.getLong(songID);
                String currentTitle = songs.getString(songTitle);
                song = new Song(longSongID, currentTitle);
                valuesList.add(song);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return musicBind; }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public static void stopPlayMusic() { // עוצר את המוזיקה
        player.stop();
        player.release();
    }

    public void initMusicPlayer() {
        // set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC); // לאתחל מוסיקה שתהיה קבועה
    }

    public void playSong() {
        // לנגן שיר מהרשימה
        if (player != null) //אם נוצר כבר
            player.reset();

        Song songToPlay = valuesList.get(songPosition);
        long songId = songToPlay.getId();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songId);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();

        player.setOnPreparedListener((new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }));
    }

    public ArrayList<Song> getValuesList() {return this.valuesList;}

    public class MusicBinder extends Binder implements IBinder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void setSong(int songIndex) {
        songPosition = songIndex;
    }

    public void pause() {
        if (player != null)
            player.pause();
    }

    public void resume() {
        // אם המוסיקה קיימת שיצרו אותה כבר אז להמשיך את המוזיקה
        if (player != null)
            player.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "Error during music service", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}