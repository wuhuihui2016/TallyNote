package com.fengyang.tallynote.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTE_PAD".
*/
public class NotePadDao extends AbstractDao<NotePad, Void> {

    public static final String TABLENAME = "NOTE_PAD";

    /**
     * Properties of entity NotePad.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Tag = new Property(0, int.class, "tag", false, "TAG");
        public final static Property Words = new Property(1, String.class, "words", false, "WORDS");
        public final static Property Time = new Property(2, String.class, "time", false, "TIME");
    }


    public NotePadDao(DaoConfig config) {
        super(config);
    }
    
    public NotePadDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTE_PAD\" (" + //
                "\"TAG\" INTEGER NOT NULL ," + // 0: tag
                "\"WORDS\" TEXT," + // 1: words
                "\"TIME\" TEXT);"); // 2: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTE_PAD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NotePad entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getTag());
 
        String words = entity.getWords();
        if (words != null) {
            stmt.bindString(2, words);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(3, time);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NotePad entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getTag());
 
        String words = entity.getWords();
        if (words != null) {
            stmt.bindString(2, words);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(3, time);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public NotePad readEntity(Cursor cursor, int offset) {
        NotePad entity = new NotePad( //
            cursor.getInt(offset + 0), // tag
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // words
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NotePad entity, int offset) {
        entity.setTag(cursor.getInt(offset + 0));
        entity.setWords(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(NotePad entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(NotePad entity) {
        return null;
    }

    @Override
    public boolean hasKey(NotePad entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
