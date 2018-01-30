package com.tm.exammobile.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tm.exammobile.models.Car;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class CarsHelper extends SQLiteOpenHelper {

    private static final String TAG = "CarsHelper";

    private static final String TABLE_NAME = "mycars";
    private static final String id = "_id";
    private static final String name = "name";
    private static final String type = "type";
    private static final String status = "status";
    private static final String quantity = "quantity";

    public CarsHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME
                + " ( " + id + " INT PRIMARY KEY, "
                + name + " TEXT, "
                + type + " TEXT, "
                + status + " TEXT, "
                + quantity + " INT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }

    public Cursor getData() {
        SQLiteDatabase db = getWritableDatabase();
        String getQuery = "SELECT * FROM " + TABLE_NAME;

        return db.rawQuery(getQuery, null);
    }

    public boolean addCar(Car car) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Log.d(TAG, "addCar: " + car.toString() + " to " + TABLE_NAME);

        Cursor data = db.rawQuery("SELECT quantity FROM " + TABLE_NAME + " WHERE _id=" + car.getId(), null);
        if (data.moveToNext()) {
            car.setQuantity(Integer.valueOf(data.getString(0)) + 1);
            contentValues.put(quantity, car.getQuantity());

            data.close();
            return db.update(TABLE_NAME, contentValues, "_id=" + car.getId(), null) != -1;
        } else {
            contentValues.put(id, car.getId());
            contentValues.put(name, car.getName());
            contentValues.put(type, car.getType());
            contentValues.put(quantity, car.getQuantity());

            data.close();
            return db.insert(TABLE_NAME, null, contentValues) != -1;
        }

    }

    public boolean deleteCar(int carId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, id + " = " + carId, null) > 0;
    }

    public boolean deleteAllCars() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    public boolean returnCar(Integer id) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Cursor data = db.rawQuery("SELECT quantity FROM " + TABLE_NAME + " WHERE _id=" + id, null);

        if (data.moveToNext()) {
            if (data.getInt(0) - 1 == 0) {
                deleteCar(id);
            } else {
                contentValues.put(quantity, data.getInt(0) - 1);

                data.close();
                return db.update(TABLE_NAME, contentValues, "_id=" + id, null) != -1;
            }
        }
        return false;
    }
}
