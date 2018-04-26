package com.breno.projeto.sqlite;

import java.util.LinkedList;
import java.util.List;

import com.breno.projeto.model.Operacoes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "OperacoesDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_OPERACOES_TABLE = "CREATE TABLE operacoes ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "corretora TEXT, "+
                "compra TEXT )";

        // create books table
        db.execSQL(CREATE_OPERACOES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS operacoes");

        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */

    // Books table name
    private static final String TABLE_OPERACOES = "operacoes";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CORRETORA = "corretora";
    private static final String KEY_COMPRA = "compra";

    private static final String[] COLUMNS = {KEY_ID,KEY_CORRETORA,KEY_COMPRA};

    public void addOperacao(Operacoes operacoes){
        //for logging
        Log.d("addOperacao", operacoes.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CORRETORA, operacoes.getCorretora()); // get title
        values.put(KEY_COMPRA, operacoes.getCompra()); // get author

        // 3. insert
        db.insert(TABLE_OPERACOES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Operacoes getOperacao(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_OPERACOES, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        Operacoes operacao = new Operacoes();
        operacao.setId(Integer.parseInt(cursor.getString(0)));
        operacao.setCorretora(cursor.getString(1));
        operacao.setCompra(cursor.getString(2));

        //log
        Log.d("getOperacao("+id+")", operacao.toString());

        // 5. return book
        return operacao;
    }

    public List<Operacoes> getAllOperacoes() {
        List<Operacoes> operacoes = new LinkedList<Operacoes>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_OPERACOES;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Operacoes operacao = null;
        if (cursor.moveToFirst()) {
            do {
                operacao = new Operacoes();
                operacao.setId(Integer.parseInt(cursor.getString(0)));
                operacao.setCorretora(cursor.getString(1));
                operacao.setCompra(cursor.getString(2));

                // Add book to books
                operacoes.add(operacao);
            } while (cursor.moveToNext());
        }

        Log.d("getAllOperacoes()", operacoes.toString());

        // return books
        return operacoes;
    }

    public int updateOperacao(Operacoes operacao) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("corretora", operacao.getCorretora()); // get title
        values.put("compra", operacao.getCompra()); // get author

        // 3. updating row
        int i = db.update(TABLE_OPERACOES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(operacao.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteOperacao(Operacoes operacao) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_OPERACOES, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(operacao.getId()) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteOperacao", operacao.toString());

    }

    public void deleteAllOperacaoes() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS operacoes");

        // create fresh books table
        this.onCreate(db);

        db.close();

    }

}
