package com.example.quicknotes;

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
    Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "notes.db", null, 1);
        this.context = context;
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
        sqLiteDatabase.execSQL(createNotesTableStatement);

//        // on app launch, check which notes are due and delete them
//        String createTrashTableStatement =
//                "CREATE TABLE " + TRASH_TABLE + " " +
//                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                        COLUMN_NOTE_TITLE + " TEXT, " +
//                        COLUMN_NOTE_CONTENT + " TEXT, " +
//                        COLUMN_DATE_CREATED + " TEXT, " +
//                        COLUMN_DATE_EDITED + " TEXT, " +
//                        COLUMN_BACKGROUND_COLOR + " INTEGER, " +
//                        COLUMN_DATE_SENT_TO_TRASH + " TEXT)" + ";";
//        sqLiteDatabase.execSQL(createTrashTableStatement);
//
//        String createArchiveTableStatement =
//                "CREATE TABLE " + ARCHIVE_TABLE + " " +
//                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                        COLUMN_NOTE_TITLE + " TEXT, " +
//                        COLUMN_NOTE_CONTENT + " TEXT, " +
//                        COLUMN_DATE_CREATED + " TEXT, " +
//                        COLUMN_DATE_EDITED + " TEXT, " +
//                        COLUMN_BACKGROUND_COLOR + " INTEGER, " +
//                        COLUMN_DATE_ARCHIVED + " TEXT)" + ";";
//        sqLiteDatabase.execSQL(createArchiveTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * @return a list of the notes in the notes_table in the database
     */
    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> allNotes = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + NOTES_TABLE;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        boolean hasNext = cursor.moveToFirst();
        while (hasNext) {
            long noteIdentifier = cursor.getLong(0);
            String noteTitle = cursor.getString(1);
            String noteContent = cursor.getString(2);
            String dateCreatedString = cursor.getString(3);
            String dateEditedString = cursor.getString(4);
            int backgroundColor = cursor.getInt(5);
            LocalDateTime dateCreated = LocalDateTime.parse(dateCreatedString, dateTimeFormatter);
            LocalDateTime dateEdited = LocalDateTime.parse(dateEditedString, dateTimeFormatter);

            Note currentNote = new Note(noteIdentifier, noteTitle, noteContent,
                    dateCreated, dateEdited, backgroundColor);

            allNotes.add(currentNote);
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return allNotes;
    }

    /**
     * INSERTS A NOTE INTO THE DATABASE
     * @param note Note to be inserted
     * @return identifier of the inserted note
     */
    public long addNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());

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
     * CREATES A NEW EMPTY NOTE IN THE DATABASE
     * @return RETURNS NOTE IDENTIFIER IF SUCCESSFUL, -1 IF UNSUCCESSFUL
     */
    public long createNewNote() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Note note = new Note("", "", LocalDateTime.now(), LocalDateTime.now(), 0);
        return addNote(note);
    }

    // THIS ONE WORKS NOW
    public Note getNote(long noteIdentifier) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String queryString = "SELECT * FROM " + NOTES_TABLE + " WHERE " + COLUMN_ID + " = " + noteIdentifier;

        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        cursor.moveToFirst();
        String noteTitle = cursor.getString(1);
        String noteContent = cursor.getString(2);
        String dateCreatedString = cursor.getString(3);
        String dateEditedString = cursor.getString(4);
        int backgroundColor = cursor.getInt(5);
        LocalDateTime dateCreated = LocalDateTime.parse(dateCreatedString, dateTimeFormatter);
        LocalDateTime dateEdited = LocalDateTime.parse(dateEditedString, dateTimeFormatter);

        cursor.close();
        sqLiteDatabase.close();
        return new Note(noteIdentifier, noteTitle, noteContent, dateCreated, dateEdited, backgroundColor);
    }

    // THIS ALSO WORKS NOW
    public boolean updateNote(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());
        contentValues.put(COLUMN_ID, note.getNoteIdentifier());
        int numberOfRowsAffected = sqLiteDatabase.update(NOTES_TABLE, contentValues,
                "ID = ?", new String[] { String.valueOf(note.getNoteIdentifier()) });
        sqLiteDatabase.close();
        return numberOfRowsAffected == 1;
    }

