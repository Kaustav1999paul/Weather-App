package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Home extends AppCompatActivity {

    private TextView temperature, status, humidity, ws, cl;
    private String url;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    double latitude, longitude;
    private ImageView currLocation, mainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        temperature = findViewById(R.id.temperature);
        status = findViewById(R.id.status);
        mainImage = findViewById(R.id.mainImage);
        humidity = findViewById(R.id.humidity);
        ws = findViewById(R.id.windspeed);
        cl = findViewById(R.id.cloudy);

        currLocation = findViewById(R.id.currLocation);
        currLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            getLocation();
        }


        getData();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(Home.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(Home.this).removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void getData() {

        url ="https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&units=metric&exclude=hourly,daily&appid=24fadf0771f572e79650afaf3373566e";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject object = new JSONObject(response);
                    try{
                        JSONObject op = object.getJSONObject("current");
                        double temp = Double.parseDouble(op.getString("temp"));
                        int aa = (int) Math.round(temp);
                        String bb = String.valueOf(aa);
                        temperature.setText(bb);
                        humidity.setText(op.getString("humidity")+"%");
                        ws.setText(op.getString("wind_speed")+" km/h");
                        cl.setText(op.getString("clouds")+"%");

                        JSONArray jArray3 = op.getJSONArray("weather");
                        for(int i = 0; i < jArray3.length(); i++){
                            JSONObject object3 = jArray3.getJSONObject(i);

                            String state = object3.getString("main");

                            if (state.equals("Clear")){
                                status.setText(state);
                                mainImage.setBackgroundResource(R.drawable.sunny);
                            }else if (state.equals("Clouds")){
                                status.setText(state);
                                mainImage.setBackgroundResource(R.drawable.cloud);
                            }else if (state.equals("Atmosphere")){
                                status.setText(state);
                                mainImage.setBackgroundResource(R.drawable.atmosphere);
                            }else if (state.equals("Snow")){
                                status.setText(state);
                                mainImage.setBackgroundResource(R.drawable.rain);
                            }else if (state.equals("Rain")){
                                status.setText(state);
                                mainImage.setBackgroundResource(R.drawable.rain);
                            }else if (state.equals("Drizzle")){
                                status.setText(state);
                                mainImage.setBackgroundResource(R.drawable.atmosphere);
                            }else if (state.equals("Thunderstorm")){
                                status.setText(state);
                                mainImage.setBackgroundResource(R.drawable.flash);
                            }
                        }




                    }catch(JSONException e){
                        String x = e.getMessage();
                        Toast.makeText(Home.this, "Error0: "+x, Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException ex){
                    String a = ex.getMessage();
                    Toast.makeText(Home.this, "Error1: "+a, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errormessage = error.getMessage().toString();

                Toast.makeText(Home.this, "Error2: "+errormessage, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue req = Volley.newRequestQueue(this);
        req.add(request);
    }
}
