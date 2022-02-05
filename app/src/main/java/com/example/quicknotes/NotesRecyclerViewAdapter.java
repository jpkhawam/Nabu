package com.example.quicknotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

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
        holder.noteTitle.setText(notes.get(position).getTitle());
        holder.noteContent.setText(notes.get(position).getContent());
        holder.materialCardView.setOnLongClickListener(view -> {
            holder.materialCardView.setChecked(!holder.materialCardView.isChecked());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(ArrayList<Note> notes) {
        // TODO: needs to be reworked
        this.notes = notes;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView noteTitle;
        private final TextView noteContent;
        private final MaterialCardView materialCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.material_card_view);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
        }
    }

}
