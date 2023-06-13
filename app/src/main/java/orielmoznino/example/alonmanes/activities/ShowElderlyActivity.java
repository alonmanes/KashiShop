package orielmoznino.example.alonmanes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import orielmoznino.example.alonmanes.Constants;
import orielmoznino.example.alonmanes.R;
import orielmoznino.example.alonmanes.model.Elderly;

public class ShowElderlyActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton ibSMS, ibEmail;
    TextView tvName, tvPhone, tvEmail, tvCity;
    ImageView ivImage;

    Elderly getElderly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_elderly);

        initObjects();
        initWidgets();


    }

    public void initObjects() {
        Intent intent = getIntent();
        getElderly = new Elderly((HashMap<String, Object>)intent.getSerializableExtra(Constants.HASH_MAP_INTENT_FROM_ELDERLY_MAIN_TO_SHOW_ELDERLY));
    }

    public void initWidgets() {
        tvName = findViewById(R.id.tvName);
        tvName.setText(String.valueOf(getElderly.userName));
        tvPhone = findViewById(R.id.tvPhone);
        tvPhone.setText(String.valueOf(getElderly.phone));
        tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(String.valueOf(getElderly.email));
        tvCity = findViewById(R.id.tvCity);
        tvCity.setText(String.valueOf(getElderly.city));

        ibSMS = findViewById(R.id.ibSMS);
        ibSMS.setOnClickListener(this);

        ibEmail = findViewById(R.id.ibEmail);
        ibEmail.setOnClickListener(this);


        ivImage = findViewById(R.id.ivImage);
        Glide.with(this).load(getElderly.url).into(ivImage);

    }

    public void sendSMS()
    {
        // שולח sms לאיש קשר לגביי המשחק
        String message = "Hello My name is " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + ", I take content with you on KashiShop, what can I help with?";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + getElderly.phone));
        intent.putExtra("sms_body", message);
        finish();
        startActivity(intent);
    }

    public void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{getElderly.email});
        email.putExtra(Intent.EXTRA_SUBJECT, "KashiShop email from youth :)");
        String message = "Hello My name is " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + ", I take content with you on KashiShop, what can I help with?";
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ibSMS:
                sendSMS();
                break;
            case R.id.ibEmail:
                sendEmail();
                break;
        }
    }
}