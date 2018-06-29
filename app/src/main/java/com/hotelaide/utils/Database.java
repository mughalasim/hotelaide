package com.hotelaide.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotelaide.BuildConfig;
import com.hotelaide.main_pages.models.WorkExperienceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HotelAide.db";
    private static final String TAG_LOG = "DATABASE";


    // WORK EXPERIENCE STORED IN THE DATABASE ================================================
    private static final String EXPERIENCE_TABLE_NAME = "WORK_EXPERIENCE";
    private static final String EXPERIENCE_ID = "id";
    private static final String EXPERIENCE_COMPANY_NAME = "company_name";
    private static final String EXPERIENCE_POSITION = "position";
    private static final String EXPERIENCE_START_DATE = "start_date";
    private static final String EXPERIENCE_END_DATE = "end_date";
    private static final String EXPERIENCE_RESPONSIBILITIES = "responsibilities";
    private static final String EXPERIENCE_CURRENT = "current";


    public Database() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // WORK EXPERIENCE TABLE CREATE
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + EXPERIENCE_TABLE_NAME +
                "(" + EXPERIENCE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                EXPERIENCE_COMPANY_NAME + " TEXT," +
                EXPERIENCE_POSITION + " TEXT," +
                EXPERIENCE_START_DATE + " TEXT," +
                EXPERIENCE_END_DATE + " TEXT," +
                EXPERIENCE_RESPONSIBILITIES + " TEXT," +
                EXPERIENCE_CURRENT + " INTEGER" +
                ")"
        );
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


    // DELETE ALL FUNCTIONS ========================================================================
    public void deleteExperienceTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + EXPERIENCE_TABLE_NAME);

    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + EXPERIENCE_TABLE_NAME);
        onCreate(db);
    }


    // WORK EXPERIENCE FUNCTIONS ===================================================================
    public Boolean setWorkExperienceFromJson(JSONObject work_object) {
        try {
            WorkExperienceModel workExperienceModel = new WorkExperienceModel();
            workExperienceModel.id = work_object.getInt("id");
            workExperienceModel.company_name = work_object.getString("company_name");
            workExperienceModel.position = work_object.getString("position");
            workExperienceModel.start_date = work_object.getString("start_date");
            workExperienceModel.end_date = work_object.getString("end_date");
            workExperienceModel.responsibilities = work_object.getString("responsibilities");
            workExperienceModel.current = work_object.getBoolean("current");


            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(EXPERIENCE_ID, workExperienceModel.id);
            contentValues.put(EXPERIENCE_COMPANY_NAME, workExperienceModel.company_name);
            contentValues.put(EXPERIENCE_POSITION, workExperienceModel.position);
            contentValues.put(EXPERIENCE_START_DATE, workExperienceModel.start_date);
            contentValues.put(EXPERIENCE_END_DATE, workExperienceModel.end_date);
            contentValues.put(EXPERIENCE_RESPONSIBILITIES, workExperienceModel.responsibilities);
            contentValues.put(EXPERIENCE_CURRENT, workExperienceModel.current);

            String whereClause = EXPERIENCE_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(workExperienceModel.id)};
            int no_of_rows_affected = db.update(EXPERIENCE_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(EXPERIENCE_TABLE_NAME, null, contentValues);
            }

            return true;
        } catch (JSONException e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            return false;
        }
    }

    public WorkExperienceModel getWorkExperienceByID(String id) {
        WorkExperienceModel workExperienceModel = new WorkExperienceModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = EXPERIENCE_ID + " = ?";
        String[] whereArgs = new String[]{id};
        Cursor cursor = db.query(EXPERIENCE_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    workExperienceModel.id = cursor.getInt(cursor.getColumnIndex(EXPERIENCE_ID));
                    workExperienceModel.company_name = cursor.getString(cursor.getColumnIndex(EXPERIENCE_COMPANY_NAME));
                    workExperienceModel.position = cursor.getString(cursor.getColumnIndex(EXPERIENCE_POSITION));
                    workExperienceModel.start_date = cursor.getString(cursor.getColumnIndex(EXPERIENCE_START_DATE));
                    workExperienceModel.end_date = cursor.getString(cursor.getColumnIndex(EXPERIENCE_END_DATE));
                    workExperienceModel.responsibilities = cursor.getString(cursor.getColumnIndex(EXPERIENCE_RESPONSIBILITIES));
                    workExperienceModel.current = cursor.getInt(cursor.getColumnIndex(EXPERIENCE_CURRENT)) > 0;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return workExperienceModel;
    }

    public ArrayList<WorkExperienceModel> getAllWorkExperience() {

        ArrayList<WorkExperienceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(EXPERIENCE_TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    WorkExperienceModel workExperienceModel = new WorkExperienceModel();
                    workExperienceModel.id = cursor.getInt(cursor.getColumnIndex(EXPERIENCE_ID));
                    workExperienceModel.company_name = cursor.getString(cursor.getColumnIndex(EXPERIENCE_COMPANY_NAME));
                    workExperienceModel.position = cursor.getString(cursor.getColumnIndex(EXPERIENCE_POSITION));
                    workExperienceModel.start_date = cursor.getString(cursor.getColumnIndex(EXPERIENCE_START_DATE));
                    workExperienceModel.end_date = cursor.getString(cursor.getColumnIndex(EXPERIENCE_END_DATE));
                    workExperienceModel.responsibilities = cursor.getString(cursor.getColumnIndex(EXPERIENCE_RESPONSIBILITIES));
                    workExperienceModel.current = cursor.getInt(cursor.getColumnIndex(EXPERIENCE_CURRENT)) > 0;

                    list.add(workExperienceModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }


}