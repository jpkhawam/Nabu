package com.jpkhawam.nabu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String NOTES_TABLE = "NOTES_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOTE_TITLE = "NOTE_TITLE";
    public static final String COLUMN_NOTE_CONTENT = "NOTE_CONTENT";
    public static final String COLUMN_DATE_CREATED = "DATE_CREATED";
    public static final String COLUMN_DATE_EDITED = "DATE_EDITED";
    public static final String COLUMN_IN_TRASH = "IN_TRASH";
    public static final String COLUMN_IN_ARCHIVE = "IN_ARCHIVE";
    public static final String COLUMN_DATE_SENT_TO_TRASH = "DATE_SENT_TO_TRASH";

    /*
            long noteIdentifier = cursor.getLong(0);
            String noteTitle = cursor.getString(1);
            String noteContent = cursor.getString(2);
            boolean inTrash = cursor.getInt(3) != 0;
            boolean inArchive = cursor.getInt(4) != 0;
            LocalDateTime dateCreated = LocalDateTime.parse(cursor.getString(5), dateTimeFormatter);
            LocalDateTime dateEdited = LocalDateTime.parse(cursor.getString(6), dateTimeFormatter);
            LocalDateTime dateSentToTrash = LocalDateTime.parse(cursor.getString(7), dateTimeFormatter);
     */

    Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "notes.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createNotesTableStatement = "CREATE TABLE " + NOTES_TABLE + " " + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + COLUMN_NOTE_TITLE + " TEXT, " + COLUMN_NOTE_CONTENT + " TEXT, " + COLUMN_IN_TRASH + " BOOLEAN NOT NULL," + COLUMN_IN_ARCHIVE + " BOOLEAN NOT NULL," + COLUMN_DATE_CREATED + " TEXT NOT NULL, " + COLUMN_DATE_EDITED + " TEXT NOT NULL, " + COLUMN_DATE_SENT_TO_TRASH + " TEXT)";
        sqLiteDatabase.execSQL(createNotesTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     * @return a list of the notes in the notes_table in the database
     */
    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + COLUMN_IN_TRASH + " = 0 AND " + COLUMN_IN_ARCHIVE + " = 0";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        boolean hasNext = cursor.moveToFirst();
        while (hasNext) {
            long noteIdentifier = cursor.getLong(0);
            String noteTitle = cursor.getString(1);
            String noteContent = cursor.getString(2);
            LocalDateTime dateCreated = LocalDateTime.parse(cursor.getString(5), dateTimeFormatter);
            LocalDateTime dateEdited = LocalDateTime.parse(cursor.getString(6), dateTimeFormatter);

            Note currentNote = new Note(noteIdentifier, noteTitle, noteContent, dateCreated, dateEdited);
            notes.add(currentNote);
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return notes;
    }

    /**
     * @return a list of the notes in the trash
     */
    public ArrayList<Note> getAllNotesFromTrash() {
        ArrayList<Note> notes = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + COLUMN_IN_TRASH + " = 1 AND " + COLUMN_IN_ARCHIVE + " = 0";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        boolean hasNext = cursor.moveToFirst();
        while (hasNext) {
            long noteIdentifier = cursor.getLong(0);
            String noteTitle = cursor.getString(1);
            String noteContent = cursor.getString(2);
            LocalDateTime dateCreated = LocalDateTime.parse(cursor.getString(5), dateTimeFormatter);
            LocalDateTime dateEdited = LocalDateTime.parse(cursor.getString(6), dateTimeFormatter);
            // LocalDateTime dateSentToTrash = LocalDateTime.parse(cursor.getString(7), dateTimeFormatter);
            // this is where you check if you should just delete the note instead.
            // you could also have a setting to turn it off
            // if (AppSettings.autoDeleteSet()) { check() }
            Note currentNote = new Note(noteIdentifier, noteTitle, noteContent, dateCreated, dateEdited);
            notes.add(currentNote);
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return notes;
    }

    /**
     * @return a list of the notes in the archive_table in the database
     */
    public ArrayList<Note> getAllNotesFromArchive() {
        ArrayList<Note> notes = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + COLUMN_IN_TRASH + " = 0 AND " + COLUMN_IN_ARCHIVE + " = 1";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        boolean hasNext = cursor.moveToFirst();
        while (hasNext) {
            long noteIdentifier = cursor.getLong(0);
            String noteTitle = cursor.getString(1);
            String noteContent = cursor.getString(2);
            LocalDateTime dateCreated = LocalDateTime.parse(cursor.getString(5), dateTimeFormatter);
            LocalDateTime dateEdited = LocalDateTime.parse(cursor.getString(6), dateTimeFormatter);
            Note currentNote = new Note(noteIdentifier, noteTitle, noteContent, dateCreated, dateEdited);
            notes.add(currentNote);
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return notes;
    }

    /**
     * INSERTS A NEW NOTE INTO THE DATABASE
     *
     * @param note to be inserted
     * @return identifier of the inserted note
     */
    public long addNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);

        final long insert = sqLiteDatabase.insert(NOTES_TABLE, null, contentValues);
        String queryString = "SELECT " + COLUMN_ID + " FROM " + NOTES_TABLE + " WHERE rowid = " + insert;
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.moveToFirst();
        final long noteIdentifier = cursor.getLong(0);
        sqLiteDatabase.close();
        cursor.close();

        if (insert == -1) {
            return -1;
        }
        return noteIdentifier;
    }

    /**
     * RETURNS A NOTE OBJECT FROM THE DATABASE
     *
     * @param noteIdentifier noteIdentifier of desired note
     * @return resulting note object
     */
    public Note getNote(long noteIdentifier) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + COLUMN_ID + " = " + noteIdentifier;

        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.moveToFirst();
        String noteTitle = cursor.getString(1);
        String noteContent = cursor.getString(2);
        LocalDateTime dateCreated = LocalDateTime.parse(cursor.getString(5), dateTimeFormatter);
        LocalDateTime dateEdited = LocalDateTime.parse(cursor.getString(6), dateTimeFormatter);
        cursor.close();
        sqLiteDatabase.close();
        return new Note(noteIdentifier, noteTitle, noteContent, dateCreated, dateEdited);
    }

    /**
     * UPDATES A NOTE'S TITLE AND CONTENT
     *
     * @param note to update
     */
    public void updateNote(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(note.getNoteIdentifier())});
        sqLiteDatabase.close();
    }

    /**
     * SEND A NOTE FROM NOTES_TABLE TO TRASH
     *
     * @param note note to be sent to trash
     */
    public void deleteNote(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 1);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);
        contentValues.put(COLUMN_DATE_SENT_TO_TRASH, String.valueOf(LocalDateTime.now()));
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(note.getNoteIdentifier())});
        sqLiteDatabase.close();
    }

    /**
     * SEND A NOTE FROM NOTES_TABLE TO TRASH
     *
     * @param noteIdentifier of note to be sent to trash
     */
    public void deleteNote(long noteIdentifier) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 1);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(noteIdentifier)});
        sqLiteDatabase.close();
    }

    /**
     * DELETES A NOTE FROM TRASH_TABLE
     *
     * @param note note to be deleted
     */
    public void deleteNoteFromTrash(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE, "ID = ?", new String[]{String.valueOf(note.getNoteIdentifier())});
        sqLiteDatabase.close();
    }

    /**
     * Restore a note from TRASH OR ARCHIVE back to NOTES
     *
     * @param note note to be restored
     */
    public void restoreNote(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);
        contentValues.put(COLUMN_DATE_SENT_TO_TRASH, String.valueOf(LocalDateTime.now()));
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(note.getNoteIdentifier())});
        sqLiteDatabase.close();
    }

    /**
     * Restore a note from TRASH OR ARCHIVE back to NOTES
     *
     * @param noteIdentifier id of note to be restored
     */
    public void restoreNote(long noteIdentifier) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);
        contentValues.put(COLUMN_DATE_SENT_TO_TRASH, String.valueOf(LocalDateTime.now()));
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(noteIdentifier)});
        sqLiteDatabase.close();
    }

    /**
     * MARK A NOTE AS ARCHIVED
     *
     * @param note note to be archived
     */
    public void archiveNote(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 1);
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(note.getNoteIdentifier())});
        sqLiteDatabase.close();
    }

    /**
     * MARK A NOTE AS ARCHIVED
     *
     * @param noteIdentifier note identifier of note to be archived
     */
    public void archiveNote(long noteIdentifier) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 1);
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(noteIdentifier)});
        sqLiteDatabase.close();
    }

    /**
     * EXPORTS ALL NOTES INSIDE ARCHIVE_TABLE BACK TO NOTES_TABLE
     */
    public void exportArchive() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);
        sqLiteDatabase.update(NOTES_TABLE, contentValues, COLUMN_IN_ARCHIVE + " = ?", new String[]{String.valueOf(1)});
        sqLiteDatabase.close();
    }

    /**
     * Restore a note from ARCHIVE back to NOTES
     *
     * @param note note to be restored
     */
    public void unarchiveNote(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(note.getNoteIdentifier())});
        sqLiteDatabase.close();
    }

    /**
     * Restore a note from ARCHIVE back to NOTES
     *
     * @param noteIdentifier of note to be restored
     */
    public void unarchiveNote(long noteIdentifier) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IN_TRASH, 0);
        contentValues.put(COLUMN_IN_ARCHIVE, 0);
        sqLiteDatabase.update(NOTES_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(noteIdentifier)});
        sqLiteDatabase.close();
    }

    public boolean isInTrash(Note note) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String queryString = "SELECT " + COLUMN_IN_TRASH + " FROM " + NOTES_TABLE + " WHERE " + COLUMN_ID + " = " + note.getNoteIdentifier();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.moveToFirst();
        boolean inTrash = cursor.getInt(0) != 0;
        sqLiteDatabase.close();
        cursor.close();
        return inTrash;
    }

    public boolean isInArchive(Note note) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String queryString = "SELECT " + COLUMN_IN_ARCHIVE + " FROM " + NOTES_TABLE + " WHERE " + COLUMN_ID + " = " + note.getNoteIdentifier();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.moveToFirst();
        boolean inArchive = cursor.getInt(0) != 0;
        sqLiteDatabase.close();
        cursor.close();
        return inArchive;
    }

    /**
     * DELETES ALL NOTES FROM TRASH
     */
    public void emptyTrash() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE, COLUMN_IN_TRASH + " = ?", new String[]{String.valueOf(1)});
        sqLiteDatabase.close();
    }

    /**
     * DELETES ALL NOTE FROM ARCHIVE
     */
    public void emptyArchive() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE, COLUMN_IN_ARCHIVE + " = ?", new String[]{String.valueOf(1)});
        sqLiteDatabase.close();
    }

    /**
     * RESETS APP DATA, DELETING ALL DATABASE CONTENT
     */
    public void resetData() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE, COLUMN_IN_ARCHIVE + " = ?", new String[]{String.valueOf(0)});
        sqLiteDatabase.delete(NOTES_TABLE, COLUMN_IN_ARCHIVE + " = ?", new String[]{String.valueOf(1)});
        sqLiteDatabase.close();
    }
}
