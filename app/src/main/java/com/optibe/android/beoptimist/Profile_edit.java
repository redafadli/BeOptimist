package com.optibe.android.beoptimist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Profile_edit extends AppCompatActivity {

    private Button save_button;
    private FirebaseAuth mAuth;
    private EditText set_up_name;
    private ImageView setup_image, back;
    private static final int INT_CONST = 023;
    private static final int REQ = 22;
    private Uri url;
    private String name;
    private Bitmap bitmap;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile_edit);
        super.onCreate(savedInstanceState);
        back = findViewById(R.id.back_btn);
        mAuth = FirebaseAuth.getInstance();
        set_up_name = findViewById(R.id.set_up_name);
        save_button = findViewById(R.id.save_button);
        setup_image = findViewById(R.id.setup_image);

        firebaseFirestore = FirebaseFirestore.getInstance();
        loadUserInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Profile_edit.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Profile_edit.this, "Denied", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(Profile_edit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 33);
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = set_up_name.getText().toString();
                if (name.isEmpty()) {
                    set_up_name.setError("Name Required");
                    set_up_name.requestFocus();
                } else {
                    saveUserInfo();
                    finish();
                }
            }
        });

        setup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Profile_edit.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Choose Image"), INT_CONST);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadUserInfo() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(Profile_edit.this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(setup_image);
            }
            if (user.getDisplayName() != null) {
                String displayname = user.getDisplayName();
                set_up_name.setText(displayname);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INT_CONST && resultCode == RESULT_OK && data.getData() != null) {
            url = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setup_image.setImageBitmap(bitmap);
            uploadFile(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(Profile_edit.this, Home.class);
            startActivity(intent);
        }
    }

    @SuppressLint("NewApi")
    private void saveUserInfo() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user_id = FirebaseAuth.getInstance().getUid();
        if (user != null && url != null) {

            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(url).build();
            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Map<String, String> u = new HashMap<>();
                        u.put("name", name);
                        u.put("image", url.toString());
                        firebaseFirestore.collection("users").document(user_id).set(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(Profile_edit.this, "Success", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(Profile_edit.this, "Error", Toast.LENGTH_SHORT).show();
//                                }
                            }
                        });

                        Toast.makeText(Profile_edit.this, "Profile Updated", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (user != null) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Map<String, String> u = new HashMap<>();
                        u.put("name", name);
                        firebaseFirestore.collection("users").document(user_id).set(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(Profile_edit.this, "Success", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(Profile_edit.this, "Error", Toast.LENGTH_SHORT).show();
//                                }
                            }
                        });

                        Toast.makeText(Profile_edit.this, "Profile Updated", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference ImagesRef = storageRef.child("images/" + mAuth.getCurrentUser().getUid() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = ImagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Error:", exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", task.getException().toString());
                        }
                        return ImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
//                            Toast.makeText(log_in.this, "Upload Successfull", Toast.LENGTH_SHORT).show();
                            url = downloadUri;
                            //StorageReference ref = FirebaseStorage.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
                            assert downloadUri != null;
                            Log.i("seeThisUri", downloadUri.toString());// This is the one you should store
                            //ref.child("imageURL").setValue(downloadUri.toString());
                        } else {
                            Log.i("wentWrong", "downloadUri failure");
                        }
                    }
                });
            }
        });
    }
}