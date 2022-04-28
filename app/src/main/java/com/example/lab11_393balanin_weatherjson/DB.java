package com.example.lab11_393balanin_weatherjson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class DB extends SQLiteOpenHelper {
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE PICTURES (LINK txt,PIC BLOB)";
        db.execSQL(sql);
        sql = "CREATE TABLE HISTORY (DATE txt PRIMARY KEY,CITY txt,CONTENT txt)";
        db.execSQL(sql);
    }


    public byte[] PictureExists(String link)
    {
        String sql = "SELECT * FROM PICTURES WHERE LINK ='" + link + "'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst()) return cur.getBlob(1);
        return null;
    }

    public void AddPicture(String link, Bitmap bmp) {
        byte[] b;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        SQLiteDatabase db = getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("LINK",link);
        insertValues.put("Pic", byteArray);
        db.insert("PICTURES", null, insertValues);
        Log.i("Inserting","Inserting method completed");
    }

    public void AddRequest(String date, String city, String info) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("DATE", date);
        insertValues.put("CITY", city);
        insertValues.put("CONTENT", info);

        db.insert("HISTORY", null, insertValues);
        Log.i("HISTORY_INSERT","Method worked");
    }

    public List<WeatherCall> GetWeatherCalls() {
        List<WeatherCall> calls = new ArrayList<WeatherCall>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM HISTORY";
        Cursor cur = db.rawQuery(sql,null);
        if (cur.moveToFirst() == true) {
            do {
                WeatherCall wc = new WeatherCall();
                wc.Date = cur.getString(0);
                wc.City = cur.getString(1);
                wc.Information = cur.getString(2);
                calls.add(wc);
            } while (cur.moveToNext() == true);
        }
        Log.i("!!!!!!!!!!GETWEATHERCALLS", "Method worked");
        return calls;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

