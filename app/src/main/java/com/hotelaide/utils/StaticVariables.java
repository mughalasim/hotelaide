package com.hotelaide.utils;

public class StaticVariables {

    // SHARED PREFERENCE VARIABLES =====================================================================
    public final static int INT_ANIMATION_TIME = 1200;

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
    public static final String USER_ABOUT = "USER_ABOUT";
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
    public static final String ALLOW_PUSH_MESSAGES = "ALLOW_PUSH_MESSAGES";
    public static final String ALLOW_PUSH_NOTIFICATIONS = "ALLOW_PUSH_NOTIFICATIONS";
    public static final String APP_IS_RUNNING = "APP_IS_RUNNING";

    // BOOLEAN FIRST LAUNCHED SCREENS ==============================================================
    public static final String FIRST_LAUNCH_DASH = "FIRST_LAUNCH_DASH";
    public static final String FIRST_LAUNCH_PROFILE = "FIRST_LAUNCH_PROFILE";
    public static final String FIRST_LAUNCH_PROFILE_EDIT = "FIRST_LAUNCH_PROFILE_EDIT";
    public static final String FIRST_LAUNCH_ADDRESS = "FIRST_LAUNCH_ADDRESS";
    public static final String FIRST_LAUNCH_SEARCH = "FIRST_LAUNCH_SEARCH";
    public static final String FIRST_LAUNCH_SEARCH_MEMBERS = "FIRST_LAUNCH_SEARCH_MEMBERS";
    public static final String FIRST_LAUNCH_MESSAGES = "FIRST_LAUNCH_MESSAGES";

    // FLOAT VARIABLES
    public final static float FLOAT_GOOGLE_MAP_ZOOM = 14f;

    // PERMISSION VARIABLES ========================================================================
    public final static int INT_PERMISSIONS_CAMERA = 601;
    public final static int INT_PERMISSIONS_LOCATIONS = 602;
    public final static int INT_PERMISSIONS_CALL = 603;
    public final static int INT_PERMISSIONS_STORAGE = 604;
    public static int INT_JOB_ID = 0;

    // BUNDLE EXTRA VARIABLES ======================================================================
    public final static String EXTRA_STRING = "EXTRA_STRING";
    public final static String EXTRA_START_FIRST_TIME = "FIRST_TIMER";
    public final static String EXTRA_START_RETURN = "RETURN";
    public final static String EXTRA_START_LAUNCH = "LAUNCH";
    public final static String EXTRA_PROFILE_BASIC = "BASIC";
    public final static String EXTRA_PROFILE_ADDRESS = "ADDRESS";
    public final static String EXTRA_PROFILE_DOCUMENTS = "DOCUMENTS";
    public final static String EXTRA_PROFILE_EDUCATION = "EDUCATION";
    public final static String EXTRA_PROFILE_WORK = "WORK";
    public final static String EXTRA_PROFILE_PASS = "PASSWORD";
    public final static String EXTRA_FAILED = "FAILED";
    public final static String EXTRA_PASSED = "PASSED";
    public final static String EXTRA_MY_MESSAGES_INBOX = "EXTRA_MY_MESSAGES_INBOX";
    public final static String EXTRA_MY_MESSAGES_NOTIFICATIONS = "EXTRA_MY_MESSAGES_NOTIFICATIONS";

    // BROADCAST VARIABLES =========================================================================
    public final static String BROADCAST_LOG_OUT = "BROADCAST_LOG_OUT";
    public final static String BROADCAST_UPLOAD_COMPLETE = "BROADCAST_UPLOAD_COMPLETE";
    public final static String BROADCAST_SET_USER_COMPLETE = "BROADCAST_SET_USER_COMPLETE";

    // GLOBAL STATIC VARIABLES======================================================================
    public static String STR_SHARE_LINK = "";
    public static String CHANNEL_ID = "CHANNEL_ID";
    public static String CHANNEL_NAME = "CHANNEL_NAME";
    public static String CHANNEL_DESC = "CHANNEL_DESC";

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
    // FILTERED JOBS TABLE =========================================================================
    public static final String FILTERED_JOBS_TABLE_NAME = "FILTERED_JOBS";
    public static final String FILTERED_JOBS_TABLE_ID = "id";
    public static final String FILTERED_JOBS_ID = "job_id";
    public static final String FILTERED_JOBS_BY = "filter_by";
    public static final String FILTER_TYPE_APPLIED = "APPLIED";
    public static final String FILTER_TYPE_SAVED = "SAVED";
    // DOCUMENTS TABLE =============================================================================
    public static final String DOCUMENTS_TABLE_NAME = "DOCUMENTS";
    public static final String DOCUMENTS_ID = "id";
    public static final String DOCUMENTS_NAME = "name";
    public static final String DOCUMENTS_IMAGE = "image";
    public static final String DOCUMENTS_FILE_URL = "url";
    public static final String DOCUMENTS_DATE_UPLOADED = "date_uploaded";
    public static final String DOCUMENTS_FILE_TYPE = "file_type";
    public static final String DOCUMENTS_IS_DIRTY = "is_dirty";
    // NOTIFICATIONS TABLE =========================================================================
    public static final String NOTIFICATION_TABLE_NAME = "NOTIFICATIONS";
    public static final String NOTIFICATION_TABLE_ID = "table_id";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_PREVIEW = "preview";
    public static final String NOTIFICATION_BODY = "body";
    public static final String NOTIFICATION_JOB_ID = "id";
    public static final String NOTIFICATION_DATE = "date";
    public static final String NOTIFICATION_READ = "read";
    public static final String NOTIFICATION_TYPE_CODE = "type_code";
    public static final int NOTIFICATION_TYPE_CODE_MESSAGE = 1;
    public static final int NOTIFICATION_TYPE_CODE_SHORTLIST = 2;
    public static final int NOTIFICATION_TYPE_CODE_INTERVIEW = 3;

}
