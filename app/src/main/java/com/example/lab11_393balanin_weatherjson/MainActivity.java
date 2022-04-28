package com.example.lab11_393balanin_weatherjson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String key = "822e6efb4eb3412f95e65836220704";
    EditText et; //city editText
    EditText et_apikey;
    Intent i;
        //Balanin 393 lab 11
    ListView lstctl;
    ArrayList<WeatherCall> lst = new ArrayList<>();
    ArrayAdapter<WeatherCall> adp;
    int position= -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et =findViewById(R.id.et_City); //city editText
        et_apikey = findViewById(R.id.et_apikey);
        et_apikey.setText(key);
        //Creating DB object
        g.weather_DB = new DB(this, "weather.db",null,1);
        //List for listView
        lstctl = findViewById(R.id.list_calls);
        //adapter for listView
        adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lst);
        lstctl.setAdapter(adp);
        //open selected listView item in second Activity (result_activity)
        lstctl.setOnItemClickListener((parent, view, position, id) -> {
            WeatherCall n = adp.getItem(position);
            Intent j = new Intent(this, activity_historyList.class);
            j.putExtra("date", n.Date);
            j.putExtra("city",n.City);
            j.putExtra("information",n.Information);
            //launch activity to show weather call information
            startActivityForResult(i,1);
        });

    }

    public void onQuery_Click(View v)
    {//balanin 393 lab 11
        key =  et_apikey.getText().toString();
        String city = et.getText().toString();
        //Creating new Thread
        Thread t = new Thread(() -> {
            try {
                URL url = new URL("http://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + city + "&aqi=no");
                //Opening connection
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = con.getInputStream();
                byte[] buf = new byte[1024];
                String res = "";
                while (true) {//reading data
                    int len = is.read(buf, 0, buf.length);
                    if (len < 0) break;
                    res = res + new String(buf, 0, len);
                }
                con.disconnect(); //close connection
                Log.d("json", res);

                JSONObject doc = new JSONObject(res); //Pulling content from JSON file

                JSONObject curr = doc.getJSONObject("current");
                float temp = (float) curr.getDouble("temp_c");

                JSONObject loc = doc.getJSONObject("location");

                String info = "Country: " + loc.getString("country") + "\n";
                info += "Time: " + loc.getString("localtime") + "\n";
                String date = loc.getString("localtime"); //need this later
                info += "Temperature: " + temp + " C\n";

                info += "Feels like: " + curr.getString("feelslike_c") + " C\n";
                info += "Wind speed: " + curr.getString("wind_kph") + " kph\n";
                info += "Wind direction: " + curr.getString("wind_dir") + "\n";
                info += "Wind direction in degrees: " + curr.getString("wind_degree") + "\n";
                info += "Precipitation: " + String.valueOf(curr.getDouble("precip_mm")) + " mm\n";

                final String resulti = info; //Result string that will be written on activity

                JSONObject cond = curr.getJSONObject("condition");
                String icon = cond.getString("icon");

                byte[] bytes;
                //finding bitmap by url in DB, return byte[] of image if found, if not found - return null
                byte[] b = g.weather_DB.PictureExists("http:" + icon);
                Bitmap bmp;
                if (b != null) { //if bitmap was found
                    Log.i("DB_activity","Picture found in database, pulling it out");
                    bytes = b;
                }
                else { //if theres no image in database - download it from internet
                URL url1 = new URL("http:" + icon);
                HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();

                InputStream is1 = con1.getInputStream();
                bmp = BitmapFactory.decodeStream(is1);
                con1.disconnect();

                //Converting bitmap to byte[] to put it into DB
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bytes = stream.toByteArray();

                g.weather_DB.AddPicture("http:" + icon, bmp);
                    Log.i("DB_activity","Picture not found in database, downloading from internet");
                }
                runOnUiThread(() -> {
                    //putting info into intent
                    i = new Intent(this,result_activity.class);
                   i.putExtra("cityname",city);
                   i.putExtra("info",resulti);
                   i.putExtra("picture_bytes", bytes); //sending byte[]
                   g.weather_DB.AddRequest(date,city,resulti); //put into DB
                    //launching activity to show result
                   startActivityForResult(i,555);
                });
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        t.start();
    }

    public void LoadHistory(View v) {
        lst.clear();
        List<WeatherCall> calls = g.weather_DB.GetWeatherCalls();
        WeatherCall[] array = new WeatherCall[calls.size()];
        calls.toArray(array); // fill the array
        for (int i = 0; i< array.length; i++) {
            lst.add(array[i]);
        }
        adp.notifyDataSetChanged();
//balanin 393 lab 11
    }

}