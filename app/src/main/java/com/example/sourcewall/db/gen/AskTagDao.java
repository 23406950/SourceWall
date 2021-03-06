package com.example.sourcewall.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.example.sourcewall.db.gen.AskTag;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ASK_TAG.
*/
public class AskTagDao extends AbstractDao<AskTag, Long> {

    public static final String TABLENAME = "ASK_TAG";

    /**
     * Properties of entity AskTag.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Section = new Property(1, int.class, "section", false, "SECTION");
        public final static Property Type = new Property(2, int.class, "type", false, "TYPE");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Value = new Property(4, String.class, "value", false, "VALUE");
        public final static Property Selected = new Property(5, boolean.class, "selected", false, "SELECTED");
        public final static Property Order = new Property(6, int.class, "order", false, "ORDER");
    };


    public AskTagDao(DaoConfig config) {
        super(config);
    }
    
    public AskTagDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ASK_TAG' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'SECTION' INTEGER NOT NULL ," + // 1: section
                "'TYPE' INTEGER NOT NULL ," + // 2: type
                "'NAME' TEXT NOT NULL ," + // 3: name
                "'VALUE' TEXT," + // 4: value
                "'SELECTED' INTEGER NOT NULL ," + // 5: selected
                "'ORDER' INTEGER NOT NULL );"); // 6: order
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ASK_TAG'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AskTag entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSection());
        stmt.bindLong(3, entity.getType());
        stmt.bindString(4, entity.getName());
 
        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(5, value);
        }
        stmt.bindLong(6, entity.getSelected() ? 1l: 0l);
        stmt.bindLong(7, entity.getOrder());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AskTag readEntity(Cursor cursor, int offset) {
        AskTag entity = new AskTag( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // section
            cursor.getInt(offset + 2), // type
            cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // value
            cursor.getShort(offset + 5) != 0, // selected
            cursor.getInt(offset + 6) // order
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AskTag entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSection(cursor.getInt(offset + 1));
        entity.setType(cursor.getInt(offset + 2));
        entity.setName(cursor.getString(offset + 3));
        entity.setValue(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSelected(cursor.getShort(offset + 5) != 0);
        entity.setOrder(cursor.getInt(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AskTag entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(AskTag entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
