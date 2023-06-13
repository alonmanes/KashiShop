package orielmoznino.example.alonmanes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import orielmoznino.example.alonmanes.Constants;
import orielmoznino.example.alonmanes.ElderlyAdapter;
import orielmoznino.example.alonmanes.MainActivity;
import orielmoznino.example.alonmanes.R;
import orielmoznino.example.alonmanes.model.Elderly;

public class YouthMainActivity extends MainActivity {

    RecyclerView rvElderly;
    ElderlyAdapter elderlyAdapter;
    ArrayList<Elderly> elderlyArrayList;

    EditText etSearch;
    Spinner spSearch;

    String wordOfSearch;
    String chosenFilter;

    ArrayList<String> spinnerArrayTopic = new ArrayList<>();

    final int NAME_SPINNER_INDEX = 0;
    final int CITY_SPINNER_INDEX = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth_main);
        initObjects();
        initWidgets();
        showElderly();
        setList();
    }

    public void initObjects() {
        spinnerArrayTopic.add("Name");
        spinnerArrayTopic.add("City");
        wordOfSearch = "";
        chosenFilter = spinnerArrayTopic.get(0);
    }

    public void initWidgets() {
        rvElderly = findViewById(R.id.rvElderly);
        rvElderly.setHasFixedSize(false);
        rvElderly.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordOfSearch = s.toString();
                setList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spSearch = findViewById(R.id.spSearch);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        spinnerArrayTopic);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spSearch.setAdapter(spinnerArrayAdapter);

        spSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenFilter = parent.getSelectedItem().toString();
                setList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showElderly() {
        elderlyArrayList = new ArrayList<>();
        elderlyAdapter = new ElderlyAdapter(this,this,elderlyArrayList);
        rvElderly.setAdapter(elderlyAdapter);
    }

    public void setList() {
        FirebaseDatabase.getInstance().getReference(Constants.ELDERLY_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                elderlyArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Elderly temp = new Elderly((HashMap<String, Object>) dataSnapshot.getValue());
                    if(!wordOfSearch.isEmpty()) {
                        if(chosenFilter.equals(spinnerArrayTopic.get(NAME_SPINNER_INDEX)))
                            if(temp.userName.contains(wordOfSearch))
                                elderlyArrayList.add(temp);

                        if(chosenFilter.equals(spinnerArrayTopic.get(CITY_SPINNER_INDEX)))
                            if(temp.city.contains(wordOfSearch))
                                elderlyArrayList.add(temp);
                    }
                    else elderlyArrayList.add(temp);
                }
                elderlyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}