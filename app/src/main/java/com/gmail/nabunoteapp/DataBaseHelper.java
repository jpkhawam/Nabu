package com.gmail.nabunoteapp;

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

        // TODO: on app launch, check which notes are due and delete them
        String createTrashTableStatement =
                "CREATE TABLE " + TRASH_TABLE + " " +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOTE_TITLE + " TEXT, " +
                        COLUMN_NOTE_CONTENT + " TEXT, " +
                        COLUMN_DATE_CREATED + " TEXT, " +
                        COLUMN_DATE_EDITED + " TEXT, " +
                        COLUMN_BACKGROUND_COLOR + " INTEGER, " +
                        COLUMN_DATE_SENT_TO_TRASH + " TEXT)" + ";";
        sqLiteDatabase.execSQL(createTrashTableStatement);

        String createArchiveTableStatement =
                "CREATE TABLE " + ARCHIVE_TABLE + " " +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOTE_TITLE + " TEXT, " +
                        COLUMN_NOTE_CONTENT + " TEXT, " +
                        COLUMN_DATE_CREATED + " TEXT, " +
                        COLUMN_DATE_EDITED + " TEXT, " +
                        COLUMN_BACKGROUND_COLOR + " INTEGER, " +
                        COLUMN_DATE_ARCHIVED + " TEXT)" + ";";
        sqLiteDatabase.execSQL(createArchiveTableStatement);
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
     *
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
     *
     * @return RETURNS NOTE IDENTIFIER IF SUCCESSFUL, -1 IF UNSUCCESSFUL
     */
    public long createNewNote() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Note note = new Note("", "", LocalDateTime.now(), LocalDateTime.now(), 0);
        return addNote(note);
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
        String dateCreatedString = cursor.getString(3);
        String dateEditedString = cursor.getString(4);
        int backgroundColor = cursor.getInt(5);
        LocalDateTime dateCreated = LocalDateTime.parse(dateCreatedString, dateTimeFormatter);
        LocalDateTime dateEdited = LocalDateTime.parse(dateEditedString, dateTimeFormatter);

        cursor.close();
        sqLiteDatabase.close();
        return new Note(noteIdentifier, noteTitle, noteContent, dateCreated, dateEdited, backgroundColor);
    }

    /**
     * UPDATES A NOTE'S TITLE AND CONTENT
     *
     * @param note New state of note
     * @return true when affects one row only
     */
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
                "ID = ?", new String[]{String.valueOf(note.getNoteIdentifier())});
        sqLiteDatabase.close();
        return numberOfRowsAffected == 1;
    }

    /**
     * SEND A NOTE FROM NOTES_TABLE TO TRASH
     *
     * @param note note to be sent to trash
     * @return returns note ID inside trash
     */
    public long deleteNote(Note note) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(NOTES_TABLE, "ID = ?",
                new String[]{String.valueOf(note.getNoteIdentifier())});

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());
        contentValues.put(COLUMN_DATE_SENT_TO_TRASH, dateTimeFormatter.format(LocalDateTime.now()));

        final long insert = sqLiteDatabase.insert(TRASH_TABLE, null, contentValues);
        String queryString = "SELECT " + COLUMN_ID + " FROM " + TRASH_TABLE + " WHERE rowid = " + insert;
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
     * @return a list of the notes in the trash_table in the database
     */
    public ArrayList<Note> getAllNotesFromTrash() {
        ArrayList<Note> allNotes = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + TRASH_TABLE;
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
            String dateSentToTrashString = cursor.getString(6);
            LocalDateTime dateCreated = LocalDateTime.parse(dateCreatedString, dateTimeFormatter);
            LocalDateTime dateEdited = LocalDateTime.parse(dateEditedString, dateTimeFormatter);
            LocalDateTime dateSentToTrash = LocalDateTime.parse(dateSentToTrashString, dateTimeFormatter);

            Note currentNote = new Note(noteIdentifier, noteTitle, noteContent,
                    dateCreated, dateEdited, backgroundColor, dateSentToTrash);

            allNotes.add(currentNote);
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return allNotes;
    }

    /**
     * DELETES A NOTE FROM TRASH_TABLE
     *
     * @param note note to be deleted
     */
    public void deleteNotePermanently(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TRASH_TABLE, "ID = ?",
                new String[]{String.valueOf(note.getNoteIdentifier())});
    }

    /**
     * Restore a note from TRASH back to NOTES
     *
     * @param note note to be restored
     * @return returns note identifier
     */
    public long restoreNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        deleteNotePermanently(note);
        return addNote(note);
    }

    /**
     * SEND A NOTE FROM NOTES_TABLE TO ARCHIVE_TABLE
     *
     * @param note note to be archived
     * @return returns note ID inside archive
     */
    public long archiveNote(Note note) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(NOTES_TABLE, "ID = ?",
                new String[]{String.valueOf(note.getNoteIdentifier())});

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());
        contentValues.put(COLUMN_DATE_ARCHIVED, dateTimeFormatter.format(LocalDateTime.now()));

        final long insert = sqLiteDatabase.insert(ARCHIVE_TABLE, null, contentValues);
        String queryString = "SELECT " + COLUMN_ID + " FROM " + ARCHIVE_TABLE + " WHERE rowid = " + insert;
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
     * @return a list of the notes in the archive_table in the database
     */
    public ArrayList<Note> getAllNotesFromArchive() {
        ArrayList<Note> archivedNotes = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + ARCHIVE_TABLE;
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
            String dateArchivedString = cursor.getString(6);
            LocalDateTime dateCreated = LocalDateTime.parse(dateCreatedString, dateTimeFormatter);
            LocalDateTime dateEdited = LocalDateTime.parse(dateEditedString, dateTimeFormatter);
            LocalDateTime dateArchived = LocalDateTime.parse(dateArchivedString, dateTimeFormatter);

            Note currentNote = new Note(noteIdentifier, noteTitle, noteContent,
                    dateCreated, dateEdited, backgroundColor, dateArchived);

            archivedNotes.add(currentNote);
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return archivedNotes;
    }

    /**
     * SENDS A NOTE FROM ARCHIVE_TABLE TO TRASH_TABLE
     *
     * @param note note to be deleted
     * @return note identifier in trash
     */
    public long deleteNoteFromArchive(Note note) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(ARCHIVE_TABLE, "ID = ?",
                new String[]{String.valueOf(note.getNoteIdentifier())});

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());
        contentValues.put(COLUMN_DATE_CREATED, note.getDateCreated());
        contentValues.put(COLUMN_DATE_EDITED, note.getDateEdited());
        contentValues.put(COLUMN_BACKGROUND_COLOR, note.getBackgroundColor());
        contentValues.put(COLUMN_DATE_SENT_TO_TRASH, dateTimeFormatter.format(LocalDateTime.now()));

        final long insert = sqLiteDatabase.insert(TRASH_TABLE, null, contentValues);
        String queryString = "SELECT " + COLUMN_ID + " FROM " + TRASH_TABLE + " WHERE rowid = " + insert;
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
     * EXPORTS ALL NOTES INSIDE ARCHIVE_TABLE BACK TO NOTES_TABLE
     */
    public void exportArchive() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + ARCHIVE_TABLE;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
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

            addNote(currentNote);
            hasNext = cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    /**
     * Restore a note from ARCHIVE back to NOTES
     *
     * @param note note to be restored
     * @return returns note identifier
     */
    public long unArchiveNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(ARCHIVE_TABLE, "ID = ?",
                new String[]{String.valueOf(note.getNoteIdentifier())});
        return addNote(note);
    }

    /**
     * Restore a note from ARCHIVE back to NOTES
     *
     * @param archivedNoteIdentifier noteIdentifier of the note to be restored
     * @return returns note identifier
     */
    public long unArchiveNote(long archivedNoteIdentifier) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String queryString = "SELECT * FROM " + ARCHIVE_TABLE + " WHERE " + COLUMN_ID + " = "
                + archivedNoteIdentifier;
        final Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);

        boolean notEmpty = cursor.moveToFirst();
        if (notEmpty) {
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

            sqLiteDatabase.delete(ARCHIVE_TABLE, "ID = ?",
                    new String[]{String.valueOf(noteIdentifier)});

            return addNote(currentNote);
        }
        return -1;
    }

    /**
     * DELETES ALL NOTES FROM TRASH
     */
    public void emptyTrash() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TRASH_TABLE, null, null);
        sqLiteDatabase.close();
    }

    /**
     * DELETES ALL NOTE FROM ARCHIVE
     */
    public void emptyArchive() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(ARCHIVE_TABLE, null, null);
        sqLiteDatabase.close();
    }

    /**
     * RESETS APP DATA, DELETING ALL DATABASE CONTENT
     */
    public void resetData() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE, null, null);
        emptyTrash();
        emptyArchive();
        sqLiteDatabase.close();
    }

}
