package com.hotelaide.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotelaide.BuildConfig;
import com.hotelaide.main.models.ExperienceModel;
import com.hotelaide.main.models.JobModel;
import com.hotelaide.main.models.SearchFilterModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hotelaide.utils.StaticVariables.CATEGORIES_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXP_CURRENT;
import static com.hotelaide.utils.StaticVariables.EXP_END_DATE;
import static com.hotelaide.utils.StaticVariables.EXP_ID;
import static com.hotelaide.utils.StaticVariables.EXP_NAME;
import static com.hotelaide.utils.StaticVariables.EXP_POSITION_LEVEL;
import static com.hotelaide.utils.StaticVariables.EXP_RESPONSIBILITIES_FIELD;
import static com.hotelaide.utils.StaticVariables.EXP_START_DATE;
import static com.hotelaide.utils.StaticVariables.EXP_TABLE_ID;
import static com.hotelaide.utils.StaticVariables.EXP_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXP_TYPE;
import static com.hotelaide.utils.StaticVariables.FILTER_ID;
import static com.hotelaide.utils.StaticVariables.FILTER_NAME;
import static com.hotelaide.utils.StaticVariables.JOB_HOTEL_ID;
import static com.hotelaide.utils.StaticVariables.JOB_HOTEL_IMAGE;
import static com.hotelaide.utils.StaticVariables.JOB_HOTEL_LOCATION;
import static com.hotelaide.utils.StaticVariables.JOB_ID;
import static com.hotelaide.utils.StaticVariables.JOB_NAME;
import static com.hotelaide.utils.StaticVariables.JOB_POSTED_ON;
import static com.hotelaide.utils.StaticVariables.JOB_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.JOB_TYPE_TABLE_NAME;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HotelAide.db";
    private static final String TAG_LOG = "DATABASE";


    public Database() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // WORK EXPERIENCE TABLE ===================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + EXP_TABLE_NAME +
                "(" +
                EXP_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                EXP_ID + " INTEGER," +
                EXP_NAME + " TEXT," +
                EXP_POSITION_LEVEL + " TEXT," +
                EXP_START_DATE + " TEXT," +
                EXP_END_DATE + " TEXT," +
                EXP_RESPONSIBILITIES_FIELD + " TEXT," +
                EXP_CURRENT + " INTEGER," +
                EXP_TYPE + " TEXT" +
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

        // COUNTY TABLE ============================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + COUNTY_TABLE_NAME +
                "(" + FILTER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTER_NAME + " TEXT" +
                ")"
        );

        // JOB TYPE TABLE ==========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + JOB_TYPE_TABLE_NAME +
                "(" + FILTER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTER_NAME + " TEXT" +
                ")"
        );

        // CATEGORIES TABLE ========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + CATEGORIES_TABLE_NAME +
                "(" + FILTER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTER_NAME + " TEXT" +
                ")"
        );

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


    // DELETE ALL FUNCTIONS ========================================================================
    public void deleteExperienceTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + EXP_TABLE_NAME);

    }

    public void deleteJobTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + JOB_TABLE_NAME);

    }

    public void deleteCountyTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + COUNTY_TABLE_NAME);

    }

    public void deleteJobTypeTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + JOB_TYPE_TABLE_NAME);

    }

    public void deleteCategoriesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CATEGORIES_TABLE_NAME);

    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + EXP_TABLE_NAME);
        db.execSQL("DELETE FROM " + JOB_TABLE_NAME);
        db.execSQL("DELETE FROM " + COUNTY_TABLE_NAME);
        db.execSQL("DELETE FROM " + CATEGORIES_TABLE_NAME);
        db.execSQL("DELETE FROM " + JOB_TYPE_TABLE_NAME);
        onCreate(db);
    }


    // WORK EXPERIENCE FUNCTIONS ===================================================================
    public Boolean setExperienceFromJson(JSONObject work_object, String type) {
        try {
            ExperienceModel experienceModel = new ExperienceModel();
            if (type.equals(EXPERIENCE_TYPE_WORK)) {
                experienceModel.experience_id = work_object.getInt("id");
                experienceModel.name = work_object.getString("company_name");
                experienceModel.position_level = work_object.getString("position");
                experienceModel.start_date = work_object.getString("start_date");
                experienceModel.end_date = work_object.getString("end_date");
                experienceModel.responsibilities_field = work_object.getString("responsibilities");
                if (work_object.getBoolean("current")) {
                    experienceModel.current = 1;
                }
                experienceModel.type = EXPERIENCE_TYPE_WORK;

            } else {
                experienceModel.experience_id = work_object.getInt("id");
                experienceModel.name = work_object.getString("institution_name");
                experienceModel.position_level = work_object.getString("education_level");
                experienceModel.start_date = work_object.getString("start_date");
                experienceModel.end_date = work_object.getString("end_date");
                experienceModel.responsibilities_field = work_object.getString("study_field");
//                if (work_object.getBoolean("current")) {
//                    experienceModel.current = 1;
//                }
                experienceModel.type = EXPERIENCE_TYPE_EDUCATION;
            }


            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(EXP_ID, experienceModel.experience_id);
            contentValues.put(EXP_NAME, experienceModel.name);
            contentValues.put(EXP_POSITION_LEVEL, experienceModel.position_level);
            contentValues.put(EXP_START_DATE, experienceModel.start_date);
            contentValues.put(EXP_END_DATE, experienceModel.end_date);
            contentValues.put(EXP_RESPONSIBILITIES_FIELD, experienceModel.responsibilities_field);
            contentValues.put(EXP_CURRENT, experienceModel.current);
            contentValues.put(EXP_TYPE, experienceModel.type);

            String whereClause = EXP_ID + " = ? AND " + EXP_TYPE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(experienceModel.experience_id), experienceModel.type };
            int no_of_rows_affected = db.update(EXP_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(EXP_TABLE_NAME, null, contentValues);
            }

            return true;
        } catch (JSONException e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            return false;
        }
    }

    public void deleteExperienceByID(String exp_work_id, String exp_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = EXP_ID + " = ? AND " + EXP_TYPE + " = ?";
        String[] whereArgs = new String[]{exp_work_id, exp_type};
        db.delete(EXP_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public ArrayList<ExperienceModel> getAllExperience(String exp_type) {

        ArrayList<ExperienceModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = EXP_TYPE + " = ?";
        String[] whereArgs = new String[]{exp_type};

        Cursor cursor = db.query(EXP_TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    ExperienceModel experienceModel = new ExperienceModel();
                    experienceModel.experience_id = cursor.getInt(cursor.getColumnIndex(EXP_ID));
                    experienceModel.name = cursor.getString(cursor.getColumnIndex(EXP_NAME));
                    experienceModel.position_level = cursor.getString(cursor.getColumnIndex(EXP_POSITION_LEVEL));
                    experienceModel.start_date = cursor.getString(cursor.getColumnIndex(EXP_START_DATE));
                    experienceModel.end_date = cursor.getString(cursor.getColumnIndex(EXP_END_DATE));
                    experienceModel.responsibilities_field = cursor.getString(cursor.getColumnIndex(EXP_RESPONSIBILITIES_FIELD));
                    experienceModel.current = cursor.getInt(cursor.getColumnIndex(EXP_CURRENT));
                    experienceModel.type = cursor.getString(cursor.getColumnIndex(EXP_TYPE));

                    list.add(experienceModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    public void deleteExperienceTableByType(String exp_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = EXP_TYPE + " = ?";
        String[] whereArgs = new String[]{exp_type};
        db.delete(EXP_TABLE_NAME, whereClause, whereArgs);
        db.close();
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

    public ArrayList<JobModel> getAllJobModelsBySearch(String search, String location) {

        ArrayList<JobModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + JOB_TABLE_NAME
                        + " WHERE " + JOB_NAME + "  LIKE  '%" + search + "%' "
                        + " AND " + JOB_HOTEL_LOCATION + "  LIKE  '%" + location + "%' "
                , null);

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

    public ArrayList<JobModel> getAllJobs(String county) {

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
    public void setFilter(String table_name, SearchFilterModel searchFilterModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FILTER_ID, searchFilterModel.id);
        contentValues.put(FILTER_NAME, searchFilterModel.name);

        String whereClause = FILTER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(searchFilterModel.id)};

        int no_of_rows_affected = db.update(table_name, contentValues, whereClause, whereArgs);

        if (no_of_rows_affected == 0) {
            db.insert(table_name, null, contentValues);
        }
    }

    public List<SearchFilterModel> getAllFilterItems(String table_name) {
        final List<SearchFilterModel> search_array_list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(table_name, null, null, null, null, null, FILTER_NAME);

        SearchFilterModel searchFilterModelNull = new SearchFilterModel();
        searchFilterModelNull.id = 0;
        searchFilterModelNull.name = "All";
        search_array_list.add(searchFilterModelNull);

        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                SearchFilterModel searchFilterModel = new SearchFilterModel();
                searchFilterModel.id = cursor.getInt(cursor.getColumnIndex(FILTER_ID));
                searchFilterModel.name = cursor.getString(cursor.getColumnIndex(FILTER_NAME));
                search_array_list.add(searchFilterModel);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return search_array_list;
    }

    public int getFilterIDByString(String table_name, String county_name) {
        int filter_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = FILTER_NAME + " = ?";
        String[] whereArgs = new String[]{county_name};
        Cursor cursor = db.query(table_name, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    filter_id = cursor.getInt(cursor.getColumnIndex(FILTER_ID));
                    cursor.moveToNext();
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return filter_id;
    }

    public String getFilterNameByID(String table_name, int county_id) {
        String filter_name = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = FILTER_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(county_id)};
        Cursor cursor = db.query(table_name, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    filter_name = cursor.getString(cursor.getColumnIndex(FILTER_NAME));
                    cursor.moveToNext();
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return filter_name;
    }


}