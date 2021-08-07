package com.master.myuiapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
// Can't use because of "ViewModelProviders.of(Fragment)"
//import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.master.myuiapplication.Utils.BarChartView;
import com.master.myuiapplication.db.DataGenerator;
import com.master.myuiapplication.db.entity.BrushDataEntity;
import com.master.myuiapplication.db.viewmodel.BrushDataViewModel;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "HomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_BRUSH_NAME = "brush_name";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String brushName;

    public static ArrayList<Integer> barDataList = new ArrayList<Integer>();
    public static int maxBarData = 10; // barDataList contents with the range of [0-maxBarData]
    public static int multBarData = 100; // barDataList Y axis multiplied => 1 / 10 / 100 / ...

    private OnFragmentInteractionListener mListener;

    public static int margin;
    public static int barChartHeight;
    public static int homeViewHeight;
    public static int dayRunValue;
    public static int dayActivityValue;

    private TextView tvLastDataSync;
    private Button buttonRun;
    private Button buttonActivity;
    private Button buttonGen;
    private ColumnChartView colChart;
    private ColumnChartView colChartDay;
    private ColumnChartData colChartData;
    private ColumnChartData colChartDataDay;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;

    public final static String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    public final static String[] daysOfWeek = new String[]{"Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"};
    public boolean dayDataDisplay = false;
    public static String dayToday = String.valueOf(android.text.format.DateFormat.format("EEE", new Date()));
//    public static LocalDateTime dateToday = LocalDateTime.now();
    public static LocalDate dateToday = LocalDate.now();

    private BrushDataViewModel mBrushDataViewModel;

    private static final int DBSIZE = 50;

    private List<BrushDataEntity> brushDataWeeklyEntities;

//    private boolean  firstTimeDisplayTest = false;

    private int[] minutesActiveWeekly = new int[8];
    private int[] minutesActiveDay = new int[25];

    public int ONE_CHUNK_SIZE = 20;
    public byte[] dummyData = new byte[180];
    private String[] BRUSHNAME = new String[]{
            "DMBrush 1", "DMBrush ABCDEF", "DMBrush 123456", "DMBrush 7890"};
    private BitSet newBitSet;
    /* No need to insert the 25th entry when split hour */
    private int[] minutesActive = new int[24];
    //        LocalDateTime ldtNow = LocalDateTime.now();
//        String ldtNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    private LocalDateTime ldtNow;
    private int nowMin;
    private int posIndex = 0;
    private int minActIndex = 0;

    private static List<String> localBrushNames;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
     * @param brush_name Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static HomeFragment newInstance(String param1, String param2) {
    public static HomeFragment newInstance(String brush_name, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_BRUSH_NAME, brush_name);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
            brushName = getArguments().getString(ARG_BRUSH_NAME);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        dayToday = getTodayDay();
//        dayToday = String.valueOf(android.text.format.DateFormat.format("EEE", new Date()));
    }


    private void generateOneChunkData(){
        byte[] dummyDataTemp = new byte[ONE_CHUNK_SIZE];
        for (int k = 0; k < ONE_CHUNK_SIZE; k++) {
            dummyDataTemp[k] = (byte) new Random().nextInt(255 + 1);
        }
//        System.arraycopy(dummyDataTemp, 0, dummyData, j*ONE_CHUNK_SIZE, ONE_CHUNK_SIZE);
        appendToData(dummyDataTemp, posIndex);
        if(posIndex >= 8) {
            posIndex = 0;
        } else {
            posIndex++;
        }

        Log.d(TAG, " generateOneChunkData NEXT posIndex " +posIndex);
        Log.d(TAG, " dummyDataTemp " +Arrays.toString(dummyDataTemp));
        Log.d(TAG, " dummyData " +Arrays.toString(dummyData));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View homeView;
        // Inflate the layout for this fragment
        homeView = inflater.inflate(R.layout.fragment_home, container, false);

/*
        margin = homeView.getWidth()/100;
        Log.d("", " margin " +margin);
*/
/*
        final BarChartView barChartView = (BarChartView) homeView.findViewById(R.id.bar_view);
        randomSet(barChartView);
*/
        buttonRun = (Button) homeView.findViewById(R.id.buttonRunDay);
        buttonGen = (Button) homeView.findViewById(R.id.buttonGen);
        buttonActivity = (Button) homeView.findViewById(R.id.buttonActivDay);
//        tvLastDataSync = (TextView) homeView.findViewById(R.id.data_last_sync);

        buttonGen.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                // 20 bytes of random values
                generateOneChunkData();

                if(posIndex == 1){
                    // in case first time,
                    processInsertFirstData();
                } else if(posIndex == 0 ) {
                    // if it is last data, process all the remaining of dummyData[]
                    processInsertLastData();
                    // do we need to reset minutesActive[] and dummyData[] here ???, ...
                    // ..after  InsertLastData is done..
                    Arrays.fill(minutesActive, 0);
                    Arrays.fill(dummyData, (byte) 0);
                } else {
                    // else process InsertNextData
                    processInsertNextData();
                }
            }
        });

