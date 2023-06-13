package orielmoznino.example.alonmanes;

import static orielmoznino.example.alonmanes.MainActivity.IS_PLAYING;
import static orielmoznino.example.alonmanes.MainActivity.MUSIC_SERVICE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {

    ListView lvMusic;
    ArrayList<String> listSongs;
    final int READ_EXTERNAL_STORAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        // אם אין הרשאה, לבקש, אם יש, להציג שירים מהטלפון
        if (ContextCompat.checkSelfPermission(MusicListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
        } else {
            showSongs();
        }
    }



    public void showSongs() {
        listSongs = new ArrayList<>();
        for(int i=0; i<MUSIC_SERVICE.getValuesList().size(); i++) {
            listSongs.add(MUSIC_SERVICE.getValuesList().get(i).getName());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listSongs);
        lvMusic = findViewById(R.id.lvMusic);
        lvMusic.setAdapter(adapter);
        // מכניס את השירים לרשימה הנגללת
        lvMusic.setOnItemClickListener((adapterView, view, i, l) -> {
            // change playing song to the chosen song
            if(IS_PLAYING) {
                MUSIC_SERVICE.setSong(i);
                MUSIC_SERVICE.playSong();
            } else Toast.makeText(MusicListActivity.this, "Turn on music!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            showSongs();
        } else Toast.makeText(this, "permission denied change it on settings", Toast.LENGTH_SHORT).show();
    }
}