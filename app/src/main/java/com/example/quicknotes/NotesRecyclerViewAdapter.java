package com.example.quicknotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.divider.MaterialDivider;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter
        extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Note> notes = new ArrayList<>();

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
        if (notes.get(position).getTitle() != null) {
            holder.noteTitle.setText(notes.get(position).getTitle());
            holder.noteTitle.setVisibility(View.VISIBLE);
        }
        if (notes.get(position).getContent() != null) {
            holder.noteContent.setText(notes.get(position).getContent());
            holder.noteContent.setVisibility(View.VISIBLE);
        }
        if (holder.noteTitle.getVisibility() == View.VISIBLE && holder.noteContent.getVisibility() == View.VISIBLE) {
            holder.materialDivider.setVisibility(View.VISIBLE);
        }
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
        private final MaterialDivider materialDivider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.material_card_view);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
            materialDivider = itemView.findViewById(R.id.divider);
        }
    }

}
