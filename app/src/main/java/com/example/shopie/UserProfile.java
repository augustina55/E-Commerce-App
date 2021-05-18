package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    Button btnSubmit;
    FirebaseUserProfile fuser;
    CircleImageView profile_image;
    TextInputEditText editPhone,editEmail,editLastname,editFirstname;
    FirebaseAuth mAuth;
    DatabaseReference dbref;
    String user;
    String downloadURL="NULL";
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    StorageReference storageReference,strg1;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserProfile.this,MyAccount.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbref.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    editFirstname.setText(snapshot.child("firstname").getValue().toString());
                    editLastname.setText(snapshot.child("lastname").getValue().toString());
                    editEmail.setText(snapshot.child("email").getValue().toString());
                    editPhone.setText(snapshot.child("phone").getValue().toString());
                    String url = snapshot.child("imageurl").getValue().toString();
                    if (url.equals("NULL")) {
                    } else{
                        Glide.with(UserProfile.this)
                                .load(url)
                                .centerCrop()
                                .placeholder(R.drawable.ic_baseline_person_24).into(profile_image);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mAuth=FirebaseAuth.getInstance();
        dbref=FirebaseDatabase.getInstance().getReference("USER_PROFILE");
        user = mAuth.getCurrentUser().getUid();

        editFirstname=findViewById(R.id.editFirstname);
        editLastname=findViewById(R.id.editLastname);
        editEmail=findViewById(R.id.editEmail);
        editPhone=findViewById(R.id.editPhone);
        profile_image=findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });
        btnSubmit=findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editFirstname.getText().toString().isEmpty())
                    editFirstname.setError("Please enter first name");
                else  if(editLastname.getText().toString().isEmpty())
                    editLastname.setError("Please enter last name");
                else  if(editEmail.getText().toString().isEmpty())
                    editEmail.setError("Please enter email");
                else  if(editPhone.getText().toString().isEmpty())
                    editPhone.setError("Please enter phone number");
                else
                    saveDb();
            }
        });
    }
    private void saveDb(){
        fuser=new FirebaseUserProfile();
        fuser.setFirstname(editFirstname.getText().toString().trim());
        fuser.setLastname(editLastname.getText().toString().trim());
        fuser.setEmail(editEmail.getText().toString().trim());
        fuser.setPhone(editPhone.getText().toString().trim());
        fuser.setImageurl(downloadURL);
        dbref.child(String.valueOf(user)).setValue(fuser);
        Toast.makeText(UserProfile.this,"Profile Saved",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(UserProfile.this.getContentResolver(), filePath);
                //Setting image to ImageView
                profile_image.setImageBitmap(bitmap);
                if(filePath != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(UserProfile.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    StorageReference ref = storageReference.child("profile/" + "photo");
                    ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            strg1.child("profile/"+ "photo").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURL=String.valueOf(uri);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfile.this,"Failed!",Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}