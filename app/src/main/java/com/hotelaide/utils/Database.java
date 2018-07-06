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


    // WORK EXPERIENCE STORED IN THE DATABASE ======================================================
    private static final String WORK_EXP_TABLE_NAME = "WORK_EXPERIENCE";
    private static final String WORK_EXP_ID = "id";
    private static final String WORK_EXP_COMPANY_NAME = "company_name";
    private static final String WORK_EXP_POSITION = "position";
    private static final String WORK_EXP_START_DATE = "start_date";
    private static final String WORK_EXP_END_DATE = "end_date";
    private static final String WORK_EXP_RESPONSIBILITIES = "responsibilities";
    private static final String WORK_EXP_CURRENT = "current";

    // EDUCATIONAL EXPERIENCE STORED IN THE DATABASE ===============================================
    private static final String EDU_EXP_TABLE_NAME = "EDUCATIONAL_EXPERIENCE";
    private static final String EDU_EXP_ID = "id";
    private static final String EDU_EXP_COMPANY_NAME = "company_name";
    private static final String EDU_EXP_POSITION = "position";
    private static final String EDU_EXP_START_DATE = "start_date";
    private static final String EDU_EXP_END_DATE = "end_date";
    private static final String EDU_EXP_RESPONSIBILITIES = "responsibilities";
    private static final String EDU_EXP_CURRENT = "current";


    public Database() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // WORK EXPERIENCE TABLE CREATE
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + WORK_EXP_TABLE_NAME +
                "(" + WORK_EXP_ID + " INTEGER PRIMARY KEY NOT NULL," +
                WORK_EXP_COMPANY_NAME + " TEXT," +
                WORK_EXP_POSITION + " TEXT," +
                WORK_EXP_START_DATE + " TEXT," +
                WORK_EXP_END_DATE + " TEXT," +
                WORK_EXP_RESPONSIBILITIES + " TEXT," +
                WORK_EXP_CURRENT + " INTEGER" +
                ")"
        );
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


    // DELETE ALL FUNCTIONS ========================================================================
    public void deleteExperienceTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + WORK_EXP_TABLE_NAME);

    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + WORK_EXP_TABLE_NAME);
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
            contentValues.put(WORK_EXP_ID, workExperienceModel.id);
            contentValues.put(WORK_EXP_COMPANY_NAME, workExperienceModel.company_name);
            contentValues.put(WORK_EXP_POSITION, workExperienceModel.position);
            contentValues.put(WORK_EXP_START_DATE, workExperienceModel.start_date);
            contentValues.put(WORK_EXP_END_DATE, workExperienceModel.end_date);
            contentValues.put(WORK_EXP_RESPONSIBILITIES, workExperienceModel.responsibilities);
            contentValues.put(WORK_EXP_CURRENT, workExperienceModel.current);

            String whereClause = WORK_EXP_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(workExperienceModel.id)};
            int no_of_rows_affected = db.update(WORK_EXP_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(WORK_EXP_TABLE_NAME, null, contentValues);
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

        String whereClause = WORK_EXP_ID + " = ?";
        String[] whereArgs = new String[]{id};
        Cursor cursor = db.query(WORK_EXP_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    workExperienceModel.id = cursor.getInt(cursor.getColumnIndex(WORK_EXP_ID));
                    workExperienceModel.company_name = cursor.getString(cursor.getColumnIndex(WORK_EXP_COMPANY_NAME));
                    workExperienceModel.position = cursor.getString(cursor.getColumnIndex(WORK_EXP_POSITION));
                    workExperienceModel.start_date = cursor.getString(cursor.getColumnIndex(WORK_EXP_START_DATE));
                    workExperienceModel.end_date = cursor.getString(cursor.getColumnIndex(WORK_EXP_END_DATE));
                    workExperienceModel.responsibilities = cursor.getString(cursor.getColumnIndex(WORK_EXP_RESPONSIBILITIES));
                    workExperienceModel.current = cursor.getInt(cursor.getColumnIndex(WORK_EXP_CURRENT)) > 0;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return workExperienceModel;
    }

    public void deleteWorkExperienceByID(String work_exp_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = WORK_EXP_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(work_exp_id)};
        db.delete(WORK_EXP_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }


    public ArrayList<WorkExperienceModel> getAllWorkExperience() {

        ArrayList<WorkExperienceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(WORK_EXP_TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    WorkExperienceModel workExperienceModel = new WorkExperienceModel();
                    workExperienceModel.id = cursor.getInt(cursor.getColumnIndex(WORK_EXP_ID));
                    workExperienceModel.company_name = cursor.getString(cursor.getColumnIndex(WORK_EXP_COMPANY_NAME));
                    workExperienceModel.position = cursor.getString(cursor.getColumnIndex(WORK_EXP_POSITION));
                    workExperienceModel.start_date = cursor.getString(cursor.getColumnIndex(WORK_EXP_START_DATE));
                    workExperienceModel.end_date = cursor.getString(cursor.getColumnIndex(WORK_EXP_END_DATE));
                    workExperienceModel.responsibilities = cursor.getString(cursor.getColumnIndex(WORK_EXP_RESPONSIBILITIES));
                    workExperienceModel.current = cursor.getInt(cursor.getColumnIndex(WORK_EXP_CURRENT)) > 0;

                    list.add(workExperienceModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }


}