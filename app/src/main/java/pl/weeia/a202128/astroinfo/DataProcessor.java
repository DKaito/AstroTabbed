package pl.weeia.a202128.astroinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DataProcessor {

    private AstroDateTime astroDateTime = new AstroDateTime();
    private AstroCalculator astroCalculator;
    private Calendar calendar = Calendar.getInstance();
    private double latitude, longitude;
    private SharedPreferences sharedPref;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private static DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.GERMANY);


    public DataProcessor(){

    }

    public static DataProcessor getDataProcessor(){
        return new DataProcessor();
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

        Log.d("CALCULATE","calculate");
    }


    public void refreshCalculations(Context context, SunFragment sunFragment, String time){

        Log.d("REFRESH","refresh");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if(!((sharedPref.getString("latitude", null)) == null && (sharedPref.getString("longitude", null)) == null)) {
            latitude = Double.parseDouble(sharedPref.getString("latitude", null));
            longitude = Double.parseDouble(sharedPref.getString("longitude", null));
        }
        else{
            latitude = 0;
            longitude = 0;
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

        String timeToSunset = "";

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
        sunFragment.setFields(timeSunrise,azimuthRise, timeSunset, azimuthSet, daybreak, dawn, timeToSunset);
    }
}
