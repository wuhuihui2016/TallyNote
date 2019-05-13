package com.fengyang.tallynote.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.fengyang.tallynote.model.DayNote;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DAY_NOTE".
*/
public class DayNoteDao extends AbstractDao<DayNote, Void> {

    public static final String TABLENAME = "DAY_NOTE";

    /**
     * Properties of entity DayNote.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UseType = new Property(0, int.class, "useType", false, "USE_TYPE");
        public final static Property Money = new Property(1, String.class, "money", false, "MONEY");
        public final static Property Remark = new Property(2, String.class, "remark", false, "REMARK");
        public final static Property Time = new Property(3, String.class, "time", false, "TIME");
        public final static Property Duration = new Property(4, String.class, "duration", false, "DURATION");
    }


    public DayNoteDao(DaoConfig config) {
        super(config);
    }
    
    public DayNoteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DAY_NOTE\" (" + //
                "\"USE_TYPE\" INTEGER NOT NULL ," + // 0: useType
                "\"MONEY\" TEXT," + // 1: money
                "\"REMARK\" TEXT," + // 2: remark
                "\"TIME\" TEXT," + // 3: time
                "\"DURATION\" TEXT);"); // 4: duration
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DAY_NOTE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DayNote entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getUseType());
 
        String money = entity.getMoney();
        if (money != null) {
            stmt.bindString(2, money);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(3, remark);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(4, time);
        }
 
        String duration = entity.getDuration();
        if (duration != null) {
            stmt.bindString(5, duration);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DayNote entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getUseType());
 
        String money = entity.getMoney();
        if (money != null) {
            stmt.bindString(2, money);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(3, remark);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(4, time);
        }
 
        String duration = entity.getDuration();
        if (duration != null) {
            stmt.bindString(5, duration);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public DayNote readEntity(Cursor cursor, int offset) {
        DayNote entity = new DayNote( //
            cursor.getInt(offset + 0), // useType
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // money
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // remark
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // duration
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DayNote entity, int offset) {
        entity.setUseType(cursor.getInt(offset + 0));
        entity.setMoney(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRemark(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDuration(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(DayNote entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(DayNote entity) {
        return null;
    }

    @Override
    public boolean hasKey(DayNote entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
