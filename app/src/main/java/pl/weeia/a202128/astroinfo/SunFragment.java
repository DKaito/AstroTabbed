package pl.weeia.a202128.astroinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SunFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private AstroDateTime astroDateTime = new AstroDateTime();
    private AstroCalculator astroCalculator;
    private Calendar calendar = Calendar.getInstance();
    private double latitude, longitude;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private static DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.GERMANY);
    private TextView timeSunrise, azimuthSunrise, timeSunset, azimuthSunset, timeDaybreak, timeDawn, timeToSunset;

    public SunFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view;

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE)
            view = inflater.inflate(R.layout.fragment_sun, container, false);
        else {
            if (getContext().getResources().getConfiguration().orientation == 1)
                view = inflater.inflate(R.layout.fragment_sun, container, false);
            else
                view = inflater.inflate(R.layout.fragment_sun_landscape, container, false);
        }
        timeSunrise = view.findViewById(R.id.timeSunrise);
        azimuthSunrise = view.findViewById(R.id.azymuthSunrise);
        timeSunset = view.findViewById(R.id.timeSunset);
        azimuthSunset = view.findViewById(R.id.azymuthSunset);
        timeDaybreak = view.findViewById(R.id.timeDaybreak);
        timeDawn = view.findViewById(R.id.timeDawn);
        timeToSunset = view.findViewById(R.id.timeToSunset);
        refreshCalculations(getActivity());

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public void setFields(String sunrise, String azimuthRise, String sunset, String azimuthSet, String daybreak, String dawn, String timeToSunset){
        this.timeSunrise.setText(sunrise);
        this.timeSunset.setText(sunset);
        this.azimuthSunrise.setText(azimuthRise);
        this.azimuthSunset.setText(azimuthSet);
        this.timeDaybreak.setText(daybreak);
        this.timeDawn.setText(dawn);
        this.timeToSunset.setText(timeToSunset);
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

    public void refreshCalculations(Context context){

        latitude = 0;
        longitude = 0;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if(!((sharedPref.getString("latitude", null)) == null && (sharedPref.getString("longitude", null)) == null)) {
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
        }

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

        String timeToSunset="";

        Calendar calendar = Calendar.getInstance();
        String time =  dateFormat.format(calendar.getTime());

       try {
            Date timeOfSundown = dateFormat.parse(timeSunset);
            Date currentTime = dateFormat.parse(time);

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
       setFields(timeSunrise,azimuthRise, timeSunset, azimuthSet, daybreak, dawn, timeToSunset);
    }
}
