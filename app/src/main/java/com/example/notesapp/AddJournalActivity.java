package com.example.notesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.notesapp.databinding.ActivityAddJournalBinding;
import com.example.notesapp.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {
    //databinding
    private ActivityAddJournalBinding addJournalBinding;

    //Firebase Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    //Firebbase Storage
    private CollectionReference collectionReference = db.collection("Journal");

    //FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private String userId;
    private String userName;

    //Using Activity Result Launcher
    private ActivityResultLauncher<String> mTakePhoto;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_journal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //DataBinding
        addJournalBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_journal);

        addJournalBinding.postProgressBar.setVisibility(View.INVISIBLE);

        //Activity Result Launcher
        mTakePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        //Displaying Image on imageView
                        addJournalBinding.postImageView.setImageURI(o);
                        //Getting the image Uri
                        imageUri = o;

                    }
                }
        );

        //Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        //FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Getting Current User
        if (user != null){
            userId = user.getEmail(); // Use email as userId
            userName = user.getDisplayName();
        }

        //Save Btn ClickListener
        addJournalBinding.postSaveJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournal();
            }
        });

        //Upload photo Btn click Listener
        addJournalBinding.postCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting image from gallery
                mTakePhoto.launch("image/*");

            }
        });

    }

    private void saveJournal() {
        String title = addJournalBinding.postTitleEt.getText().toString().trim();
        String desc = addJournalBinding.postDescriptionEt.getText().toString().trim();

        addJournalBinding.postProgressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && imageUri != null){
            final StorageReference filePath = storageReference.
                    child("journal_images").
                    child("my_image_"+ Timestamp.now().getSeconds());

            filePath.putFile(imageUri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            //Creating a Journal Object
                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(desc);
                            journal.setImageUrl(imageUrl);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setUserId(userId);
                            journal.setUserName(userName);

                            collectionReference.add(journal).
                                    addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    addJournalBinding.postProgressBar.setVisibility(View.INVISIBLE);

                                    Intent intent = new Intent(AddJournalActivity.this
                                            ,JournalActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddJournalActivity.this,
                                                    "Failure: "+e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                            });

                        }
                    });
                }
            });
        }else {
            addJournalBinding.postProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
    }
}