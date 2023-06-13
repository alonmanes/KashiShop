package orielmoznino.example.alonmanes.model;

import static orielmoznino.example.alonmanes.MainActivity.CURRENT_USER;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import orielmoznino.example.alonmanes.Constants;
import orielmoznino.example.alonmanes.MainActivity;
import orielmoznino.example.alonmanes.activities.ElderlyMainActivity;
import orielmoznino.example.alonmanes.activities.YouthMainActivity;

public class AuthHelper {

    Activity activity;

    public AuthHelper(Activity activity) {
        this.activity = activity;
    }

    private boolean isThereEmptyData(String email, String password) {
        return email.trim().isEmpty() || password.trim().isEmpty();
    }

    private boolean isThereEmptyData(String email, String password, String phone, String userName, String city) {
        return email.trim().isEmpty() || password.trim().isEmpty() || phone.trim().isEmpty() || userName.trim().isEmpty() ||
                city.trim().isEmpty();
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = activity.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    public void updateUserData(ProgressDialog progressDialog, Youth youth, Uri uri) {

        final StorageReference fileRef = FirebaseStorage.getInstance().getReference(Constants.YOUTH_REFERENCE).child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(url -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.YOUTH_REFERENCE);
            youth.url = url.toString();
            ref.child(youth.uid).setValue(youth.toMap());
            CURRENT_USER = youth;
            progressDialog.dismiss();
            activity.startActivity(new Intent(activity, YouthMainActivity.class));
            activity.finish();
        })).addOnProgressListener(snapshot -> {
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(activity, "The upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    public void updateUserData(ProgressDialog progressDialog, Elderly elderly, Uri uri) { // עדכון התמונה בREALTIME DATABASE ואת התמונה בFIREBASE STORAGE
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference(Constants.ELDERLY_REFERENCE).child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(url -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.ELDERLY_REFERENCE);
            elderly.url = url.toString();
            ref.child(elderly.uid).setValue(elderly.toMap());
            CURRENT_USER = elderly;
            progressDialog.dismiss();
            activity.startActivity(new Intent(activity,ElderlyMainActivity.class));
            activity.finish();
        })).addOnProgressListener(snapshot -> {
        }).addOnFailureListener(e -> {
            Toast.makeText(activity, "The upload failed", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }


    public void login(String email, String password, boolean isRemember) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (!isThereEmptyData(email.trim(), password.trim()))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference((Constants.ELDERLY_REFERENCE));
                        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean(Constants.IS_REMEMBER_SHARED_PREFERENCE,isRemember);
                                    if (isRemember) {
                                        editor.putString(Constants.EMAIL_SHARED_PREFERENCE,email.trim());
                                        editor.putString(Constants.PASSWORD_SHARED_PREFERENCE,password.trim());
                                    }
                                    editor.apply();

                                    if(task.getResult().getValue()!=null) {
                                        CURRENT_USER = new Elderly((HashMap<String, Object>) task.getResult().getValue());
                                        progressDialog.dismiss();
                                        activity.startActivity(new Intent(activity, ElderlyMainActivity.class));
                                        activity.finish();
                                    } else {
                                        // know as youth
                                        DatabaseReference refYouth = FirebaseDatabase.getInstance().getReference((Constants.YOUTH_REFERENCE));
                                        refYouth.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    CURRENT_USER = new Youth((HashMap<String, Object>) task.getResult().getValue());

                                                    progressDialog.dismiss();
                                                    activity.startActivity(new Intent(activity,YouthMainActivity.class));
                                                    activity.finish();
                                                } else {
                                                    Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        else {
            Toast.makeText(activity, "Enter all Data!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    public void registerAsYouth(String email, String password, String userName, Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(uri==null) {
            Toast.makeText(activity, "Add your image!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if (!isThereEmptyData(email.trim(), password.trim()))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) { // אם הוא מצליח ליצור משתמש חדש אז מתקדם לשמירת נתונים בREALTIMEDATABASE
                    updateUserData(progressDialog, new Youth(FirebaseAuth.getInstance().getCurrentUser().getUid(), email.trim(), userName.trim(), ""),uri);
                } else {
                    Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss(); // אם לא מצליח להכנס אז לא משתנה כלום וניתנת הודעת שגיאה למשתמש שהפעולה לא עבדה
                }
            });
        else {
            Toast.makeText(activity, "Enter all Data!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    public void registerAsElderly(String email, String password, String userName, String phone , String city, Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(uri==null) {
            Toast.makeText(activity, "Add your image!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if (!isThereEmptyData(email.trim(), password.trim(), phone.trim(), userName.trim(), city.trim())) { // מעיר האם המבוגר הזין את כל פרטי המידע
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        updateUserData(progressDialog, new Elderly(FirebaseAuth.getInstance().getCurrentUser().getUid(), email.trim(), userName.trim(), "", phone.trim(), city.trim()), uri);
                    }
                    else {
                        Toast.makeText(activity, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(activity, "Enter all Data!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}
