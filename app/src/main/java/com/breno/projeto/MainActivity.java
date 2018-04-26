package com.breno.projeto;

import java.util.List;

import com.breno.projeto.model.Operacoes;
import com.breno.projeto.sqlite.MySQLiteHelper;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    private static TextView internetStatus;

    private ListView lv;

    private BroadcastReceiver broadcastReceiverConnectionChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ChecaInternet.isNetworkAvailable(MainActivity.this)){
                changeTextStatus(false);
            }
            else{
                changeTextStatus(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internetStatus = (TextView) findViewById(R.id.internet_status);

        MySQLiteHelper db = new MySQLiteHelper(this);

        lv = (ListView) findViewById(R.id.list);

        List<Operacoes> list = db.getAllOperacoes();

        ArrayAdapter<Operacoes> adapter = new ArrayAdapter<Operacoes>(this,android.R.layout.simple_list_item_1,list);

        lv.setAdapter(adapter);


        /**
         * CRUD Operations
         * */
        // add Books
        //db.addOperacao(new Operacoes("Android Application Development Cookbook", "Wei Meng Lee"));
        //db.addOperacao(new Operacoes("Android Programming: The Big Nerd Ranch Guide", "Bill Phillips and Brian Hardy"));
        //db.addOperacao(new Operacoes("Learn Android App Development", "Wallace Jackson"));

        // get all books
        //List<Operacoes> list = db.getAllOperacoes();

        // delete one book
        //db.deleteOperacao(list.get(0));

        // get all books
        //db.getAllOperacoes();

    }

    public void mudarActivity(View view) {
        Intent intent = new Intent(this, CorretorasActivity.class);
        startActivity(intent);
    }

    public void cadastroActivity(View view) {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    public void deleteActivity(View view) {
        MySQLiteHelper db = new MySQLiteHelper(this);
        db.deleteAllOperacaoes();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Method to change the text status
    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            internetStatus.setText("Conectado a Internet.");
            internetStatus.setTextColor(Color.parseColor("#00ff00"));
        } else {
            internetStatus.setText("Desconectado da Internet.");
            internetStatus.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        //  remove broadcast receiver when activity stops
        unregisterReceiver(broadcastReceiverConnectionChanged);
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        //  register broadcast receiver after starting activity
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiverConnectionChanged, intentFilter);
    }

}
