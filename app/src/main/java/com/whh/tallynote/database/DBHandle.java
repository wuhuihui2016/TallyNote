package com.whh.tallynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.LogUtils;

import java.util.ArrayList;

/**
 * 数据库操作基类
 * Created by wuhuihui on 2020/6/10.
 */
public class DBHandle {

    private DBHelper mOpenHelper;
    private static DBHandle instance;

    public static DBHandle getInstance(Context mContext) {
        if (null == instance)
            instance = new DBHandle(mContext.getApplicationContext());
        return instance;
    }

    public DBHandle(Context context) {
        mOpenHelper = DBHelper.getInstance(context.getApplicationContext());
    }

    /**
     * 根据条件查询数据
     *
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public synchronized Cursor query(String table, String[] columns, String selection,
                                     String[] selectionArgs, String groupBy, String having,
                                     String orderBy) {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        if (c != null && db.isOpen()) {
            return c;
        } else {
            return null;
        }
    }

    /**
     * 根据条件查询数据
     *
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public synchronized Cursor query(String table, String[] columns, String selection,
                                     String[] selectionArgs, String groupBy, String having,
                                     String orderBy, String limit) {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return c;
    }

    /**
     * 插入单条数据
     *
     * @param cv ContentValues
     * @return 插入数据库影响数据的条数
     */
    public synchronized int insert(String table, ContentValues cv) {
        if (cv == null) {
            return -1;
        }
        SQLiteDatabase db = null;
        int id = -1;
        try {
            db = mOpenHelper.getWritableDatabase();
            id = (int) db.insert(table, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if(db != null){
//                db.close();
//            }
        }
        return id;
    }

    /**
     * 插入多条数据
     *
     * @param tableName 数据库的表名
     * @param cv        ContentValues 集合
     * @return 是否成功插入，一条失败，集体失败
     */
    public synchronized boolean multiInsert(String tableName, ContentValues[] cv) {
        if (cv == null || cv.length == 0) {
            return false;
        }
        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();
            for (ContentValues value : cv) {
                if (value != null) {
                    id = db.insert(tableName, null, value);
                    if (id < 0) {
                        return false;
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
            }
        }
        return true;
    }

    /**
     * 删除数据
     *
     * @param tableName     数据库的表名
     * @param selection     删除数据列名
     * @param selectionArgs 删除数据的条件
     * @return 删除影响的条数
     */
    public synchronized int delete(String tableName, String selection, String[] selectionArgs) {
        int rownum = 0;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getWritableDatabase();
            rownum = db.delete(tableName, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
//                db.close();
            }
        }
        return rownum;
    }

    /**
     * 更新单条数据
     *
     * @param tableName     数据库的表名
     * @param cv            ContentValues
     * @param selection     删除数据列名
     * @param selectionArgs 删除数据的条件
     * @return 成功更新的條數
     */
    public synchronized int update(String tableName, ContentValues cv, String selection, String[] selectionArgs) {
        if (cv == null || TextUtils.isEmpty(selection)) {
            return 0;
        }
        int result = 0;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getWritableDatabase();
            result = db.update(tableName, cv, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
//                db.close();
            }
        }
        return result;
    }

    /**
     * 更新多条数据
     *
     * @param tableName     数据库的表名
     * @param cv            需要更新的ContentValues集合
     * @param selection     删除数据列名
     * @param selectionArgs 删除数据的条件
     * @return
     */
    public synchronized boolean multiUpdate(String tableName, ContentValues[] cv, String selection, String[] selectionArgs) {
        if (cv == null || cv.length < 1) {
            return false;
        }
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();
            for (ContentValues value : cv) {
                int result = db.update(tableName, value, selection, selectionArgs);
                if (result < 1) {
                    return false;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.endTransaction(); // 处理完成
//                db.close();
            }
        }
        return true;
    }

    /**
     * 查询表rawQuery方法
     *
     * @param sql
     * @param condition
     * @return
     */
    public synchronized int rawQuery(String sql, String condition) {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            if (condition.isEmpty()) {
                c = db.rawQuery(sql, null);
            } else {
                c = db.rawQuery(sql, new String[]{condition});
            }
            c.moveToFirst();
            return (int) c.getLong(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return 0;
    }

    /**
     * 查询表rawQuery方法
     *
     * @param sql
     * @param condition
     * @return
     */
    public synchronized Cursor rawQuery(String sql, String[] condition) {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            c = db.rawQuery(sql, condition);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return c;
    }

    /**
     * 查询表rawQuery方法
     *
     * @param sql
     * @return
     */
    public synchronized Cursor rawQuery(String sql) {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            c = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return c;
    }

    public byte[] bytes;

    /**
     * 查询infoQueue表info的方法
     *
     * @return
     */
    public synchronized ArrayList<byte[]> rawInfoQuery() {
        Cursor sizeCursor = null;
        SQLiteDatabase db = null;
        ArrayList<byte[]> infoList = new ArrayList<>();
        try {
            db = mOpenHelper.getReadableDatabase();
            sizeCursor = db.rawQuery("select length(info)from infoQueue", null);
            while (sizeCursor.moveToNext()) {
                long blobStart = 1;
                long blobLen = 1;
                long blobSize = sizeCursor.getLong(0);
                bytes = blobSize > 0 ? new byte[(int) blobSize] : null;
                while (blobSize > 0) {
                    blobLen = blobSize > 1000000 ? 1000000 : blobSize;
                    blobSize -= blobLen;
                    Cursor blobCursor = db.rawQuery("select substr(info," + blobStart + "," + blobLen + ")from infoQueue where id ='" + (sizeCursor.getPosition() + 1) + "'", null);
                    if (blobCursor.moveToNext()) {
                        byte[] barr = blobCursor.getBlob(0);
                        if (barr != null)
                            System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length - 1);
                    }
                    blobCursor.close();
                    blobStart += blobLen;
                }
                if (bytes != null) {
                    infoList.add(bytes);
                    bytes = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sizeCursor != null) {
                sizeCursor.close();
            }
        }
        return infoList;
    }

    /**
     * 查询infoQueue表info的方法
     *
     * @return
     */
    public synchronized String rawInfoQueryLimit(String type) {
        Cursor sizeCursor = null;
        SQLiteDatabase db = null;
        int num = 0;
        StringBuffer result = new StringBuffer();
        try {
            db = mOpenHelper.getReadableDatabase();
            sizeCursor = db.rawQuery("select length(info) from infoQueue where type =  \"" + type + "\" limit 1 offset 0", null);
            while (sizeCursor.moveToNext()) {
                long blobStart = 1;
                long blobLen = 1;
                long blobSize = sizeCursor.getLong(0);
                LogUtils.e("0621", "blobSize  ======  " + blobSize);
                while (blobSize > 0) {
                    blobLen = blobSize > 1_000_000 ? 1_000_000 : blobSize;
                    blobSize -= blobLen;
                    Cursor blobCursor;
                    blobCursor = db.rawQuery("select substr(info," + blobStart + "," + blobLen + ") from infoQueue  where type = \"" + type + "\" limit 1 offset 0", null);

                    if (blobCursor.moveToNext()) {
                        String string = blobCursor.getString(0);
                        result.append(string);
//                        LogUtils.e("0621", "length  ======  " + length);
//                        byte[] barr = blobCursor.getBlob(0);
//                        LogUtils.e("0621", "barr  ======  " + barr.length);
//                        if (barr != null)
//                            System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length - 1);
                    }
                    blobCursor.close();
                    if (num == 1) {
                        blobStart += blobLen - 1;
                    } else {
                        blobStart += blobLen;
                    }
                }

                if (result != null) {
                    num = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("0621", e.toString());
        } finally {
            if (sizeCursor != null) {
                sizeCursor.close();
            }
        }
        return result.toString();
    }

    /**
     * 获取记录总数
     *
     * @return
     */
    public int getCount4Record(int type) {
        String table = null;
        if (type == ContansUtils.DAY) {
            table = DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE;
        } else if (type == ContansUtils.DAY_HISTORY) {
            table = DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE_HISTORY;
        } else if (type == ContansUtils.MONTH) {
            table = DBCommon.MonthNoteRecordColumns.TABLE_NAME_MONTHNOTE;
        } else if (type == ContansUtils.INCOME) {
            table = DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE;
        } else if (type == ContansUtils.MEMO) {
            table = DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE;
        } else if (type == ContansUtils.NOTEPAD) {
            table = DBCommon.NotePadRecordColumns.TABLE_NAME_NOTEPAD;
        } else if (type == ContansUtils.ALL) {
            getCount4Record(ContansUtils.DAY);
            getCount4Record(ContansUtils.MONTH);
            getCount4Record(ContansUtils.INCOME);
            getCount4Record(ContansUtils.DAY_HISTORY);
            getCount4Record(ContansUtils.MEMO);
            getCount4Record(ContansUtils.NOTEPAD);
        }
        if (table == null) return 0;
        int count = rawQuery("select count(*) from " + table, "");
        LogUtils.e("whh0611", "getCount4Record：table=" + table + ", count=" + count);
        return count;
    }

    /**
     * 清除备忘录表数据
     */
    public void clearTableData(int type) {
        if (type == ContansUtils.DAY) {
            delete(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE, null, null);
        } else if (type == ContansUtils.DAY_HISTORY) {
            delete(DBCommon.DayNoteRecordColumns.TABLE_NAME_DAYNOTE_HISTORY, null, null);
        } else if (type == ContansUtils.MONTH) {
            delete(DBCommon.MonthNoteRecordColumns.TABLE_NAME_MONTHNOTE, null, null);
        } else if (type == ContansUtils.INCOME) {
            delete(DBCommon.IncomeNoteRecordColumns.TABLE_NAME_INCOMENOTE, null, null);
        } else if (type == ContansUtils.MEMO) {
            delete(DBCommon.MemoNoteRecordColumns.TABLE_NAME_MEMONOTE, null, null);
        } else if (type == ContansUtils.NOTEPAD) {
            delete(DBCommon.NotePadRecordColumns.TABLE_NAME_NOTEPAD, null, null);
        } else if (type == ContansUtils.ALL) { //清除所有数据
            clearTableData(ContansUtils.DAY);
            clearTableData(ContansUtils.MONTH);
            clearTableData(ContansUtils.INCOME);
            clearTableData(ContansUtils.DAY_HISTORY);
            clearTableData(ContansUtils.MEMO);
            clearTableData(ContansUtils.NOTEPAD);
        }

    }

}
