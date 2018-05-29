package pl.weeia.a202128.astroweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAstroInfo(View v){
        Intent intent = new Intent(this, AstroInfo.class);
        startActivity(intent);
    }
    public void startAstroWeather(View v){
        Intent intent = new Intent(this, AstroWeather.class);
        startActivity(intent);
    }
}
