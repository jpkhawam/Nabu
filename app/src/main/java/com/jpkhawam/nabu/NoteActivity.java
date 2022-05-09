package com.jpkhawam.nabu;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_IDENTIFIER_KEY = "noteIdentifier";
    public static final String ARCHIVED_NOTE_IDENTIFIER_KEY = "archivedNoteId";
    public static final String UNARCHIVED_NOTE_IDENTIFIER_KEY = "unarchivedNoteId";
    public static final String DISCARDED_NOTE_KEY = "discardedNote";
    private Note currentNote;
    private CoordinatorLayout parent;
    private TextInputEditText editTextTitle;
    private TextInputEditText editTextContent;
    int editTitleFontSizeInt = 20;
    int editContentFontSizeInt = 16;

    @SuppressLint({"NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        // Get Font Type SharedPreferences
        String fontType = settings.getString("settings_fonttype", "Default");

        // Add Dyslexia-Friendly fontFamily Style To The Default Theme According To Font Type SharedPreferences
        if (fontType.equals("Dyslexia-friendly")) {
            getTheme().applyStyle(R.style.DyslexiaTheme, false);
            getTheme().applyStyle(R.style.DyslexiaThemeExcludingLogo, false);
        }
        setContentView(R.layout.activity_note);

        parent = findViewById(R.id.note_layout);
        editTextTitle = findViewById(R.id.input_note_title);
        editTextContent = findViewById(R.id.input_note_content);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(NoteActivity.this);
        // Get Font Size SharedPreferences
        String fontSize = settings.getString("settings_fontsize", "Small");

        // Set Font Size Value According To Font Size SharedPreferences
        if (fontSize.equals("Small")) {
            editTitleFontSizeInt = 20;
            editContentFontSizeInt = 16;
        }
        if (fontSize.equals("Medium")) {
            editTitleFontSizeInt = (int) (20 * 1.5);
            editContentFontSizeInt = (int) (16 * 1.5);
        }
        if (fontSize.equals("Large")) {
            editTitleFontSizeInt = 20 * 2;
            editContentFontSizeInt = 16 * 2;
        }

        // Set Note Edit Title and Content Font Size According to Font Size Value
        editTextTitle.setTextSize(editTitleFontSizeInt);
        editTextContent.setTextSize(editContentFontSizeInt);

        // Change Note Edit Title and Content Font Type to Dyslexia-friendly According To Font Type SharedPreferences
        if (fontType.equals("Dyslexia-friendly")) {
            Typeface dysBold = ResourcesCompat.getFont(this, R.font.opendyslexic_bold);
            Typeface dysRegular = ResourcesCompat.getFont(this, R.font.opendyslexic_regular);
            editTextTitle.setTypeface(dysBold);
            editTextContent.setTypeface(dysRegular);
        }

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            long noteIdentifier = intentReceived.getLongExtra(NOTE_IDENTIFIER_KEY, -1);
            if (noteIdentifier != -1) {
                currentNote = dataBaseHelper.getNote(noteIdentifier);
                editTextTitle.setText(currentNote.getTitle());
                editTextContent.setText(currentNote.getContent());

            } else {
                currentNote = dataBaseHelper.getNote(dataBaseHelper.addNote(new Note()));
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
                Intent outgoingIntent;
                if (dataBaseHelper.isInTrash(currentNote))
                    outgoingIntent = new Intent(this, TrashActivity.class);
                else if (dataBaseHelper.isInArchive(currentNote))
                    outgoingIntent = new Intent(this, ArchiveActivity.class);
                else
                    outgoingIntent = new Intent(this, MainActivity.class);
                if ((currentNote.getTitle() == null || currentNote.getTitle().equals("")) &&
                        (currentNote.getContent() == null || currentNote.getContent().equals(""))) {
                    dataBaseHelper.deleteNote(currentNote);
                    dataBaseHelper.deleteNoteFromTrash(currentNote);
                    outgoingIntent.putExtra(DISCARDED_NOTE_KEY, true);
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
                        Intent outgoingIntent;
                        if (dataBaseHelper.isInTrash(currentNote))
                            outgoingIntent = new Intent(this, TrashActivity.class);
                        else if (dataBaseHelper.isInArchive(currentNote))
                            outgoingIntent = new Intent(this, ArchiveActivity.class);
                        else
                            outgoingIntent = new Intent(this, MainActivity.class);
                        if ((currentNote.getTitle() == null && currentNote.getContent() == null)
                                || (currentNote.getTitle().equals("") && currentNote.getContent().equals(""))) {
                            Snackbar.make(parent, "Cannot archive empty note", Snackbar.LENGTH_SHORT).show();
                        } else if (dataBaseHelper.isInArchive(currentNote)) {
                            dataBaseHelper.unarchiveNote(currentNote);
                            outgoingIntent.putExtra(UNARCHIVED_NOTE_IDENTIFIER_KEY, currentNote.getNoteIdentifier());
                            startActivity(outgoingIntent);
                        } else {
                            dataBaseHelper.archiveNote(currentNote);
                            outgoingIntent.putExtra(ARCHIVED_NOTE_IDENTIFIER_KEY, currentNote.getNoteIdentifier());
                            startActivity(outgoingIntent);
                        }
                        return true;
                    default:
                        return false;
                }
            });

            BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
            bottomAppBar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.note_copy:
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData;
                        String copyData = "";
                        if (currentNote.getTitle() != null && !currentNote.getTitle().equals("")) {
                            copyData = currentNote.getTitle();
                        }
                        if (currentNote.getContent() != null && !currentNote.getContent().equals("")) {
                            if (!copyData.equals(""))
                                copyData = copyData + '\n' + currentNote.getContent();
                            else copyData = currentNote.getContent();
                        }
                        if (!copyData.equals("")) {
                            clipData = ClipData.newPlainText("Copied text", copyData);
                            clipboardManager.setPrimaryClip(clipData);
                            Snackbar.make(parent, R.string.copied_text_clipboard, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(parent, R.string.didnt_copy_text_clipboard, Snackbar.LENGTH_SHORT).show();
                        }
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
                            if (currentNote.getContent() != null && !currentNote.getContent().equals("")) {
                                currentNote.setContent(currentNote.getContent() + " " + pasteData);
                            } else {
                                currentNote.setContent(pasteData);
                            }
                            editTextContent.setText(currentNote.getContent());
                            Snackbar.make(parent, R.string.clipboard_pasted, Snackbar.LENGTH_SHORT)
                                    .setAction(R.string.undo, view -> {
                                        currentNote.setContent(backUpText);
                                        editTextContent.setText(backUpText);
                                    })
                                    .show();
                        }
                        return true;
                    default:
                        return false;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(NoteActivity.this);
        Intent outgoingIntent;
        if (dataBaseHelper.isInTrash(currentNote))
            outgoingIntent = new Intent(this, TrashActivity.class);
        else if (dataBaseHelper.isInArchive(currentNote))
            outgoingIntent = new Intent(this, ArchiveActivity.class);
        else
            outgoingIntent = new Intent(this, MainActivity.class);
        if ((currentNote.getTitle() == null || currentNote.getTitle().equals("")) &&
                (currentNote.getContent() == null || currentNote.getContent().equals(""))) {
            dataBaseHelper.deleteNote(currentNote);
            dataBaseHelper.deleteNoteFromTrash(currentNote);
            outgoingIntent.putExtra(DISCARDED_NOTE_KEY, true);
        }
        startActivity(outgoingIntent);
    }
}
