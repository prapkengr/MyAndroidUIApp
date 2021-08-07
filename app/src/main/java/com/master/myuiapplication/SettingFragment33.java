package com.master.myuiapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
// Can't use because of "ViewModelProviders.of(Fragment)"
//import android.app.Fragment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.master.myuiapplication.Utils.Timers;
import com.master.myuiapplication.db.entity.BrushDataEntity;
import com.master.myuiapplication.db.entity.InactiveTimeEntity;
import com.master.myuiapplication.db.viewmodel.BrushDataViewModel;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.*;
import static java.text.DateFormat.getTimeInstance;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment33.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment33#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment33 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_BRUSH_NAME = "brush_name";
    private static final String TAG = "SettingFragment33";

    // TODO: Rename and change types of parameters
    private String brushName;
    private String mParam2;
    private static boolean inactiveTimerSaved = false; //ensure SET btn is clicked (saved)
    public  EditText startTime;
    public  EditText endTime;
    public  Button btnSet; //SET button for inactive timer
//    public static ArrayList<Timers> inactiveTimes = null;//This is store all the start & end times
    private static final int MAX_NUM_INACTIVE_TIMERS = 5;
    private boolean startTimeFilled = false;

    private View setView;
    private ListView listTimers;

    // Use the current date & time as the default date in the picker
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int min = c.get(Calendar.MINUTE);

    private OnFragmentInteractionListener mListener;

    private BrushDataViewModel mBrushDataViewModel;

    private TimerListAdapter mTimerAdapter;
    public SettingFragment33() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param brush_name Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment33.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment33 newInstance(String brush_name, String param2) {
        SettingFragment33 fragment = new SettingFragment33();
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

        //When we go back an activity from InactiveTimerList we want to reset this to false
        inactiveTimerSaved = false;

        mTimerAdapter = new TimerListAdapter(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setView = inflater.inflate(R.layout.fragment_setting, container, false);

        listTimers = (ListView) setView.findViewById(R.id.listViewTimers);
        listTimers.setAdapter(mTimerAdapter); // populate ListView with all the inactive times
        // when list is empty, display "No timers to show !"
        listTimers.setEmptyView(setView.findViewById(R.id.emptyElement));

        //Start & end time are clickable EditTexts
        startTime = (EditText) setView.findViewById(R.id.selectStartTime);
        endTime = (EditText) setView.findViewById(R.id.selectEndTime);
        btnSet = (Button) setView.findViewById(R.id.buttonSet);
//        MainActivity.inactiveTimes = new ArrayList<Timers>();

        // NB This will initially set the number of inactive timers we currently have
        // +1 because we want to be at the NEXT inactive timer screen
        // i.e. if we have 6 inactive timers set we want to be on the 7th timer to add another
        //EXCEPTION: when the max number 10 has been reached DO NOT add 1 to it

        //when the intent is created we want to check wheter we have the Max number of timers
        //If the max is reached we want to do the following

        if (getNumberOfInactiveTimers() >= MAX_NUM_INACTIVE_TIMERS) {
            startTime.setText("Max inactive times set");
            endTime.setText("Max inactive times set");
            startTime.setEnabled(false);
            endTime.setEnabled(false);
        }

        LocalDateTime ldtNow = LocalDateTime.parse(LocalDateTime.now().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
/*
        BrushDataViewModel.Factory factory = new BrushDataViewModel.Factory(
                getActivity().getApplication(), getArguments().getString(ARG_BRUSH_NAME), ldtNow);
*/
        BrushDataViewModel.Factory factory = new BrushDataViewModel.Factory(
                getActivity().getApplication(), brushName, ldtNow);
        mBrushDataViewModel = ViewModelProviders.of(this, factory).get(BrushDataViewModel.class);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showStartDateTimePickerDialog(v);
                showStartTimePickerDialog(v);
                startTimeFilled = true;
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTimeFilled) {//ensure we have a start time set initially
//                    showEndDateTimePickerDialog(v);
                    showEndTimePickerDialog(v);
                } else {
                    showShortToast("Please select a Start Time first");
                }
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimer();
            }
        });

        // display timers list
        displaySavedTimes();
        Log.d(TAG, "MainActivity.inactiveTimes " +MainActivity.inactiveTimes.toString());
        Log.d(TAG, "mTimerAdapter  " +mTimerAdapter.toString());

        // Testing
        String currentTime = getCurrentTime();  // "HH:mm"
        String hours = currentTime.substring(0, 2); //hours  eg 23
        String minutes = currentTime.substring(3, 5); //minutes eg 59
        Log.d(TAG, " getCurrentTime() hours " +hours +" min " +minutes); // getCurrentTime() hours 4: min 7  or  getCurrentTime() hours 16 min 07   depending on phone system settings
        Log.d(TAG, " FULL " +SimpleDateFormat.getTimeInstance(SimpleDateFormat.FULL).format(new Date()) // 5:25:32 PM Irish Standard Time
                +" LONG " +SimpleDateFormat.getTimeInstance(SimpleDateFormat.LONG).format(new Date())   // 5:25:32 PM IST
                +" MEDIUM " +SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM).format(new Date())   // HH:mm:SS
                +" SHORT " +SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(new Date()));   // HH:mm
        LocalTime localTime = LocalTime.now();
        Log.d(TAG, " SimpleDateFormat time " +SimpleDateFormat
					.getTimeInstance(SimpleDateFormat.SHORT).format(new Date())); 
					// SimpleDateFormat time 15:56 or 03:56 PM depending on phone system settings
        Log.d(TAG, " localTime " +localTime);   // localTime 15:56:06.845


        return setView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mBrushDataViewModel.loadInactiveTimes(ARG_BRUSH_NAME).observe(this, new Observer<List<InactiveTimeEntity>>() {
        mBrushDataViewModel.loadInactiveTimes(brushName).observe(this, new Observer<List<InactiveTimeEntity>>() {
            @Override
            public void onChanged(@Nullable final List<InactiveTimeEntity> inactiveEntities) {

//                int i =0;

                // reset
//                                Arrays.fill(MainActivity.inactiveTimes, 0);
//                MainActivity.inactiveTimes.clear();     // need to clear before
                mTimerAdapter.clearAllItems();  // clear() is faster than removeAll()

                for (InactiveTimeEntity inactiveEntity : inactiveEntities) {
                    Log.d(TAG, " loadInactiveTimes "
                            + "  " + inactiveEntity.getBrushName() + "  " + inactiveEntity.getInactiveTime());
//                    MainActivity.inactiveTimes.add(new Timers(startTime.getText().toString(), endTime.getText().toString()));
/*
                    MainActivity.inactiveTimes.set(i, inactiveEntity.getInactiveTime());
                    i++;
*/
//                    MainActivity.inactiveTimes.add(inactiveEntity.getInactiveTime());
                    mTimerAdapter.addItem(inactiveEntity.getInactiveTime());
                    Log.d(TAG, " onChanged MainActivity.inactiveTimes " +MainActivity.inactiveTimes.toString());
                    Log.d(TAG, " onChanged mTimerAdapter  " +mTimerAdapter.toString());
                }
                // display after there is a change
//                notifyDataSetChanged();
                displaySavedTimes();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
/*
        mTimerAdapter = new TimerListAdapter(getContext());
        listTimers = (ListView) setView.findViewById(R.id.listViewTimers);
        listTimers.setAdapter(mTimerAdapter); // populate ListView with all the inactive times
        // when list is empty, display "No timers to show !"
        listTimers.setEmptyView(setView.findViewById(R.id.emptyElement));
*/
    }

    public static String getCurrentTime() {
/*
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date currentTime = Calendar.getInstance().getTime();
//        Log.d(TAG, " getCurrentTime() " +dateFormat.format(currentTime));
        return (dateFormat.format(currentTime)); //08:52:46
*/
        return(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(new Date()));
    }

    //This is for the SAVE button the start & end times
    public void addTimer() {
        //If MAX_NUM_INACTIVE_TIMERS timers have been save we cannot save another
        if (getNumberOfInactiveTimers() == MAX_NUM_INACTIVE_TIMERS) {
            showShortToast("Maximum number of inactive timers("
                    + MAX_NUM_INACTIVE_TIMERS
                    + ") have been set. Please delete an existing timer to add another");
            return;
        }

        //Ensure both start and end times are selected before saving
        if (!timersFilled()) {
            return;
        }

        //NB NB This is the shared preferences where all the times are getting stored
        if (MainActivity.inactiveTimes == null) {
            return;
        }

//        MainActivity.inactiveTimes.add(new Timers(startTime.getText().toString(), endTime.getText().toString()));
//        Log.d(TAG, " addTimer() inactiveTimes " +MainActivity.inactiveTimes.toString());

        Log.d(TAG, " startTime " +startTime.getText().toString() +" endTime " +endTime.getText().toString());

        InactiveTimeEntity inactiveValue = new InactiveTimeEntity();
//        inactiveValue.setBrushName(ARG_BRUSH_NAME);
        inactiveValue.setBrushName(brushName);
        inactiveValue.setInactiveTime(new Timers(startTime.getText().toString(), endTime.getText().toString()));
        mBrushDataViewModel.insertInactive(inactiveValue);
//        SingleTinyDB.newInstance(this).putTimerArray(Constants.TIMER_ARRAY_KEY, inactiveTimesArr);

        //Successfully saved inactive timer
        inactiveTimerSaved = true;
        //When start/end times are eventually saved we want to grey out the SAVE, start & end times
        //We don't want to save the same time twice. We will need to add another inactive time.
/*
        btnSet.setEnabled(false);
        startTime.setEnabled(false);
        endTime.setEnabled(false);
*/
//        btnSet.clearComposingText();
        startTime.getText().clear();
        endTime.getText().clear();

        //update the next closest start time
//        nextTimerSetForTV.setText(nextTimerSetFor());
        showShortToast("Inactive timer saved");

        //Inactive timers have been changed
        //we must display a warning to the farmer that the times have been changed and has to be re-synced to the brush
        //in case he/she forgets to re-sync it.
//        showAlertWarningOnInactiveTimersScreen = true;

        //immediately jump to display timers page when we save
        displaySavedTimes();
        //We must also use shared preference if the app shuts down and the times have not being resynced
        //SingleTinyDB.newInstance(this).putBoolean(Constants.SHOW_ALERT_WARNING_ON_INACTIVE_TIMERS_SCREEN, showAlertWarningOnInactiveTimersScreen);
    }

    private void displaySavedTimes() {

        //if the times have been modified somewhat then warn the farmer to resync times in case he forgets
        //We must use a shared preference for this in cases a change is made and the app/phone is shut down
/*
        if (showAlertWarningOnInactiveTimersScreen) {
            showReSyncAlert();
        }
*/

        // when list is empty, display "No timers to show !"
        listTimers.setEmptyView(setView.findViewById(R.id.emptyElement));

        mTimerAdapter.notifyDataSetChanged();
    }

    //Quick method to check whether both a start and end time have been filled in when add/saving a timer
    public boolean timersFilled() {
        //Ensure both start and end times are selected before saving
        if ((startTime.getText().toString().contains("Start") &&
                endTime.getText().toString().contains("End")) ||
            (startTime.getText().toString().matches("") &&
                    endTime.getText().toString().matches(""))){
            showShortToast("Please select a start & stop time");
            return false;
        } else if (startTime.getText().toString().contains("Start")
                || startTime.getText().toString().matches("")) {
            showShortToast("Please select a start time");
            return false;
        } else if (endTime.getText().toString().contains("End")
                || endTime.getText().toString().matches("")) {
            showShortToast("Please select an end time");
            return false;
        }
        return true;
    }

    public void syncTimesToBrush() {

    }

    private void showShortToast(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void showLongToast(String message) {
/*
        if(null != toast) toast.cancel();
        (toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
*/
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void showStartDateTimePickerDialog(View v) {

        DatePickerDialog dd = new DatePickerDialog(getActivity(), R.style.DatePickerDialogCustomTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

/*
                        startTime.setText(String.format("%02d", dayOfMonth) + "/"
                                +String.format("%02d", (monthOfYear+1)) + "/"
                                + year);
*/
                        startTime.setText(String.format(Locale.US,"%02d", dayOfMonth)
                                .concat("/" +String.format(Locale.US,"%02d", (monthOfYear+1)) +"/" +year));

                        TimePickerDialog td = new TimePickerDialog(getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

/*
                                        startTime.setText(startTime.getText() + " - "
                                                +String.format("%02d", hourOfDay) + ":"
                                                +String.format("%02d", minute));
*/
                                        startTime.setText(startTime.getText().append(" - "
                                                +String.format(Locale.US, "%02d", hourOfDay)
                                                        .concat( ":" +String.format(Locale.US, "%02d", minute))));
/*
                                        if (hourOfDay >= 12) {
                                            startTime.setText(startTime.getText() + " - " + hourOfDay + ":"	+ minute +" PM ");
                                        } else{
                                            startTime.setText(startTime.getText() + " - " + hourOfDay + ":"	+ minute +" AM ");
                                        }
*/

                                    }
                                }, hour, min,
//                                    false
                                true
                        );
                        td.show();

                    }
                }, year, month, day
        );
        dd.show();

    }

    private void showStartTimePickerDialog(View v) {
        TimePickerDialog td = new TimePickerDialog(getActivity(), R.style.DatePickerDialogCustomTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

/*
                        startTime.setText(String.format("%02d", hourOfDay) + ":"
                                +String.format("%02d", minute));
*/
                        startTime.setText(String.format(Locale.US, "%02d", hourOfDay)
                                        .concat(":" +String.format(Locale.US, "%02d", minute)));

/*
                        if (hourOfDay >= 12) {
                            startTime.setText(startTime.getText() + " - " + hourOfDay + ":"	+ minute +" PM ");
                        } else{
                            startTime.setText(startTime.getText() + " - " + hourOfDay + ":"	+ minute +" AM ");
                        }
*/
                    }
                }, hour, min,
//                                    false
                true
        );
//        td.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
//        TimePickerDialog.OnClickListener.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//        listTimers.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        td.show();
    }

    /**
     * End Time (Inactive Time) picker dialog
     * @param v
     */
    private void showEndTimePickerDialog(View v) {
        TimePickerDialog td = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
/*
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        boolean b = false;
                        Date start_tm = sdf.parse(startTime);
                        Date end_tm = sdf.parse(endTime);
                        // Function to check whether a time is after an another time
                        b = end_tm.after(start_tm);
                        if(){
                            showShortToast("Please select End Time later than Start Time");
                        } else {
*/
/*
                            endTime.setText(String.format("%02d", hourOfDay) + ":"
                                    + String.format("%02d", minute));
*/
                        endTime.setText(String.format(Locale.US, "%02d", hourOfDay)
                                    .concat(":" +String.format(Locale.US, "%02d", minute)));

//                        }
                    }
                }, hour, min,
