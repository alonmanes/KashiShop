package orielmoznino.example.alonmanes.activities;

import static orielmoznino.example.alonmanes.MainActivity.CURRENT_USER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import orielmoznino.example.alonmanes.Constants;
import orielmoznino.example.alonmanes.MainActivity;
import orielmoznino.example.alonmanes.R;
import orielmoznino.example.alonmanes.model.Elderly;

public class ElderlyMainActivity extends MainActivity implements View.OnClickListener {


    ImageView ivImage;
    Button btnEditImage, btnSave;
    EditText etEmail, etUserName, etPhone, etCity;

    Uri mImageUri;

    private final int CAMERA_REQUEST = 0;
    private final int MY_CAMERA_PERMISSION_CODE = 1;
    private final int GALLERY_REQUEST = 2;
    private final int MY_GALLERY_PERMISSION_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_main);

        initWidgets();
    }

    public void editImage() {
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

        // לאחר הלקיחת תמונה בודק האם התמונה ריקה או לא ואם לא מעדכן את הURI ומעדכן את התמונה המוצגת במסך
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(ivImage);
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // המצלמה מחזירה תמונת BITMAP אותוה נמיר לURI כדי להעלות אותה לFIREBASE
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mImageUri = convertBitmapToUri(photo);
            Glide.with(this).load(mImageUri).into(ivImage); // ספרייה המביאה תמונה מהפיירבייס ומציגה אותה על IMAGE VIEW
        }
    }

    public Uri convertBitmapToUri(Bitmap inImage) {
        // ממיר BITMAP לURI
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //בקשת הרשאות
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


    public void initWidgets() {
        Elderly elderlyTemp = (Elderly) CURRENT_USER;

        etEmail = findViewById(R.id.etEmail);
        etEmail.setText(String.valueOf(elderlyTemp.email));

        etUserName = findViewById(R.id.etUserName);
        etUserName.setText(String.valueOf(elderlyTemp.userName));

        etPhone = findViewById(R.id.etPhone);
        etPhone.setText(String.valueOf(elderlyTemp.phone));

        etCity = findViewById(R.id.etCity);
        etCity.setText(String.valueOf(elderlyTemp.city));


        ivImage = findViewById(R.id.ivImage);
        Glide.with(this).load(CURRENT_USER.url).into(ivImage);

        btnEditImage = findViewById(R.id.btnEditImage);
        btnEditImage.setOnClickListener(this);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

    }


    private String getFileExtension(Uri mUri) { // ליצור לתמונה כתובת בלעדי
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    public void editWithImage() {
        // לאחר סיום מחיקת התמונה הישנה מעלה את התמונה החדשה ועורך את שאר המידע
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        deleteImage(progressDialog).addOnSuccessListener(unused -> {
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference(Constants.ELDERLY_REFERENCE).child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            fileRef.putFile(mImageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(url -> {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.ELDERLY_REFERENCE);
                Elderly elderlyTemp = (Elderly) CURRENT_USER;
                elderlyTemp.phone = etPhone.getText().toString().trim();
                elderlyTemp.userName = etUserName.getText().toString().trim();
                elderlyTemp.city = etCity.getText().toString().trim();
                elderlyTemp.url = url.toString();
                CURRENT_USER = elderlyTemp;
                ref.child(CURRENT_USER.uid).setValue(CURRENT_USER.toMap()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ElderlyMainActivity.this, "The data has been saved :)", Toast.LENGTH_SHORT).show();
                        mImageUri = null;
                    } else
                        Toast.makeText(ElderlyMainActivity.this, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });

            })).addOnProgressListener(snapshot -> {
            }).addOnFailureListener(e -> {
                Toast.makeText(ElderlyMainActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        });
    }

    public Task<Void> deleteImage(ProgressDialog progressDialog) {
        //מוחק את התמונה הישנה ומחזיר לנו משימה
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(CURRENT_USER.url);
        return storageReference.delete().addOnSuccessListener(aVoid -> {

        }).addOnFailureListener(exception -> {
            progressDialog.dismiss();
            Toast.makeText(ElderlyMainActivity.this, "" + exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void editWithOutImage() {
        if (!isThereEmptyData(etPhone.getText().toString().trim(), etUserName.getText().toString().trim(), etCity.getText().toString().trim())) {
            Elderly elderlyTemp = (Elderly) CURRENT_USER;
            elderlyTemp.phone = etPhone.getText().toString().trim();
            elderlyTemp.userName = etUserName.getText().toString().trim();
            elderlyTemp.city = etCity.getText().toString().trim();

            CURRENT_USER = elderlyTemp;

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Saving...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseDatabase.getInstance().getReference(Constants.ELDERLY_REFERENCE).child(CURRENT_USER.uid).setValue(CURRENT_USER.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(ElderlyMainActivity.this, "The data has been saved :)", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ElderlyMainActivity.this, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else Toast.makeText(this, "Enter all data!", Toast.LENGTH_SHORT).show();
    }

    public boolean isThereEmptyData(String phone, String name, String city) {
        return phone.trim().isEmpty() || name.trim().isEmpty() || city.trim().isEmpty();
    }
    private boolean isThereEmptyData() {
        return etPhone.getText().toString().trim().isEmpty() ||
                etUserName.getText().toString().trim().isEmpty() ||
                etCity.getText().toString().trim().isEmpty();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
            if(!isThereEmptyData()){
                if (mImageUri == null)
                    editWithOutImage();
                else
                    editWithImage();
            } else Toast.makeText(this, "Enter all Data", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnEditImage:
                editImage();
                break;
        }
    }
}