// on using Column Chart library from "hellocharts"
        colChart = (ColumnChartView) homeView.findViewById(R.id.chart_bar);
        colChart.setOnValueTouchListener(new ValueTouchListener());
//        colChartDay = (ColumnChartView) homeView.findViewById(R.id.chart_bar);
// this does not need listener !

/*
        reset();
        generateDefaultData();
        dayDataDisplay = false;
*/

// views are not built yet in onCreate(), onStart(), or onResume(). their dimensions are 0.
/*
        barChartView.post(new Runnable() {
            @Override
            public void run() {
                barChartHeight = barChartView.getHeight(); //height is ready
            }
        });
        homeView.post(new Runnable() {
            @Override
            public void run() {
                homeViewHeight = homeView.getHeight(); //height is ready
            }
        });
*/
/*
        homeView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    homeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    homeView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                homeViewHeight = homeView.getHeight(); //height is ready
                Log.d(" onCreateView ", " homeView height " +homeViewHeight);
            }
        });
        barChartView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                barChartView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                barChartHeight = barChartView.getHeight(); //height is ready
                Log.d(" onCreateView ", " barChartView height " +barChartHeight);
            }
        });

        Log.d(" onCreateView ", " homeView height " +homeViewHeight
                +" barChartView height " +barChartHeight);
*/

        return homeView;
    }

    private static void addDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    public void processInsertFirstData(){

        BrushDataEntity brushvalue = new BrushDataEntity();

        ldtNow = LocalDateTime.parse(LocalDateTime.now().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        nowMin = ldtNow.getMinute();
        newBitSet = BitSet.valueOf(dummyData); // needs API 19. Set in Gradle. API 18 market share is around 0.5%, is negligible
        minActIndex = 0;

        Log.d(TAG, " ldtNow " +ldtNow +" dummyData " +Arrays.toString(dummyData));
        Log.d(TAG, " dummyData bitset valueOf " +BitSet.valueOf(dummyData) +"  " +newBitSet.get(0, nowMin) );
        Log.d(TAG, " dummyData countSetBits " +countSetBits(dummyData) +" Cardinality " +newBitSet.cardinality()
                +" newBitSet get cardinality " +newBitSet.get(0, nowMin).cardinality() +" nowMin " +nowMin);

// starting from the 24th hour old data, first byte
        Log.d(TAG, " ldtNow " +ldtNow);
        ldtNow = ldtNow.minusHours(24);

        if( nowMin == 0) {
// insert when it is perfect zero. if it is split time, this is oldest (25th) data.
// we can discard.
            minutesActive[minActIndex] = newBitSet.get(0, 60 - nowMin).cardinality();
            // here, insert Brush name, ldtNow, minutesActive to DB
            Log.d(TAG, " start " + " ldtNow " + ldtNow + " nowMin " + nowMin);
            brushvalue.setBrushName(BRUSHNAME[new Random().nextInt(BRUSHNAME.length)]);
            brushvalue.setLocalDateTime(ldtNow);
            brushvalue.setBrushValue(minutesActive[minActIndex]);
            Log.d(TAG, " processInsertFirstData brushvalue " + brushvalue);
//  insert 24th hour data only if it is not split hour (i.e. exact __:00) hour ?
// should not insert non-zero-minute entries in the table !!??
            mBrushDataViewModel.insertDataVal(brushvalue);

            minActIndex = 1;     // index getting ready to insert next value to minutesActive[]
        }

        ldtNow = ldtNow.plusMinutes(60-nowMin); // for the next minutesActive
        nowMin = 60-nowMin;

        Log.d(TAG, " minutesActive " +Arrays.toString(minutesActive));
    }

    public void processInsertNextData(){
        BrushDataEntity brushvalue = new BrushDataEntity();

        newBitSet = BitSet.valueOf(dummyData); // needs API 19. Set in Gradle. API 18 market share is around 0.5%, is negligible

//        for(int i=1; i< minutesActive.length; i++){
        for(int i=0; i< 2; i++){
            minutesActive[minActIndex] = newBitSet.get(nowMin, nowMin+60).cardinality();
            // here, insert Brush name, ldtNow, minutesActive to DB
            Log.d(TAG, " i " +i +" nowMin " +nowMin +" ldtNow " +ldtNow);

            // Thread sleeping. Now 40 ms foreach loop...
            //  is necessary & sufficient, allows for insertDataVal to run in another thread
            addDelay(40);

            brushvalue.setBrushName(BRUSHNAME[new Random().nextInt(BRUSHNAME.length)]);
            brushvalue.setLocalDateTime(ldtNow);
            brushvalue.setBrushValue(minutesActive[minActIndex]);
            Log.d(TAG, " processInsertNextData brushvalue " +brushvalue);
            mBrushDataViewModel.insertDataVal(brushvalue);

            ldtNow = ldtNow.plusHours(1);   // ready for the next minutesActive
            nowMin += 60;
            minActIndex++;
        }
        Log.d(TAG, " minutesActive " +Arrays.toString(minutesActive));

    }

    public void processInsertLastData(){
        BrushDataEntity brushvalue = new BrushDataEntity();

        newBitSet = BitSet.valueOf(dummyData); // needs API 19. Set in Gradle. API 18 market share is around 0.5%, is negligible

//        for(int i=1; i< minutesActive.length; i++){
        for(; minActIndex < 24; minActIndex++){
            minutesActive[minActIndex] = newBitSet.get(nowMin, nowMin+60).cardinality();
            // here, insert Brush name, ldtNow, minutesActive to DB
            Log.d(TAG, " minActIndex " +minActIndex +" nowMin " +nowMin +" ldtNow " +ldtNow);

            // Thread sleeping. Now 40 ms foreach loop...
            //  is necessary & sufficient, allows for insertDataVal to run in another thread
            addDelay(40);

            brushvalue.setBrushName(BRUSHNAME[new Random().nextInt(BRUSHNAME.length)]);
            brushvalue.setLocalDateTime(ldtNow);
            brushvalue.setBrushValue(minutesActive[minActIndex]);
            Log.d(TAG, " processInsertNextData brushvalue " +brushvalue);
            mBrushDataViewModel.insertDataVal(brushvalue);

            ldtNow = ldtNow.plusHours(1);   // ready for the next minutesActive
            nowMin += 60;
        }
        Log.d(TAG, " minutesActive " +Arrays.toString(minutesActive));

    }

    public void appendToData(byte[] dataTemp, int pos){
        System.arraycopy(dataTemp, 0, dummyData, pos*ONE_CHUNK_SIZE, ONE_CHUNK_SIZE);
    }

    public void processInsertData(){

        BrushDataEntity brushvalue = new BrushDataEntity();
        byte[] dummyDataTemp = new byte[ONE_CHUNK_SIZE];

        for(int j=0; j<9; j++) {
            for (int k = 0; k < ONE_CHUNK_SIZE; k++) {
                dummyDataTemp[k] = (byte) new Random().nextInt(255 + 1);
            }
            System.arraycopy(dummyDataTemp, 0, dummyData, j*ONE_CHUNK_SIZE, ONE_CHUNK_SIZE);
//            System.arraycopy(dummyDataTemp, 0, dummyData, dummyData.length, ONE_CHUNK_SIZE);
            Log.d(TAG, " dummyDataTemp " +Arrays.toString(dummyDataTemp));
            Log.d(TAG, " dummyData " +Arrays.toString(dummyData));
        }

        newBitSet = BitSet.valueOf(dummyData); // needs API 19. Set in Gradle. API 18 market share is around 0.5%, is negligible

        Log.d(TAG, " ldtNow " +ldtNow +" dummyData " +Arrays.toString(dummyData));
        Log.d(TAG, " dummyData bitset valueOf " +BitSet.valueOf(dummyData) +"  " +newBitSet.get(0, nowMin) );
        Log.d(TAG, " dummyData countSetBits " +countSetBits(dummyData) +" Cardinality " +newBitSet.cardinality()
                +" newBitSet get cardinality " +newBitSet.get(0, nowMin).cardinality() +" nowMin " +nowMin);

// starting from the 24th hour old data, first byte
//        int start = 0;
        Log.d(TAG, " ldtNow " +ldtNow);
        ldtNow = ldtNow.minusHours(24);
/*
        int[] minutesActive;
        if(nowMin != 0){
            minutesActive = new int[25];    // 25 entries because split hour
        } else {
            minutesActive = new int[24];
        }
*/

//        if(nowMin != 0){
        minutesActive[0] = newBitSet.get(0, 60-nowMin).cardinality();
        // here, insert Brush name, ldtNow, minutesActive to DB
        Log.d(TAG, " start " +" ldtNow " +ldtNow +" nowMin " +nowMin);
        brushvalue.setBrushName(BRUSHNAME[new Random().nextInt(BRUSHNAME.length)]);
        brushvalue.setLocalDateTime(ldtNow);
        brushvalue.setBrushValue(minutesActive[0]);
        Log.d(TAG, " onCreateView brushvalue " +brushvalue);

//  insert 24th hour data only if it is not split hour (i.e. exact __:00) hour ?
// should not insert non-zero-minute entries in the table !!??
//      if(nowMin == 0)
        mBrushDataViewModel.insertDataVal(brushvalue);

        ldtNow = ldtNow.plusMinutes(60-nowMin); // for the next minutesActive
        nowMin = 60-nowMin;
//            start = 1;
/*
        } else {

            ldtNow = ldtNow.plusHours(1);     // starting from 23rd hour data - to 24th hour
            nowMin = 0;
        }
*/
//        for(int i=start; i< minutesActive.length; i++){
        for(int i=1; i< minutesActive.length; i++){
            minutesActive[i] = newBitSet.get(nowMin, nowMin+60).cardinality();
            // here, insert Brush name, ldtNow, minutesActive to DB
            Log.d(TAG, " i " +i +" nowMin " +nowMin +" ldtNow " +ldtNow);

            // Thread sleeping. Now 40 ms foreach loop...
            //  is necessary & sufficient, allows for insertDataVal to run in another thread
            addDelay(40);

            brushvalue.setBrushName(BRUSHNAME[new Random().nextInt(BRUSHNAME.length)]);
            brushvalue.setLocalDateTime(ldtNow);
            brushvalue.setBrushValue(minutesActive[i]);
            Log.d(TAG, " onCreateView brushvalue " +brushvalue);
            mBrushDataViewModel.insertDataVal(brushvalue);

            ldtNow = ldtNow.plusHours(1);   // for the next minutesActive
            nowMin += 60;
        }

/*
// starting from the latest data => 1439th bit (179th byte)
        int start = 0;
        if(nowMin != 0){
            minutesActive[0] = newBitSet.get(1439-nowMin, 1439).cardinality();
            ldtNow = ldtNow.minusMinutes(nowMin);
            nowMin = 1439-nowMin;
            start = 1;
        }
        Log.d(TAG, " start " +start +" ldtNow " +ldtNow);
        for(int i=start; nowMin > 0; i++, nowMin -= 60){
            if(nowMin >= 60){
                minutesActive[i] = newBitSet.get(nowMin-60, nowMin).cardinality();
                ldtNow = ldtNow.minusHours(1);
            } else {
                minutesActive[i] = newBitSet.get(0, nowMin).cardinality();
                ldtNow = ldtNow.minusMinutes(nowMin+1);
            }
            Log.d(TAG, " i " +i +" nowMin " +nowMin +" ldtNow " +ldtNow);
        }
*/
        Log.d(TAG, " minutesActive " +Arrays.toString(minutesActive));

/*
        BrushDataViewModel.Factory factory = new BrushDataViewModel.Factory(
                getActivity().getApplication(), brushName);
        mBrushDataViewModel = ViewModelProviders.of(this, factory).get(BrushDataViewModel.class);
        Log.d(TAG, " onActivityCreated mBrushDataViewModel " +mBrushDataViewModel);
        for (int i = 0; i < DBSIZE; i++) {
            BrushDataEntity brushvalue = DataGenerator.generateValue();
            Log.d(TAG, " onCreateView brushvalue " +brushvalue);
            mBrushDataViewModel.insertDataVal(brushvalue);
        }
*/

        /* test the OBSERVE Live data update */
/*
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                LocalDateTime ldtNow = LocalDateTime.parse(LocalDateTime.now().
                        format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
                int nowMin = ldtNow.getMinute();
                ldtNow = ldtNow.plusMinutes(60-nowMin); // for the next minutesActive
                nowMin = 60-nowMin;
                for(int i=1; i< minutesActive.length; i++){
                    minutesActive[i] = newBitSet.get(nowMin, nowMin+60).cardinality();
                    // here, insert Brush name, ldtNow, minutesActive to DB
                    Log.d(TAG, " i " +i +" nowMin " +nowMin +" ldtNow " +ldtNow);
                    // Thread sleeping. Now 40 ms foreach loop...
                    //  is necessary & sufficient, allows for insertDataVal to run in another thread
                    addDelay(40);
                    brushvalue.setBrushName(BRUSHNAME[new Random().nextInt(BRUSHNAME.length)]);
                    brushvalue.setLocalDateTime(ldtNow);
                    brushvalue.setBrushValue(minutesActive[i]);
                    Log.d(TAG, " onCreateView brushvalue " +brushvalue);
                    mBrushDataViewModel.insertDataVal(brushvalue);
                    ldtNow = ldtNow.plusHours(1);   // for the next minutesActive
                    nowMin += 60;
                }
            }
        }, 20000);
*/

        /* check if changes are recognised */
/*
        addDelay(5000);
        brushvalue.setBrushName("DMBrush 1");
        brushvalue.setLocalDateTime(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
        brushvalue.setBrushValue(11);
        mBrushDataViewModel.insertDataVal(brushvalue);
        addDelay(5000);
*/

// calculate total set bits & clear bits in the byte[]
/*
        byte[] dummyData = new byte[256];
// 180 bytes long dummy data [] contains random values between 0 ~ 60 min values
// a byte can be anything from all 0's to all 1's
        for(int i=0; i<256; i++) dummyData[i] = (byte)i;
//        for(int i=0; i<256; i++) dummyData[i] = (byte)new Random().nextInt(255 + 1);
        int setBits = countSetBits(dummyData);
        Log.d("", " Total setBits " +setBits
                +" noTotalBits " +dummyData.length*8 +" noClearBits " +(dummyData.length*8 - setBits) +"\n");
*/

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalDateTime ldtNow = LocalDateTime.parse(LocalDateTime.now().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));

        BrushDataViewModel.Factory factory = new BrushDataViewModel.Factory(
                getActivity().getApplication(), brushName, ldtNow);
        mBrushDataViewModel = ViewModelProviders.of(this, factory).get(BrushDataViewModel.class);
//        mBrushDataViewModel = ViewModelProviders.of(this).get(BrushDataViewModel.class);
        Log.d(TAG, " onActivityCreated mBrushDataViewModel " +mBrushDataViewModel);

        /* delete all the more than week - old data from database */
        mBrushDataViewModel.deleteByTimeWeekOld();
        /* keep watching for weeks data of this given brush */
        mBrushDataViewModel.loadWeeksBrushData(brushName).observe(this, new Observer<List<BrushDataEntity>>() {
            @Override
            public void onChanged(@Nullable final List<BrushDataEntity> brushDataEntities) {
                for (BrushDataEntity brushDataEntity : brushDataEntities) {
                    Log.d(TAG, " loadWeeksBrushData " +brushDataEntity.getLocalDateTime()
                            +"  " +brushDataEntity.getBrushName() +"  " +brushDataEntity.getBrushValue());
                }
                brushDataWeeklyEntities = brushDataEntities;

                // first, calculate minutesActiveWeekly[] ...
                barValuesCalculator();
                Log.d(TAG, " after barValuesCalculator()  minutesActiveWeekly " +Arrays.toString(minutesActiveWeekly));
                // ... and then, plot graph.
                reset();
// if out of HOME in day-data-display and back to HOME fragment, ...
                generateDefaultData();
                dayDataDisplay = false;
            }
        });

/*
        if(localBrushNames != null){
            // if there are brush names in DB
            Log.d(TAG, " localBrushNames not null " +localBrushNames
                    +" localBrushNames size " +localBrushNames.size()
                    +" localBrushNames isEmpty " +localBrushNames.isEmpty());

        } else {
            // No brush names found in DB
            // display BLANK graph / message "no data found"
            Log.d(TAG, " localBrushNames null " +localBrushNames);
        }
*/

        /* keep watching for brush names */
/*
        mBrushDataViewModel.loadBrushNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> brushDeviceNames) {
                localBrushNames = brushDeviceNames;
                Log.d(TAG, " brushDeviceNames localBrushNames " +localBrushNames);

                for (String brushName : brushDeviceNames) {
                    Log.d(TAG, " brushName " +brushName);
                }
                localBrushNames.clear();
                if(localBrushNames != null){
                    // if there are brush names in DB
                    Log.d(TAG, " after clear localBrushNames not null " +localBrushNames
                                +" localBrushNames size " +localBrushNames.size()
                                +" localBrushNames isEmpty " +localBrushNames.isEmpty());
                } else {
                    // No brush names found in DB
                    // display BLANK graph / message "no data found"
                    Log.d(TAG, " after clear localBrushNames null " +localBrushNames
                            +" localBrushNames size " +localBrushNames.size()
                            +" localBrushNames isEmpty " +localBrushNames.isEmpty());
                }

            }
        });
*/

                /* keep watching for day's data of this given brush */
/*
        Log.d(TAG, " loadDaysBrushData : ldtNow " +ldtNow);
        mBrushDataViewModel.loadDaysBrushData(brushName, ldtNow).observe(this, new Observer<List<BrushDataEntity>>() {
            @Override
            public void onChanged(@Nullable final List<BrushDataEntity> brushDataEntities) {
                for (BrushDataEntity brushDataEntity : brushDataEntities) {
                    Log.d(TAG, " loadDaysBrushData " +brushDataEntity.getLocalDateTime()
                            +"  " +brushDataEntity.getBrushName() +"  " +brushDataEntity.getBrushValue());
                }

            }
        });
*/


//        processInsertData();
    }


    public static int countSetBits(byte[] array) {
//        return BitSet.valueOf(array).cardinality();
        int sum = 0;
        for (int val : array) {
            sum += Long.bitCount((val&0xFF)); // & with 0xFF to avoid becoming Signed Long type
//            Log.d("", " val " +(val&0xFF) +" noBitsSet " +Long.bitCount((val&0xFF)) +"\n");
        }
        return sum;
    }

