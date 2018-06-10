package com.breno.projeto;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.breno.projeto.json.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class PrecoActivity extends AppCompatActivity implements AccelerometerListener {

    private static TextView preco;
    private static TextView data;
    private String price;

    private String TAG = MainActivity.class.getSimpleName();

    Locale meuLocal = new Locale( "en", "US" );


    // URL to get contacts JSON
    private static String url = "https://api.binance.com/api/v1/ticker/price?symbol=BTCUSDT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preco);
        preco = (TextView) findViewById(R.id.preco);
        data = (TextView) findViewById(R.id.data);
        new GetPrice().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake(float force) {
        Toast.makeText(this, "Valores atualizados!", Toast.LENGTH_SHORT).show();
        new GetPrice().execute();
    }

    @Override
    public void onStop() {
        super.onStop();

//Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

//Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onStop Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onDestroy Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private class GetPrice extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    price=jsonObj.get("price").toString();
                    Double numParsed = Double.parseDouble(price);
                    price = DecimalFormat.getCurrencyInstance(meuLocal).format(numParsed);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /**
             * Updating parsed JSON data into ListView
             * */
            preco.setText(price);

            long date = System.currentTimeMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String dateString = sdf.format(date);
            data.setText(dateString);
        }

    }
}
