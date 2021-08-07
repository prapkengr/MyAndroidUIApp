package com.master.myuiapplication.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.master.myuiapplication.AppExecutors;
import com.master.myuiapplication.Utils.Timers;
import com.master.myuiapplication.db.dao.BrushDataDao;
import com.master.myuiapplication.db.dao.InactiveTimeDao;
import com.master.myuiapplication.db.entity.BrushDataEntity;
import com.master.myuiapplication.db.entity.InactiveTimeEntity;

import org.threeten.bp.LocalDateTime;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * This class holds the implementation code for the methods that interact with the database.
 * Using a repository allows us to group the implementation methods together,
 * and allows the ViewModel to be a clean interface between the rest of the app
 * and the database.
 *
 * For insert, update and delete, and longer-running queries,
 * you must run the database interaction methods in the background.
 *
 * Typically, all you need to do to implement a database method
 * is to call it on the data access object (DAO), in the background if applicable.
 */

public class BrushDataRepository {

    private BrushDataDao mBrushDataDao;
    private InactiveTimeDao mInactiveTimeDao;

    private LiveData<List<BrushDataEntity>> mAllDataValues;
//    private List<BrushDataEntity> mAllDataValues;
    private LiveData<List<BrushDataEntity>> mDaysDataValues;
//    BrushRoomDatabase db;

    private LiveData<List<InactiveTimeEntity>> mBrushInactives;

    private LiveData<List<String>> mBrushDeviceNames;

//    private AppExecutors mAppExecutors;

    public BrushDataRepository(Application application, String bName, LocalDateTime lDT) {
        BrushRoomDatabase db = BrushRoomDatabase.getDatabase(application);
//        db = BrushRoomDatabase.getDatabase(application);
        mBrushDataDao = db.brushDataDao();
        mInactiveTimeDao = db.inactiveTimeDao();
        Log.d("BrushDataRepository", " BrushDataRepository mBrushDataDao " +mBrushDataDao +" mInactiveTimeDao " +mInactiveTimeDao);

        mAllDataValues = mBrushDataDao.loadWeeksBrushData(bName);
//        mDaysDataValues = mBrushDataDao.loadDaysBrushData(bName, lDT);
        mBrushDeviceNames = mBrushDataDao.loadBrushNames();

        mBrushInactives = mInactiveTimeDao.loadBrushInactiveTimes(bName);

//        mAppExecutors = new AppExecutors();
    }

    public LiveData<List<BrushDataEntity>> loadWeeksBrushData(final String bName) {
//        return mBrushDataDao.loadWeeksBrushData(bName);
        return mAllDataValues;
    }

/*
    public List<BrushDataEntity> loadWeeksBrushData(final String bName) {
//        return mAllDataValues;

        AppExecutors mAppExecutors = new AppExecutors();
        mAppExecutors.diskIO().execute(() -> {
            return mBrushDataDao.loadWeeksBrushData(bName);
        });

    }
*/

    public LiveData<List<BrushDataEntity>> loadDaysBrushData(final String bName, LocalDateTime lDT) {
//        return mBrushDataDao.loadDaysBrushData(bName, lDT);
        return mDaysDataValues;
    }

    public LiveData<List<InactiveTimeEntity>> loadInactiveTimes(final String bName) {
        return mBrushInactives;
    }

    public LiveData<List<String>> loadBrushNames() {
        return mBrushDeviceNames;
    }

    public void insertDataVal(BrushDataEntity datavalue) {
        new insertAsyncTask(mBrushDataDao).execute(datavalue);
//        mBrushDataDao.insertDataVal(datavalue);
//        db.mDao.insertDataVal(datavalue);
/*
        db.runInTransaction(() -> {
            mBrushDataDao.insertDataVal(datavalue);
        });
*/
//        BrushRoomDatabase.insertData(db, datavalue);

/*
        AppExecutors mAppExecutors = new AppExecutors();
        mAppExecutors.diskIO().execute(() -> {
            mBrushDataDao.insertDataVal(datavalue);
        });
*/
    }

    public void insertInactiveTime(InactiveTimeEntity dataValue) {
        new insertInactivesAsyncTask(mInactiveTimeDao).execute(dataValue);
    }

    public void deleteAll()  {
        new deleteAllAsyncTask(mBrushDataDao).execute();
    }

    // Must run off main thread
    public void deleteByTimeWeekOld() {
        new deleteWeekOldAsyncTask(mBrushDataDao).execute();
    }


    // Must run off main thread
    public void deleteInactiveTime(InactiveTimeEntity dataValue) {
        new deleteInactivesAsyncTask(mInactiveTimeDao).execute(dataValue);
    }

    // Static inner classes below here to run database interactions in the background.

    /**
     * Inserts a row of BrushDataEntity into the database.
     */
    private static class insertAsyncTask extends AsyncTask<BrushDataEntity, Void, Void> {

        private BrushDataDao mAsyncTaskDao;

        insertAsyncTask(BrushDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BrushDataEntity... params) {
            mAsyncTaskDao.insertDataVal(params[0]);
            return null;
        }
    }

    /**
     * Inserts a row of InactiveTimeEntity into the database.
     */
    private static class insertInactivesAsyncTask extends AsyncTask<InactiveTimeEntity, Void, Void> {

        private InactiveTimeDao mAsyncTaskDao;

        insertInactivesAsyncTask(InactiveTimeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final InactiveTimeEntity... params) {
            mAsyncTaskDao.insertInactiveTime(params[0]);
            return null;
        }
    }


    /**
     * Deletes all data values from the database (does not delete the table).
     */
    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private BrushDataDao mAsyncTaskDao;

        deleteAllAsyncTask(BrushDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    /**
     *  Deletes older than a week from the database.
     */
    private static class deleteWeekOldAsyncTask extends AsyncTask<Void, Void, Void> {
        private BrushDataDao mAsyncTaskDao;

        deleteWeekOldAsyncTask(BrushDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("deleteWeekOldAsyncTask ", "calling mAsyncTaskDao.deleteByTimeWeekOld" );
            mAsyncTaskDao.deleteByTimeWeekOld();
            return null;
        }
    }


    /**
     *  Deletes Inactive times from the database, given Brush name and Inactive Start - End Time.
     */
    private static class deleteInactivesAsyncTask extends AsyncTask<InactiveTimeEntity, Void, Void> {
        private InactiveTimeDao mAsyncTaskDao;

        deleteInactivesAsyncTask(InactiveTimeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(InactiveTimeEntity... params) {
            Log.d("deleteInactivesAsync ", "calling deleteInactivesAsyncTask" );
            mAsyncTaskDao.deleteInactiveTimes(params[0]);
            return null;
        }
    }



}