//                DateFormat.is24HourFormat(getActivity())
                false
        );
        td.show();
    }


    private void showEndDateTimePickerDialog(View v) {

        DatePickerDialog dd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

/*
                        endTime.setText(String.format("%02d", dayOfMonth) + "/"
                                +String.format("%02d", (monthOfYear+1)) + "/"
                                + year);
*/

                        endTime.setText(String.format(Locale.US, "%02d", dayOfMonth)
                                .concat("/" +String.format(Locale.US, "%02d", (monthOfYear+1)) +"/" +year));

                        TimePickerDialog td = new TimePickerDialog(getActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

/*
                                        endTime.setText(endTime.getText() + " - "
                                                +String.format("%02d", hourOfDay) + ":"
                                                +String.format("%02d", minute));
*/
                                        endTime.setText(endTime.getText().append(" - "
                                                +String.format(Locale.US, "%02d", hourOfDay)
                                                + ":"
                                                +String.format(Locale.US, "%02d", minute)));

                                    }
                                }, hour, min,
                                DateFormat.is24HourFormat(getActivity())
                        );
                        td.show();

                    }
                }, year, month, day
        );
        dd.show();

    }

    //This method returns the size of the shared preferences list i.e. Number of inactive timers
    public int getNumberOfInactiveTimers() {
//        return MainActivity.inactiveTimes.size();
        return mTimerAdapter.getCount();
    }


        // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSetFragmentInteraction(uri);
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
        void onSetFragmentInteraction(Uri uri);
    }


