package com.example.quicknotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String NOTES_TABLE = "NOTES_TABLE";
    public static final String TRASH_TABLE = "TRASH_TABLE";
    public static final String ARCHIVE_TABLE = "ARCHIVE_TABLE";
    public static final String COLUMN_DATE_CREATED = "DATE_CREATED";
    public static final String COLUMN_DATE_EDITED = "DATE_EDITED";
    public static final String COLUMN_NOTE_TITLE = "NOTE_TITLE";
    public static final String COLUMN_NOTE_CONTENT = "NOTE_CONTENT";
    public static final String COLUMN_BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final String COLUMN_DATE_SENT_TO_TRASH = "DATE_SENT_TO_TRASH";
    public static final String COLUMN_DATE_ARCHIVED = "DATE_ARCHIVED";
    public static final String COLUMN_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createNotesTableStatement =
                "CREATE TABLE " + NOTES_TABLE + " " +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOTE_TITLE + " TEXT, " +
                        COLUMN_NOTE_CONTENT + " TEXT, " +
                        COLUMN_DATE_CREATED + " TEXT, " +
                        COLUMN_DATE_EDITED + " TEXT, " +
                        COLUMN_BACKGROUND_COLOR + " INTEGER)";

        // on app launch, check which notes are due and delete them
        String createTrashTableStatement =
                "CREATE TABLE " + TRASH_TABLE + " " +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOTE_TITLE + " TEXT, " +
                        COLUMN_NOTE_CONTENT + " TEXT, " +
                        COLUMN_DATE_CREATED + " TEXT, " +
                        COLUMN_DATE_EDITED + " TEXT, " +
                        COLUMN_BACKGROUND_COLOR + " INTEGER, " +
                        COLUMN_DATE_SENT_TO_TRASH + " TEXT)";
        sqLiteDatabase.execSQL(createTrashTableStatement);

        String createArchiveTableStatement =
                "CREATE TABLE " + ARCHIVE_TABLE + " " +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOTE_TITLE + " TEXT, " +
                        COLUMN_NOTE_CONTENT + " TEXT, " +
                        COLUMN_DATE_CREATED + " TEXT, " +
                        COLUMN_DATE_EDITED + " TEXT, " +
                        COLUMN_BACKGROUND_COLOR + " INTEGER, " +
                        COLUMN_DATE_ARCHIVED + " TEXT)";
        sqLiteDatabase.execSQL(createArchiveTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addNote(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());

        final long insert = sqLiteDatabase.insert(NOTES_TABLE, null, contentValues);
        sqLiteDatabase.close();
        return insert != -1;
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> allNotes = new ArrayList<>();

        String queryString = "SELECT * FROM " + NOTES_TABLE;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        boolean hasNext = cursor.moveToFirst();
        while (hasNext) {
            int noteIdentifier = cursor.getInt(0);
            String noteTitle = cursor.getString(1);
            String noteContent = cursor.getString(2);
            String dateCreatedString = cursor.getString(3);
            String dateEditedString = cursor.getString(4);
            int backgroundColor = cursor.getInt(5);

            LocalDateTime dateCreated = LocalDateTime.parse(dateCreatedString);
            LocalDateTime dateEdited = LocalDateTime.parse(dateEditedString);

            Note currentNote = new Note(noteIdentifier, noteTitle, noteContent, dateCreated, dateEdited, backgroundColor);
            allNotes.add(currentNote);

            hasNext = cursor.moveToNext();
        }

        cursor.close();
        sqLiteDatabase.close();
        return allNotes;
    }

    public boolean deleteNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String queryString =
                "DELETE FROM " + NOTES_TABLE +
                        "WHERE " + COLUMN_ID + " = " + note.getNoteIdentifier();
        // TODO: SEND TO TRASH
        // TODO: SHOW "NOTE DELETED" AND GIVE OPTION TO UNDO
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.close();
        sqLiteDatabase.close();
        return cursor.moveToFirst();
    }

    public boolean emptyTrash() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String queryString =
                "DELETE FROM " + TRASH_TABLE;
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.close();
        sqLiteDatabase.close();
        return cursor.moveToFirst();
    }

    public boolean emptyArchive() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String queryString =
                "DELETE FROM " + ARCHIVE_TABLE;
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.close();
        sqLiteDatabase.close();
        return cursor.moveToFirst();
    }

    public boolean resetData() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String queryString =
                "DELETE FROM " + NOTES_TABLE;
        emptyTrash();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.close();
        sqLiteDatabase.close();
        return cursor.moveToFirst();
    }

}
