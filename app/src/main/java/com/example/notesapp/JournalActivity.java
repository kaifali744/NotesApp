package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.databinding.ActivityJournalBinding;
import com.example.notesapp.model.Journal;
import com.example.notesapp.view.MyAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class JournalActivity extends AppCompatActivity {

    ActivityJournalBinding binding;

    //FireBase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    //FireBase FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");

    //FireBase Storage
    private StorageReference storageReference;

    //List of Journals
    private ArrayList<Journal> journalArrayList;

    //RecyclerView
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_journal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //DataBinding
        binding = DataBindingUtil.setContentView(this,R.layout.activity_journal);

        //RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        //Fiebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Journals list
        journalArrayList = new ArrayList<>();

        //FAB
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JournalActivity.this, AddJournalActivity.class);
                startActivity(i);
            }
        });


    }
    //Menu Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_add){
            if (firebaseUser != null && firebaseAuth != null){
                Intent intent= new Intent(JournalActivity.this,
                        AddJournalActivity.class);
                startActivity(intent);
            }
        }else if (itemId == R.id.action_signOut) {
            if(firebaseUser!= null && firebaseAuth != null){
                firebaseAuth.signOut();
                Intent i = new Intent(JournalActivity.this,MainActivity.class);
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String userId = firebaseUser.getEmail();
        collectionReference.get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                    //Converting Document to Custom Journal Object
                    Journal journal = journals.toObject(Journal.class);
                    journalArrayList.add(journal);
                }
                //RecyclerView
                myAdapter= new MyAdapter(JournalActivity.this, journalArrayList);
                recyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JournalActivity.this,
                        "Oops! Something went wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}