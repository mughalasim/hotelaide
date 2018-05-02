package com.hotelaide.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main_pages.models.CollectionModel;
import com.hotelaide.main_pages.models.ReservationModel;
import com.hotelaide.main_pages.models.RestaurantModel;
import com.hotelaide.main_pages.models.UserModel;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EatOut_DataBase.db";
    private static final String TAG_LOG = "DATABASE";

    // DATABASE FETCH FUNCTIONS ====================================================================
    public static final String RETRIEVE_ALL_RESTAURANTS = "RETRIEVE_ALL_RESTAURANTS";
    public static final String RETRIEVE_LIKED_RESTAURANTS = "RETRIEVE_LIKED_RESTAURANTS";
    public static final String RETRIEVE_COLLECTION_RESTAURANTS = "RETRIEVE_COLLECTION_RESTAURANTS";

    // CUISINE DETAILS STORED IN THE DATABASE ======================================================
    public static final String CUISINE_TABLE_NAME = "Cuisine_Table";
    public static final String CUISINE_TABLE_ID = "Cuisine_Id";
    public static final String CUISINE_NAME = "Name";

    // CITY DETAILS STORED IN THE DATABASE =========================================================
    public static final String CITY_TABLE_NAME = "City_Table";
    public static final String CITY_TABLE_ID = "City_Id";
    public static final String CITY_NAME = "Name";

    // AREA DETAILS STORED IN THE DATABASE =========================================================
    public static final String AREA_TABLE_NAME = "Area_Table";
    public static final String AREA_TABLE_ID = "Area_Id";
    private static final String AREA_CITY_ID = "City_Id";
    public static final String AREA_NAME = "Name";

    // TYPE DETAILS STORED IN THE DATABASE =========================================================
    public static final String TYPE_TABLE_NAME = "Type_Table";
    public static final String TYPE_TABLE_ID = "Type_Id";
    public static final String TYPE_NAME = "Name";

    // LIKED RESTAURANTS STORED IN THE DATABASE ====================================================
    private static final String LIKED_RESTAURANTS_TABLE_NAME = "Liked_Restaurant_Table";
    private static final String LIKED_RESTAURANT_TABLE_ID = "Restaurant_Id";

    // COLLECTION RESTAURANT STORED IN THE DATABASE ================================================
    private static final String COLLECTION_RESTAURANT_TABLE_NAME = "Collection_Restaurant_Table";
    private static final String COLLECTION_RESTAURANT_TABLE_ID = "Table_Id";
    private static final String COLLECTION_RESTAURANT_COLLECTION_ID = "Collection_Id";
    private static final String COLLECTION_RESTAURANT_COLLECTION_URL = "Url";
    private static final String COLLECTION_RESTAURANT_TITLE = "Title";
    private static final String COLLECTION_RESTAURANT_DESC = "Description";
    private static final String COLLECTION_RESTAURANT_ID = "Restaurant_Id";

    // RESTAURANTS STORED IN THE DATABASE =========================================================
    private static final String RESTAURANT_TABLE_NAME = "Restaurant_Table";
    private static final String RESTAURANT_ID = "Restaurant_Id";
    private static final String RESTAURANT_NAME = "Name";
    private static final String RESTAURANT_SLUG = "Slug";
    private static final String RESTAURANT_AVERAGE_RATING = "Average_Rating";
    private static final String RESTAURANT_IMAGE = "Image";
    private static final String RESTAURANT_CUISINE_NAME = "Cuisine_Name";
    private static final String RESTAURANT_DISTANCE = "Distance";
    private static final String RESTAURANT_CITY_ID = "City_Id";
    private static final String RESTAURANT_CITY_NAME = "City_Name";
    private static final String RESTAURANT_AREA_NAME = "Area_Name";
    private static final String RESTAURANT_OFFER_ID = "Offer_Id";
    private static final String RESTAURANT_PREMIUM_LEVEL = "Premium_Level";
    private static final String RESTAURANT_OFFER_ICON = "Offer_Icon";
    private static final String RESTAURANT_SPONSOR_ICON = "Sponsor_Icon";

    // RESTAURANTS STORED IN THE DATABASE =========================================================
    private static final String USER_TABLE_NAME = "User_Table";
    private static final String USER_ID = "User_Id";
    private static final String USER_TOKEN = "Token";
    private static final String USER_SALUTATION = "Salutation";
    private static final String USER_FIRST_NAME = "First_Name";
    private static final String USER_LAST_NAME = "Last_Name";
    private static final String USER_EMAIL = "Email";
    private static final String USER_IMAGE = "Image";
    private static final String USER_COUNTRY_CODE = "Country_Code";
    private static final String USER_CITY_ID = "City_Id";
    private static final String USER_PHONE = "Phone";
    private static final String USER_DOB = "Dob";
    private static final String USER_POINTS = "Points";
    private static final String USER_FB_ID = "Fb_Id";

    // RESERVATIONS STORED IN THE DATABASE =========================================================
    private static final String RESERVATION_TABLE_NAME = "Reservation_Table";
    private static final String RESERVATION_ID = "Reservation_Id";
    private static final String RESERVATION_USER_ID = "User_id";
    private static final String RESERVATION_RESTAURANT_ID = "Restaurant_Id";
    private static final String RESERVATION_RESTAURANT_NAME = "Restaurant_Name";
    private static final String RESERVATION_IMAGE = "Image";
    private static final String RESERVATION_TIME = "Time";
    private static final String RESERVATION_DATE = "Date";
    private static final String RESERVATION_CREATED = "Created_Date";
    private static final String RESERVATION_PERSONS = "Persons";
    private static final String RESERVATION_CHILDREN = "Children";
    private static final String RESERVATION_NOTE = "Note";
    private static final String RESERVATION_STATE = "State";

    public static UserModel userModel = new UserModel();

    public Database() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + CUISINE_TABLE_NAME +
                "(" + CUISINE_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                CUISINE_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + CITY_TABLE_NAME +
                "(" + CITY_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                CITY_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + AREA_TABLE_NAME +
                "(" + AREA_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                AREA_CITY_ID + "  TEXT," +
                AREA_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TYPE_TABLE_NAME +
                "(" + TYPE_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                TYPE_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + LIKED_RESTAURANTS_TABLE_NAME +
                "(" + LIKED_RESTAURANT_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + COLLECTION_RESTAURANT_TABLE_NAME +
                "(" + COLLECTION_RESTAURANT_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                COLLECTION_RESTAURANT_COLLECTION_ID + " TEXT," +
                COLLECTION_RESTAURANT_COLLECTION_URL + " TEXT," +
                COLLECTION_RESTAURANT_TITLE + " TEXT," +
                COLLECTION_RESTAURANT_DESC + " TEXT," +
                COLLECTION_RESTAURANT_ID + " TEXT" +
                ")"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + RESTAURANT_TABLE_NAME +
                "(" + RESTAURANT_ID + " INTEGER PRIMARY KEY NOT NULL," +
                RESTAURANT_NAME + " TEXT," +
                RESTAURANT_SLUG + " TEXT," +
                RESTAURANT_AVERAGE_RATING + " TEXT," +
                RESTAURANT_IMAGE + " TEXT," +
                RESTAURANT_CITY_NAME + " TEXT," +
                RESTAURANT_AREA_NAME + " TEXT," +
                RESTAURANT_CUISINE_NAME + " TEXT," +
                RESTAURANT_OFFER_ICON + " TEXT," +
                RESTAURANT_SPONSOR_ICON + " TEXT," +
                RESTAURANT_OFFER_ID + " INTEGER," +
                RESTAURANT_PREMIUM_LEVEL + " INTEGER," +
                RESTAURANT_DISTANCE + " TEXT," +
                RESTAURANT_CITY_ID + " TEXT" +
                ")"
        );


        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                USER_TABLE_NAME + "(" +
                USER_ID + " INTEGER PRIMARY KEY NOT NULL," +
                USER_TOKEN + " TEXT," +
                USER_SALUTATION + " TEXT," +
                USER_FIRST_NAME + " TEXT," +
                USER_LAST_NAME + " TEXT," +
                USER_EMAIL + " TEXT," +
                USER_IMAGE + " TEXT," +
                USER_COUNTRY_CODE + " TEXT," +
                USER_CITY_ID + " TEXT," +
                USER_PHONE + " TEXT," +
                USER_DOB + " TEXT," +
                USER_POINTS + " TEXT," +
                USER_FB_ID + " TEXT" +
                ")"
        );


        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                RESERVATION_TABLE_NAME + "(" +
                RESERVATION_ID + " INTEGER PRIMARY KEY NOT NULL," +
                RESERVATION_USER_ID + " TEXT," +
                RESERVATION_RESTAURANT_ID + " TEXT," +
                RESERVATION_RESTAURANT_NAME + " TEXT," +
                RESERVATION_IMAGE + " TEXT," +
                RESERVATION_TIME + " TEXT," +
                RESERVATION_DATE + " TEXT," +
                RESERVATION_CREATED + " TEXT," +
                RESERVATION_PERSONS + " TEXT," +
                RESERVATION_CHILDREN + " TEXT," +
                RESERVATION_NOTE + " TEXT," +
                RESERVATION_STATE + " TEXT" +
                ")"
        );

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


    // DELETE ALL FUNCTIONS ========================================================================
    public void deleteLikeTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LIKED_RESTAURANTS_TABLE_NAME);

    }

    public void deleteReservationTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + RESERVATION_TABLE_NAME);

    }

    public void deleteCollectionTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + COLLECTION_RESTAURANT_TABLE_NAME);
    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CUISINE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AREA_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RESTAURANT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + COLLECTION_RESTAURANT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RESERVATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LIKED_RESTAURANTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);

        onCreate(db);
    }


    // GENERIC FUNCTIONS FOR THE LISTS IN THE SEARCH PAGE ==========================================
    private void setToDB(String tableName, String columnName, String tableID, ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = columnName + " = ?";
        String[] whereArgs = new String[]{tableID};
        int no_of_rows_affected = db.update(tableName, contentValues, whereClause,
                whereArgs);
        if (no_of_rows_affected == 0) {
            db.insert(tableName, null, contentValues);
        }
    }

    public List<String> getListItems(String tableName, String columnName) {
        final List<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                arrayList.add(cursor.getString(cursor.getColumnIndex(columnName)));
                cursor.moveToNext();
            }
        } else {
            arrayList.add("");
        }
        cursor.close();
        return arrayList;
    }

    public List<String> getAreasBasedOnCity(String cityID) {
        final List<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = AREA_CITY_ID + " = ?";
        String[] whereArgs = new String[]{cityID};
        Cursor cursor = db.query(AREA_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                arrayList.add(cursor.getString(cursor.getColumnIndex(AREA_NAME)));
                cursor.moveToNext();
            }
        } else {
            arrayList.add("");
        }
        cursor.close();
        return arrayList;
    }

    public String getListIDByName(String tableName, String columnName, String columnID, String selectedName){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = columnName + " = ?";
        String[] whereArgs = new String[]{selectedName};
        Cursor cursor = db.query(tableName, null, whereClause, whereArgs,
                null, null, null);
        cursor.moveToFirst();
        String response = "";
        try {
            response = cursor.getString(cursor.getColumnIndex(columnID));
            cursor.close();
            return response;

        } catch (Exception e) {
            cursor.close();
            return response;
        }
    }

    public String getCityNameByID(String cityID){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = CITY_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{cityID};
        Cursor cursor = db.query(CITY_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        cursor.moveToFirst();
        String response = "";
        try {
            response = cursor.getString(cursor.getColumnIndex(CITY_NAME));
            cursor.close();
            return response;

        } catch (Exception e) {
            cursor.close();
            return response;
        }
    }

    // CITY FUNCTIONS ==============================================================================
    public void setCity(String cityID, String cityName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CITY_TABLE_ID, cityID);
        contentValues.put(CITY_NAME, cityName);
        setToDB(CITY_TABLE_NAME, CITY_TABLE_ID, cityID, contentValues);
    }


    // CUISINE FUNCTIONS ===========================================================================
    public void setCuisine(String CuisineID, String CuisineName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUISINE_TABLE_ID, CuisineID);
        contentValues.put(CUISINE_NAME, CuisineName);
        setToDB(CUISINE_TABLE_NAME, CUISINE_TABLE_ID, CuisineID, contentValues);
    }


    // AREA FUNCTIONS ==============================================================================
    public void setArea(String areaID, String areaName, String areaCityID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AREA_TABLE_ID, areaID);
        contentValues.put(AREA_NAME, areaName);
        contentValues.put(AREA_CITY_ID, areaCityID);
        setToDB(AREA_TABLE_NAME, AREA_TABLE_ID, areaID, contentValues);
    }


    // TYPE FUNCTIONS ==============================================================================
    public void setType(String typeID, String typeName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TYPE_TABLE_ID, typeID);
        contentValues.put(TYPE_NAME, typeName);
        setToDB(TYPE_TABLE_NAME, TYPE_TABLE_ID, typeID, contentValues);
    }


    // LIKED RESTAURANT FUNCTIONS ==================================================================
    public void setLikedRestaurant(String RestaurantID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LIKED_RESTAURANT_TABLE_ID, RestaurantID);
        setToDB(LIKED_RESTAURANTS_TABLE_NAME, LIKED_RESTAURANT_TABLE_ID, RestaurantID, contentValues);
    }

    public Boolean getLikedRestaurantIDMatch(String RestaurantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = LIKED_RESTAURANT_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(RestaurantID)};
        Cursor cursor = db.query(LIKED_RESTAURANTS_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public void deleteLikedRestaurant(String RestaurantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = LIKED_RESTAURANT_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(RestaurantID)};
        db.delete(LIKED_RESTAURANTS_TABLE_NAME, whereClause, whereArgs);
    }


    // COLLECTION RESTAURANT =======================================================================
    public void setCollectionRestaurants(CollectionModel collectionModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLLECTION_RESTAURANT_COLLECTION_ID, collectionModel.collection_id);
        contentValues.put(COLLECTION_RESTAURANT_COLLECTION_URL, collectionModel.collection_url);
        contentValues.put(COLLECTION_RESTAURANT_TITLE, collectionModel.collection_title);
        contentValues.put(COLLECTION_RESTAURANT_DESC, collectionModel.collection_desc);
        contentValues.put(COLLECTION_RESTAURANT_ID, collectionModel.collection_rest_id);
        db.insert(COLLECTION_RESTAURANT_TABLE_NAME, null, contentValues);
    }

    public CollectionModel getCollectionRestaurant(String collection_id) {
        CollectionModel collectionModel = new CollectionModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COLLECTION_RESTAURANT_COLLECTION_ID + " = ?";
        String[] whereArgs = new String[]{collection_id};
        Cursor cursor = db.query(COLLECTION_RESTAURANT_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    collectionModel.collection_title = cursor.getString(cursor.getColumnIndex(COLLECTION_RESTAURANT_TITLE));
                    collectionModel.collection_desc = cursor.getString(cursor.getColumnIndex(COLLECTION_RESTAURANT_DESC));
                    collectionModel.collection_url = cursor.getString(cursor.getColumnIndex(COLLECTION_RESTAURANT_COLLECTION_URL));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return collectionModel;
    }


    // RESTAURANT FUNCTIONS ========================================================================
    public RestaurantModel setRestaurants(JSONObject json_data) {

        RestaurantModel restaurantModel = new RestaurantModel();
        try {
            restaurantModel.id = json_data.getString("id");
            restaurantModel.restaurant_name = json_data.getString("name");
            restaurantModel.restaurant_slug = json_data.getString("slug");
            restaurantModel.average_rating = json_data.getString("average_rating");
            restaurantModel.image = json_data.getString("full_thumb_image_url");

            if (json_data.getDouble("distance") == 0) {
                restaurantModel.distance = "Unknown";
            } else {
                restaurantModel.distance = formatDistance(json_data.getDouble("distance"));
            }

            if(!json_data.isNull("area")) {
                JSONObject area_name = json_data.getJSONObject("area");
                if (!area_name.isNull("name")) {
                    restaurantModel.area_name = area_name.getString("name");
                }
                JSONObject city = json_data.getJSONObject("city");
                if (!area_name.isNull("name")) {
                    restaurantModel.city_id = city.getString("id");
                    restaurantModel.city_name = city.getString("name");
                }
            }

            restaurantModel.sponsor_icon = json_data.getString("sponsor_icon");

            restaurantModel.offer_icon = json_data.getString("offer_icon");
            if (!json_data.getString("offer_id").equals("")) {
                restaurantModel.offer_id = json_data.getInt("offer_id");
            }

            restaurantModel.premium_level = json_data.getInt("premium_level");

            JSONArray cuisine_name_array = json_data.getJSONArray("cuisines");
            for (int v = 0; v < cuisine_name_array.length(); v++) {
                JSONObject cuisine_name = cuisine_name_array.getJSONObject(v);
                if (!cuisine_name.isNull("name")) {
                    restaurantModel.cuisine_name = restaurantModel.cuisine_name.
                            concat(cuisine_name.getString("name").concat(" | "));
                }
            }

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(RESTAURANT_ID, restaurantModel.id);
            contentValues.put(RESTAURANT_NAME, restaurantModel.restaurant_name);
            contentValues.put(RESTAURANT_SLUG, restaurantModel.restaurant_slug);
            contentValues.put(RESTAURANT_AVERAGE_RATING, restaurantModel.average_rating);
            contentValues.put(RESTAURANT_IMAGE, restaurantModel.image);
            contentValues.put(RESTAURANT_CITY_NAME, restaurantModel.city_name);
            contentValues.put(RESTAURANT_AREA_NAME, restaurantModel.area_name);
            contentValues.put(RESTAURANT_CUISINE_NAME, restaurantModel.cuisine_name);
            contentValues.put(RESTAURANT_OFFER_ICON, restaurantModel.offer_icon);
            contentValues.put(RESTAURANT_SPONSOR_ICON, restaurantModel.sponsor_icon);
            contentValues.put(RESTAURANT_OFFER_ID, restaurantModel.offer_id);
            contentValues.put(RESTAURANT_PREMIUM_LEVEL, restaurantModel.premium_level);
            contentValues.put(RESTAURANT_DISTANCE, restaurantModel.distance);
            contentValues.put(RESTAURANT_CITY_ID, restaurantModel.city_id);

            String whereClause = RESTAURANT_ID + " = ?";
            String[] whereArgs = new String[]{restaurantModel.id};
            int no_of_rows_affected = db.update(RESTAURANT_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(RESTAURANT_TABLE_NAME, null, contentValues);
            }


        } catch (JSONException e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            return restaurantModel;
        }
        return restaurantModel;
    }

    public ArrayList<RestaurantModel> getAllRestaurants(String RetrievalType, String COLLECTION_ID) {

        ArrayList<RestaurantModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause;
        String[] whereArgs;
        Cursor cursor = null;

        switch (RetrievalType) {
            case RETRIEVE_ALL_RESTAURANTS:
                whereClause = RESTAURANT_CITY_ID + " = ?";
                whereArgs = new String[]{userModel.city_id};
                cursor = db.query(RESTAURANT_TABLE_NAME, null, whereClause, whereArgs,
                        null, null, RESTAURANT_PREMIUM_LEVEL + " DESC, " + RESTAURANT_NAME + " ASC");
                break;

            case RETRIEVE_LIKED_RESTAURANTS:
                cursor = db.rawQuery(createJoin(LIKED_RESTAURANTS_TABLE_NAME,
                        LIKED_RESTAURANT_TABLE_ID), null);
                break;

            case RETRIEVE_COLLECTION_RESTAURANTS:
                cursor = db.rawQuery(createJoinWithClause(COLLECTION_RESTAURANT_TABLE_NAME,
                        COLLECTION_RESTAURANT_ID, COLLECTION_ID), null);
                break;

        }
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    RestaurantModel restaurantModel = new RestaurantModel();
                    restaurantModel.id = cursor.getString(cursor.getColumnIndex(RESTAURANT_ID));
                    restaurantModel.restaurant_name = cursor.getString(cursor.getColumnIndex(RESTAURANT_NAME));
                    restaurantModel.restaurant_slug = cursor.getString(cursor.getColumnIndex(RESTAURANT_SLUG));
                    restaurantModel.average_rating = cursor.getString(cursor.getColumnIndex(RESTAURANT_AVERAGE_RATING));
                    restaurantModel.image = cursor.getString(cursor.getColumnIndex(RESTAURANT_IMAGE));
                    restaurantModel.city_name = cursor.getString(cursor.getColumnIndex(RESTAURANT_CITY_NAME));
                    restaurantModel.area_name = cursor.getString(cursor.getColumnIndex(RESTAURANT_AREA_NAME));
                    restaurantModel.cuisine_name = cursor.getString(cursor.getColumnIndex(RESTAURANT_CUISINE_NAME));
                    restaurantModel.offer_icon = cursor.getString(cursor.getColumnIndex(RESTAURANT_OFFER_ICON));
                    restaurantModel.sponsor_icon = cursor.getString(cursor.getColumnIndex(RESTAURANT_SPONSOR_ICON));
                    restaurantModel.offer_id = cursor.getInt(cursor.getColumnIndex(RESTAURANT_OFFER_ID));
                    restaurantModel.premium_level = cursor.getInt(cursor.getColumnIndex(RESTAURANT_PREMIUM_LEVEL));
                    restaurantModel.distance = cursor.getString(cursor.getColumnIndex(RESTAURANT_DISTANCE));

                    list.add(restaurantModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    public void deleteOneRestaurant(String RestaurantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = RESTAURANT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(RestaurantID)};
        db.delete(RESTAURANT_TABLE_NAME, whereClause, whereArgs);
    }


    // RESERVATION FUNCTIONS =======================================================================
    public ReservationModel setReservation(JSONObject json_data) {

        ReservationModel reservationModel = new ReservationModel();
        try {
            reservationModel.id = json_data.getInt("id");
            reservationModel.user_id = json_data.getString("user_id");
            reservationModel.restaurant_id = json_data.getString("restaurant_id");
            reservationModel.restaurant_name = json_data.getString("restaurant_name");
            reservationModel.image = json_data.getString("full_restaurant_thumb_image");
            reservationModel.time = json_data.getString("reservation_time");
            reservationModel.date = json_data.getString("reservation_date");
            reservationModel.created_date = json_data.getString("created_date");
            reservationModel.persons = json_data.getString("persons");
            reservationModel.children = json_data.getString("children");
            reservationModel.note = json_data.getString("note");
            reservationModel.state = json_data.getString("state");


            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(RESERVATION_ID, reservationModel.id);
            contentValues.put(RESERVATION_USER_ID, reservationModel.user_id);
            contentValues.put(RESERVATION_RESTAURANT_ID, reservationModel.restaurant_id);
            contentValues.put(RESERVATION_RESTAURANT_NAME, reservationModel.restaurant_name);
            contentValues.put(RESERVATION_IMAGE, reservationModel.image);
            contentValues.put(RESERVATION_TIME, reservationModel.time);
            contentValues.put(RESERVATION_DATE, reservationModel.date);
            contentValues.put(RESERVATION_CREATED, reservationModel.created_date);
            contentValues.put(RESERVATION_PERSONS, reservationModel.persons);
            contentValues.put(RESERVATION_CHILDREN, reservationModel.children);
            contentValues.put(RESERVATION_NOTE, reservationModel.note);
            contentValues.put(RESERVATION_STATE, reservationModel.state);

            String whereClause = RESERVATION_ID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(reservationModel.id)};
            int no_of_rows_affected = db.update(RESERVATION_TABLE_NAME, contentValues, whereClause,
                    whereArgs);

            if (no_of_rows_affected == 0) {
                db.insert(RESERVATION_TABLE_NAME, null, contentValues);
            }


        } catch (JSONException e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            return reservationModel;
        }
        return reservationModel;
    }

    public ArrayList<ReservationModel> getAllReservations() {

        ArrayList<ReservationModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(RESERVATION_TABLE_NAME, null, null, null, null, null, RESERVATION_ID + " DESC");
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    ReservationModel reservationModel = new ReservationModel();
                    reservationModel.id = cursor.getInt(cursor.getColumnIndex(RESERVATION_ID));
                    reservationModel.user_id = cursor.getString(cursor.getColumnIndex(RESERVATION_USER_ID));
                    reservationModel.restaurant_id = cursor.getString(cursor.getColumnIndex(RESERVATION_RESTAURANT_ID));
                    reservationModel.restaurant_name = cursor.getString(cursor.getColumnIndex(RESERVATION_RESTAURANT_NAME));
                    reservationModel.image = cursor.getString(cursor.getColumnIndex(RESERVATION_IMAGE));
                    reservationModel.time = cursor.getString(cursor.getColumnIndex(RESERVATION_TIME));
                    reservationModel.date = cursor.getString(cursor.getColumnIndex(RESERVATION_DATE));
                    reservationModel.created_date = cursor.getString(cursor.getColumnIndex(RESERVATION_CREATED));
                    reservationModel.persons = cursor.getString(cursor.getColumnIndex(RESERVATION_PERSONS));
                    reservationModel.children = cursor.getString(cursor.getColumnIndex(RESERVATION_CHILDREN));
                    reservationModel.note = cursor.getString(cursor.getColumnIndex(RESERVATION_NOTE));
                    reservationModel.state = cursor.getString(cursor.getColumnIndex(RESERVATION_STATE));

                    list.add(reservationModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }


    // USER FUNCTIONS ==============================================================================
    public Boolean setUser(JSONObject main) {
        Helpers.LogThis(TAG_LOG, "USER SET");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Boolean response;
        Helpers.LogThis(TAG_LOG, main.toString());
        try {
            String request_msg = main.getString("success");
            if (request_msg.equals("true")) {

                JSONObject user = main.getJSONObject("user");

                userModel.user_id = user.getInt("id");
                userModel.user_token = formatString(user.getString("user_token"));
                userModel.salutation = formatString(user.getString("salutation"));
                userModel.first_name = formatString(user.getString("first_name"));
                userModel.last_name = formatString(user.getString("last_name"));
                userModel.email = formatString(user.getString("email"));
                userModel.profile_pic = formatString(user.getString("thumbnail"));
                userModel.banner_pic = formatString(user.getString("banner_pic"));
                userModel.phone = formatString(user.getString("phone"));
                userModel.dob = formatString(user.getString("date_of_birth"));
                userModel.points = user.getString("points");
                userModel.fb_id = user.getString("fb_id");
                JSONObject city = user.getJSONObject("city");
                if (!city.isNull("id")) {
                    userModel.city_id = city.getString("id");
                } else {
                    userModel.city_id = "";
                }

                contentValues.put(USER_ID, userModel.user_id);
                contentValues.put(USER_TOKEN, userModel.user_token);
                contentValues.put(USER_SALUTATION, userModel.salutation);
                contentValues.put(USER_FIRST_NAME, userModel.first_name);
                contentValues.put(USER_LAST_NAME, userModel.last_name);
                contentValues.put(USER_EMAIL, userModel.email);
                contentValues.put(USER_IMAGE, userModel.profile_pic);
                contentValues.put(USER_COUNTRY_CODE, userModel.banner_pic);
                contentValues.put(USER_CITY_ID, userModel.city_id);
                contentValues.put(USER_PHONE, userModel.phone);
                contentValues.put(USER_DOB, userModel.dob);
                contentValues.put(USER_POINTS, userModel.points);
                contentValues.put(USER_FB_ID, userModel.fb_id);

                String whereClause = USER_ID + " = ?";
                String[] whereArgs = new String[]{String.valueOf(userModel.user_id)};
                int no_of_rows_affected = db.update(USER_TABLE_NAME, contentValues, whereClause,
                        whereArgs);

                if (no_of_rows_affected == 0) {
                    db.insert(USER_TABLE_NAME, null, contentValues);
                }

                Helpers.LogThis(TAG_LOG, "AFTER UPDATE " +
                        userModel.user_id + " - " +
                        userModel.user_token + " - " +
                        userModel.salutation + " - " +
                        userModel.first_name + " - " +
                        userModel.last_name + " - " +
                        userModel.email + " - " +
                        userModel.profile_pic + " - " +
                        userModel.banner_pic + " - " +
                        userModel.phone + " - " +
                        userModel.dob + " - " +
                        userModel.points + " - " +
                        userModel.city_id + " - " +
                        userModel.fb_id
                );
                response = true;

            } else {
                response = false;
            }
        } catch (JSONException e) {
            Helpers.LogThis(TAG_LOG, MyApplication.getAppContext().getString(R.string.log_exception) + e.toString());
            response = false;
        } catch (Exception e) {
            Helpers.LogThis(TAG_LOG, MyApplication.getAppContext().getString(R.string.log_exception) + e.toString());
            response = false;
        }

        return response;
    }

    public UserModel getUser() {
        Helpers.LogThis(TAG_LOG, "USER GET");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(USER_TABLE_NAME, null, null, null,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    userModel.user_id = cursor.getInt(cursor.getColumnIndex(USER_ID));
                    userModel.user_token = cursor.getString(cursor.getColumnIndex(USER_TOKEN));
                    userModel.salutation = cursor.getString(cursor.getColumnIndex(USER_SALUTATION));
                    userModel.first_name = cursor.getString(cursor.getColumnIndex(USER_FIRST_NAME));
                    userModel.last_name = cursor.getString(cursor.getColumnIndex(USER_LAST_NAME));
                    userModel.email = cursor.getString(cursor.getColumnIndex(USER_EMAIL));
                    userModel.profile_pic = cursor.getString(cursor.getColumnIndex(USER_IMAGE));
                    userModel.banner_pic = cursor.getString(cursor.getColumnIndex(USER_COUNTRY_CODE));
                    userModel.city_id = cursor.getString(cursor.getColumnIndex(USER_CITY_ID));
                    userModel.phone = cursor.getString(cursor.getColumnIndex(USER_PHONE));
                    userModel.dob = cursor.getString(cursor.getColumnIndex(USER_DOB));
                    userModel.points = cursor.getString(cursor.getColumnIndex(USER_POINTS));
                    userModel.fb_id = cursor.getString(cursor.getColumnIndex(USER_FB_ID));

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return userModel;
    }

    public Boolean validateUserName() {
        String firstname = "", lastname = "";
        String[] column = {USER_FIRST_NAME, USER_LAST_NAME};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, column, null, null,
                null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    firstname = cursor.getString(cursor.getColumnIndex(USER_FIRST_NAME));
                    lastname = cursor.getString(cursor.getColumnIndex(USER_LAST_NAME));

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Helpers.LogThis(TAG_LOG, "USER FULL NAME:" + firstname + "-" + lastname);
        return (firstname.equals("") || lastname.equals(""));
    }


    // INNER JOINS =================================================================================
    private String createJoin(String NewTableName, String NewId) {
        return "SELECT * FROM " + NewTableName + " l INNER JOIN " + RESTAURANT_TABLE_NAME + " a ON l."
                + NewId + " = a." + RESTAURANT_ID;
    }

    private String createJoinWithClause(String NewTableName, String NewId, String CollectionID) {
        return "SELECT * FROM " + NewTableName + " l INNER JOIN " + RESTAURANT_TABLE_NAME + " a ON l."
                + NewId + " = a." + RESTAURANT_ID + " WHERE " + COLLECTION_RESTAURANT_COLLECTION_ID
                + " = " + CollectionID;
    }


    // VALIDATIONS AND FORMATTING ==================================================================
    public Boolean checkRestaurantTableCount() {
        boolean empty = true;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + RESTAURANT_TABLE_NAME, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) != 0);
            cur.close();
        }

        return empty;
    }

    private String formatDistance(Double d) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.round(new MathContext(4));
        double rounded = bd.doubleValue();
        if (rounded < 1) {
            BigDecimal bd2 = new BigDecimal(rounded * 1000);
            bd2 = bd2.round(new MathContext(4));
            return String.valueOf(bd2) + " Meters";
        } else {
            return String.valueOf(rounded) + " Kilometers";
        }
    }

    private String formatString(String value) {
        if (value.equals("null")) {
            return "";
        } else {
            return value;
        }
    }


    // NAVIGATION PURPOSES =========================================================================
    public String getRestaurantIDFromSlug(String Slug) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = RESTAURANT_SLUG + " = ?";
        String rest_id = "";
        String[] whereArgs = new String[]{Slug};
        Cursor cursor;
        cursor = db.query(RESTAURANT_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    rest_id = cursor.getString(cursor.getColumnIndex(RESTAURANT_ID));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return rest_id;
    }

    public String getCollectionIDFromURL(String URL) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLLECTION_RESTAURANT_COLLECTION_URL + " = ?";
        String collection_id = "";
        String[] whereArgs = new String[]{URL};
        Cursor cursor;
        cursor = db.query(COLLECTION_RESTAURANT_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);

        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    collection_id = cursor.getString(cursor.getColumnIndex(COLLECTION_RESTAURANT_COLLECTION_ID));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return collection_id;
    }

}