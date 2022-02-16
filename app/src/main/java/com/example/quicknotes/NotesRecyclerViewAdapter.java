package com.example.quicknotes;

import static com.example.quicknotes.NoteActivity.NOTE_IDENTIFIER_KEY;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter
        extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private static boolean USER_IS_CHECKING_NOTES = false;
    private static int NUMBER_OF_NOTES_CHECKED = 0;
    private final Context context;
    private ArrayList<Note> notes = new ArrayList<>();

    public NotesRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.notes_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // here is where we can modify the attributes of the note in main activity view
        // and set on click listeners, you can access elements from the holder
        if (notes.get(position).getTitle() != null) {
            holder.noteTitle.setText(notes.get(position).getTitle());
            holder.noteTitle.setVisibility(View.VISIBLE);
        }
        if (notes.get(position).getContent() != null) {
            String noteContent = notes.get(position).getContent();
            if (noteContent.length() > 170) {
                StringBuilder contentPreview = new StringBuilder();
                Character current_character;
                for (int i = 0; i < 170; i++) {
                    current_character = noteContent.charAt(i);
                    if (current_character.equals(' ') && i > 130)
                        break;
                    contentPreview.append(noteContent.charAt(i));
                }
                contentPreview = new StringBuilder(contentPreview.toString().concat("..."));
                holder.noteContent.setText(contentPreview.toString());
            } else {
                holder.noteContent.setText(notes.get(position).getContent());
            }
            holder.noteContent.setVisibility(View.VISIBLE);
        }
        holder.materialCardView.setOnLongClickListener(view -> {
            if (!holder.materialCardView.isChecked())
                NUMBER_OF_NOTES_CHECKED++;
            else
                NUMBER_OF_NOTES_CHECKED--;
            USER_IS_CHECKING_NOTES = NUMBER_OF_NOTES_CHECKED > 0;
            holder.materialCardView.setChecked(!holder.materialCardView.isChecked());
            return true;
        });
        holder.materialCardView.setOnClickListener(view -> {
            if (USER_IS_CHECKING_NOTES) {
                if (!holder.materialCardView.isChecked())
                    NUMBER_OF_NOTES_CHECKED++;
                else
                    NUMBER_OF_NOTES_CHECKED--;
                holder.materialCardView.setChecked(!holder.materialCardView.isChecked());
                if (NUMBER_OF_NOTES_CHECKED == 0)
                    USER_IS_CHECKING_NOTES = false;
            } else {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra(NOTE_IDENTIFIER_KEY, notes.get(position).getNoteIdentifier());
                Toast.makeText(context, "intent sent " + notes.get(position).getNoteIdentifier(), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });
//        if (notes.get(position).getBackgroundColor() != 0) {
//            holder.materialCardView.setCardBackgroundColor(context.getColor(notes.get(position).getBackgroundColor()));
//        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // TODO: remove this function later
    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public void addNote(Note note) {
        this.notes.add(note);
        // TODO:
        //  JP: i am not sure which item needs to have a listener for this event
        notifyItemInserted(notes.size());
        // if it doesn't work try notifyDataSetChanged()
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