//    // TODO: SHOW "NOTE DELETED" AND GIVE OPTION TO UNDO
    // THIS WORKS NOW
    public boolean deleteNote(Note note) {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(NOTES_TABLE, "ID = ?",
                new String[] { String.valueOf(note.getNoteIdentifier()) });

//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
//        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
//        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
//        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
//        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());
//        contentValues.put(COLUMN_DATE_SENT_TO_TRASH, dateTimeFormatter.format(LocalDateTime.now()));

//        final long insert = sqLiteDatabase.insert(TRASH_TABLE, null, contentValues);
//
//        boolean success = cursor.moveToFirst();
//        cursor.close();
//        sqLiteDatabase.close();
//        return success && (insert != -1);

        return true;
    }

//    public boolean deleteNotePermanently(Note note) {
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        String queryString = "DELETE FROM " + TRASH_TABLE + "WHERE " + COLUMN_ID +
//                " = " + note.getNoteIdentifier() + ";";
//        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
//        boolean success = cursor.moveToFirst();
//        cursor.close();
//        sqLiteDatabase.close();
//        return success;
//    }

//    public boolean restoreNote(Note note) {
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//
//        String queryString = "DELETE FROM " + TRASH_TABLE + "WHERE " + COLUMN_ID +
//                " = " + note.getNoteIdentifier() + ";";
//        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
//        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
//        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
//        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
//        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());
//
//        final long insert = sqLiteDatabase.insert(NOTES_TABLE, null, contentValues);
//
//        boolean success = cursor.moveToFirst();
//        cursor.close();
//        sqLiteDatabase.close();
//        return success && (insert != -1);
//    }

//    public boolean archiveNote(Note note) {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        String queryString = "DELETE FROM " + NOTES_TABLE + "WHERE " + COLUMN_ID +
//                " = " + note.getNoteIdentifier() + ";";
//        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
//        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
//        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
//        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
//        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());
//        contentValues.put(COLUMN_DATE_ARCHIVED, dateTimeFormatter.format(LocalDateTime.now()));
//
//        final long insert = sqLiteDatabase.insert(ARCHIVE_TABLE, null, contentValues);
//
//        boolean success = cursor.moveToFirst();
//        cursor.close();
//        sqLiteDatabase.close();
//        return success && (insert != -1);
//    }

//    public boolean exportArchive() {
//        String queryString = "SELECT * FROM " + ARCHIVE_TABLE + ";";
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
//
//        boolean noErrorYet = true;
//        boolean hasNext = cursor.moveToFirst();
//        while (hasNext) {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(COLUMN_ID, cursor.getLong(0));
//            contentValues.put(COLUMN_NOTE_TITLE, cursor.getString(1));
//            contentValues.put(COLUMN_NOTE_CONTENT, cursor.getString(2));
//            contentValues.put(COLUMN_DATE_CREATED, cursor.getString(3));
//            contentValues.put(COLUMN_DATE_EDITED, cursor.getString(4));
//            contentValues.put(COLUMN_BACKGROUND_COLOR, cursor.getInt(5));
//
//            final long insert = sqLiteDatabase.insert(NOTES_TABLE, null, contentValues);
//            if (noErrorYet)
//                noErrorYet = (insert != -1);
//            hasNext = cursor.moveToNext();
//        }
//        cursor.close();
//        sqLiteDatabase.close();
//        return noErrorYet;
//    }

//    public boolean emptyTrash() {
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        String queryString = "DELETE FROM " + TRASH_TABLE + ";";
//        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
//        boolean success = cursor.moveToFirst();
//        cursor.close();
//        sqLiteDatabase.close();
//        return success;
//    }

//    public boolean emptyArchive() {
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        String queryString = "DELETE FROM " + ARCHIVE_TABLE + ";";
//        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
//        boolean success = cursor.moveToFirst();
//        cursor.close();
//        sqLiteDatabase.close();
//        return success;
//    }

    // THIS WORKS
    public void resetData() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE, null, null);
        // do the same for trash and archive
        sqLiteDatabase.close();
    }

}
