package pl.weeia.a202128.astroinfo;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextClock;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SunFragment.OnFragmentInteractionListener, MoonFragment.OnFragmentInteractionListener {

    private int interval, currentItem;
    //private TextClock textClock;
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
        setContentView(R.layout.activity_main);

        localisation = findViewById(R.id.localisation);
       // textClock = findViewById(R.id.textClock);

        fab = (FloatingActionButton) findViewById(R.id.refreshButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });


    }

    public void init(){

        if(mViewPager!=null)
            currentItem = mViewPager.getCurrentItem();
        else
            currentItem = 0;

        sunFragment = new SunFragment();
        moonFragment = new MoonFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(!(sharedPref.getString("refreshRate",null)==(null)) && !((sharedPref.getString("latitude", null)) == null && (sharedPref.getString("longitude", null)) == null)) {
            interval = Integer.parseInt(sharedPref.getString("refreshRate", null));
            localisation.setText("Szerokość: " + sharedPref.getString("latitude", null) + " Długość: " + sharedPref.getString("longitude", null));
        }else {
            interval = 10;
            localisation.setText("Szerokość: 0"+ " Długość: 0");
        }

        mViewPager.setCurrentItem(currentItem);
    }


    @Override
    public void onStart() {
        init();

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


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
