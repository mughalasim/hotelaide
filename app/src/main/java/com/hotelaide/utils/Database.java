package com.hotelaide.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotelaide.BuildConfig;
import com.hotelaide.main_pages.models.JobModel;
import com.hotelaide.main_pages.models.CountyModel;
import com.hotelaide.main_pages.models.WorkExperienceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HotelAide.db";
    private static final String TAG_LOG = "DATABASE";


    // JOB SEARCHES IN THE DATABASE ======================================================
    private static final String JOB_TABLE_NAME = "JOB_SEARCH";
    private static final String JOB_ID = "id";
    private static final String JOB_NAME = "job_name";
    private static final String JOB_POSTED_ON = "posted_on";
    private static final String JOB_HOTEL_ID = "hotel_id";
    private static final String JOB_HOTEL_IMAGE = "hotel_image";
    private static final String JOB_HOTEL_LOCATION = "hotel_loation";

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

    // COUNTIES STORED IN THE DATABASE =============================================================
    private static final String COUNTY_TABLE_NAME = "COUNTIES";
    private static final String COUNTY_ID = "id";
    private static final String COUNTY_NAME = "name";


    public Database() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // WORK EXPERIENCE TABLE ===================================================================
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

        // JOB SEARCH TABLE ========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + JOB_TABLE_NAME +
                "(" + JOB_ID + " INTEGER PRIMARY KEY NOT NULL," +
                JOB_NAME + " TEXT," +
                JOB_POSTED_ON + " TEXT," +
                JOB_HOTEL_ID + " INTEGER," +
                JOB_HOTEL_IMAGE + " TEXT," +
                JOB_HOTEL_LOCATION + " TEXT" +
                ")"
        );

        // REGION TABLE ========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + COUNTY_TABLE_NAME +
                "(" + COUNTY_ID + " INTEGER PRIMARY KEY NOT NULL," +
                COUNTY_NAME + " TEXT" +
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

    public void deleteJobTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + JOB_TABLE_NAME);

    }

    public void deleteRegionTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + COUNTY_TABLE_NAME);

    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + WORK_EXP_TABLE_NAME);
        db.execSQL("DELETE FROM " + JOB_TABLE_NAME);
        db.execSQL("DELETE FROM " + COUNTY_TABLE_NAME);
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


    // JOB SEARCH FUNCTIONS ========================================================================
    public JobModel setJobFromJson(JSONObject job_object) {
        JobModel jobModel = new JobModel();
        try {
            jobModel.id = job_object.getInt("id");
            jobModel.name = job_object.getString("title");
            jobModel.posted_on = job_object.getString("posted_on");

            JSONObject hotel_object = job_object.getJSONObject("hotel");
            jobModel.hotel_id = hotel_object.getInt("id");
            jobModel.hotel_image = hotel_object.getString("image");
            jobModel.hotel_location = hotel_object.getString("full_address");

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(JOB_ID, jobModel.id);
            contentValues.put(JOB_NAME, jobModel.name);
            contentValues.put(JOB_POSTED_ON, jobModel.posted_on);
            contentValues.put(JOB_HOTEL_ID, jobModel.hotel_id);
            contentValues.put(JOB_HOTEL_IMAGE, jobModel.hotel_image);
            contentValues.put(JOB_HOTEL_LOCATION, jobModel.hotel_location);

            String whereClause = JOB_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(jobModel.id)};
            int no_of_rows_affected = db.update(JOB_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(JOB_TABLE_NAME, null, contentValues);
            }

            return jobModel;

        } catch (JSONException e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            return jobModel;
        }
    }

    public JobModel getHotelIdByJobID(String job_id) {
        JobModel jobModel = new JobModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = JOB_ID + " = ?";
        String[] whereArgs = new String[]{job_id};
        Cursor cursor = db.query(JOB_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    jobModel.id = cursor.getInt(cursor.getColumnIndex(JOB_ID));
                    jobModel.name = cursor.getString(cursor.getColumnIndex(JOB_NAME));
                    jobModel.posted_on = cursor.getString(cursor.getColumnIndex(JOB_POSTED_ON));
                    jobModel.hotel_id = cursor.getInt(cursor.getColumnIndex(JOB_HOTEL_ID));
                    jobModel.hotel_image = cursor.getString(cursor.getColumnIndex(JOB_HOTEL_IMAGE));
                    jobModel.hotel_location = cursor.getString(cursor.getColumnIndex(JOB_HOTEL_LOCATION));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return jobModel;
    }

    public ArrayList<JobModel> getAllJobModelsBySearch(String search) {

        ArrayList<JobModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + JOB_TABLE_NAME + " WHERE " + JOB_NAME + "  LIKE  '%" + search + "%' ", null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    JobModel jobModel = new JobModel();
                    jobModel.id = cursor.getInt(cursor.getColumnIndex(JOB_ID));
                    jobModel.name = cursor.getString(cursor.getColumnIndex(JOB_NAME));
                    jobModel.posted_on = cursor.getString(cursor.getColumnIndex(JOB_POSTED_ON));
                    jobModel.hotel_id = cursor.getInt(cursor.getColumnIndex(JOB_HOTEL_ID));
                    jobModel.hotel_image = cursor.getString(cursor.getColumnIndex(JOB_HOTEL_IMAGE));
                    jobModel.hotel_location = cursor.getString(cursor.getColumnIndex(JOB_HOTEL_LOCATION));

                    list.add(jobModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    public void deleteJobByID(String work_exp_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = JOB_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(work_exp_id)};
        db.delete(JOB_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public ArrayList<JobModel> getAllJobs() {

        ArrayList<JobModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(JOB_TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    JobModel jobModel = new JobModel();
                    jobModel.id = cursor.getInt(cursor.getColumnIndex(JOB_ID));
                    jobModel.name = cursor.getString(cursor.getColumnIndex(JOB_NAME));
                    jobModel.posted_on = cursor.getString(cursor.getColumnIndex(JOB_POSTED_ON));
                    jobModel.hotel_id = cursor.getInt(cursor.getColumnIndex(JOB_HOTEL_ID));
                    jobModel.hotel_image = cursor.getString(cursor.getColumnIndex(JOB_HOTEL_IMAGE));
                    jobModel.hotel_location = cursor.getString(cursor.getColumnIndex(JOB_HOTEL_LOCATION));

                    list.add(jobModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }


    // COUNTY FUNCTIONS ============================================================================
    public void setCounties(CountyModel countyModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COUNTY_ID, countyModel.id);
        contentValues.put(COUNTY_NAME, countyModel.name);

        String whereClause = COUNTY_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(countyModel.id)};
        int no_of_rows_affected = db.update(COUNTY_TABLE_NAME, contentValues, whereClause, whereArgs);

        if (no_of_rows_affected == 0) {
            db.insert(COUNTY_TABLE_NAME, null, contentValues);
        }
    }

    public List<CountyModel> getAllCounties() {
        final List<CountyModel> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(COUNTY_TABLE_NAME, null, null, null, null, null, null);

        CountyModel countyModelNull = new CountyModel();
        countyModelNull.id = 0;
        countyModelNull.name = "Please set a County";
        arrayList.add(countyModelNull);

        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                CountyModel countyModel = new CountyModel();
                countyModel.id = cursor.getInt(cursor.getColumnIndex(COUNTY_ID));
                countyModel.name = cursor.getString(cursor.getColumnIndex(COUNTY_NAME));
                arrayList.add(countyModel);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

}