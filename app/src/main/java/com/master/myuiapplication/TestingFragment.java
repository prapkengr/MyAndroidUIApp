package com.master.myuiapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
// Can't use because of "ViewModelProviders.of(Fragment)"
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final int SECOND_MILLISEC = 1000;
    final int BRUSH_TEST_DURATION = 2*60*SECOND_MILLISEC; //i.e. brush spinning for 2 minutes
    private boolean isPlaying = false;
    CountDownTimer cdTimer = null;
    int brushTestSec = 0;
    int brushTestMin = 0;
    int brushTestHr = 0;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ImageButton buttonPlayPause;
    private ImageButton buttonStop;
    private ImageButton buttonRightRot;
    private ImageButton buttonLeftRot;
    private TextView tvTest;
    private ImageView imgTesting;
    private TextView mTextTimer;
//    private boolean timerPaused = false;
    private long milliLeft;     // for storing in case of PAUSE

    public TestingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestingFragment newInstance(String param1, String param2) {
        TestingFragment fragment = new TestingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View viewTest;

        // Inflate the layout for this fragment
        viewTest = inflater.inflate(R.layout.fragment_testing, container, false);

        mTextTimer = (TextView) viewTest.findViewById(R.id.textviewTimeTested);

        buttonPlayPause = (ImageButton) viewTest.findViewById(R.id.imgbuttonPlayPause);
        buttonPlayPause.setVisibility(View.INVISIBLE);
        buttonStop = (ImageButton) viewTest.findViewById(R.id.imgbuttonStop);
        buttonStop.setVisibility(View.INVISIBLE);

        buttonRightRot = (ImageButton) viewTest.findViewById(R.id.imgbuttonRightRot);
        buttonLeftRot = (ImageButton) viewTest.findViewById(R.id.imgbuttonLeftRot);

        imgTesting = viewTest.findViewById(R.id.imgviewTesting);

        tvTest = viewTest.findViewById(R.id.textviewTest);

        // perform click event on button's
        buttonRightRot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRightRot.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right_selected_copy));
                buttonLeftRot.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_not_selected));
                displayTesting();
                startBrushTimer(BRUSH_TEST_DURATION, SECOND_MILLISEC);
            }
        });
        buttonLeftRot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLeftRot.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_left_selected_copy));
                buttonRightRot.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_not_selected_copy));
                displayTesting();
                startBrushTimer(BRUSH_TEST_DURATION, SECOND_MILLISEC);
            }
        });
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
// Now Playing. Do Pause, and display Play button
                    buttonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.start_play_test));
//                    Toast.makeText(getActivity(), "Play button; isPlaying= " +isPlaying, Toast.LENGTH_SHORT).show();
                    pauseTimer();
                } else {
                    buttonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_test));
                    pauseToPlayTimer();
                }
                isPlaying = !isPlaying;
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayStartTest();
                cancelTimer();
            }
        });

        return viewTest;
    }

    public void displayTesting() {

        imgTesting.setImageDrawable(getResources().getDrawable(R.drawable.bovi_brush_testing));
        buttonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_test));
        buttonPlayPause.setVisibility(View.VISIBLE);
        isPlaying = true;
//        Toast.makeText(getActivity(), "displayTesting() isPlaying= " +isPlaying, Toast.LENGTH_SHORT).show();
        buttonStop.setVisibility(View.VISIBLE);
        tvTest.setText(R.string.text_testing);

    }

    public void displayStartTest() {

        imgTesting.setImageDrawable(getResources().getDrawable(R.drawable.bovi_brush_stopped));
//        buttonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.start_play_test));
        buttonPlayPause.setVisibility(View.INVISIBLE);
        isPlaying = false;
//        Toast.makeText(getActivity(), "displayStartTest() isPlaying= " +isPlaying, Toast.LENGTH_SHORT).show();
        buttonStop.setVisibility(View.INVISIBLE);
        tvTest.setText(R.string.text_starttest);
        buttonLeftRot.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_not_selected));
        buttonRightRot.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_not_selected_copy));

    }


    public void startBrushTimer(long duration, long timingInterval) {
        //initializing the clock according to the 'BRUSH_TIMER_DURATION' supplied
/*
        brushTestSec =
                (BRUSH_TEST_DURATION % (SECOND_MILLISEC * 60)) / SECOND_MILLISEC; //121,000 % 60000 = 1000 / 1000 = 1
        brushTestMin = (BRUSH_TEST_DURATION / (SECOND_MILLISEC * 60)) % 60;
        brushTestHr = BRUSH_TEST_DURATION / (SECOND_MILLISEC * 60 * 60);
*/

        //If the timer HAS already started, don't start it again
        if (cdTimer == null) {
            //how long do we want to timer to last and the timing interval
            cdTimer = new CountDownTimer(duration, timingInterval) {
                @Override
                public void onTick(long millisUntilFinished) {

                    milliLeft = millisUntilFinished; 	// needed for timer pause 
                    brushTestSec = (int) ((millisUntilFinished %(SECOND_MILLISEC * 60))/SECOND_MILLISEC);
                    brushTestMin = (int) ((millisUntilFinished / (SECOND_MILLISEC * 60)) % 60);
                    brushTestHr = (int) (millisUntilFinished / (SECOND_MILLISEC * 60 * 60));
                    mTextTimer.setText(String.format(Locale.getDefault(), "%02d", brushTestHr)
                            + ":"
                            + String.format(Locale.getDefault(), "%02d", brushTestMin)
                            + ":"
                            + String.format(Locale.getDefault(), "%02d", brushTestSec));

                }

                @Override
                public void onFinish() {
                    brushTestHr = 0;
                    brushTestMin = 0;
                    brushTestSec = 0;
                    //When the 'duration' is reached reset the timer
                    mTextTimer.setText(String.format(Locale.getDefault(), "%02d", brushTestHr)
                            + ":"
                            + String.format(Locale.getDefault(), "%02d", brushTestMin)
                            + ":"
                            + String.format(Locale.getDefault(), "%02d", brushTestSec));

                    if(isPlaying) {  // because PAUSE also comes through here !
                        //When 2 minutes is up. show button as released
                        displayStartTest();
                    }

                    //When the timer has finished cancel it & reset it back to null, so we can start over if we want
                    cancelTimer();
                }
            };
            cdTimer.start();
        }

    }

    void cancelTimer() {
        if (cdTimer != null) {
            cdTimer.cancel();
            cdTimer = null;
        }
    }

    void pauseTimer() {
//        timerPaused = true;
        cancelTimer();
    }

    void pauseToPlayTimer() {
//        timerPaused = false;
        startBrushTimer(milliLeft, SECOND_MILLISEC);    // re - start timer with the remaining time
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTestFragmentInteraction(uri);
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
        void onTestFragmentInteraction(Uri uri);
    }
}
