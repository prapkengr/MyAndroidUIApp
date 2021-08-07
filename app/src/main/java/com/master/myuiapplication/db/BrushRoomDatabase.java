package com.master.myuiapplication.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.master.myuiapplication.db.dao.BrushDataDao;
import com.master.myuiapplication.db.entity.BrushDataEntity;

import com.master.myuiapplication.db.dao.InactiveTimeDao;
import com.master.myuiapplication.db.entity.InactiveTimeEntity;

import java.util.List;

/**
 * BrushRoomDatabase. Includes code to create the database.
 * After the app creates the database, all further interactions
 * with it happen through the ViewModel.
 */

@Database(entities = {BrushDataEntity.class, InactiveTimeEntity.class}, version = 1)
public abstract class BrushRoomDatabase extends RoomDatabase {

    public abstract BrushDataDao brushDataDao();
    public abstract InactiveTimeDao inactiveTimeDao();

    private static final int DBSIZE = 50;

    private static BrushRoomDatabase INSTANCE;

    private static final String TAG = "BrushRoomDatabase";

    private static BrushDataDao mBrushDao;

/*
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // If we didn't alter the table, there's nothing else to do here.
            // otherwise, need to mention the changes from this version to next version of db.
            database.execSQL("CREATE TABLE inactivetimes (brushName TEXT NOT NULL,"
                    + "inactiveTime TEXT NOT NULL, PRIMARY KEY(brushName, inactiveTime))");
        }
    };
*/

    public static BrushRoomDatabase getDatabase(final Context context) {
        Log.d("BrushRoomDatabase", " getDatabase INSTANCE " +INSTANCE);
        if (INSTANCE == null) {
            synchronized (BrushRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BrushRoomDatabase.class, "brush-values-db")
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this practical.
//                            .fallbackToDestructiveMigration()
                            // pre-fill / populate the database using this call-back
//                            .addCallback(sRoomDatabaseCallback)
                            // database upgrade / migration, need update here !
//                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        Log.d("BrushRoomDatabase", " getDatabase INSTANCE " +INSTANCE);
        return INSTANCE;
    }

    // This callback is called when the database has opened.
    // In this case, use PopulateDbAsync to populate the database
    // with the initial data set if the database has no entries.
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onCreate (@NonNull SupportSQLiteDatabase db){
                    super.onCreate(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }

/*
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
*/

            };

    // Populate the database with the initial data set
    // only if the database has no entries.
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final BrushDataDao mDao;
        private final InactiveTimeDao iTimeDao;

        // Initial data set
/*
        private static String [] words = {"dolphin", "crocodile", "cobra", "elephant", "goldfish",
                "tiger", "snake"};
*/

        PopulateDbAsync(BrushRoomDatabase db) {
            mDao = db.brushDataDao();
            iTimeDao = db.inactiveTimeDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // If we have no words, then create the initial list of words.
//            if (mDao.getAnyWord().length < 1) {
                for (int i = 0; i < DBSIZE; i++) {
                    BrushDataEntity brushvalue = DataGenerator.generateValue();
//                    INSTANCE.brushDataDao().insertDataVal(brushvalue);
                    Log.d(TAG, " doInBackground brushvalue " +brushvalue);

                    mDao.insertDataVal(brushvalue);
                }

                // here, in this background task, can insert to iTimeDao also.
//            }
            return null;
        }
    }


    public static void insertData(final BrushRoomDatabase database, final BrushDataEntity dataVal) {
        database.runInTransaction(() -> {
            database.brushDataDao().insertDataVal(dataVal);
        });
    }

//    @Override
/*
    public static List<String> loadBrushNames() {
        mBrushDao = INSTANCE.brushDataDao();
        return mBrushDao.loadBrushNames();
    }
*/


}

