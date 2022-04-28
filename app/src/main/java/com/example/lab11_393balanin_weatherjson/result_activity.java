package com.example.lab11_393balanin_weatherjson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class result_activity extends AppCompatActivity {

    Intent i;
    TextView tv_city;
    TextView tv_information;
    ImageView img;
//balanin 393 lab 11
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tv_city = findViewById(R.id.tv_city);
        tv_information = findViewById(R.id.tv_text);
        img = findViewById(R.id.imageview_pic);

        i = getIntent();
        String cityname = i.getStringExtra("cityname");
        String info = i.getStringExtra("info");

        tv_city.setText("City: "+ cityname);
        tv_information.setText(info);

        byte[] bytes = i.getByteArrayExtra("picture_bytes");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        img.setImageBitmap(bmp);
    }

    public void onButtonBack_Click(View v)
    {
        setResult(555);
        finish();
    }
}