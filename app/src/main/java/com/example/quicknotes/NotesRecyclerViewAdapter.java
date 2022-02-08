package com.example.quicknotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter
        extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Note> notes = new ArrayList<>();
    private final Context context;

    public NotesRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new View via the layout inflater
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.notes_list_item, parent, false);
        // and return a new ViewHolder object instantiated with this view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // here is where we can modify the attributes of the note in main activity view
        // and set on click listeners, and set the display
        // you can access them from the holder directly
        // for example
        // holder.parentLayout.setOnClickListener(v -> {});
        // (this onClick is set for one element only since each element gets one layout)
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(ArrayList<Note> notes) {
        //TODO: fill this method
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.noteConstraintLayout);
        }
        //TODO: finish this class

    }

}
