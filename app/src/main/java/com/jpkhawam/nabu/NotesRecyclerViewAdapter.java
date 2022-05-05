package com.jpkhawam.nabu;

import static com.jpkhawam.nabu.NoteActivity.NOTE_IDENTIFIER_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter
        extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {
    int titleFontSizeInt = 17;
    int contentFontSizeInt = 16;
    private static boolean USER_IS_CHECKING_NOTES = false;
    private static int NUMBER_OF_NOTES_CHECKED = 0;
    private final Context context;
    private ArrayList<Note> notes = new ArrayList<>();
    protected static ArrayList<Note> selectedNotes = new ArrayList<>();
    private ActionMode mActionMode;

    public NotesRecyclerViewAdapter(Context context) {
        this.context = context;

        // Get Font Size SharedPreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = settings.getString("settings_fontsize", "Small");

        // Set Font Size Value According To Font Size SharedPreferences
        if (fontSize.equals("Small")){
            titleFontSizeInt = 17;
            contentFontSizeInt = 16;
        }
        if (fontSize.equals("Medium")){
            titleFontSizeInt = (int) (17 * 1.5);
            contentFontSizeInt = (int) (16 * 1.5);
        }
        if (fontSize.equals("Large")){
            titleFontSizeInt = 17 * 2;
            contentFontSizeInt = 16 * 2;
        }
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
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        long noteIdentifier = notes.get(position).getNoteIdentifier();
        Note note = dataBaseHelper.getNote(noteIdentifier);
        // here is where we can modify the attributes of the note in main activity view
        // and set on click listeners, you can access elements from the holder
        if (notes.get(position).getTitle() != null && !notes.get(position).getTitle().equals("")) {
            holder.noteTitle.setText(notes.get(position).getTitle());
            holder.noteTitle.setVisibility(View.VISIBLE);
        }
        if (notes.get(position).getContent() != null && !notes.get(position).getContent().equals("")) {
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
            if(holder.materialCardView.isChecked()) {
                selectedNotes.add(note);
            } else {
                selectedNotes.remove(note);
            }
            MyActionModeCallback callback = new MyActionModeCallback(context);
            mActionMode = view.startActionMode(callback);
            if (NUMBER_OF_NOTES_CHECKED == 0) {
                mActionMode.setTitle("");
                mActionMode.finish();
            } else if (NUMBER_OF_NOTES_CHECKED == 1)
                mActionMode.setTitle(NUMBER_OF_NOTES_CHECKED + " note selected");
            else
                mActionMode.setTitle(NUMBER_OF_NOTES_CHECKED + " notes selected");
            return true;
        });
        holder.materialCardView.setOnClickListener(view -> {
            if (USER_IS_CHECKING_NOTES) {
                if (!holder.materialCardView.isChecked())
                    NUMBER_OF_NOTES_CHECKED++;
                else
                    NUMBER_OF_NOTES_CHECKED--;
                holder.materialCardView.setChecked(!holder.materialCardView.isChecked());
                if(holder.materialCardView.isChecked()) {
                    selectedNotes.add(note);
                } else {
                    selectedNotes.remove(note);
                }
                if (NUMBER_OF_NOTES_CHECKED == 0) {
                    USER_IS_CHECKING_NOTES = false;
                    mActionMode.setTitle("");
                    mActionMode.finish();
                } else if (NUMBER_OF_NOTES_CHECKED == 1)
                    mActionMode.setTitle(NUMBER_OF_NOTES_CHECKED + " note selected");
                else
                    mActionMode.setTitle(NUMBER_OF_NOTES_CHECKED + " notes selected");
            } else {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra(NOTE_IDENTIFIER_KEY, notes.get(position).getNoteIdentifier());
                context.startActivity(intent);
            }
        });
//        if (notes.get(position).getBackgroundColor() != 0) {
//            holder.materialCardView.setCardBackgroundColor(context.getColor(notes.get(position).getBackgroundColor()));
//        }
    }

    static class MyActionModeCallback implements ActionMode.Callback {
        private final Context context;

        public MyActionModeCallback(Context context) {
            this.context = context;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.contextual_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
            switch(menuItem.getItemId()) {
                case R.id.note_send_to_trash:
                    for(Note note: NotesRecyclerViewAdapter.selectedNotes)
                        dataBaseHelper.deleteNote(note);
                    break;
                case R.id.note_send_to_archive:
                    for(Note note: NotesRecyclerViewAdapter.selectedNotes)
                        dataBaseHelper.archiveNote(note);
                    break;
                default:
                    return false;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // TODO: remove this function later
    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView noteTitle;
        private final TextView noteContent;
        private final MaterialCardView materialCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.material_card_view);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
            // Set Note Title and Content Font Size According to Font Size Value
            noteTitle.setTextSize(titleFontSizeInt);
            noteContent.setTextSize(contentFontSizeInt);
        }
    }

}
