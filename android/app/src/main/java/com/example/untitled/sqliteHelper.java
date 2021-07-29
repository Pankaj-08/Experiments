package com.example.untitled;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;

import static android.util.Log.e;

public class sqliteHelper extends SQLiteOpenHelper {

    public sqliteHelper(@Nullable Context context) {
        super(context,"latlng_database.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addLocation(long time, double lattitude, double longitude, double accuracy) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("insert into latlng values(" + time + "," + lattitude + "," + longitude + "," + accuracy + ")");
        } catch (Exception e) {
            e("35", "sqliteHelper -> addLocation  ->  : "+e.getMessage());
        }
    }
}
