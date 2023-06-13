package orielmoznino.example.alonmanes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;

import orielmoznino.example.alonmanes.Constants;
import orielmoznino.example.alonmanes.model.AuthCase;
import orielmoznino.example.alonmanes.model.AuthHelper;
import orielmoznino.example.alonmanes.R;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    EditText etEmail, etPassword, etPhone, etUserName, etCity;
    TextInputLayout etPhoneContainer, etUserNameContainer, etCityContainer;
    AuthCase authCase;
    Button btnLogin, btnAddImage;
    TextView tvSwitchCase;
    RadioGroup rgUserType;
    ImageView ivImage;
    AuthHelper authHelper;

    Uri mImageUri;


    private final int CAMERA_REQUEST = 0;
    private final int MY_CAMERA_PERMISSION_CODE = 1;
    private final int GALLERY_REQUEST = 2;
    private final int MY_GALLERY_PERMISSION_CODE = 3;
    CheckBox cbRemember;

    boolean isRemember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initWidgets();

        switchCase(AuthCase.LOGIN);
        authHelper = new AuthHelper(this);
        mImageUri = null;

       isRememberLogin();
    }

    public void isRememberLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean(Constants.IS_REMEMBER_SHARED_PREFERENCE, false);
        if(isRemember) {
            String email = preferences.getString(Constants.EMAIL_SHARED_PREFERENCE, "");
            String password = preferences.getString(Constants.PASSWORD_SHARED_PREFERENCE, "");
            if(!email.isEmpty() && !password.isEmpty()) {
                authHelper.login(email,password,true);
            }
        }
    }

    public void initWidgets() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etUserName = findViewById(R.id.etUserName);
        etPhoneContainer = findViewById(R.id.etPhoneContainer);
        etUserNameContainer = findViewById(R.id.etUserNameContainer);
        etCity = findViewById(R.id.etCity);
        etCityContainer = findViewById(R.id.etCityContainer);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        tvSwitchCase = findViewById(R.id.tvSwitchCase);
        tvSwitchCase.setOnClickListener(this);

        ivImage = findViewById(R.id.ivImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(this);


        rgUserType = findViewById(R.id.rgUserType);
        rgUserType.setOnCheckedChangeListener(this);

        cbRemember = findViewById(R.id.cbRemember);
        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemember = isChecked;
            }
        });
    }

    public void switchCase(AuthCase newCase) {
        authCase = newCase;
        switch (authCase) {
            case LOGIN:
                btnLogin.setText("LOGIN");
                tvSwitchCase.setText("Haven't user? REGISTER!");
                etPhoneContainer.setVisibility(View.GONE);
                etUserNameContainer.setVisibility(View.GONE);
                etCityContainer.setVisibility(View.GONE);
                rgUserType.setVisibility(View.GONE);
                btnAddImage.setVisibility(View.GONE);
                ivImage.setVisibility(View.GONE);
                break;
            case YOUTH_REGISTER:
                btnLogin.setText("REGISTER");
                tvSwitchCase.setText("Have user? LOGIN!");
                etPhoneContainer.setVisibility(View.GONE);
                etCityContainer.setVisibility(View.GONE);
                etUserNameContainer.setVisibility(View.VISIBLE);
                rgUserType.setVisibility(View.VISIBLE);
                btnAddImage.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.VISIBLE);
                break;
            case ELDERLY_REGISTER:
                btnLogin.setText("REGISTER");
                tvSwitchCase.setText("Have user? LOGIN!");
                etPhoneContainer.setVisibility(View.VISIBLE);
                etCityContainer.setVisibility(View.VISIBLE);
                etUserNameContainer.setVisibility(View.VISIBLE);
                rgUserType.setVisibility(View.VISIBLE);
                btnAddImage.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void startAuth() {
        switch (authCase) {
            case LOGIN:
                authHelper.login(etEmail.getText().toString().trim(),etPassword.getText().toString().trim(),isRemember);
                break;
            case YOUTH_REGISTER:
                authHelper.registerAsYouth(etEmail.getText().toString().trim(),etPassword.getText().toString().trim(),etUserName.getText().toString().trim(),mImageUri);
                break;
            case ELDERLY_REGISTER:
                authHelper.registerAsElderly(etEmail.getText().toString().trim(),etPassword.getText().toString().trim(),etUserName.getText().toString().trim(),etPhone.getText().toString().trim(),etCity.getText().toString().trim(),mImageUri);
                break;
        }
    }

    public void addImage() {
        // פותח דיאלוג לקחת תמונה מהטלפון
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.add_image_dialog);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.setCancelable(true);

        d.findViewById(R.id.lGallery).setOnClickListener(v -> {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_GALLERY_PERMISSION_CODE);
                d.dismiss();
            } else {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                d.dismiss();
            }

        });

        d.findViewById(R.id.lCamera).setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                d.dismiss();
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                d.dismiss();
            }
        });

        d.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            ivImage.setImageURI(mImageUri);
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data !=null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mImageUri = convertBitmapToUri(photo);
            ivImage.setImageURI(mImageUri);
        }
    }

    public Uri convertBitmapToUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MY_GALLERY_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "gallery permission granted", Toast.LENGTH_LONG).show();
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            } else {
                Toast.makeText(this, "gallery permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                startAuth();
                break;
            case R.id.tvSwitchCase:
                if(authCase == AuthCase.LOGIN)
                    switchCase(AuthCase.YOUTH_REGISTER);
                else
                    switchCase(AuthCase.LOGIN);
                break;
            case R.id.btnAddImage:
                addImage();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbYouth:
                switchCase(AuthCase.YOUTH_REGISTER);
                break;
            case R.id.rbElderly:
                switchCase(AuthCase.ELDERLY_REGISTER);
                break;
        }
    }
}