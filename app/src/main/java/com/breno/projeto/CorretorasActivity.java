package com.breno.projeto;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.breno.projeto.json.HttpHandler;

public class CorretorasActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    Locale meuLocal = new Locale( "pt", "BR" );


    // URL to get contacts JSON
    private static String url = "https://api.bitvalor.com/v1/ticker.json";

    ArrayList<HashMap<String, String>> corretorasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corretoras);

        corretorasList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetCorretoras().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetCorretoras extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CorretorasActivity.this);
            pDialog.setMessage("Por Favor Aguarde...");
            pDialog.setCancelable(false);
            pDialog.show();

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

                    // Getting JSON Array node
                    JSONObject ticker = jsonObj.getJSONObject("ticker_24h");
                    JSONObject exchanges = ticker.getJSONObject("exchanges");

                    // looping through All Contacts
                    for (Iterator i = exchanges.keys(); i.hasNext(); ) {
                        String keys = (String) i.next();
                        Log.v(TAG, "Exchange name is " + keys);
                        JSONObject temp = (JSONObject) exchanges.get(keys);
                        String price=temp.get("last").toString();
                        Double numParsed = Double.parseDouble(price);
                        price = DecimalFormat.getCurrencyInstance(meuLocal).format(numParsed);
                        // tmp hash map for single contact
                        HashMap<String, String> exchange = new HashMap<>();

                        keys = keys.replace("NEG", "NegocieCoins");
                        keys = keys.replace("MBT", "Mercado Bitcoin");
                        keys = keys.replace("LOC", "LocalBitcoins");
                        keys = keys.replace("FOX", "FoxBit");
                        keys = keys.replace("FLW", "flowBTC");
                        keys = keys.replace("BZX", "Braziliex");
                        keys = keys.replace("BTD", "BitcoinTrade");
                        keys = keys.replace("B2U", "BitcoinToYou");

                        // adding each child node to HashMap key => value
                        exchange.put("name", keys);
                        exchange.put("price", price);

                        // adding contact to contact list
                        corretorasList.add(exchange);
                    }
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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    CorretorasActivity.this, corretorasList,
                    R.layout.list_item, new String[]{"name", "price"
                    }, new int[]{R.id.corretora,
                    R.id.preco});

            lv.setAdapter(adapter);
        }

    }
}
