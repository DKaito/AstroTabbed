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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SunFragment.OnFragmentInteractionListener, MoonFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TextClock textClock;
    private TextView localisation;
    private AstroDateTime astroDateTime = new AstroDateTime();
    private AstroCalculator astroCalculator;
    private Calendar calendar = Calendar.getInstance();
    private double latitude, longitude;
    private SunFragment sunFragment = SunFragment.getInstance();
    private SharedPreferences sharedPref;
    private String timeToSunset;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private static DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.GERMANY);
    private int interval;
    private Handler handler = new Handler();
    private Runnable schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        localisation = findViewById(R.id.localisation);
        textClock = findViewById(R.id.textClock);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(!(sharedPref.getString("refreshRate",null)==(null)))
            interval = Integer.parseInt(sharedPref.getString("refreshRate",null));
        else
            interval =10;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.refreshButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshCalculations();
                Log.d("Delay", Integer.toString(interval));
                handler.removeCallbacks(schedule);
                schedule = new Runnable() {
                    @Override
                    public void run() {
                        refreshCalculations();
                        handler.postDelayed(this, interval*60*1000);
                    }
                };
                handler.postDelayed(schedule,interval*60*1000);
            }
        });

        schedule = new Runnable() {
            @Override
            public void run() {
                refreshCalculations();
                handler.postDelayed(this, interval*60*1000);
            }
        };

        handler.postDelayed(schedule, 250);
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return sunFragment;
                case 1:
                    MoonFragment moonFragment = new MoonFragment();
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

    protected void Calculate(){

        astroDateTime.setYear(calendar.get(Calendar.YEAR));
        astroDateTime.setMonth(calendar.get(Calendar.MONTH)+1);
        astroDateTime.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        astroDateTime.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        astroDateTime.setMinute(calendar.get(Calendar.MINUTE));
        astroDateTime.setSecond(calendar.get(Calendar.SECOND));
        astroDateTime.setTimezoneOffset(2);
        astroDateTime.setDaylightSaving(false);
        astroCalculator = new AstroCalculator(astroDateTime, new AstroCalculator.Location(latitude,longitude));
    }

    public void refreshCalculations(){

        Calculate();


        if(!((sharedPref.getString("latitude", null)) == null && (sharedPref.getString("longitude", null)) == null)) {
            localisation.setText("Szerokość: " + sharedPref.getString("latitude", null) + " Długość: " + sharedPref.getString("longitude", null));
            latitude = Double.parseDouble(sharedPref.getString("latitude", null));
            longitude = Double.parseDouble(sharedPref.getString("longitude", null));
        }
        else
            localisation.setText("Szerokość: 0"+ " Długość: 0");

        if(!(sharedPref.getString("refreshRate",null)==(null)))
            interval = Integer.parseInt(sharedPref.getString("refreshRate",null));
        else
            interval =10;


        Calculate();

        String timeSunrise = astroCalculator.getSunInfo().getSunrise().toString();
        timeSunrise = timeSunrise.substring(timeSunrise.indexOf(" "),timeSunrise.lastIndexOf(" "));
        String timeSunset = astroCalculator.getSunInfo().getSunset().toString();
        timeSunset = timeSunset.substring(timeSunset.indexOf(" "),timeSunset.lastIndexOf(" "));

        String azimuthRise = df2.format(astroCalculator.getSunInfo().getAzimuthRise());
        String azimuthSet = df2.format(astroCalculator.getSunInfo().getAzimuthSet());

        String daybreak = astroCalculator.getSunInfo().getTwilightMorning().toString();
        daybreak = daybreak.substring(daybreak.indexOf(" "), daybreak.lastIndexOf(" "));
        String dawn = astroCalculator.getSunInfo().getTwilightEvening().toString();
        dawn = dawn.substring(dawn.indexOf(" "), dawn.lastIndexOf(" "));

        try {
            Date timeOfSundown = dateFormat.parse(timeSunset);
            Date currentTime = dateFormat.parse(textClock.getText().toString());

            double toSunset = timeOfSundown.getTime() - currentTime.getTime();

            double diffSeconds = toSunset / 1000 % 60;
            double diffMinutes = toSunset / (60 * 1000) % 60;
            double diffHours = toSunset / (60 * 60 * 1000) % 24;

            if(diffHours>0 && diffMinutes>0 && diffSeconds>0)
                timeToSunset = (diffHours<10 ? "0" + (int)diffHours : (int)diffHours) + ":" + (diffMinutes < 10 ? "0" + (int)diffMinutes : (int)diffMinutes) + ":" + (diffSeconds < 10 ? "0" + (int)diffSeconds : (int)diffSeconds);
            else
                timeToSunset = "Słońce zaszło";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        sunFragment.setFields(timeSunrise,azimuthRise, timeSunset, azimuthSet, daybreak, dawn, timeToSunset);
    }
}
