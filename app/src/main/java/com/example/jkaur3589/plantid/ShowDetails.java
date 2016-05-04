package com.example.jkaur3589.plantid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShowDetails extends AppCompatActivity {

    Firebase myFirebaseRef;
    String[] name = new String[100];
    TextView textView;
    JSONArray jsonNameArr;
    JSONArray jsonTypeArr;
    JSONArray jsonLocationArr;
    JSONArray jsonDetailsArr;
    JSONArray jsonMsgArr;

    JSONObject jsonObject;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        Intent intent = getIntent();
        final String value = intent.getStringExtra("name");

        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase("https://blinding-inferno-6828.firebaseio.com/");

        textView1 = (TextView) findViewById(R.id.textView9);
        textView2 = (TextView) findViewById(R.id.textView12);
        textView3 = (TextView) findViewById(R.id.textView13);
        textView4 = (TextView) findViewById(R.id.textView15);

        myFirebaseRef.child("plants").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                String s = (String) snapshot.getValue();
              //  textView = (TextView) findViewById(R.id.textView3);
              //  textView.setText(s);

                try {
                    JSONObject obj = new JSONObject(s);

                    jsonTypeArr = obj.getJSONArray("type");
                  /*  jsonNameArr = obj.getJSONArray("name");
                    jsonLocationArr = obj.getJSONArray("location");
                    jsonDetailsArr = obj.getJSONArray("detail");
                    jsonMsgArr = obj.getJSONArray("msgs");*/

                    for (int i = 0; i < jsonTypeArr.length(); i++) {
                        JSONObject json_data = jsonTypeArr.getJSONObject(i);
                        if(value == json_data.toString())
                        {
                            JSONObject json_name = jsonTypeArr.getJSONObject(i);
                            JSONObject json_details = jsonTypeArr.getJSONObject(i);
                            JSONObject json_location = jsonTypeArr.getJSONObject(i);
                            JSONObject json_msgs = jsonTypeArr.getJSONObject(i);
                            textView1.setText(json_name.toString());
                            textView2.setText(json_details.toString());
                            textView3.setText(json_location.toString());
                            textView4.setText(json_msgs.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });
    }
}
