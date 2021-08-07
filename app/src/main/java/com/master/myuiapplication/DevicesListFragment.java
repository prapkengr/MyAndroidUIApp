package com.master.myuiapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.master.myuiapplication.dummy.DummyContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Can't use because of "ViewModelProviders.of(Fragment)"
//import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevicesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevicesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//public class DevicesListFragment extends Fragment implements AdapterView.OnItemClickListener {
public class DevicesListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
*/
    private static final String TAG = DevicesListFragment.class.getSimpleName();

    private Handler handler = new Handler();

    private TextView mTextMessage;

    private OnFragmentInteractionListener mListener;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private View viewDevices;

    private static String BRUSH_NAME_TEMP;

//    private MyDevicesListRecyclerViewAdapter mDevicesListAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DevicesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    public static DevicesListFragment newInstance(String param1, String param2) {
        DevicesListFragment fragment = new DevicesListFragment();
        Bundle args = new Bundle();
/*
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
*/
        fragment.setArguments(args);
        return fragment;
    }

    public static DevicesListFragment newInstance() {
        Log.d(TAG, " newInstance ");
        return (new DevicesListFragment());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate ");

/*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, " onCreateView ");

//        final View viewDevices;

        // Inflate the layout for this fragment
        viewDevices = inflater.inflate(R.layout.fragment_deviceslist, container, false);

/*
        // Set the adapter
        if (viewDevices instanceof RecyclerView) {
            Context context = viewDevices.getContext();
            RecyclerView recyclerView = (RecyclerView) viewDevices;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mDevicesListAdapter = new MyDevicesListRecyclerViewAdapter(MainActivity.getLocalBrushNames(), mListener);
            recyclerView.setAdapter(mDevicesListAdapter);
        }
*/

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();

//        ListView lvDevices = (ListView) findViewById(R.id.lvDevices);
        ListView lvDevices = (ListView) viewDevices.findViewById(R.id.lvDevices);
        lvDevices.setAdapter(mLeDeviceListAdapter);
//        lvDevices.setOnItemClickListener(this);

        return viewDevices;
    }

    public void onButtonPressed(int position) {
        Log.d(TAG, " onButtonPressed ");

/*
        if (mListener != null) {
            mListener.onDevicesListFragmentInteraction(position);
        }
*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, " onAttach ");
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
        Log.d(TAG, " onDetach ");

        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, " onResume ");

/*
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
//        ListView lvDevices = (ListView) findViewById(R.id.lvDevices);
        ListView lvDevices = (ListView) viewDevices.findViewById(R.id.lvDevices);
        lvDevices.setAdapter(mLeDeviceListAdapter);
        lvDevices.setOnItemClickListener(this);
*/

    }

    private void setListAdapter(BaseAdapter baseAdapter) {
    }

        @Override
    public void onPause() {
        super.onPause();
            Log.d(TAG, " onPause ");

//        mDevicesListAdapter.clear();
//        mLeDeviceListAdapter.clear();
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
        void onDevicesListFragmentInteraction(int position, String deviceName);
    }


/*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onListItemClick  position " +position +" id " +id);


    }
*/


    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private List<String> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
//            mLeDevices = new List<String>();
            mLeDevices = MainActivity.getLocalBrushNames();
            Log.d(TAG, " LeDeviceListAdapter getLocalBrushNames " +MainActivity.getLocalBrushNames());
            mInflator = DevicesListFragment.this.getLayoutInflater();
            Log.d(TAG, " LeDeviceListAdapter mLeDevices " +mLeDevices);
        }

        public void addDevice(String device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                notifyDataSetChanged();
            }
        }

        public String getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            String device = mLeDevices.get(i);
            ViewHolder viewHolder;
            // General ListView optimization code.
/*
            if (view == null) {
                view = mInflator.inflate(R.layout.fragment_deviceslist_item, viewGroup);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
*/
//            view = mInflator.inflate(R.layout.fragment_deviceslist_item, null);
            if (view == null) {
                view = mInflator.inflate(R.layout.fragment_deviceslist_item, viewGroup, false);
            }
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);

//            final View finalView = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onItemClick(null, finalView, i, i);
                    mListener.onDevicesListFragmentInteraction(i, getDevice(i));
                }
            });


/*
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceRssi.setText("(RSSI: " + String.valueOf(mDevicesRssi.get(device)) + ")");
*/
            viewHolder.deviceName.setText(device);

            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
    }
}
