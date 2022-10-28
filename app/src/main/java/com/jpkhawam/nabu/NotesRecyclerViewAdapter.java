package com.jpkhawam.nabu;

import static com.jpkhawam.nabu.NoteActivity.NOTE_IDENTIFIER_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {
    public static final String MAIN_ACTIVITY = "MainActivity";
    public static final String ARCHIVE_ACTIVITY = "ArchiveActivity";
    public static final String TRASH_ACTIVITY = "TrashActivity";
    protected static ArrayList<Note> selectedNotes = new ArrayList<>();
    protected static ArrayList<MaterialCardView> checkedCards = new ArrayList<>();
    private final Context context;
    private final DrawerLayout drawerLayout;
    private int titleFontSizeInt = 17;
    private int contentFontSizeInt = 16;
    private boolean font_defaultSize = false;
    private boolean font_mediumSize = false;
    private boolean font_largeSize = false;
    private ArrayList<Note> notes = new ArrayList<>();
    private ActionMode mActionMode;

    public NotesRecyclerViewAdapter(Context context, DrawerLayout drawerLayout) {
        this.context = context;
        this.drawerLayout = drawerLayout;
        selectedNotes.clear();
        checkedCards.clear();
        // Get Font Size SharedPreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = settings.getString("settings_fontsize", context.getString(R.string.font_size_small));

        // Set Font Size Value According To Font Size SharedPreferences
        if (fontSize.equals(context.getString(R.string.font_size_small))) {
            titleFontSizeInt = 17;
            contentFontSizeInt = 16;
            font_defaultSize = true;
        }
        if (fontSize.equals(context.getString(R.string.font_size_medium))) {
            titleFontSizeInt = (int) (17 * 1.5);
            contentFontSizeInt = (int) (16 * 1.5);
            font_mediumSize = true;
        }
        if (fontSize.equals(context.getString(R.string.font_size_large))) {
            titleFontSizeInt = 17 * 2;
            contentFontSizeInt = 16 * 2;
            font_largeSize = true;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
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
        // if content is too long, only preview first 170~ characters
        if (notes.get(position).getContent() != null && !notes.get(position).getContent().equals("")) {
            String noteContent = notes.get(position).getContent();
            int max_characters = 0;
            if (font_defaultSize) max_characters = 120;
            else if (font_mediumSize) max_characters = 30;
            else if (font_largeSize) max_characters = 26;
            if (noteContent.length() > max_characters) {
                StringBuilder contentPreview = new StringBuilder();
                Character current_character;
                for (int i = 0; i < max_characters; i++) {
                    current_character = noteContent.charAt(i);
                    if (current_character.equals(' ') && i > max_characters - 20) break;
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
            holder.materialCardView.setChecked(!holder.materialCardView.isChecked());
            if (holder.materialCardView.isChecked()) {
                selectedNotes.add(note);
                checkedCards.add(holder.materialCardView);
            } else {
                selectedNotes.remove(note);
            }
            MyActionModeCallback callback = new MyActionModeCallback(context);
            mActionMode = view.startActionMode(callback);
            if (selectedNotes.isEmpty()) {
                mActionMode.setTitle("");
                mActionMode.finish();
            } else if (selectedNotes.size() == 1)
                mActionMode.setTitle(selectedNotes.size() + " " + context.getString(R.string.note_selected));
            else
                mActionMode.setTitle(selectedNotes.size() + " " + context.getString(R.string.notes_selected));
            return true;
        });
        holder.materialCardView.setOnClickListener(view -> {
            if (!selectedNotes.isEmpty()) {
                holder.materialCardView.setChecked(!holder.materialCardView.isChecked());
                if (holder.materialCardView.isChecked()) {
                    checkedCards.add(holder.materialCardView);
                    selectedNotes.add(note);
                } else {
                    selectedNotes.remove(note);
                }
                if (selectedNotes.isEmpty()) {
                    mActionMode.setTitle("");
                    mActionMode.finish();
                } else if (selectedNotes.size() == 1)
                    mActionMode.setTitle(selectedNotes.size() + " " + context.getString(R.string.note_selected));
                else
                    mActionMode.setTitle(selectedNotes.size() + " " + context.getString(R.string.notes_selected));
            } else {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra(NOTE_IDENTIFIER_KEY, notes.get(position).getNoteIdentifier());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    class MyActionModeCallback implements ActionMode.Callback {
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

        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
            // this is to know which activity we are in since this adapter is shared
            String currentActivity = MAIN_ACTIVITY;
            if (dataBaseHelper.isInTrash(NotesRecyclerViewAdapter.selectedNotes.get(0)))
                currentActivity = TRASH_ACTIVITY;
            else if (dataBaseHelper.isInArchive(NotesRecyclerViewAdapter.selectedNotes.get(0)))
                currentActivity = ARCHIVE_ACTIVITY;

            switch (menuItem.getItemId()) {
                case R.id.note_send_to_trash:
                    if (currentActivity.equals(TRASH_ACTIVITY)) {
                        new MaterialAlertDialogBuilder(context).setTitle(R.string.confirm_action).setMessage(R.string.are_you_sure_delete).setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                            for (MaterialCardView materialCardView : checkedCards) {
                                materialCardView.setChecked(false);
                            }
                            selectedNotes.clear();
                            checkedCards.clear();
                        }).setNegativeButton(R.string.delete_permanently, (dialogInterface, i) -> {
                            for (Note note : NotesRecyclerViewAdapter.selectedNotes) {
                                dataBaseHelper.deleteNoteFromTrash(note);
                            }
                            for (MaterialCardView materialCardView : checkedCards) {
                                materialCardView.setChecked(false);
                            }
                            selectedNotes.clear();
                            checkedCards.clear();
                            notifyDataSetChanged();
                        }).show();
                    } else {
                        for (Note note : NotesRecyclerViewAdapter.selectedNotes) {
                            dataBaseHelper.deleteNote(note);
                        }
                        String finalCurrentActivity = currentActivity;
                        Snackbar.make(drawerLayout, R.string.notes_sent_trash, Snackbar.LENGTH_SHORT).setAction(R.string.Undo, view -> {
                            for (Note note : NotesRecyclerViewAdapter.selectedNotes) {
                                dataBaseHelper.restoreNote(note);
                                if (finalCurrentActivity.equals(ARCHIVE_ACTIVITY))
                                    dataBaseHelper.archiveNote(note);
                            }
                            for (MaterialCardView materialCardView : checkedCards) {
                                materialCardView.setChecked(false);
                            }
                            selectedNotes.clear();
                            checkedCards.clear();
                            notifyDataSetChanged();
                        }).addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                for (MaterialCardView materialCardView : checkedCards) {
                                    materialCardView.setChecked(false);
                                }
                                selectedNotes.clear();
                                checkedCards.clear();
                                notifyDataSetChanged();
                            }
                        }).show();
                    }
                    for (MaterialCardView materialCardView : checkedCards) {
                        materialCardView.setChecked(false);
                    }
                    notifyDataSetChanged();
                    mActionMode.setTitle("");
                    mActionMode.finish();
                    return true;

                case R.id.note_send_to_archive:
                    for (Note note : NotesRecyclerViewAdapter.selectedNotes) {
                        if (currentActivity.equals(ARCHIVE_ACTIVITY))
                            dataBaseHelper.unarchiveNote(note);
                        else dataBaseHelper.archiveNote(note);
                    }
                    notifyDataSetChanged();
                    String finalCurrentActivityArchive = currentActivity;
                    if (!currentActivity.equals(ARCHIVE_ACTIVITY)) {
                        Snackbar.make(drawerLayout, R.string.notes_archived, Snackbar.LENGTH_SHORT).setAction(R.string.Undo, view -> {
                            for (Note note : NotesRecyclerViewAdapter.selectedNotes) {
                                dataBaseHelper.unarchiveNote(note);
                                if (finalCurrentActivityArchive.equals(TRASH_ACTIVITY))
                                    dataBaseHelper.deleteNote(note);
                            }
                            for (MaterialCardView materialCardView : checkedCards) {
                                materialCardView.setChecked(false);
                            }
                            selectedNotes.clear();
                            checkedCards.clear();
                            notifyDataSetChanged();
                        }).addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                for (MaterialCardView materialCardView : checkedCards) {
                                    materialCardView.setChecked(false);
                                }
                                selectedNotes.clear();
                                checkedCards.clear();
                                notifyDataSetChanged();
                            }
                        }).show();
                    } else {
                        Snackbar.make(drawerLayout, R.string.notes_unarchived, Snackbar.LENGTH_SHORT).setAction(R.string.Undo, view -> {
                            for (Note note : NotesRecyclerViewAdapter.selectedNotes) {
                                dataBaseHelper.archiveNote(note);
                            }
                            for (MaterialCardView materialCardView : checkedCards) {
                                materialCardView.setChecked(false);
                            }
                            selectedNotes.clear();
                            checkedCards.clear();
                            notifyDataSetChanged();
                        }).addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                for (MaterialCardView materialCardView : checkedCards) {
                                    materialCardView.setChecked(false);
                                }
                                selectedNotes.clear();
                                checkedCards.clear();
                                notifyDataSetChanged();
                            }
                        }).show();
                    }
                    for (MaterialCardView materialCardView : checkedCards) {
                        materialCardView.setChecked(false);
                    }
                    notifyDataSetChanged();
                    mActionMode.setTitle("");
                    mActionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            for (MaterialCardView materialCardView : checkedCards) {
                materialCardView.setChecked(false);
            }
        }
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

            // Get Font Type SharedPreferences
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String fontType = settings.getString("settings_fonttype", context.getString(R.string.font_type_default));

            // Change Note Title and Content Font Type to Dyslexia-friendly According to Font Type SharedPreferences
            if (fontType.equals(context.getString(R.string.font_type_dyslexia))) {
                Typeface dysBold = ResourcesCompat.getFont(context, R.font.opendyslexic_bold);
                Typeface dysRegular = ResourcesCompat.getFont(context, R.font.opendyslexic_regular);
                noteTitle.setTypeface(dysBold);
                noteContent.setTypeface(dysRegular);
            }
        }
    }
}
