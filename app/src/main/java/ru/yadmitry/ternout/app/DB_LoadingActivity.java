package ru.yadmitry.ternout.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.util.ArrayList;

public class DB_LoadingActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    //private final String LOG_TAG = "myLogs";
    private static final int CM_DELETE_ID = 1;
    ListView lvData;
    DB db;
    SimpleCursorAdapter scAdapter;

    /** Called when the activity is first created. */

    public void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db__loading);
        // открываем подключение к БД
        db = new DB(this);
        db.open();

        // адаптер spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, db.data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title"); // заголовок spinner
        spinner.setSelection(0);    // выделяем элемент spinner

        // устанавливаем обработчик нажатия spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String[] from = new String[] { "img", "name" };
                int[] to = new int[] { R.id.ivImg, R.id.tvText };

                switch (position) {
                    case 0: db.curTableName = "Street";
                        db.curFields = db.fieldsStreet;
                        db.curTypes = db.typesStreet;
                        db.curFileFieldCnt = 3;
                        from = new String[] { "img", "name" }; // формируем столбцы сопоставления
                        to = new int[] { R.id.ivImg, R.id.tvText };
                        break;
                    case 1: db.curTableName = "Address";
                        db.curFields = db.fieldsAddress;
                        db.curTypes = db.typesAddress;
                        db.curFileFieldCnt = 4;
                        from = new String[] { "img", "street_code", "house" }; // формируем столбцы сопоставления
                        to = new int[] { R.id.ivImg, R.id.tvText, R.id.tvText1 };
                        break;
                    case 2: db.curTableName = "People";
                        db.curFields = db.fieldsPeople;
                        db.curTypes = db.typesPeople;
                        db.curFileFieldCnt = 9;
                        from = new String[] { "img", "last_name", "first_name", "middle_name" }; // формируем столбцы сопоставления
                        to = new int[] { R.id.ivImg, R.id.tvText, R.id.tvText1, R.id.tvText2 };
                        break;
                    case 3: db.curTableName = "Call";
                        db.curFields = db.fieldsCall;
                        db.curTypes = db.typesCall;
                        db.curFileFieldCnt = 7;
                        from = new String[] { "img", "_id", "call_date", "temperature", "pressure", "pulse" }; // формируем столбцы сопоставления
                        to = new int[] { R.id.ivImg, R.id.tvText, R.id.tvText1, R.id.tvText2 , R.id.tvText3, R.id.tvText4 };
                        break;
                    case 4: db.curTableName = "Complaint_p";
                        db.curFields = db.fieldsComplaint_p;
                        db.curTypes = db.typesComplaint_p;
                        db.curFileFieldCnt = 3;
                        from = new String[] { "img", "name" }; // формируем столбцы сопоставления
                        to = new int[] { R.id.ivImg, R.id.tvText };
                        break;
                    case 5: db.curTableName = "Complaint";
                        db.curFields = db.fieldsComplaint;
                        db.curTypes = db.typesComplaint;
                        db.curFileFieldCnt = 4;
                        from = new String[] { "img", "name" }; // формируем столбцы сопоставления
                        to = new int[] { R.id.ivImg, R.id.tvText };
                        break;
                    case 6: db.curTableName = "tb_complaints";
                        db.curFields = db.fieldsTb_complaints;
                        db.curTypes = db.typesTb_complaints;
                        db.curFileFieldCnt = 4;
                        from = new String[] { "img","call_id","code_complaint" }; // формируем столбцы сопоставления
                        to = new int[] { R.id.ivImg, R.id.tvText, R.id.tvText1 };
                        break;
                }
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                Context cnt = parent.getContext();
                // создааем адаптер и настраиваем список ListView
                scAdapter = new SimpleCursorAdapter(cnt, R.layout.item, null, from, to, 0);
                lvData = (ListView) findViewById(R.id.lvData);
                lvData.setAdapter(scAdapter);
                getSupportLoaderManager().getLoader(0).forceLoad();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        db.curTableName = "Street";
        // формируем столбцы сопоставления
        String[] from = new String[] { "img", "name" };
        int[] to = new int[] { R.id.ivImg, R.id.tvText };

        // создааем адаптер и настраиваем список ListView
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        // добавляем контекстное меню к списку ListView
        registerForContextMenu(lvData);
        // создаем лоадер для чтения данных ListView
        getSupportLoaderManager().initLoader(0, null, this);
    }

    // обработка нажатия кнопок
    public void onButtonClick(View v) {
        //ContentValues cv = new ContentValues();
        ArrayList<String> list = new ArrayList<String>();

        switch (v.getId()) {
            case R.id.btnAddRecord: // добавляем запись
                list.add( String.valueOf(scAdapter.getCount() + 1));
                list.add("sometext " + (scAdapter.getCount() + 1));
                db.addRecFields("street", list, db.fieldsStreet, 0, "");
                // получаем новый курсор с данными
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;
            case R.id.btnLoad:
                list.clear();
                list = db.getListFromCsvFile(db.curTableName+".csv");
                db.InsertToTable(db.curTableName, list, db.curFields, db.curFileFieldCnt);
                // получаем новый курсор с данными
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;
            case R.id.btnDelTable:
                db.delTable(db.curTableName);
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;
            case R.id.btnClearTable:
                db.delAllRec(db.curTableName);
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;
            case R.id.btnInfo:
                TextView tvDbVersion = (TextView) findViewById(R.id.tvInfo);
                String ver = String.valueOf( db.showVersion());
                String sql =  db.getSQL(db.curTableName,db.curFields,db.curTypes);

                tvDbVersion.setText("DB version: " + ver+"  Table: "+db.curTableName+" >" +sql);
                break;
            case R.id.btnMainActivity:
                finish();
                break;

        }
    }

    //public void OnItemSelected(View v) {    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.delRec(db.curTableName, acmi.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllData(db.curTableName);
        /*
        try {
            TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
            return cursor;
        }

    }


}
