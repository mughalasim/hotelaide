package com.hotelaide.utils;

public class StaticVariables {

// SHARED PREFERENCE VARIABLES =====================================================================
    public final static int INT_ANIMATION_TIME = 800;

    // INTEGER VARIABLE NAMES ======================================================================
    public static final String DATABASE_VERSION = "DATABASE_VERSION";
    public static final String USER_ID = "USER_ID";
    public static final String USER_GENDER = "USER_GENDER";
    public static final String USER_AVAILABILITY = "USER_AVAILABILITY";
    // STRING VARIABLE NAMES =======================================================================
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String USER_F_NAME = "USER_F_NAME";
    public static final String USER_L_NAME = "USER_L_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_IMG_AVATAR = "USER_IMG_AVATAR";
    public static final String USER_IMG_BANNER = "USER_IMG_BANNER";
    public static final String PROFILE_URL = "PROFILE_URL";
    public static final String USER_COUNTRY_CODE = "USER_COUNTRY_CODE";
    public static final String USER_PHONE = "USER_PHONE";
    public static final String USER_DOB = "USER_DOB";
    public static final String USER_FB_ID = "USER_FB_ID";
    public static final String USER_GOOGLE_ID = "USER_GOOGLE_ID";
    public static final String USER_LAT = "USER_LAT";
    public static final String USER_LNG = "USER_LNG";
    public static final String USER_POSTAL_CODE = "USER_POSTAL_CODE";
    public static final String USER_FULL_ADDRESS = "USER_FULL_ADDRESS";
    public static final String USER_COUNTY = "USER_COUNTY";
    public static final String USER_URL = "USER_URL";
    public static final String USER_PROFILE_COMPLETION = "USER_PROFILE_COMPLETION";
    public static final String EXPERIENCE_TYPE_WORK = "WORK_EXPERIENCE";
    public static final String EXPERIENCE_TYPE_EDUCATION = "EDUCATION_EXPERIENCE";
    // BOOLEAN VARIABLE NAMES ======================================================================
    public static final String ALLOW_UPDATE_APP = "ALLOW_UPDATE_APP";
    public static final String ALLOW_MESSAGE_PUSH = "ALLOW_MESSAGE_PUSH";
    public static final String APP_IS_RUNNING = "APP_IS_RUNNING";
    // FLOAT VARIABLES
    public final static float FLOAT_GOOGLE_MAP_ZOOM = 14f;

// PERMISSION VARIABLES ============================================================================
    public final static int INT_PERMISSIONS_CAMERA = 601;
    public final static int INT_PERMISSIONS_LOCATIONS = 602;
    public final static int INT_PERMISSIONS_CALL = 603;
    public final static int INT_PERMISSIONS_STORAGE = 604;


// BUNDLE EXTRA VARIABLES ==========================================================================
    public final static String START_FIRST_TIME = "FIRST_TIMER";
    public final static String START_RETURN = "RETURN";
    public final static String START_LAUNCH = "LAUNCH";

// BROADCAST VARIABLES =============================================================================
    public final static String BroadcastValue = "com.hotelaide.ACTIONLOGOUT";
    public static final String BROADCAST_GPS = "BROADCAST_GPS";

// GLOBAL STATIC VARIABLES==========================================================================
    public static String STR_SHARE_LINK = "";

// DATABASE VARIABLES ==========================================================================
    // JOBS TABLE ==================================================================================
    public static final String JOB_TABLE_NAME = "JOB_SEARCH";
    public static final String JOB_ID = "id";
    public static final String JOB_NAME = "job_name";
    public static final String JOB_POSTED_ON = "posted_on";
    public static final String JOB_ESTABLISHMENT_ID = "establishment_id";
    public static final String JOB_ESTABLISHMENT_IMAGE = "establishment_image";
    public static final String JOB_ESTABLISHMENT_LOCATION = "establishment_location";
    // EXPERIENCE TABLE ============================================================================
    public static final String EXP_TABLE_NAME = "EXPERIENCE";
    public static final String EXP_TABLE_ID = "id";
    public static final String EXP_ID = "experience_id";
    public static final String EXP_NAME = "name";
    public static final String EXP_POSITION = "position";
    public static final String EXP_LEVEL = "level";
    public static final String EXP_START_DATE = "start_date";
    public static final String EXP_END_DATE = "end_date";
    public static final String EXP_RESPONSIBILITIES_FIELD = "responsibilities_field";
    public static final String EXP_CURRENT = "current";
    public static final String EXP_TYPE = "type";
    // FILTERS TABLES ==============================================================================
    public static final String COUNTY_TABLE_NAME = "COUNTIES";
    public static final String JOB_TYPE_TABLE_NAME = "JOB_TYPE";
    public static final String CATEGORIES_TABLE_NAME = "CATEGORIES";
    public static final String EDUCATION_LEVEL_TABLE_NAME = "EDUCATION_LEVEL";
    public static final String FILTER_ID = "id";
    public static final String FILTER_NAME = "name";
    // APPLIED JOBS TABLE ==================================================================================
    public static final String APPLIED_JOBS_TABLE_NAME = "APPLIED_JOBS";
    public static final String APPLIED_JOBS_TABLE_ID = "id";
    public static final String APPLIED_JOBS_ID = "job_id";

}
