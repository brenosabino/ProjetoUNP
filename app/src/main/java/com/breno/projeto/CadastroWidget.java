package com.breno.projeto;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.breno.projeto.json.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class CadastroWidget extends AppWidgetProvider {

    private String TAG = MainActivity.class.getSimpleName();
    Locale meuLocal = new Locale( "pt", "BR" );

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        CharSequence widgetText = context.getString(R.string.appwidget_text);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cadastro_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.appwidget_time, timeString);

        //Create an Intent with the AppWidgetManager.ACTION_APPWIDGET_UPDATE action//
        Intent intentUpdate = new Intent(context, CadastroWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        //Update the current widget instance only, by creating an array that contains the widget’s unique ID//
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        //Wrap the intent as a PendingIntent, using PendingIntent.getBroadcast()//
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Send the pending intent in response to the user tapping the ‘Update’ TextView//
        views.setOnClickPendingIntent(R.id.appwidget_update, pendingUpdate);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public class GetPreco extends AsyncTask<String, Void, String> {

        private RemoteViews views;
        private int WidgetID;
        private AppWidgetManager WidgetManager;

        public GetPreco(RemoteViews views, int appWidgetID, AppWidgetManager appWidgetManager){
            this.views = views;
            this.WidgetID = appWidgetID;
            this.WidgetManager = appWidgetManager;
        }

        @Override
        protected String doInBackground(String... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "https://api.bitvalor.com/v1/ticker.json";
            String jsonStr = sh.makeServiceCall(url);
            String price = null;

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject ticker = jsonObj.getJSONObject("ticker_24h");
                    JSONObject total = ticker.getJSONObject("total");
                    price = total.get("last").toString();
                    Double numParsed = Double.parseDouble(price);
                    price = DecimalFormat.getCurrencyInstance(meuLocal).format(numParsed);
                    Log.e(TAG, "Teste: " + price);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return price;
        }

        @Override
        protected void onPostExecute(String price) {
            views.setTextViewText(R.id.appwidget_price, price);
            WidgetManager.updateAppWidget(WidgetID, views);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cadastro_widget);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            new GetPreco(views, appWidgetId, appWidgetManager).execute();
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Toast.makeText(context, "Widget foi atualizado! ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