/*
    public static long convertToLong(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getLong();
    }
*/

    private String getTodayDay() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch(day){
            case Calendar.SUNDAY:
                dayToday =  "Sun";
                break;
            case Calendar.MONDAY:
                dayToday =  "Mon";
                break;
            case Calendar.TUESDAY:
                dayToday =  "Tue";
                break;
            case Calendar.WEDNESDAY:
                dayToday = "Wed";
                break;
            case Calendar.THURSDAY:
                dayToday = "Thu";
                break;
            case Calendar.FRIDAY:
                dayToday = "Fri";
                break;
            case Calendar.SATURDAY:
                dayToday = "Sat";
                break;
        }
        Log.d("getTodayDay", " dayToday " +dayToday);
        return dayToday;
    }

/*
    private void randomSet(BarChartView barView) {
        int noDays = 7;
        ArrayList<String> test = new ArrayList<String>();
//        for (int i = 0; i < noDays; i++) {
            test.add("Mon");
            test.add("Tue");
            test.add("Wed");
            test.add("Thu");
            test.add("Fri");
            test.add("Sat");
            test.add("Sun");
//        }
        barView.setBottomTextList(test);

        barDataList.clear();
        for (int i = 0; i < noDays; i++) {
            barDataList.add((int) (Math.random() * 10*multBarData));
        }
        Log.d(" randomSet() ", " barDataList " +barDataList);
//        barView.setDataList(barDataList, maxBarData);    // "max" for max valid value of data in the list
        barView.setDataList(barDataList, multBarData*BarChartView.getVerticalGridlNum());    // "max" for max valid value of data in the list
//        barView.setDataList(barDataList, BarChartView.getVerticalGridlNum());    // "max" for max valid value of data in the list
    }
*/

