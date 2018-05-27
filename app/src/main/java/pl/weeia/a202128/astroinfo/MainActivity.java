package pl.weeia.a202128.astroinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SunFragment.OnFragmentInteractionListener, MoonFragment.OnFragmentInteractionListener {

    private int interval, orientation;
    private FloatingActionButton fab;
    private Runnable schedule;
    private Handler handler = new Handler();
    private SunFragment sunFragment;
    private MoonFragment moonFragment ;
    private TextView localisation;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orientation = this.getResources().getConfiguration().orientation;

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                orientation+=2;
                if(orientation == 4)
                    setContentView(R.layout.activity_main_large_landscape);
                else
                    setContentView(R.layout.activity_main_large);
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                orientation+=2;
                if(orientation == 4)
                    setContentView(R.layout.activity_main_large_landscape);
                else
                    setContentView(R.layout.activity_main_large);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                if(orientation == 2)
                    setContentView(R.layout.activity_main_landscape);
                else
                    setContentView(R.layout.activity_main);
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                if(orientation == 2)
                    setContentView(R.layout.activity_main_landscape);
                else
                    setContentView(R.layout.activity_main);
                break;
            default:
                if(orientation == 2)
                    setContentView(R.layout.activity_main_landscape);
                else
                    setContentView(R.layout.activity_main);
                break;

        }

        localisation = findViewById(R.id.localisation);

        fab =  (FloatingActionButton) findViewById(R.id.refreshButton);

   /*     fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
*/
    }

    public void init(){

        int currentItem;
        if(mViewPager!=null)
            currentItem = mViewPager.getCurrentItem();
        else
            currentItem = 0;

        sunFragment = new SunFragment();
        moonFragment = new MoonFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(orientation==1||orientation==2) {

            SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);


            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
            mViewPager.setCurrentItem(currentItem);
        }
        else{
          //  getSupportFragmentManager().beginTransaction().remove(sunFragment).commit();
          //  getSupportFragmentManager().beginTransaction().remove(moonFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment,sunFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment2,moonFragment).commit();
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(!(sharedPref.getString("refreshRate",null)==(null)) && !((sharedPref.getString("latitude", null)) == null && (sharedPref.getString("longitude", null)) == null)) {
            interval = Integer.parseInt(sharedPref.getString("refreshRate", null));

            Double longitude = 0D;
            Double latitude = 0D;

            if(!sharedPref.getString("longitude", null).equals("")) {
                longitude = Double.parseDouble(sharedPref.getString("longitude", null));
                if (longitude > 180)
                    longitude = 180D;
                else if (longitude < -180)
                    longitude = -180D;
            }
            if(!sharedPref.getString("latitude", null).equals("")) {
                latitude = Double.parseDouble(sharedPref.getString("latitude", null));
                if (latitude > 90)
                    latitude = 90D;
                else if (latitude < -90)
                    latitude = -90D;
            }
            localisation.setText("Długość: " + longitude.toString() + " Szerokość: " +latitude.toString());
        }else {
            interval = 10;
            localisation.setText("Długość: 0" + " Szerokość: 0");
        }

       // mViewPager.setCurrentItem(currentItem);
    }


    @Override
    public void onStart() {
        //init();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
                handler.removeCallbacks(schedule);
                schedule = new Runnable() {
                    @Override
                    public void run() {
                        init();
                        handler.postDelayed(this, interval*60*1000);
                    }
                };
                handler.postDelayed(schedule,interval*60*1000);
            }
        });

        schedule = new Runnable() {
            @Override
            public void run() {
                init();
                handler.postDelayed(this, interval*60*1000);
            }
        };

        handler.postDelayed(schedule, 250);
        super.onStart();
    }
/*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

*/
    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return sunFragment ;
                case 1:
                    return moonFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
