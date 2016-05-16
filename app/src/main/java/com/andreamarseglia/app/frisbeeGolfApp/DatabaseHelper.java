package com.andreamarseglia.app.frisbeeGolfApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joni on 7.9.2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FrisbeeGolf";
    private static final String LOG = DatabaseHelper.class.getName();

    // Table Names
    private static final String COURSE_TABLE = "course";
    private static final String LANE_TABLE = "lane";
    private static final String PLAYER_TABLE = "players";
    private static final String STATS_TABLE = "stats";
    private static final String GAME_TABLE = "game";

    // General column names
    private static final String COURSE_ID = "course_id";
    private static final String LANE_ID = "lane_id";
    private static final String PLAYER_ID = "player_id";

    // Course Table - column names
    private static final String COURSE_NAME = "course_name";
    private static final String NUMBER_OF_LANES = "number_of_lanes";

    // Lane Table - column names
    private static final String PAR = "par";

    // Player Table - column names
    private static final String PLAYER_NAME = "player_name";

    // Stats Table - column names
    private static final String TOTAL_RESULT = "total_result";
    private static final String RUN_RESULTS = "run_results";
    private static final String SAVED_AT = "saved_at";

    // Game Table - column names
    private static final String RESULT = "result";

    // Table create statements
    private static final String CREATE_COURSE_TABLE = "CREATE TABLE " + COURSE_TABLE
            + "(" + COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COURSE_NAME + " TEXT, " + NUMBER_OF_LANES + " INTEGER)";
    private static final String CREATE_LANE_TABLE = "CREATE TABLE " + LANE_TABLE
            + "("+ LANE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COURSE_ID + " INTEGER, " + PAR + " INTEGER)";
    private static final String CREATE_PLAYER_TABLE = "CREATE TABLE " + PLAYER_TABLE
            + "(" + PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PLAYER_NAME + " TEXT)";
    private static final String CREATE_STATS_TABLE = "CREATE TABLE " + STATS_TABLE
            + "(" + PLAYER_ID + " INTEGER, " + COURSE_ID + " INTEGER, " + TOTAL_RESULT + " INTEGER, "
            + RUN_RESULTS + " TEXT, " + SAVED_AT + " INTEGER(4) NOT NULL DEFAULT (strftime('%s', 'now')), "
            + "PRIMARY KEY(PLAYER_ID, COURSE_ID))";
    private static final String CREATE_GAME_TABLE = "CREATE TABLE " + GAME_TABLE
            + "(" + PLAYER_ID + " INTEGER, " + LANE_ID + " INTEGER, "
            + RESULT + " INTEGER, PRIMARY KEY(PLAYER_ID, COURSE_ID, LANE_ID)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(LOG, CREATE_COURSE_TABLE);
        Log.e(LOG, CREATE_LANE_TABLE);
        Log.e(LOG, CREATE_PLAYER_TABLE);
        Log.e(LOG, CREATE_STATS_TABLE);
        Log.e(LOG, CREATE_GAME_TABLE);

        db.execSQL(CREATE_COURSE_TABLE);
        db.execSQL(CREATE_LANE_TABLE);
        db.execSQL(CREATE_PLAYER_TABLE);
        db.execSQL(CREATE_STATS_TABLE);
        db.execSQL(CREATE_GAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXIST " + GAME_TABLE);
        db.execSQL("DROP TABLE IF EXIST " + STATS_TABLE);
        db.execSQL("DROP TABLE IF EXIST " + PLAYER_TABLE);
        db.execSQL("DROP TABLE IF EXIST " + COURSE_TABLE);
        db.execSQL("DROP TABLE IF EXIST " + LANE_TABLE);
        // create new tables
        onCreate(db);
    }

    // ---------------------- Create course list ---------------------------------------- //

    public CourseList getCourseList () {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + COURSE_TABLE;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        CourseList courseList = new CourseList();

        if(c.moveToFirst()) {
            do {
                courseList.addNewCourse(getCourse(c.getString(c.getColumnIndex(COURSE_NAME))));
            }
            while (c.moveToNext());
        }

        return courseList;
    }

    // ---------------------- Course table ---------------------------------------------- //

    /**
     * Create a course
     */
    public long createCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COURSE_NAME, course.getName());
        values.put(NUMBER_OF_LANES, course.getNumberOfLanes());

        // insert row
        long course_id = db.insert(COURSE_TABLE, null, values);

        // insert holes
        for(int par : course.getPars()) {
            createLane(course_id, par);
        }

        return  course_id;
    }

    public Course getCourse(String courseName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + COURSE_TABLE + " WHERE " + COURSE_NAME + " = \'" + courseName + "\'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if(c != null)
            c.moveToFirst();

        Course course = new Course(courseName, c.getInt(c.getColumnIndex(NUMBER_OF_LANES)));
        int courseId = c.getInt(c.getColumnIndex(COURSE_ID));
        selectQuery = "SELECT * FROM " + LANE_TABLE + " WHERE \'" + courseId + "\' = " +
                COURSE_ID;

        Log.e(LOG, selectQuery);
        c = db.rawQuery(selectQuery, null);

        int [] list = new int[course.getNumberOfLanes()];
        int i = 0;
        if(c.moveToFirst()) {
            do {
                course.setLanePar(c.getInt(c.getColumnIndex(PAR)), i);
                i++;
            }
            while (c.moveToNext());
        }
        return course;
    }

    public int getCourseId(String courseName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + COURSE_TABLE + " WHERE " + COURSE_NAME + " = \'" + courseName + "\'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if(c != null)
            c.moveToFirst();

        return c.getInt(c.getColumnIndex(COURSE_ID));
    }

    public boolean isCourseExist(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + COURSE_TABLE + " WHERE \'" + name + "\' = " + COURSE_NAME;
        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        return c.moveToFirst();
    }

    public void deleteCourse(String courseName) {
        SQLiteDatabase db = this.getWritableDatabase();

        int courseId = getCourseId(courseName);

        db.delete(COURSE_TABLE, COURSE_NAME + " = ?", new String[] {courseName});

        db.delete(LANE_TABLE, COURSE_ID + " = ?", new String[] {String.valueOf(courseId)});

        db.close();
    }

    // ---------------------- Lane table ---------------------------------------------- //

    /**
     * Create lane
     */
    private long createLane(long course_id, int par) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COURSE_ID, course_id);
        values.put(PAR, par);

        long id = db.insert(LANE_TABLE, null, values);

        return id;
    }

    // ---------------------- Player table ---------------------------------------------- //

    /**
     * Check if player already exist
     * @param name player name
     * @return if player exist true otherwise else
     */
    private boolean playerExist(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + PLAYER_NAME + " FROM " + PLAYER_TABLE + " WHERE \'"
                + name + "\' = " + PLAYER_NAME;

        Cursor c = db.rawQuery(selectQuery, null);
        if(c == null)
            return false;
        else
            return true;
    }

    /**
     * Create new player
     * @param name player name
     * @return if player was created true otherwise else
     */
    private boolean createPlayer(String name){
        if(!playerExist(name)) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(PLAYER_NAME, name);
            db.insert(PLAYER_TABLE, null, values);

            return true;
        }
        else
            return false;
    }


    /**
     * Get String array of player names
     * @return list of names
     */
    private List<String> getPlayerList() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + PLAYER_NAME + " FROM " + PLAYER_TABLE;

        Cursor c = db.rawQuery(selectQuery, null);
        List<String>playerList = new ArrayList<String>();
        int i = 0;
        if(c.moveToFirst()) {
            do {
                playerList.add(c.getString(c.getColumnIndex(COURSE_NAME)));
                i++;
            }
            while (c.moveToNext());
        }
        return playerList;
    }

    /**
     * Get player id
     */
    private long playerId(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + PLAYER_TABLE + " WHERE \'"
                + name + "\' = " + PLAYER_NAME;

        Cursor c = db.rawQuery(selectQuery, null);
        if(c.moveToFirst())
            return c.getInt(c.getColumnIndex(PLAYER_ID));
        else
            return 0;
    }

    // ---------------------- Stats table ---------------------------------------------- //


    // ---------------------- Game table ---------------------------------------------- //

    /**
     * Add result to game table
     * @param name player who's result is added
     * @param lid lane id
     * @param result result which is added
     */
    private void addResult(String name, long lid, int result){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYER_ID, playerId(name));
        values.put(LANE_ID, lid);
        values.put(RESULT, result);

        db.insert(GAME_TABLE, null, values);
    }

    /**
     * Return players result on lane
     * @param name player name
     * @param lid lane id
     */
    private int getResult(String name, long lid){
        SQLiteDatabase db = this.getReadableDatabase();

        long pid = playerId(name);
        String selectQuery = "SELECT " + RESULT + " FROM " + GAME_TABLE + " WHERE \'" + lid + "\'=" + LANE_ID
            + " AND \'" + pid + "\'=" + PLAYER_ID;

        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst())
            return c.getInt(c.getColumnIndex(RESULT));
        else
            return 0;
    }

}