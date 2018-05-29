package pl.weeia.a202128.astroweather;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchWeatherData {

    private Context context;


    public FetchWeatherData(Context context){
        this.context = context;
    }

    public void getWoeid(Context context, String urlWebService){
        getJsonData(context, urlWebService, 1);
    }

    public void getWeather(Context context, String urlWebService){
        getJsonData(context, urlWebService, 2);
    }

    public void getJsonData(final Context context, final String urlWebService, final int option) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(option == 1){
                    try {
                        Toast.makeText(context, extractWoeidFromJson(s), Toast.LENGTH_SHORT).show();
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error processing response data", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    try {
                        Toast.makeText(context, extractWeatherFromJson(s), Toast.LENGTH_SHORT).show();
                       // saveWeatherDataToFile(context);
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Nothing here.", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                try{

                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                }catch (Exception e) {
                    return null;
                }

            }
        }

        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    protected String extractWoeidFromJson(String json) throws JSONException{

        JSONObject jsonObject = new JSONObject(json);
        jsonObject = jsonObject.getJSONObject("query");
        jsonObject = jsonObject.getJSONObject("results");
        jsonObject = jsonObject.getJSONObject("place");
        String woeid= jsonObject.getString("woeid");

        return woeid;
    }

    protected String extractWeatherFromJson(String json) throws JSONException{

        JSONObject jsonObject = new JSONObject(json);
        jsonObject = jsonObject.getJSONObject("query");
        jsonObject = jsonObject.getJSONObject("results");
        jsonObject = jsonObject.getJSONObject("channel");

        //JSONArray jsonArray = new JSONArray(jsonObject.);
       // jsonArray = jsonObject.toJSONArray(jsonObject.names());


        String woeid= jsonObject.getJSONObject("item").names().toString();

        saveWeatherDataToFile(woeid);

        return woeid;
    }

    protected void saveWeatherDataToFile(String s){

        String filename = "WeatherData";
        FileOutputStream outputStream;

        try {
            outputStream = context.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(s.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
