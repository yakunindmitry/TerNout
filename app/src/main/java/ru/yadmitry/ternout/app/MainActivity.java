package ru.yadmitry.ternout.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    static final private int CALL_ACTIVITY = 0;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    ListView lvData;
    DB db;
    SimpleCursorAdapter scAdapter;
    //XMLPreferencesLib XMLpref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // открываем подключение к БД
        db = new DB(this);
        db.open();
        db.curTableName = "Call";
        db.curFields = db.fieldsCall;
        db.curTypes = db.typesCall;
        db.curFileFieldCnt = 9;

        // формируем столбцы сопоставления
        String[] from = new String[] { "img", "call_date", "last_name", "first_name", "middle_name", "temperature", "pressure", "pulse", "state" };
        int[] to = new int[] { R.id.ivImg, R.id.tvText, R.id.tvText1, R.id.tvText2, R.id.tvText3, R.id.tvText4, R.id.tvText5, R.id.tvText6, R.id.tvText7 };
        scAdapter = new SimpleCursorAdapter(this, R.layout.item_call, null, from, to, 0); // создааем адаптер и настраиваем список ListView
        lvData = (ListView) findViewById(R.id.lvCall);
        lvData.setAdapter(scAdapter);

        // добавляем контекстное меню к списку ListView
        registerForContextMenu(lvData);
        // создаем лоадер для чтения данных ListView
        getSupportLoaderManager().initLoader(0, null, this);
    }


    // обработка нажатия кнопок
    public void onButtonClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnDB_Loading:
                intent = new Intent(this, DB_LoadingActivity.class);
                startActivityForResult(intent, CALL_ACTIVITY);
                break;
            case R.id.btnAddCall:
                intent = new Intent(this, AddCallActivity.class);
                intent.putExtra(AddCallActivity.CALL_flEdit, 0);
                startActivityForResult(intent, CALL_ACTIVITY);
                break;
            default:
                break;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int ItemId = item.getItemId();

        // получаем из пункта контекстного меню данные по пункту списка
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //String sDate =((TextView) ((LinearLayout) info.targetView).getChildAt(0)).getText().toString();

        if ( ItemId == CM_DELETE_ID) {
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.delRec(db.curTableName, info.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        if (item.getItemId() == CM_EDIT_ID) {
            Cursor c = db.getCallData(Long.toString(info.id)); // sDate, sFName, sLName, sMName
            if ( c.getCount() == 1 ) {
                c.moveToFirst();
                //              0            1         2           3     4
                //SELECT c.call_date,c.temperature,c.pressure,c.pulse,c.state,
                //      5            6           7          8       9
                //p.last_name,p.first_name,p.middle_name,c.sex,st.name,
                //   10    11     12      13    14    15
                //p._id,st._id,p.code,st.code,c.img,c._id
                Intent intent = new Intent(this, AddCallActivity.class);
                intent.putExtra(AddCallActivity.CALL_lName, c.getString(5));
                intent.putExtra(AddCallActivity.CALL_fName, c.getString(6));
                intent.putExtra(AddCallActivity.CALL_mName, c.getString(7));
                intent.putExtra(AddCallActivity.CALL_sex, c.getString(8));
                intent.putExtra(AddCallActivity.CALL_temperature, c.getString(1));
                intent.putExtra(AddCallActivity.CALL_pressure, c.getString(2));
                intent.putExtra(AddCallActivity.CALL_pulse, c.getString(3));
                intent.putExtra(AddCallActivity.CALL_StreetName, c.getString(9));
                intent.putExtra(AddCallActivity.CALL_PeopleCode, c.getString(12));
                intent.putExtra(AddCallActivity.CALL_StreetCode, c.getString(13));
                intent.putExtra(AddCallActivity.CALL_idCall, c.getString(15));
                intent.putExtra(AddCallActivity.CALL_flEdit, 1);

                startActivityForResult(intent, CALL_ACTIVITY);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        super.onActivityResult(requestCode, resultCode, intentData);

        if (requestCode == CALL_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Cursor c = db.getCallData(null);
                scAdapter.swapCursor(c);
            }
        }
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
            Cursor cursor = db.getCallData(null);
            return cursor;
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }
}
