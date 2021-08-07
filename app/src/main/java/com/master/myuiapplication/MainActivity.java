package com.master.myuiapplication;

import android.app.FragmentTransaction;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
//import android.app.FragmentManager;
// Can't use because of "ViewModelProviders.of(Fragment)"
//import android.app.Fragment;

//import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar;
//import android.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.master.myuiapplication.JunkFragment;
import com.master.myuiapplication.Utils.Timers;
import com.master.myuiapplication.db.viewmodel.BrushDataViewModel;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;


public class MainActivity extends AppCompatActivity  implements InfoFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener, TestingFragment.OnFragmentInteractionListener,
        SettingFragment22.OnFragmentInteractionListener, DevicesListFragment.OnFragmentInteractionListener{

    private final static String TAG = "MainActivity";
//    private TextView mTextMessage;
    InfoFragment infoFragment;
    HomeFragment homeFragment;
    TestingFragment testFragment;
    SettingFragment22 setFragment;
    DevicesListFragment devicesListFragment;


    ImageView myToolbarImage;
    TextView myToolbarTitle;
    android.support.v7.widget.Toolbar myToolbar;

    private static String BRUSH_NAME_TEMP = "DMBrush 1";

    //save our header or result
    private Drawer resultDrawer = null;

    private static BrushDataViewModel mBrushDataViewModel;
    public static List<String> localBrushNames;

    //    public static EditText startTime;
//    public  EditText endTime;
    public static ArrayList<Timers> inactiveTimes = null; // store all the start & end times
// <StartTimeHH:mm> - <EndTimeHH:mm> pair array.

    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragManagerHome = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragTransaction = fragManagerHome.beginTransaction();
/*
            FragmentManager fragManager = getFragmentManager();
            FragmentTransaction fragTransaction = fragManager.beginTransaction();
*/

//            android.support.v7.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            Log.d(TAG, " onNavigationItemSelected() item " +item);

            switch (item.getItemId()) {
                case R.id.navigation_home2:
//                    Log.d(TAG, " navigation_home inactiveTimes " +inactiveTimes.toString());

                    setUpToolBar(R.drawable.ic_home_icon, BRUSH_NAME_TEMP);
                    homeFragment = HomeFragment.newInstance(BRUSH_NAME_TEMP, " ");
                    fragTransaction.replace(R.id.container_fragment, homeFragment)
                            .commit();
                    return true;
                case R.id.navigation_set2:
//                    Log.d(TAG, " navigation_set inactiveTimes " +inactiveTimes.toString());
                    setUpToolBar(R.drawable.ic_settings_header, "Settings");
                    setFragment = SettingFragment22.newInstance(BRUSH_NAME_TEMP, " ");
                    fragTransaction.replace(R.id.container_fragment, setFragment)
                            .commit();
                    return true;
                case R.id.navigation_test2:
                    setUpToolBar(R.drawable.ic_test_header_copy3, "Brush Test");
                    testFragment = TestingFragment.newInstance("Testing", " ");
                    fragTransaction.replace(R.id.container_fragment, testFragment)
                            .commit();
                    return true;
                case R.id.navigation_information2:
                    setUpToolBar(R.drawable.ic_information_header, "Information");
                    infoFragment = InfoFragment.newInstance(" ", " ");
                    fragTransaction.replace(R.id.container_fragment, infoFragment);
                    fragTransaction.commit();
                    return true;
           }
            return false;
        }
    };

    // Method for disabling ShiftMode of BottomNavigationView
    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
// API disabled from 28.0.0
//                item.setShiftingMode(false);
                item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Unable to change value of shift mode", e);
        }
    }

    public void setUpToolBarOLDVers(android.support.v7.widget.Toolbar mToolbar, int resID, String mTitle) {
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void setUpToolBar(Toolbar mToolbar, int resID, String mTitle) {
        mToolbar.setLogo(resID);
        mToolbar.setTitle(" "+mTitle);  // space is silly hack to add a space between Logo and Title !
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorTextBright));

