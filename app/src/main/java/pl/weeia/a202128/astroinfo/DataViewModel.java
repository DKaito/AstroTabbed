package pl.weeia.a202128.astroinfo;

import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;


public class DataViewModel extends ViewModel {

    private SunFragment sunFragment;
    private DataProcessor dataProcessor;
    private ArrayList<TextView> sunFragmentList = new ArrayList<>();
    private Runnable schedule;
    private Handler handler;

    public SunFragment getSunFragment(){
        return this.sunFragment;
    }

    public void update(SunFragment sunFragment, Runnable schedule, Handler handler){
        this.sunFragment = sunFragment;
        this.schedule=schedule;
        this.handler = handler;
    }

    public void updateFragmentData(TextView timeSunrise, TextView azimuthSunrise, TextView timeSunset, TextView azimuthSunset, TextView timeDaybreak, TextView timeDawn, TextView timeToSunset){
        sunFragmentList.clear();
        sunFragmentList.add(timeSunrise);
        sunFragmentList.add(azimuthSunrise);
        sunFragmentList.add(timeSunset);
        sunFragmentList.add(azimuthSunset);
        sunFragmentList.add(timeDaybreak);
        sunFragmentList.add(timeDawn);
        sunFragmentList.add(timeToSunset);
    }

    public ArrayList<TextView> getSunFragmentList(){
        return sunFragmentList;
    }

    public DataProcessor getDataProcessor() {
        return this.dataProcessor;
    }

    public Handler getHandler(){
        return handler;
    }

    public Runnable getSchedule(){
        return schedule;
    }
}
