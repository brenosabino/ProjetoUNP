package com.breno.projeto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.breno.projeto.model.Operacoes;
import com.breno.projeto.sqlite.MySQLiteHelper;


public class CadastroActivity extends AppCompatActivity {

    EditText valor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Spinner dropdown = findViewById(R.id.spinner1);

        String[] items = new String[]{"NegocieCoins", "Mercado Bitcoin", "LocalBitcoins", "FoxBit", "flowBTC", "Braziliex", "BitcoinTrade", "BitcoinToYou"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
    }

    public void cadastrar (View view) {
        Spinner dropdown = findViewById(R.id.spinner1);
        String corretora = dropdown.getSelectedItem().toString();

        valor = (EditText)findViewById(R.id.valor);
        String valorString = valor.getText().toString();

        MySQLiteHelper db = new MySQLiteHelper(this);
        db.addOperacao(new Operacoes(corretora, valorString));

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