/*
        mToolbar.setLayoutParams(new android.support.v7.widget.Toolbar.LayoutParams(
                android.support.v7.widget.Toolbar.TEXT_ALIGNMENT_CENTER));
*/
//        android.support.v7.widget.Toolbar.LayoutParams(android.support.v7.widget.Toolbar.TEXT_ALIGNMENT_CENTER);
//        mToolbar.setLayoutParams(new Toolbar.LayoutParams(Toolbar.TEXT_ALIGNMENT_CENTER));

        mToolbar.setTextAlignment(android.support.v7.widget.Toolbar.TEXT_ALIGNMENT_CENTER);

        //        setActionBar(mToolbar);
        setSupportActionBar(mToolbar);
    }

    public void setUpToolBar(int resID, String mTitle) {
        myToolbarTitle.setText(" "+mTitle);  // space is silly hack to add a space between Logo and Title !
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorTextBright));

        myToolbarImage.setImageResource(resID);
//        setSupportActionBar(mToolbar);

    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        disableShiftMode(navigation);

//        FragmentManager fragManager = getFragmentManager();
        FragmentManager fragManager = getSupportFragmentManager();
//        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        android.support.v4.app.FragmentTransaction fragTransaction = fragManager.beginTransaction();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);    // Remove default title text
        myToolbarTitle = (TextView) myToolbar.findViewById(R.id.toolbar_title);
        myToolbarImage = (ImageView)  myToolbar.findViewById(R.id.toolbar_image);

        inactiveTimes = new ArrayList<Timers>();
        Log.d(TAG, " inactiveTimes initial val " +inactiveTimes.toString());

        setUpToolBar(R.drawable.ic_home_icon, BRUSH_NAME_TEMP);
        homeFragment = HomeFragment.newInstance(BRUSH_NAME_TEMP, " ");
        fragTransaction.replace(R.id.container_fragment, homeFragment)
                .commit();

        //Create the drawer
        resultDrawer = new DrawerBuilder(this)
                //this layout have to contain child layouts
