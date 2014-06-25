package com.schibsted;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Date;

public class LightActivity extends Activity {
    TextView textLIGHT_available, textLIGHT_reading, lightStatus;
    String locationName = "default";
    long lastUpdated = 0;
    int dim = 0;
    int threshold = 30;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        View view = this.findViewById(android.R.id.content);
        Button button = (Button) view.findViewById(R.id.location_button);
        final EditText locationText = (EditText) view.findViewById(R.id.location_text);

        locationText.setText(locationName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationName = locationText.getText().toString();
            }
        });

        Button thresholdButton = (Button) view.findViewById(R.id.threshold_button);
        final EditText thresholdText = (EditText) view.findViewById(R.id.threshold_text);
        thresholdText.setText(String.valueOf(threshold));

        thresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threshold = Integer.parseInt(thresholdText.getText().toString());
            }
        });




        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textLIGHT_available
                = (TextView) findViewById(R.id.LIGHT_available);
        textLIGHT_reading
                = (TextView) findViewById(R.id.LIGHT_reading);
        lightStatus
                = (TextView) findViewById(R.id.lightStatus);

        SensorManager mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor sensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor != null) {
            textLIGHT_available.setText("Sensor.TYPE_LIGHT Available");
            mySensorManager.registerListener(
                    listener,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            textLIGHT_available.setText("Sensor.TYPE_LIGHT NOT Available");
        }
    }

    private final SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

                textLIGHT_reading.setText("LIGHT: " + event.values[0]);
                dim = (int) event.values[0];

                lightStatus.setText("Status: " + (effectiveDim(dim).equals("1") ? "on" : "off"));

                long now = new Date().getTime();
                if(now - lastUpdated > 1000) {
                    new MyAsyncTask().execute(locationName, effectiveDim(dim));
                    lastUpdated = now;
                }
            }
        }

        private String effectiveDim(int dim) {
            if(dim > threshold) return "1";
            return "0";
        }

    };

    public void postData(String locationName, String dim) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://gotstp.herokuapp.com/places/" + locationName + "/" + dim);

        try {
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(Double result){
        }

        protected void onProgressUpdate(Integer... progress){
        }
    }
}