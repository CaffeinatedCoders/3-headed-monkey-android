package net.three_headed_monkey.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocationHistoryDatabase extends SQLiteOpenHelper {
    public static final String TAG = "LocationHistoryDatabase";

    public static final int VERSION = 2;

    private static final String DB_NAME = "location_history";
    private static final String TABLE_NAME = "locations";

    private static final String KEY_ID = "id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ALTITUDE = "altitude";
    private static final String KEY_ACCURACY = "accuracy";
    private static final String KEY_PROVIDER = "provider";
    private static final String KEY_TIME = "time";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_LATITUDE + " REAL, "
            + KEY_LONGITUDE + " REAL, "
            + KEY_ALTITUDE + " REAL, "
            + KEY_ACCURACY + " REAL, "
            + KEY_PROVIDER + " TEXT, "
            + KEY_TIME + " INTEGER)";

    public LocationHistoryDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int from, int to) {
        Log.w(TAG, "Upgrading database from version " + from + " to " + to);
        Log.w(TAG, "Upgrade not supported, recreating database");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insert(Location location) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, location.getLatitude());
        values.put(KEY_LONGITUDE, location.getLongitude());
        values.put(KEY_ALTITUDE, location.getAltitude());
        values.put(KEY_ACCURACY, location.getAccuracy());
        values.put(KEY_PROVIDER, location.getProvider());
        values.put(KEY_TIME, location.getTime());

        long id = db.insert(TABLE_NAME, null, values);
        if (id < 0) {
            Log.e(TAG, "Error inserting location into database");
        } else {
            Log.d(TAG, "Successfully added location to database");
        }
        db.close();
    }


    public LocationContainer getLast() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, KEY_ID + " DESC", "1");
        cursor.moveToFirst();
        LocationContainer locC = cursorToLocation(cursor);
        cursor.close();
        db.close();
        return locC;
    }

    public void delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=" + id, null);
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public List<Location> getAll() {
        List<Location> locations = new ArrayList<Location>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, KEY_ID, null);
        while (cursor.moveToNext()) {
            LocationContainer locC = cursorToLocation(cursor);
            if (locC != null && locC.location != null) {
                locations.add(locC.location);
            }
        }
        return locations;
    }

    private LocationContainer cursorToLocation(Cursor cursor) {
        if (cursor.getCount() == 0)
            return null;
        String provider = cursor.getString(cursor.getColumnIndex(KEY_PROVIDER));
        Location location = new Location(provider);
        location.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
        location.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
        location.setAltitude(cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)));
        location.setAccuracy(cursor.getFloat(cursor.getColumnIndex(KEY_ACCURACY)));
        location.setTime(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));
        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        return new LocationContainer(location, id);
    }

    public class LocationContainer {
        public Location location = null;
        public int id = -1;

        public LocationContainer() {
        }

        public LocationContainer(Location location, int id) {
            this.location = location;
            this.id = id;
        }
    }

}
