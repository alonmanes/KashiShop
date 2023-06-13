package orielmoznino.example.alonmanes.fragments;

import static orielmoznino.example.alonmanes.MainActivity.IS_PLAYING;
import static orielmoznino.example.alonmanes.MainActivity.MUSIC_LIST;
import static orielmoznino.example.alonmanes.MainActivity.PLAY_INTENT;
import static orielmoznino.example.alonmanes.MainActivity.MUSIC_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import orielmoznino.example.alonmanes.Constants;
import orielmoznino.example.alonmanes.MusicListActivity;
import orielmoznino.example.alonmanes.MusicService;
import orielmoznino.example.alonmanes.R;


public class MainFragment extends Fragment {

    MenuItem batteryItem, musicItem, musicList;

    private void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                batteryItem.setTitle("" + level + "%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    public void exitAppAndLogout() {
        FirebaseAuth.getInstance().signOut(); // להתנתק מהחשבון firebase
        requireActivity().stopService(PLAY_INTENT); // להתנתק מהservice
        MusicService.stopPlayMusic(); // לעצור את המוסיקה הנוכחית
        MUSIC_SERVICE = null;

        requireActivity().finishAffinity(); // לסגור את כל המסכים:
        requireActivity().finishAndRemoveTask(); // למחוק את המסכים
        requireActivity().finish();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.IS_REMEMBER_SHARED_PREFERENCE,false);
        editor.putString(Constants.EMAIL_SHARED_PREFERENCE,"");
        editor.putString(Constants.PASSWORD_SHARED_PREFERENCE,"");
        editor.apply();
        requireActivity().finishAffinity();
        requireActivity().finishAndRemoveTask(); // למחוק את המסכים
        requireActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_exit:
                exitAppAndLogout();
                break;
            case R.id.action_music:
                if (MUSIC_SERVICE != null) {
                    if (IS_PLAYING) {
                        MUSIC_SERVICE.pause();
                        item.setIcon(R.drawable.baseline_music_off_24);
                    } else {
                        MUSIC_SERVICE.resume();
                        item.setIcon(R.drawable.baseline_music_note_24);
                    }
                    IS_PLAYING = !IS_PLAYING;
                }
                break;
            case R.id.music_list:
                getActivity().startActivity(MUSIC_LIST);


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        batteryItem = menu.findItem(R.id.action_battery);
        musicItem = menu.findItem(R.id.action_music);
        musicList = menu.findItem(R.id.music_list);
        MUSIC_LIST = new Intent(getActivity(), MusicListActivity.class);
        if (IS_PLAYING) musicItem.setIcon(R.drawable.baseline_music_note_24);
        else musicItem.setIcon(R.drawable.baseline_music_off_24);

        batteryLevel();
    }

}