package com.example.frameapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class FrameAdapter  extends FirestoreRecyclerAdapter<Frame, FrameAdapter.FrameHolder> {
    private OnItemClickListener listener;
    public FrameAdapter(@NonNull FirestoreRecyclerOptions<Frame> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FrameHolder holder, int position, @NonNull Frame model) {
//        holder.textViewTitle.setText(model.getTitle());
//        holder.textViewDescription.setText(model.getDescription());
        Picasso.get().load(model.getDescription()).into(holder.imageViewTest);
//        holder.textViewPriority.setText(String.valueOf(model.getPriority()));
    }

    @NonNull
    @Override
    public FrameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_item,
                parent, false);
        return new FrameHolder(v);
    }

    class FrameHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        //        TextView textViewDescription;
        ImageView imageViewTest;
        TextView textViewPriority;

        public FrameHolder(final View itemView) {
            super(itemView);
//            textViewTitle = itemView.findViewById(R.id.text_view_title);
//            textViewDescription = itemView.findViewById(R.id.text_view_description);
            imageViewTest = itemView.findViewById(R.id.image_view_test);
//            textViewPriority = itemView.findViewById(R.id.text_view_priority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
