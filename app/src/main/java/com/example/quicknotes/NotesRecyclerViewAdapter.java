package com.example.quicknotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter
        extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Note> notes = new ArrayList<>();

    public NotesRecyclerViewAdapter() {
        //TODO: create constructor
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        //TODO: finish this class

    }

}
