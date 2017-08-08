/*
 * Copyright (c) 2017 OpenLocate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.openlocate.android.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

final class LogTable {

    private static final String TABLE_NAME = "log";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LOG = "log";
    private static final String QUERY_LIMIT = "500";

    private static final int COLUMN_LOG_INDEX = 1;

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LOG + " TEXT NOT NULL"
            + ");";
    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS ";

    static void createIfRequired(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_SQL + TABLE_NAME);
        createIfRequired(db);
    }

    static void onOpen(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    static void add(SQLiteDatabase database, LogType log) {
        if (database == null || log == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOG, log.getJson().toString());
        database.insert(TABLE_NAME, null, values);
    }

    static List<LogType> popAll(SQLiteDatabase database) {
        if (database == null) {
            return null;
        }

        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null, QUERY_LIMIT);

        if (cursor == null || cursor.isClosed()) {
            return null;
        }

        List<LogType> logs = getLogs(cursor);
        database.delete(TABLE_NAME, null, null);

        return logs;
    }

    static long size(SQLiteDatabase database) {
        if (database == null) {
            return 0;
        }

        return DatabaseUtils.queryNumEntries(database, TABLE_NAME);
    }

    private static List<LogType> getLogs(Cursor cursor) {
        List<LogType> logs = null;

        if (cursor.moveToFirst()) {
            logs = new ArrayList<>();
            do {
                if (cursor.isClosed()) {
                    break;
                }

                LogType log = new OpenLocateLog(cursor.getString(COLUMN_LOG_INDEX));
                logs.add(log);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return logs;
    }
}