//                .withRootView(R.id.container_fragment)
                .withRootView(R.id.drawer_container)
                .withToolbar(myToolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
//                    new PrimaryDrawerItem().withName(BRUSH_NAME_TEMP).withIcon(R.drawable.ic_test_header_copy3),
                    new PrimaryDrawerItem().withName(R.string.drawer_item_select).withIcon(R.drawable.ic_select_brush),
                    new PrimaryDrawerItem().withName(R.string.drawer_item_search).withIcon(R.drawable.search_menu_copy34),
                    new PrimaryDrawerItem().withName(R.string.drawer_item_sync).withIcon(R.drawable.ic_sync_blue_24dp),
//                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                    new SectionDrawerItem().withName(R.string.drawer_item_section_support),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(R.drawable.ic_help_outline_blue_24dp),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(R.drawable.ic_contact_phone_blue_24dp)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(MainActivity.this,
                                    ((Nameable) drawerItem).getName().getText(MainActivity.this)
                                            +" position " +position +" drawerItem " +drawerItem,
                                    Toast.LENGTH_LONG).show();
                            switch (position){
                                case 0: // BRUSH_NAME_TEMP
                                    // list all Local DB Brush devices
                                    FragmentManager fragManager = getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction fragTransaction = fragManager.beginTransaction();
                                    devicesListFragment = DevicesListFragment.newInstance();
                                    fragTransaction.replace(R.id.container_fragment, devicesListFragment)
                                            .commit();
                                    break;
                                case 1: // search
                                    // search BT for Brush devices
                                    break;
                                case 2: // sync
                                    // Connect and sync all DB Brush devices if nearby !
                                    break;
                                case 3: // SectionDrawer
                                case 4: // help
                                    break;
                                case 5: // contact
                                    goToWebsiteContact();
                                    break;
                                default:
                                    break;
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //set the selection to none (with the identifier -1).
        // by default, it used to keep selected first item.
        resultDrawer.setSelection(-1);

    }

    private void goToWebsiteContact(){
        Uri webPage = Uri.parse("https://www.master.com/ie/contact-us/");
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
/*
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.master.com/ie/contact-us/"));
        startActivity(browserIntent);
*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart() ");

        // for this, Brush name can be dummy - "DMBrush 1"
        LocalDateTime ldtNow = LocalDateTime.parse(LocalDateTime.now().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        BrushDataViewModel.Factory factory = new BrushDataViewModel.Factory(
                getApplication(), "DMBrush 1", ldtNow);
        mBrushDataViewModel = ViewModelProviders.of(this, factory).get(BrushDataViewModel.class);

//         keep watching for brush names
        mBrushDataViewModel.loadBrushNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> brushDeviceNames) {
                localBrushNames = brushDeviceNames;
                Log.d(TAG, " brushDeviceNames localBrushNames " +localBrushNames);
//                Log.d(TAG, " brushDeviceNames " );
//                for (String brushName : brushDeviceNames) {
//                    Log.d(TAG, " brushName " +brushName);
//                }
            }
        });

    }

    public static List<String> getLocalBrushNames(){
        return localBrushNames;
    }

/*
    @Override
    protected void onStart() {
        super.onStart();
        //Create the drawer
        result = new DrawerBuilder(this)
                //this layout have to contain child layouts
//                .withRootView(R.id.container_fragment)
                .withRootView(R.id.container_fragment2)
                .withToolbar(myToolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(BRUSH_NAME_TEMP).withIcon(R.drawable.ic_test_header_copy3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_search).withIcon(R.drawable.ic_search_black_24dp),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_sync).withIcon(R.drawable.ic_sync_black_24dp),
//                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        new SectionDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(R.drawable.ic_help_outline_black_24dp),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(R.drawable.ic_contact_phone_black_24dp)
                )
//                .withSavedInstance(savedInstanceState)
                .build();

    }
*/

    @Override
    public void onInfoFragmentInteraction(Uri uri){
        Log.v(TAG, " from InfoFragment : onFragmentInteraction() ");

    }

    @Override
    public void onTestFragmentInteraction(Uri uri){
        Log.v(TAG, " from InfoFragment : onFragmentInteraction() ");

    }

    @Override
    public void onSetFragmentInteraction(Uri uri){
        Log.v(TAG, " from InfoFragment : onFragmentInteraction() ");

    }

    @Override
    public void onHomeFragmentInteraction(Uri uri){
        Log.v(TAG, " from HomeFragment : onFragmentInteraction() ");

    }

    @Override
    public void onDevicesListFragmentInteraction(int position, String deviceName){
        Log.v(TAG, " from DevicesListFragment : onFragmentInteraction() position " +position);

        BRUSH_NAME_TEMP = deviceName;
        Log.v(TAG, " onDevicesListFragmentInteraction() BRUSH_NAME_TEMP " +BRUSH_NAME_TEMP);

/*
        FragmentManager fragManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragTransaction = fragManager.beginTransaction();
        setUpToolBar(R.drawable.ic_home_icon, BRUSH_NAME_TEMP);
        homeFragment = HomeFragment.newInstance(BRUSH_NAME_TEMP, " ");
        fragTransaction.replace(R.id.container_fragment, homeFragment)
                .commit();
*/

//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation = (BottomNavigationView) findViewById(R.id.navigation);

// this replaces fragment only, but does not change "selected item" as HOME.
//        mOnNavigationItemSelectedListener.onNavigationItemSelected(navigation.getMenu().getItem(0));
        navigation.setSelectedItemId(R.id.navigation_home2);

        Log.d(TAG, " BottomNavigationView getMenu getItem(0) " +navigation.getMenu().getItem(0)
                    +" getItem(1) " +navigation.getMenu().getItem(1));
    }


}
