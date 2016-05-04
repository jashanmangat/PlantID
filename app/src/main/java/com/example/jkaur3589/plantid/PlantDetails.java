package com.example.jkaur3589.plantid;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlantDetails extends AppCompatActivity {


    Firebase myFirebaseRef;
    String[] name = new String[100];
    EditText editText1;
    EditText editText2;
    EditText editText3;
    LocationManager myLocationManager;
    String PROVIDER = LocationManager.GPS_PROVIDER;
    String position;

    JSONArray jsonNameArr = new JSONArray();
    JSONArray jsonTypeArr = new JSONArray();
    JSONArray jsonLocationArr = new JSONArray();
    JSONArray jsonDetailsArr = new JSONArray();
    JSONArray jsonMsgArr = new JSONArray();

    JSONObject jsonObject;
    String value;
    String myPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = myLocationManager.getLastKnownLocation(PROVIDER);

        //position = location.getLatitude()+ ", " + location.getLongitude();
        position = showMyLocation(location);

        Intent intent = getIntent();
        value = intent.getStringExtra("name");

        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase("https://blinding-inferno-6828.firebaseio.com/");

        myFirebaseRef.child("plants").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String s = (String) snapshot.getValue();

                try {
                    JSONObject obj = new JSONObject(s);
                    jsonNameArr = obj.getJSONArray("name");
                    jsonTypeArr = obj.getJSONArray("type");
                    jsonLocationArr = obj.getJSONArray("location");
                    jsonDetailsArr = obj.getJSONArray("detail");
                    jsonMsgArr = obj.getJSONArray("msgs");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });



    }


    public void saveDetails(View v){
        editText1 =(EditText) findViewById(R.id.editText);
        editText2 =(EditText) findViewById(R.id.editText2);
        editText3 =(EditText) findViewById(R.id.editText3);

        jsonObject = new JSONObject();


        jsonNameArr.put(editText1.getText());
        jsonTypeArr.put(value);
        jsonLocationArr.put(position);
        jsonDetailsArr.put(editText2.getText());
        jsonMsgArr.put(editText3.getText());

        try{
            jsonObject.put("name", jsonNameArr);
            jsonObject.put("type", jsonTypeArr);
            jsonObject.put("location", jsonLocationArr);
            jsonObject.put("detail", jsonDetailsArr);
            jsonObject.put("msgs", jsonMsgArr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        myFirebaseRef.child("plants").setValue(jsonObject.toString());

        Toast.makeText(getApplicationContext(), "Data Saved.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(PlantDetails.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause(){
        // TODO Auto-generated method stub
        super.onPause();
        myLocationManager.removeUpdates(myLocationListener);
    }

    @Override
    protected void onResume(){
        // TODO Auto-generated method stub
        super.onResume();
        myLocationManager.requestLocationUpdates(
                PROVIDER,     //provider
                0,       //minTime
                0,       //minDistance
                myLocationListener); //LocationListener
    }

    private String showMyLocation(Location l){
        if(l == null){

        }
        else{
           position = l.getLatitude()+ ", " + l.getLongitude();
        }
        return position;
    }

    private LocationListener myLocationListener = new LocationListener(){
        @Override   public void onLocationChanged(Location location) {
           // showMyLocation(location);
            myPosition = location.toString();
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }};
}
