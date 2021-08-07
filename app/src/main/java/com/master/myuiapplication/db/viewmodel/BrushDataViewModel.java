package com.master.myuiapplication.db.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.master.myuiapplication.db.BrushDataRepository;
import com.master.myuiapplication.db.entity.BrushDataEntity;
import com.master.myuiapplication.db.entity.InactiveTimeEntity;

import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * The ViewModel provides the interface between the UI and the data layer of the app,
 * represented by the Repository
 */

public class BrushDataViewModel extends AndroidViewModel {

    private static final String TAG = "BrushDataViewModel";

    private BrushDataRepository mRepository;

    private LiveData<List<BrushDataEntity>> mAllDataValues;
//    private List<BrushDataEntity> mAllDataValues;
    private LiveData<List<BrushDataEntity>> mDaysDataValues;

    private LiveData<List<String>> mBrushDeviceNames;

    private LiveData<List<InactiveTimeEntity>> mBrushInactives;

    public BrushDataViewModel(Application application, String bName, LocalDateTime lDT) {
        super(application);
        mRepository = new BrushDataRepository(application, bName, lDT);
        mAllDataValues = mRepository.loadWeeksBrushData(bName);
        mDaysDataValues = mRepository.loadDaysBrushData(bName, lDT);

        mBrushDeviceNames = mRepository.loadBrushNames();

        mBrushInactives = mRepository.loadInactiveTimes(bName);

        Log.d(TAG, " BrushDataViewModel mAllDataValues " +mAllDataValues +" mDaysDataValues " +mDaysDataValues
                        +" mBrushInactives " +mBrushInactives);

    }


    public LiveData<List<BrushDataEntity>> loadWeeksBrushData(final String bName) {
//        return mBrushDataDao.loadWeeksBrushData(bName);
        return mAllDataValues;
    }
/*
    public List<BrushDataEntity> loadWeeksBrushData(final String bName) {
        return mAllDataValues;
    }
*/

    public LiveData<List<String>> loadBrushNames() {
        return mBrushDeviceNames;
    }

    public LiveData<List<InactiveTimeEntity>> loadInactiveTimes(final String bName) {
        return mBrushInactives;
    }

    public LiveData<List<BrushDataEntity>> loadDaysBrushData(final String bName, final LocalDateTime lDT) {
//        return mBrushDataDao.loadDaysBrushData(bName);
        return mDaysDataValues;
    }

    public void insertDataVal(BrushDataEntity datavalue) {
        mRepository.insertDataVal(datavalue);
    }

    public void insertInactive(InactiveTimeEntity dataValue) {
        mRepository.insertInactiveTime(dataValue);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteByTimeWeekOld() {
        Log.d(TAG, "calling mRepository.deleteByTimeWeekOld" );
        mRepository.deleteByTimeWeekOld();
    }

    public void deleteInactiveTime(InactiveTimeEntity value) {
        Log.d(TAG, "calling mRepository.deleteInactiveTime" );
        mRepository.deleteInactiveTime(value);
    }

    /**
     * A creator is used to inject into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final String mBrushName;
        private final LocalDateTime lDT;

//        private final BrushDataRepository mRepository;

        public Factory(@NonNull Application application, String bName, LocalDateTime localDateTimeT) {
            mApplication = application;
            mBrushName = bName;
            lDT = localDateTimeT;
//            mRepository = ((BasicApp) application).getRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new BrushDataViewModel(mApplication, mBrushName, lDT);
        }
    }

}
