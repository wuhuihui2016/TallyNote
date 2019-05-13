package com.fengyang.tallynote.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.fengyang.tallynote.model.MemoNote;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MEMO_NOTE".
*/
public class MemoNoteDao extends AbstractDao<MemoNote, Void> {

    public static final String TABLENAME = "MEMO_NOTE";

    /**
     * Properties of entity MemoNote.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Content = new Property(0, String.class, "content", false, "CONTENT");
        public final static Property Status = new Property(1, int.class, "status", false, "STATUS");
        public final static Property Time = new Property(2, String.class, "time", false, "TIME");
    }


    public MemoNoteDao(DaoConfig config) {
        super(config);
    }
    
    public MemoNoteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MEMO_NOTE\" (" + //
                "\"CONTENT\" TEXT," + // 0: content
                "\"STATUS\" INTEGER NOT NULL ," + // 1: status
                "\"TIME\" TEXT);"); // 2: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEMO_NOTE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MemoNote entity) {
        stmt.clearBindings();
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(1, content);
        }
        stmt.bindLong(2, entity.getStatus());
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(3, time);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MemoNote entity) {
        stmt.clearBindings();
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(1, content);
        }
        stmt.bindLong(2, entity.getStatus());
 
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
    public MemoNote readEntity(Cursor cursor, int offset) {
        MemoNote entity = new MemoNote( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // content
            cursor.getInt(offset + 1), // status
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MemoNote entity, int offset) {
        entity.setContent(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setStatus(cursor.getInt(offset + 1));
        entity.setTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(MemoNote entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(MemoNote entity) {
        return null;
    }

    @Override
    public boolean hasKey(MemoNote entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}