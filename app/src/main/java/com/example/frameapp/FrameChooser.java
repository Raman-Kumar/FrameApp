package com.example.frameapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FrameChooser extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Frames");

    private FrameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_chooser);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = notebookRef;//.orderBy("priority", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Frame> options = new FirestoreRecyclerOptions.Builder<Frame>()
                .setQuery(query, Frame.class)
                .build();

        adapter = new FrameAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new FrameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Frame note = documentSnapshot.toObject(Frame.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

//                Toast.makeText(FrameChooser.this,
//                        "Position: " + position + " ID: " + id + documentSnapshot.getData().get("description") , Toast.LENGTH_SHORT).show();

//                DocumentSnapshot document = task.getResult();
//                if (document != null) {
//                    Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData().get("privacy"));
//                    Object meta_object = task.getResult().getData().get("privacy");
//                } else {
//                    Log.d(TAG, "No such document");
//                }
//            } else {
//                Log.d(TAG, "get failed with ", task.getException());
                Intent data = new Intent();
                data.putExtra("description",""+ documentSnapshot.getData().get("description"));
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void ok(View view) {

    }
}
