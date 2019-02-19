package com.hotelaide.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotelaide.BuildConfig;
import com.hotelaide.main.models.DocumentModel;
import com.hotelaide.main.models.ExperienceModel;
import com.hotelaide.main.models.JobModel;
import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.main.models.SearchFilterModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hotelaide.utils.StaticVariables.CATEGORIES_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_DATE_UPLOADED;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_FILE_TYPE;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_FILE_URL;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_ID;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_IMAGE;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_IS_DIRTY;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_NAME;
import static com.hotelaide.utils.StaticVariables.DOCUMENTS_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EDUCATION_LEVEL_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXP_CURRENT;
import static com.hotelaide.utils.StaticVariables.EXP_END_DATE;
import static com.hotelaide.utils.StaticVariables.EXP_ID;
import static com.hotelaide.utils.StaticVariables.EXP_LEVEL;
import static com.hotelaide.utils.StaticVariables.EXP_NAME;
import static com.hotelaide.utils.StaticVariables.EXP_POSITION;
import static com.hotelaide.utils.StaticVariables.EXP_RESPONSIBILITIES_FIELD;
import static com.hotelaide.utils.StaticVariables.EXP_START_DATE;
import static com.hotelaide.utils.StaticVariables.EXP_TABLE_ID;
import static com.hotelaide.utils.StaticVariables.EXP_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXP_TYPE;
import static com.hotelaide.utils.StaticVariables.FILTERED_JOBS_BY;
import static com.hotelaide.utils.StaticVariables.FILTERED_JOBS_ID;
import static com.hotelaide.utils.StaticVariables.FILTERED_JOBS_TABLE_ID;
import static com.hotelaide.utils.StaticVariables.FILTERED_JOBS_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.FILTER_ID;
import static com.hotelaide.utils.StaticVariables.FILTER_NAME;
import static com.hotelaide.utils.StaticVariables.JOB_ESTABLISHMENT_ID;
import static com.hotelaide.utils.StaticVariables.JOB_ESTABLISHMENT_IMAGE;
import static com.hotelaide.utils.StaticVariables.JOB_ESTABLISHMENT_LOCATION;
import static com.hotelaide.utils.StaticVariables.JOB_ID;
import static com.hotelaide.utils.StaticVariables.JOB_NAME;
import static com.hotelaide.utils.StaticVariables.JOB_POSTED_ON;
import static com.hotelaide.utils.StaticVariables.JOB_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.JOB_TYPE_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_DATE;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_JOB_ID;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_PREVIEW;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TABLE_ID;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_BODY;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_READ;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TITLE;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TYPE_CODE;

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
                EXP_POSITION + " TEXT," +
                EXP_LEVEL + " TEXT," +
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
                "(" +
                JOB_ID + " INTEGER PRIMARY KEY NOT NULL," +
                JOB_NAME + " TEXT," +
                JOB_POSTED_ON + " TEXT," +
                JOB_ESTABLISHMENT_ID + " INTEGER," +
                JOB_ESTABLISHMENT_IMAGE + " TEXT," +
                JOB_ESTABLISHMENT_LOCATION + " TEXT" +
                ")"
        );

        // FILTERED JOBS TABLE =====================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + FILTERED_JOBS_TABLE_NAME +
                "(" +
                FILTERED_JOBS_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTERED_JOBS_ID + " INTEGER," +
                FILTERED_JOBS_BY + " TEXT" +
                ")"
        );

        // COUNTY TABLE ============================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + COUNTY_TABLE_NAME +
                "(" +
                FILTER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTER_NAME + " TEXT" +
                ")"
        );

        // JOB TYPE TABLE ==========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + JOB_TYPE_TABLE_NAME +
                "(" +
                FILTER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTER_NAME + " TEXT" +
                ")"
        );

        // CATEGORIES TABLE ========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + CATEGORIES_TABLE_NAME +
                "(" +
                FILTER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTER_NAME + " TEXT" +
                ")"
        );

        // EDUCATION LEVEL TABLE ===================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + EDUCATION_LEVEL_TABLE_NAME +
                "(" +
                FILTER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                FILTER_NAME + " TEXT" +
                ")"
        );

        // DOCUMENTS TABLE =========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DOCUMENTS_TABLE_NAME +
                "(" +
                DOCUMENTS_ID + " INTEGER PRIMARY KEY NOT NULL," +
                DOCUMENTS_NAME + " TEXT," +
                DOCUMENTS_IMAGE + " TEXT," +
                DOCUMENTS_FILE_URL + " TEXT," +
                DOCUMENTS_FILE_TYPE + " TEXT," +
                DOCUMENTS_DATE_UPLOADED + " TEXT," +
                DOCUMENTS_IS_DIRTY + " INTEGER" +
                ")"
        );

        // NOTIFICATIONS TABLE =========================================================================
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + NOTIFICATION_TABLE_NAME +
                "(" +
                NOTIFICATION_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                NOTIFICATION_TITLE + " TEXT," +
                NOTIFICATION_PREVIEW + " TEXT," +
                NOTIFICATION_BODY + " TEXT," +
                NOTIFICATION_DATE + " TEXT," +
                NOTIFICATION_JOB_ID + " INTEGER," +
                NOTIFICATION_READ + " INTEGER," +
                NOTIFICATION_TYPE_CODE + " INTEGER" +
                ")"
        );

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


    // DELETE ALL FUNCTIONS ========================================================================
    public void deleteJobTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + JOB_TABLE_NAME);
    }

    public void deleteFilteredJobTable(String filter_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = FILTERED_JOBS_BY + " = ?";
        String[] whereArgs = new String[]{String.valueOf(filter_type)};
        db.delete(FILTERED_JOBS_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void deleteExperienceTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + EXP_TABLE_NAME);
    }

    public void deleteDocumentsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DOCUMENTS_TABLE_NAME);
    }

    public void deleteCountyTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + COUNTY_TABLE_NAME);
    }

    public void deleteNotificationsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + NOTIFICATION_TABLE_NAME);
    }

    public void deleteJobTypeTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + JOB_TYPE_TABLE_NAME);
    }

    public void deleteCategoriesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CATEGORIES_TABLE_NAME);
    }

    public void deleteEducationLevelsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + EDUCATION_LEVEL_TABLE_NAME);
    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + JOB_TABLE_NAME);
        db.execSQL("DELETE FROM " + FILTERED_JOBS_TABLE_NAME);
        db.execSQL("DELETE FROM " + EXP_TABLE_NAME);
        db.execSQL("DELETE FROM " + COUNTY_TABLE_NAME);
        db.execSQL("DELETE FROM " + CATEGORIES_TABLE_NAME);
        db.execSQL("DELETE FROM " + EDUCATION_LEVEL_TABLE_NAME);
        db.execSQL("DELETE FROM " + JOB_TYPE_TABLE_NAME);
        db.execSQL("DELETE FROM " + DOCUMENTS_TABLE_NAME);
        db.execSQL("DELETE FROM " + NOTIFICATION_TABLE_NAME);
        onCreate(db);
    }


    // WORK EXPERIENCE FUNCTIONS ===================================================================
    public Boolean setExperienceFromJson(JSONObject work_object, String type) {
        try {
            ExperienceModel experienceModel = new ExperienceModel();
            if (type.equals(EXPERIENCE_TYPE_WORK)) {
                experienceModel.experience_id = work_object.getInt("id");
                experienceModel.name = work_object.getString("company_name");
                experienceModel.position = work_object.getString("position");
                experienceModel.start_date = work_object.getString("start_date");
                experienceModel.end_date = work_object.getString("end_date");
                experienceModel.responsibilities_field = work_object.getString("responsibilities");
                experienceModel.current = work_object.getInt("current");
                experienceModel.type = EXPERIENCE_TYPE_WORK;

            } else {
                experienceModel.experience_id = work_object.getInt("id");
                experienceModel.name = work_object.getString("institution_name");
                experienceModel.education_level = work_object.getInt("education_level");
                experienceModel.start_date = work_object.getString("start_date");
                experienceModel.end_date = work_object.getString("end_date");
                experienceModel.responsibilities_field = work_object.getString("study_field");
                experienceModel.current = work_object.getInt("current");

                experienceModel.type = EXPERIENCE_TYPE_EDUCATION;
            }


            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(EXP_ID, experienceModel.experience_id);
            contentValues.put(EXP_NAME, experienceModel.name);
            contentValues.put(EXP_POSITION, experienceModel.position);
            contentValues.put(EXP_LEVEL, experienceModel.education_level);
            contentValues.put(EXP_START_DATE, experienceModel.start_date);
            contentValues.put(EXP_END_DATE, experienceModel.end_date);
            contentValues.put(EXP_RESPONSIBILITIES_FIELD, experienceModel.responsibilities_field);
            contentValues.put(EXP_CURRENT, experienceModel.current);
            contentValues.put(EXP_TYPE, experienceModel.type);

            String whereClause = EXP_ID + " = ? AND " + EXP_TYPE + " = ?";
            String[] whereArgs = new String[]{String.valueOf(experienceModel.experience_id), experienceModel.type};
            int no_of_rows_affected = db.update(EXP_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(EXP_TABLE_NAME, null, contentValues);
            }

            return true;
        } catch (JSONException e) {
            Helpers.logThis(TAG_LOG, e.toString());
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
                    experienceModel.position = cursor.getString(cursor.getColumnIndex(EXP_POSITION));
                    experienceModel.education_level = cursor.getInt(cursor.getColumnIndex(EXP_LEVEL));
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
    public JobModel setJobFromJson(JSONObject job_object, String filter_type) {
//        Helpers.logThis(TAG_LOG, job_object.toString());
        JobModel jobModel = new JobModel();
        try {
            jobModel.id = job_object.getInt("id");
            jobModel.name = job_object.getString("title");
            jobModel.posted_on = job_object.getString("posted_on");

            JSONObject establishment = job_object.getJSONObject("establishment");
            jobModel.establishment_id = establishment.getInt("id");
            jobModel.establishment_image = establishment.getString("image");
            jobModel.establishment_location = establishment.getString("full_address");

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues content_values1 = new ContentValues();
            content_values1.put(JOB_ID, jobModel.id);
            content_values1.put(JOB_NAME, jobModel.name);
            content_values1.put(JOB_POSTED_ON, jobModel.posted_on);
            content_values1.put(JOB_ESTABLISHMENT_ID, jobModel.establishment_id);
            content_values1.put(JOB_ESTABLISHMENT_IMAGE, jobModel.establishment_image);
            content_values1.put(JOB_ESTABLISHMENT_LOCATION, jobModel.establishment_location);

            String where_clause1 = JOB_ID + " = ?";
            String[] where_args1 = new String[]{String.valueOf(jobModel.id)};
            int no_of_rows_affected = db.update(JOB_TABLE_NAME, content_values1, where_clause1,
                    where_args1);

            if (no_of_rows_affected == 0) {
                db.insert(JOB_TABLE_NAME, null, content_values1);
            }

            if (!filter_type.equals("")) {
                setAppliedJob(jobModel.id, filter_type);
            }

            return jobModel;

        } catch (JSONException e) {
            Helpers.logThis(TAG_LOG, e.toString());
            return jobModel;
        }
    }

    private void setAppliedJob(int job_id, String filter_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content_values2 = new ContentValues();

        content_values2.put(FILTERED_JOBS_ID, job_id);
        content_values2.put(FILTERED_JOBS_BY, filter_type);

        String where_clause2 = FILTERED_JOBS_ID + " = ?";

        String[] where_args2 = new String[]{String.valueOf(job_id)};
        int no_of_rows_affected2 = db.update(FILTERED_JOBS_TABLE_NAME, content_values2, where_clause2,
                where_args2);
        if (no_of_rows_affected2 == 0) {
            db.insert(FILTERED_JOBS_TABLE_NAME, null, content_values2);
        }
    }

    public Boolean isFilteredJob(int job_id, String filter_type) {
        JobModel jobModel = new JobModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = FILTERED_JOBS_ID + " = ? AND " + FILTERED_JOBS_BY + " = ?";
        String[] whereArgs = new String[]{String.valueOf(job_id), filter_type};
        Cursor cursor = db.query(FILTERED_JOBS_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.close();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public ArrayList<JobModel> getAllJobModelsBySearch(String search, String location) {

        ArrayList<JobModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + JOB_TABLE_NAME
                        + " WHERE " + JOB_NAME + "  LIKE  '%" + search + "%' "
                        + " AND " + JOB_ESTABLISHMENT_LOCATION + "  LIKE  '%" + location + "%' "
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
                    jobModel.establishment_id = cursor.getInt(cursor.getColumnIndex(JOB_ESTABLISHMENT_ID));
                    jobModel.establishment_image = cursor.getString(cursor.getColumnIndex(JOB_ESTABLISHMENT_IMAGE));
                    jobModel.establishment_location = cursor.getString(cursor.getColumnIndex(JOB_ESTABLISHMENT_LOCATION));

                    list.add(jobModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    public void deleteJobByID(String job_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = JOB_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(job_id)};
        db.delete(JOB_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void deleteFilteredJobByJobId(int job_id, String filter_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = FILTERED_JOBS_ID + " = ? AND " + FILTERED_JOBS_BY + " = ?";
        String[] whereArgs = new String[]{String.valueOf(job_id), filter_type};
        db.delete(FILTERED_JOBS_TABLE_NAME, whereClause, whereArgs);
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
                    jobModel.establishment_id = cursor.getInt(cursor.getColumnIndex(JOB_ESTABLISHMENT_ID));
                    jobModel.establishment_image = cursor.getString(cursor.getColumnIndex(JOB_ESTABLISHMENT_IMAGE));
                    jobModel.establishment_location = cursor.getString(cursor.getColumnIndex(JOB_ESTABLISHMENT_LOCATION));

                    list.add(jobModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    public ArrayList<JobModel> getAllFilteredJobs(String filter_type) {

        ArrayList<JobModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(createInnerJoin(filter_type), null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    JobModel jobModel = new JobModel();
                    jobModel.id = cursor.getInt(cursor.getColumnIndex(JOB_ID));
                    jobModel.name = cursor.getString(cursor.getColumnIndex(JOB_NAME));
                    jobModel.posted_on = cursor.getString(cursor.getColumnIndex(JOB_POSTED_ON));
                    jobModel.establishment_id = cursor.getInt(cursor.getColumnIndex(JOB_ESTABLISHMENT_ID));
                    jobModel.establishment_image = cursor.getString(cursor.getColumnIndex(JOB_ESTABLISHMENT_IMAGE));
                    jobModel.establishment_location = cursor.getString(cursor.getColumnIndex(JOB_ESTABLISHMENT_LOCATION));

                    list.add(jobModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    private String createInnerJoin(String filter_type) {
        return "SELECT * FROM " + FILTERED_JOBS_TABLE_NAME + " l INNER JOIN " + JOB_TABLE_NAME + " a ON l."
                + FILTERED_JOBS_ID + " = a." + JOB_ID + " WHERE " + FILTERED_JOBS_BY + " = \"" + filter_type + "\"";
    }


    // COUNTING TABLES =============================================================================
    public String getFilteredTableCount(String filter_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(createInnerJoin(filter_type), null);

        String str_count = "0";

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                str_count = String.valueOf(count);
            }
            cursor.close();
        }

        return str_count;
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


    // DOCUMENT FUNCTIONS ==========================================================================
    public void setDocumentFromJson(JSONObject document_object) {
        try {
            DocumentModel documentModel = new DocumentModel();
            documentModel.id = document_object.getInt("id");
            documentModel.name = document_object.getString("name");
            documentModel.image = document_object.getString("image");
            documentModel.file_url = document_object.getString("file_url");
            documentModel.file_type = document_object.getString("file_type");
            documentModel.date_uploaded = document_object.getString("date_uploaded");


            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DOCUMENTS_ID, documentModel.id);
            contentValues.put(DOCUMENTS_NAME, documentModel.name);
            contentValues.put(DOCUMENTS_IMAGE, documentModel.image);
            contentValues.put(DOCUMENTS_FILE_TYPE, documentModel.file_type);
            contentValues.put(DOCUMENTS_FILE_URL, documentModel.file_url);
            contentValues.put(DOCUMENTS_DATE_UPLOADED, documentModel.date_uploaded);
            contentValues.put(DOCUMENTS_IS_DIRTY, 0);


            String whereClause = DOCUMENTS_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(documentModel.id)};
            int no_of_rows_affected = db.update(DOCUMENTS_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(DOCUMENTS_TABLE_NAME, null, contentValues);
            }

        } catch (JSONException e) {
            Helpers.logThis(TAG_LOG, e.toString());
        }
    }

    public void setDirtyDocument() {
        DocumentModel documentModel = new DocumentModel();
        documentModel.id = 0;
        documentModel.name = "Loading...";
        documentModel.is_dirty = 1;
        documentModel.file_type = "application/pdf";
        documentModel.date_uploaded = "This may take a moment";
        documentModel.image = "https://cdn.pixabay.com/photo/2016/01/03/00/43/upload-1118929_960_720.png";


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCUMENTS_ID, documentModel.id);
        contentValues.put(DOCUMENTS_NAME, documentModel.name);
        contentValues.put(DOCUMENTS_IMAGE, documentModel.image);
        contentValues.put(DOCUMENTS_FILE_TYPE, documentModel.file_type);
        contentValues.put(DOCUMENTS_FILE_URL, documentModel.file_url);
        contentValues.put(DOCUMENTS_DATE_UPLOADED, documentModel.date_uploaded);
        contentValues.put(DOCUMENTS_IS_DIRTY, documentModel.is_dirty);

        String whereClause = DOCUMENTS_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(documentModel.id)};
        int no_of_rows_affected = db.update(DOCUMENTS_TABLE_NAME, contentValues, whereClause,
                whereArgs);

        if (no_of_rows_affected == 0) {
            db.insert(DOCUMENTS_TABLE_NAME, null, contentValues);
        }

    }

    public void deleteDocumentByID(String document_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = DOCUMENTS_ID + " = ?";
        String[] whereArgs = new String[]{document_id};
        db.delete(DOCUMENTS_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void deleteDirtyDocuments() {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = DOCUMENTS_IS_DIRTY + " = ?";
        String[] whereArgs = new String[]{"1"};
        db.delete(DOCUMENTS_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public ArrayList<DocumentModel> getAllDocuments() {

        ArrayList<DocumentModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(DOCUMENTS_TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    DocumentModel documentModel = new DocumentModel();
                    documentModel.id = cursor.getInt(cursor.getColumnIndex(DOCUMENTS_ID));
                    documentModel.name = cursor.getString(cursor.getColumnIndex(DOCUMENTS_NAME));
                    documentModel.image = cursor.getString(cursor.getColumnIndex(DOCUMENTS_IMAGE));
                    documentModel.file_url = cursor.getString(cursor.getColumnIndex(DOCUMENTS_FILE_URL));
                    documentModel.file_type = cursor.getString(cursor.getColumnIndex(DOCUMENTS_FILE_TYPE));
                    documentModel.date_uploaded = cursor.getString(cursor.getColumnIndex(DOCUMENTS_DATE_UPLOADED));

                    list.add(documentModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }


    // NOTIFICATION FUNCTIONS ======================================================================
    public void setNotification(NotificationModel notification_model) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NOTIFICATION_TITLE, notification_model.title);
            contentValues.put(NOTIFICATION_PREVIEW, notification_model.preview);
            contentValues.put(NOTIFICATION_BODY, notification_model.body);
            contentValues.put(NOTIFICATION_DATE, notification_model.date);
            contentValues.put(NOTIFICATION_READ, notification_model.read);

            db.insert(NOTIFICATION_TABLE_NAME, null, contentValues);

        } catch (Exception e) {
            Helpers.logThis(TAG_LOG, e.toString());
        }
    }

    public void updateNotificationRead(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATION_READ, 1);
        String whereClause = NOTIFICATION_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        db.update(NOTIFICATION_TABLE_NAME, contentValues, whereClause, whereArgs);
        db.close();
    }

    public void deleteNotificationByID(String notification_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = NOTIFICATION_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{notification_id};
        db.delete(NOTIFICATION_TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public ArrayList<NotificationModel> getAllNotifications() {

        ArrayList<NotificationModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(NOTIFICATION_TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    NotificationModel notificationModel = new NotificationModel();
                    notificationModel.table_id = cursor.getInt(cursor.getColumnIndex(NOTIFICATION_TABLE_ID));
                    notificationModel.title = cursor.getString(cursor.getColumnIndex(NOTIFICATION_TITLE));
                    notificationModel.body = cursor.getString(cursor.getColumnIndex(NOTIFICATION_BODY));
                    notificationModel.date = cursor.getString(cursor.getColumnIndex(NOTIFICATION_DATE));
                    notificationModel.read = cursor.getInt(cursor.getColumnIndex(NOTIFICATION_READ));

                    list.add(notificationModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    public int getAllUnreadNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = NOTIFICATION_READ + " = ?";
        String[] whereArgs = new String[]{"0"};

        Cursor cursor = db.query(NOTIFICATION_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            cursor.close();
            return count;
        } else {
            return 0;
        }
    }

}