/*
 * DIVA Android - Damn Insecure and Vulnerable App for Android
 *
 * Copyright 2016 © Payatu
 * Author: Aseem Jakhar aseem[at]payatu[dot]com
 * Websites: www.payatu.com  www.nullcon.net  www.hardwear.io www.null.co.in
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.chamelalaboratory.chamela.diva;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.text.*;

public class NotesProvider extends ContentProvider {

    // DB stuff
    private SQLiteDatabase mDB;
    private static final String DBNAME = "divanotes.db";
    private static final int DBVERSION = 1;
    private static final String TABLE = "notes";
    private static final String C_ID = "_id";
    public static final String C_TITLE = "title";
    public static final String C_NOTE = "note";

    // Constants for URI matcher
    private static final int PATH_TABLE = 1;
    private static final int PATH_ID = 2;
    private static final String AUTHORITY = "com.chamelalaboratory.chamela.diva.provider.notesprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, TABLE, PATH_TABLE);
        uriMatcher.addURI(AUTHORITY, TABLE + "/#", PATH_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case PATH_TABLE:
                count = mDB.delete(TABLE, selection, selectionArgs);
                break;
            case PATH_ID:
                String id = uri.getLastPathSegment();
                count = mDB.delete(TABLE, C_ID + " = ?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PATH_TABLE:
                return "vnd.android.cursor.dir/vnd.chamelalaboratory.chamela.diva.notes";
            case PATH_ID:
                return "vnd.android.cursor.item/vnd.chamelalaboratory.chamela.diva.notes";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = mDB.insert(TABLE, null, values);
        if (id > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        mDB = dbHelper.getWritableDatabase();
        return mDB != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE);
        switch (uriMatcher.match(uri)) {
            case PATH_TABLE:
                break;
            case PATH_ID:
                queryBuilder.appendWhere(C_ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = C_TITLE;
        }
        Cursor cursor = queryBuilder.query(mDB, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case PATH_TABLE:
                count = mDB.update(TABLE, values, selection, selectionArgs);
                break;
            case PATH_ID:
                String id = uri.getLastPathSegment();
                count = mDB.update(TABLE, values, C_ID + " = ?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private static class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context context) {
            super(context, DBNAME, null, DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE + "(" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_TITLE + " TEXT NOT NULL, " + C_NOTE + " TEXT NOT NULL)");
            // Insert initial data here if needed
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            onCreate(db);
        }
    }
}
