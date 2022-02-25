package com.gmail.nabunoteapp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_IDENTIFIER_KEY = "noteIdentifier";
    private BottomSheetDialog dialog;
    private Note currentNote;
    private CoordinatorLayout parent;
    private TextInputEditText editTextTitle;
    private TextInputEditText editTextContent;

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        parent = findViewById(R.id.note_layout);
        editTextTitle = findViewById(R.id.input_note_title);
        editTextContent = findViewById(R.id.input_note_content);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(NoteActivity.this);

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            long noteIdentifier = intentReceived.getLongExtra(NOTE_IDENTIFIER_KEY, -1);
            if (noteIdentifier != -1) {
                currentNote = dataBaseHelper.getNote(noteIdentifier);
                editTextTitle.setText(currentNote.getTitle());
                editTextContent.setText(currentNote.getContent());
            } else {
                long newNoteIdentifier = dataBaseHelper.createNewNote();
                currentNote = dataBaseHelper.getNote(newNoteIdentifier);
            }

            editTextTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    currentNote.setDateEdited(LocalDateTime.now());
                    if (editTextTitle.getText() != null) {
                        currentNote.setTitle(editTextTitle.getText().toString());
                    } else {
                        currentNote.setTitle(null);
                    }
                    dataBaseHelper.updateNote(currentNote);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            editTextContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    currentNote.setDateEdited(LocalDateTime.now());
                    if (editTextContent.getText() != null) {
                        currentNote.setContent(editTextContent.getText().toString());
                    } else {
                        currentNote.setContent(null);
                    }
                    dataBaseHelper.updateNote(currentNote);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            MaterialToolbar topAppBar = findViewById(R.id.noteTopBar);
            topAppBar.setNavigationOnClickListener(view -> {
                Intent outgoingIntent = new Intent(this, MainActivity.class);
                // redundant yes, but sometimes the app crashes otherwise, "" and null are different
                if (currentNote.getTitle() == null && currentNote.getContent() == null) {
                    dataBaseHelper.deleteNote(currentNote);
                }
                if (currentNote.getTitle().equals("") && currentNote.getContent().equals("")) {
                    dataBaseHelper.deleteNote(currentNote);
                }
                startActivity(outgoingIntent);
            });
            topAppBar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.note_pin:
                        // pin note
                        return true;
                    case R.id.note_add_reminder:
                        // add reminder
                        return true;
                    case R.id.note_send_to_archive:
                        // send to archive
                        return true;
                    default:
                        return false;
                }
            });

            BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);

            bottomAppBar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {

                    case R.id.note_color:
                        // give note color options
                        // this also can be a bottom sheet fragment
                        return true;

                    case R.id.note_label:
                        // give note label options
                        // also bottom sheet fragment
                        return true;

                    case R.id.note_copy:
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("Copied text", currentNote.getContent());
                        clipboardManager.setPrimaryClip(clipData);
                        Snackbar.make(parent, R.string.copied_text_clipboard, Snackbar.LENGTH_SHORT).show();
                        return true;

                    case R.id.note_paste:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // If it does contain data
                        if (!(clipboard.hasPrimaryClip())) {
                            Snackbar.make(parent, R.string.empty_clipboard, Snackbar.LENGTH_SHORT).show();
                        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {
                            // the clipboard has data but it is not plain text
                            Snackbar.make(parent, R.string.clipboard_not_text, Snackbar.LENGTH_SHORT).show();
                        } else {
                            // the clipboard contains plain text.
                            ClipData.Item clipDataItem = clipboard.getPrimaryClip().getItemAt(0);

                            // Gets the clipboard as text
                            String pasteData = clipDataItem.getText().toString();
                            String backUpText = currentNote.getContent();

                            currentNote.setContent(currentNote.getContent() + " " + pasteData);
                            editTextContent.setText(currentNote.getContent());

                            Snackbar.make(parent, R.string.clipboard_pasted, Snackbar.LENGTH_SHORT)
                                    .setAction(R.string.undo, view -> {
                                        currentNote.setContent(backUpText);
                                        editTextContent.setText(backUpText);
                                    })
                                    .show();
                        }

                    default:
                        return false;
                }
            });

            CoordinatorLayout layout = (CoordinatorLayout) parent;
            dialog = new BottomSheetDialog(this);
            onCreateDialog();

            bottomAppBar.setNavigationOnClickListener(view -> {
                dialog.show();
                layout.setForeground(getDrawable(R.color.dim_color));
            });

            dialog.setOnDismissListener(dialogInterface ->
                    layout.setForeground(getDrawable(R.color.reset)));

            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        }
    }

    private void onCreateDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, parent, false);
        dialog.setContentView(view);
    }

}


