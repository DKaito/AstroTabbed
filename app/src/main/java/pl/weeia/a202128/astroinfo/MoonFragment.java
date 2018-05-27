package pl.weeia.a202128.astroinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class MoonFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private AstroDateTime astroDateTime = new AstroDateTime();
    private AstroCalculator astroCalculator;
    private Calendar calendar = Calendar.getInstance();
    private double latitude, longitude;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    private static DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.GERMAN);

    private TextView timeMoonrise, newMoon, timeMoonset, fullMoon, moonDay, moonPhase;

    public MoonFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moon, container, false);
        timeMoonrise = view.findViewById(R.id.timeMoonrise);
        timeMoonset = view.findViewById(R.id.timeMoonset);
        newMoon = view.findViewById(R.id.newMoon);
        fullMoon = view.findViewById(R.id.fullMoon);
        moonDay = view.findViewById(R.id.moonDay);
        moonPhase = view.findViewById(R.id.moonPhase);
        refreshCalculations(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

    public void setFields(String moonrise, String moonset, String newmoon, String fullmoon, String moonday, String moonphase){
        timeMoonrise.setText(moonrise);
        timeMoonset.setText(moonset);
        newMoon.setText(newmoon);
        fullMoon.setText(fullmoon);
        moonDay.setText(moonday);
        moonPhase.setText(moonphase);
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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if(!((sharedPref.getString("latitude", null)) == null && (sharedPref.getString("longitude", null)) == null)) {
            latitude = Double.parseDouble(sharedPref.getString("latitude", null));
            longitude = Double.parseDouble(sharedPref.getString("longitude", null));
        }
        else{
            latitude = 0;
            longitude = 0;
        }

        Calculate();

        String timeMoonrise = astroCalculator.getMoonInfo().getMoonrise().toString();
        timeMoonrise = timeMoonrise.substring(timeMoonrise.indexOf(" "),timeMoonrise.lastIndexOf(" "));
        String timeMoonset = astroCalculator.getMoonInfo().getMoonset().toString();
        timeMoonset = timeMoonset.substring(timeMoonset.indexOf(" "),timeMoonset.lastIndexOf(" "));

        String newMoon = astroCalculator.getMoonInfo().getNextNewMoon().toString();
        newMoon = newMoon.substring(0,newMoon.lastIndexOf(" "));
        String fullMoon = astroCalculator.getMoonInfo().getNextFullMoon().toString();
        fullMoon = fullMoon.substring(0,fullMoon.lastIndexOf(" "));

        String synodicDay = String.format(Locale.GERMAN,"%.0f", astroCalculator.getMoonInfo().getAge());
        String dawn = String .format(Locale.GERMAN,"%.0f%%",astroCalculator.getMoonInfo().getIllumination()*100);

        setFields(timeMoonrise, timeMoonset, newMoon, fullMoon, synodicDay, dawn);

    }
}