/*
    class TimerListAdapter extends BaseAdapter {

        Context context;

        //Constructor
        TimerListAdapter(Context context) {
            this.context = context;
        }

        @Override public int getCount() {
            return MainActivity.inactiveTimes.size();
        }

        public void addItem(Timers timer) {
            MainActivity.inactiveTimes.add(timer);
            // iEnabledTimer add is done in-place, before addItem.
            notifyDataSetChanged();
        }

        public void removeItem(int index) {
            MainActivity.inactiveTimes.remove(index);
            notifyDataSetChanged();
        }

        public void clearAllItems() {
            MainActivity.inactiveTimes.clear();     // clear() is faster than removeAll()
            notifyDataSetChanged();
        }

        @Override public Timers getItem(int position) {
            return MainActivity.inactiveTimes.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.single_row_timers, parent,
                    false); // contains a ref to Relative Layout

            //declare out xml views here
//            ImageView image = (ImageView) row.findViewById(R.id.imgViewClock);
            TextView description = (TextView) row.findViewById(R.id.tvTimerLayStartToEnd);
            TextView timernum = (TextView) row.findViewById(R.id.tvTimerLay);
            Button delete_btn = (Button) row.findViewById(R.id.btnDelete);


            if(getCount() > 0) {
//            image.setImageResource(R.drawable.timer_img);
                timernum.setText("Timer " +(position+1));

//            Timers tTimer = MainActivity.inactiveTimes.get(position);
                Timers tTimer = getItem(position);
                description.setText(tTimer.toString()); //toString() is overridden in timer class
//            delete_btn.setText("Delete " + (position + 1));
                //NB this will determine what delete button we click and will attempt to delete that inactive timer
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    deleteTimer(position);
                }
            });
		}
            return row;
        }
    }
*/

    class TimerListAdapter extends BaseAdapter {

        Context context;

        //Constructor
        TimerListAdapter(Context context) {
            this.context = context;
        }

        @Override public int getCount() {
            return MainActivity.inactiveTimes.size();
        }

        public void addItem(Timers timer) {
            MainActivity.inactiveTimes.add(timer);
            // iEnabledTimer add is done in-place, before addItem.
            notifyDataSetChanged();
        }

        public void removeItem(int index) {
            MainActivity.inactiveTimes.remove(index);
            notifyDataSetChanged();
        }

        public void clearAllItems() {
            MainActivity.inactiveTimes.clear();     // clear() is faster than removeAll()
            notifyDataSetChanged();
        }

        @Override public Timers getItem(int position) {
            return MainActivity.inactiveTimes.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public View getView(final int position, View convertView, ViewGroup parent) {
            // holder of the views to be reused.
//            ViewHolder viewHolder;

            // 2. Have we inflated this view before?
            View itemView;
            if (convertView != null) {
                // 2a. We have so let's reuse.
                itemView = convertView;
            }
            else {
                // 2b. We have NOT so let's inflate
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                itemView = inflater.inflate(R.layout.single_row_timers, parent, false);
            }

            TextView description = (TextView) itemView.findViewById(R.id.tvTimerLayStartToEnd);
            TextView timernum = (TextView) itemView.findViewById(R.id.tvTimerLay);
            Button delete_btn = (Button) itemView.findViewById(R.id.btnDelete);
            if(getCount() > 0) {
                timernum.setText("Timer " +(position+1));

                Timers tTimer = getItem(position);
                description.setText(tTimer.toString()); //toString() is overridden in timer class
                //NB this will determine what delete button we click and will attempt to delete that inactive timer
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        deleteTimer(position);
                    }
                });

            }
            return itemView;
        }
    }

    //When we hit a delete timer button we go in here, arg is provide which timer we are deleting
    public void deleteTimer(int timerNumber) {

        Log.d(TAG, " deleteTimer " +timerNumber);

        if (getNumberOfInactiveTimers() >= MAX_NUM_INACTIVE_TIMERS) {
            startTime.getText().clear();
            endTime.getText().clear();
            startTime.setEnabled(true);
            endTime.setEnabled(true);
        }

//        SingleTinyDB.newInstance(this).putTimerArray(Constants.TIMER_ARRAY_KEY, inactiveTimes);
//        inactiveTimes = SingleTinyDB.newInstance(this).getTimerArray(Constants.TIMER_ARRAY_KEY, Timers.class);
        // update DB
        InactiveTimeEntity inactiveValue = new InactiveTimeEntity();
//        inactiveValue.setBrushName(ARG_BRUSH_NAME);
        inactiveValue.setBrushName(brushName);
//        inactiveValue.setInactiveTime(listTimers.getAdapter().getItem(timerNumber));
//        inactiveValue.setInactiveTime(MainActivity.inactiveTimes.get(timerNumber));
        inactiveValue.setInactiveTime(mTimerAdapter.getItem(timerNumber));
        // no need to worry about isEnabled, as it's not Primary Key.
        mBrushDataViewModel.deleteInactiveTime(inactiveValue);

//        MainActivity.inactiveTimes.remove(timerNumber);
        mTimerAdapter.removeItem(timerNumber);

        showShortToast( "Deleting Inactive Timer " + (timerNumber + 1));
//        updateTimerList();

        //a timer has successfully been deleted, prompt the farmer to re-sync the brush times again in case he forgets to
        showReSyncAlert();
    }


    public void showReSyncAlert() {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(getActivity());
        myAlert.setMessage("Inactive times have changed please re-sync them with BoviBrush")
                .setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Alert!")
//                .setIcon(R.drawable.warning48)
                .create();
        myAlert.show();
    }

    public void updateTimerList() {
        //Make the table layout flash for half a second
        listTimers.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                listTimers.setVisibility(View.VISIBLE);
            }
        }, 500);
        //display the new list again i.e. complete update
        listTimers.setAdapter(new TimerListAdapter(getActivity()));//This will re-draw the updated ListView
    }

}