// Returns the Data Last sync timestamp as String.
//        This is redundant, because user can make it out from the graph itself !

    private String getLastDataSync(){

        if(brushDataWeeklyEntities.isEmpty()){
            return "Unknown";
        } else {
            BrushDataEntity brushDataWeekLastEntity = brushDataWeeklyEntities.get(brushDataWeeklyEntities.size() - 1);
//        LocalDateTime ldt = brushDataWeekLastEntity.getLocalDateTime();
//        String timestamp = String.valueOf(LocalDateTime.parse(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))));
            String timestamp = String.valueOf(brushDataWeekLastEntity.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd'T'HH:mm")));
/*
        String[] separated = timestamp.split("T");
        Log.d(TAG, " getLastDataSync() timestamp " +timestamp +" yyyy-MMM-dd  separated " +separated[0] +"  HH:mm  " +separated[1]);
        // timestamp 2018-11-13T14:43 yyyy-MMM-dd  separated 2018-Nov-13  HH:mm  14:43
*/
            return timestamp;
        }
    }

    private void barValuesCalculator()
    {
        int totalValue = 0;
        LocalDate brushEntityDate;

        Arrays.fill(minutesActiveWeekly, 0);  // need to reset on every visit

        for (BrushDataEntity brushDataEntity : brushDataWeeklyEntities) {
            Log.d(TAG, " barValuesCalculator() loadDaysBrushData " +brushDataEntity.getLocalDateTime()
                    +" String.valueOf(brushDataWeekLastEntity.getLocalDateTime()) " +String.valueOf(brushDataEntity.getLocalDateTime())
                    +" String.value " +brushDataEntity.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd'T'HH:mm"))
                    +"  " +brushDataEntity.getBrushName() +"  " +brushDataEntity.getBrushValue());
            brushEntityDate = LocalDate.parse(brushDataEntity.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            minutesActiveWeekly[dateToday.compareTo(brushEntityDate)] += brushDataEntity.getBrushValue();
            Log.d(TAG, " brushEntityDate " +brushEntityDate +" minutesActiveWeekly " +Arrays.toString(minutesActiveWeekly));
        }
        Log.d(TAG, " dateToday minutesActiveWeekly " +dateToday +"  " +Arrays.toString(minutesActiveWeekly));

//        return totalValue;
    }

    private void barValuesCalculatorDay(LocalDate indexDate)
    {
        LocalDate brushEntityDate;

        Arrays.fill(minutesActiveDay, 0);  // need to reset on every visit
        for (BrushDataEntity brushDataEntity : brushDataWeeklyEntities) {
            Log.d(TAG, " loadDaysBrushData " +brushDataEntity.getLocalDateTime()
                    +"  " +brushDataEntity.getBrushName() +"  " +brushDataEntity.getBrushValue());
            brushEntityDate = LocalDate.parse(brushDataEntity.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if((indexDate.compareTo(brushEntityDate) == 0) && (brushDataEntity.getLocalDateTime().getMinute()) == 0){
                // when there is split - days , the inserted values have non - 00 minutes as 25th last value.
                // either should discard 25th value when INSERT to DB, or discard such values here !
                minutesActiveDay[brushDataEntity.getLocalDateTime().getHour()] += brushDataEntity.getBrushValue();
                Log.d(TAG, " brushDataEntity.getLocalDateTime().getHour()= " +brushDataEntity.getLocalDateTime().getHour()
                                +" brushDataEntity.getBrushValue()= " +brushDataEntity.getBrushValue());
            }
            Log.d(TAG, " brushEntityDate " +brushEntityDate +" minutesActiveDay " +Arrays.toString(minutesActiveDay));
        }
        Log.d(TAG, " dateToday minutesActiveDay " +dateToday +"  " +Arrays.toString(minutesActiveDay));

    }

    private void generateDefaultData() {
        int numSubcolumns = 1;
        int numColumns = 7;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        // Today's position in the "days[]". Today in the 7th position always in the graph displayed.
        // that is, i = 6 , and Today is days[todaysPos].
        int todaysPos = Arrays.asList(days).indexOf(dayToday);
        Log.d(TAG, " generateDefaultData todaysPos " +todaysPos);

        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
//                values.add(new SubcolumnValue((float) Math.random() * 50f + 5, R.color.colorPrimaryDark));

// different color for Today in the bar
                if(days[(i+ (todaysPos+1) ) % 7].equalsIgnoreCase(dayToday)) {
/*
//  Math.random() gives a random double
                    values.add(new SubcolumnValue((int) (Math.random() * 10 * multBarData),
                            getResources().getColor(R.color.colorPrimary)));
*/
//  generates a random integer between 0 (inclusive) and 1440 (60*24) (inclusive)
//                    dayRunValue = new Random().nextInt(1440 + 1); // needed for "RunTime Today"
                    dayRunValue = minutesActiveWeekly[6-i]; // needed for "RunTime Today"
                    values.add(new SubcolumnValue(dayRunValue,
                            getResources().getColor(R.color.colorPrimary)));
                    Log.d(TAG, " generateDefaultData i " +i +" dayRunValue " +dayRunValue
                            +" ((i+ (todaysPos+1) ) % 7) = " +(i+ (todaysPos+1) ) % 7);

                } else {
/*
//  Math.random() gives a random double
                    values.add(new SubcolumnValue((int) (Math.random() * 10 * multBarData),
                            getResources().getColor(R.color.barChartBar)));
*/
//  generates a random integer between 0 (inclusive) and 1440 (60*24) (inclusive)
/*
                    values.add(new SubcolumnValue( (new Random().nextInt(1440 + 1)),
                            getResources().getColor(R.color.barChartBar)));
*/
                    values.add(new SubcolumnValue(minutesActiveWeekly[6-i],
                            getResources().getColor(R.color.barChartBar)));
                    Log.d(TAG, " generateDefaultData i " +i +" ((i+ (todaysPos+1) ) % 7) = " +(i+ (todaysPos+1) ) % 7);
                }
            }
            Log.d(TAG, "  i " +i +" days[(i+ (todaysPos+1) ) % 7] = " +days[(i+ (todaysPos+1) ) % 7]);
            axisValues.add(new AxisValue(i).setLabel(days[(i+ (todaysPos+1) ) % 7]));
            // Today in the 7th position always in the graph displayed.
            // that is, i = 6 , and Today is days[todaysPos].

            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        colChartData = new ColumnChartData(columns);

        if (hasAxes) {
            Axis axisX = new Axis(axisValues);
            axisX.setTextColor(getResources().getColor(R.color.barChartText));
            axisX.setLineColor(getResources().getColor(R.color.barChartBackLine));
            Axis axisY = new Axis().setHasLines(true);
            axisY.setTextColor(getResources().getColor(R.color.barChartText));
            axisY.setLineColor(getResources().getColor(R.color.barChartBackLine));
            if (hasAxesNames) {
//                axisX.setName("Axis X");
                axisY.setName("Minutes per day");
            }
            colChartData.setAxisXBottom(axisX);
            colChartData.setAxisYLeft(axisY);
        } else {
            colChartData.setAxisXBottom(null);
            colChartData.setAxisYLeft(null);
        }

        colChart.setColumnChartData(colChartData);

        // need to display "RunTime Today" value every time updated today's value.
        int hrs = dayRunValue/60;
        int mins = dayRunValue%60;
        CharSequence text;
        if(hrs != 0){
             text = Integer.toString(hrs)+" Hrs "+Integer.toString(mins)+" Mins";
        } else {
             text = Integer.toString(mins)+" Mins";
        }
        buttonRun.setText(text);
        // need to display "Activity Today"
        dayActivityValue = dayRunValue/2;  //i.e. 2 minutes per cycle = 1 activation time
        buttonActivity.setText(String.valueOf(dayActivityValue));

// Data Last sync timestamp
//        This is redundant, because user can make it out from the graph itself !
/*
        String lastDataSync = getLastDataSync();
        if(lastDataSync.equalsIgnoreCase("unknown")){
            Log.d(TAG, " getLastDataSync() Unknown " );
//            ((TextView) getView().findViewById(R.id.data_last_sync)).setText(
//                    getResources().getString(R.string.text_last_sync).concat(" Unknown"));
            tvLastDataSync.setText(getResources()
                    .getString(R.string.text_last_sync).concat(" Unknown"));
        } else {
            String[] separated = lastDataSync.split("T");
            Log.d(TAG, " getLastDataSync() timestamp " + getLastDataSync() + " yyyy-MMM-dd  separated " + separated[0] + "  HH:mm  " + separated[1]);
            // timestamp 2018-11-13T14:43 yyyy-MMM-dd  separated 2018-Nov-13  HH:mm  14:43
//            ((TextView) getView().findViewById(R.id.data_last_sync)).setText(
//                    getResources().getString(R.string.text_last_sync).concat(" " + separated[0] + " at: " + separated[1]));
            tvLastDataSync.setText(getResources().getString(R.string.text_last_sync)
                    .concat(" " + separated[0] + " at: " + separated[1]));
        }
*/

    }

    private void generateDaysData(int columnIndex) {
        int numSubcolumns = 1;
        int numColumns = 24;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        LocalDate localDateIndex;

        localDateIndex = dateToday.minusDays(6-columnIndex);

        int todaysPos = Arrays.asList(days).indexOf(dayToday);

        // first, calculate minutesActiveDay[] for selected columnIndex ...
        barValuesCalculatorDay(localDateIndex);
        // ... and then plot the graph.
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
//                values.add(new SubcolumnValue((float) Math.random() * 50f + 5, R.color.colorPrimaryDark));

// different color for alternate hours in the bar
                if((i & 0x01) != 0) {
/*
//  Math.random() gives a random double
                    values.add(new SubcolumnValue((int) (Math.random() * 10 * multBarData),
                            getResources().getColor(R.color.colorPrimary)));
*/
//  generates a random integer between 0 (inclusive) and 60 (inclusive)
/*
                    values.add(new SubcolumnValue((new Random().nextInt(60 + 1)),
                            getResources().getColor(R.color.colorPrimary)));
*/
                    values.add(new SubcolumnValue(minutesActiveDay[i],
                            getResources().getColor(R.color.colorPrimary)));
                } else {
/*
//  Math.random() gives a random double
                    values.add(new SubcolumnValue((int) (Math.random() * 10 * multBarData),
                            getResources().getColor(R.color.barChartBar)));
*/
//  generates a random integer between 0 (inclusive) and 60 (inclusive)
/*
                    values.add(new SubcolumnValue((new Random().nextInt(60 + 1)),
                            getResources().getColor(R.color.barChartBar)));
*/
                    values.add(new SubcolumnValue(minutesActiveDay[i],
                            getResources().getColor(R.color.barChartBar)));
                }
            }
            axisValues.add(new AxisValue(i).setLabel(String.valueOf(i)));

            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        colChartData = new ColumnChartData(columns);

        if (hasAxes) {
            Axis axisX = new Axis(axisValues);
            axisX.setTextColor(getResources().getColor(R.color.barChartText));
            axisX.setLineColor(getResources().getColor(R.color.barChartBackLine));
            Axis axisY = new Axis().setHasLines(true);
            axisY.setTextColor(getResources().getColor(R.color.barChartText));
            axisY.setLineColor(getResources().getColor(R.color.barChartBackLine));
            if (hasAxesNames) {
//                axisX.setName(daysOfWeek[columnIndex] +" (hours)");
                Log.d(TAG, " generateDaysData()  columnIndex " +columnIndex);
//                axisX.setName(daysOfWeek[(columnIndex+1) % 7] +" (hours)");
                axisX.setName(daysOfWeek[(columnIndex+todaysPos+1) % 7] +" (hours)");

                axisY.setName("Minutes per hour");
            }
            colChartData.setAxisXBottom(axisX);
            colChartData.setAxisYLeft(axisY);
        } else {
            colChartData.setAxisXBottom(null);
            colChartData.setAxisYLeft(null);
        }

        // IS THIS CORRECT ????
//        colChartDay.setColumnChartData(colChartData);
        colChart.setColumnChartData(colChartData);

    }


    private void reset() {
        hasAxes = true;
        hasAxesNames = true;
        hasLabels = false;
        hasLabelForSelected = false;
        colChart.setValueSelectionEnabled(hasLabelForSelected);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHomeFragmentInteraction(Uri uri);
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            if( ! dayDataDisplay) { // display hourly data for the selected day
//            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
                Log.d(" ValueTouchListener ", " columnIndex " + columnIndex
                        + " subcolumnIndex " + subcolumnIndex + " SubcolumnValue " + value);
                Toast.makeText(getActivity(), "Selected " + value, Toast.LENGTH_SHORT).show();
                reset();
                generateDaysData(columnIndex);
                dayDataDisplay = true;
            }else{  // back to Week's data
                Log.d(" ValueTouchListener ", " columnIndex " + columnIndex
                        + " subcolumnIndex " + subcolumnIndex + " SubcolumnValue " + value);
                Toast.makeText(getActivity(), "Selected " + value, Toast.LENGTH_SHORT).show();
                // first, calculate minutesActiveWeekly[] ...
                barValuesCalculator();
                // ... and then, plot graph.
                reset();
                generateDefaultData();
                dayDataDisplay = false;
            }
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }


}
