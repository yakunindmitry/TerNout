package ru.yadmitry.ternout.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.*;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SelectStreetFIOActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String STREET_FIO_idSTREET   = "ru.yadmitry.ternout.STREET_FIO_idSTREET";
    public final static String STREET_FIO_codeSTREET = "ru.yadmitry.ternout.STREET_FIO_codeSTREET";
    public final static String STREET_FIO_nameSTREET = "ru.yadmitry.ternout.STREET_FIO_nameSTREET";
    public final static String STREET_FIO_fName      = "ru.yadmitry.ternout.STREET_FIO_fName";
    public final static String STREET_FIO_lName      = "ru.yadmitry.ternout.STREET_FIO_lName";
    public final static String STREET_FIO_mName      = "ru.yadmitry.ternout.STREET_FIO_mName";
    public final static String STREET_FIO_PeopleCode = "ru.yadmitry.ternout.STREET_FIO_idPeople";
    public final static String STREET_FIO_sex        = "ru.yadmitry.ternout.sex";

    ListView lvData,lvParent, lvChild;
    DB db;
    SimpleCursorAdapter scAdapter;
    String StreetName, fName, lName, mName;
    int idStreet, StreetCode, idPeople, PeopleCode, sex;
    SimpleAdapter sAdapterChild, sAdapterSelected;
    ArrayList<Map<String, Object>> dataSelected, dataChild;
    Map<String, Object> m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_complaint_p);

        db = new DB(this);
        db.open();

        db.curTableName = "Street";
        db.curFields = db.fieldsStreet;
        db.curTypes = db.typesStreet;
        db.curFileFieldCnt = 3;
        String[] from = new String[] { "img", "name" }; // формируем столбцы сопоставления
        int[] to = new int[] { R.id.ivImg, R.id.tvText };

        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0); // создааем адаптер и настраиваем список ListView
        lvData = (ListView) findViewById(R.id.lvComplaint);
        lvData.setAdapter(scAdapter);

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Cursor cursor = scAdapter.getCursor();
                cursor.moveToPosition(position);
                idStreet = cursor.getInt(cursor.getColumnIndex("_id"));
                StreetCode = cursor.getInt(cursor.getColumnIndex("code"));
                StreetName = cursor.getString(cursor.getColumnIndex("name"));
                Cursor cursorChild = db.getPeopleData(StreetCode, "");

                dataChild.clear();
                while (cursorChild.moveToNext()) {
                    idPeople = cursorChild.getInt(cursorChild.getColumnIndex("_id"));
                    PeopleCode = cursorChild.getInt(cursorChild.getColumnIndex("code"));
                    lName = cursorChild.getString(cursorChild.getColumnIndex("last_name"));
                    fName = cursorChild.getString(cursorChild.getColumnIndex("first_name"));
                    mName = cursorChild.getString(cursorChild.getColumnIndex("middle_name"));
                    sex = cursorChild.getInt(cursorChild.getColumnIndex("sex"));
                    m = new HashMap<String, Object>();
                    m.put("code", PeopleCode);
                    m.put("lName", lName);
                    m.put("fName", fName);
                    m.put("mName", mName);
                    m.put("sex", sex);
                    if (sex == 1) m.put("img", R.drawable.women24);
                    else m.put("img", R.drawable.men24);
                    dataChild.add(m);
                }
                sAdapterChild.notifyDataSetChanged();
            }
        });
        getSupportLoaderManager().restartLoader(0, null, this);

        dataChild  = new ArrayList<Map<String, Object>>();
        String[] from_ = {"img","lName", "fName",  "mName"}; // формируем столбцы сопоставления
        int[] to_ = { R.id.ivImg, R.id.tvText, R.id.tvText1, R.id.tvText2 };

        sAdapterChild = new SimpleAdapter(this, dataChild, R.layout.item, from_, to_);
        lvParent = (ListView) findViewById(R.id.lvComplaintChild); // определяем список и присваиваем ему адаптер
        lvParent.setAdapter(sAdapterChild);
        lvParent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m = (Map<String, Object>) sAdapterChild.getItem(position);
                lName = m.get("lName").toString();
                fName = m.get("fName").toString();
                mName = m.get("mName").toString();
                PeopleCode = Integer.valueOf(m.get("code").toString());
                sex = Integer.valueOf(m.get("sex").toString());
                dataSelected.clear();
                dataSelected.add(m);
                sAdapterSelected.notifyDataSetChanged();
            }
        });

        dataSelected = new ArrayList<Map<String, Object>>();
        String[] from__ = {"img","lName", "fName",  "mName" }; // формируем столбцы сопоставления
        int[] to__ = { R.id.ivImg, R.id.tvText, R.id.tvText1, R.id.tvText2 };

        // создаем адаптер
        sAdapterSelected = new SimpleAdapter(this, dataSelected, R.layout.item_bold, from__, to__);
        lvChild = (ListView) findViewById(R.id.lvComplaints); // определяем список и присваиваем ему адаптер
        lvChild.setAdapter(sAdapterSelected);

    }

    // обработка нажатия кнопок
    public void onButtonClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnDoneSelComplaint:
                intent = new Intent(this, AddCallActivity.class);
                intent.putExtra(STREET_FIO_nameSTREET, StreetName);
                intent.putExtra(STREET_FIO_idSTREET, idStreet);
                intent.putExtra(STREET_FIO_codeSTREET, StreetCode);
                intent.putExtra(STREET_FIO_fName, fName);
                intent.putExtra(STREET_FIO_lName, lName);
                intent.putExtra(STREET_FIO_mName, mName);
                intent.putExtra(STREET_FIO_PeopleCode, PeopleCode);
                intent.putExtra(STREET_FIO_sex, sex);

                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db, StreetCode);
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
        int StreetCod;

        public MyCursorLoader(Context context, DB db, int StreetCode) {
            super(context);
            this.db = db;
            this.StreetCod = StreetCode;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllData(db.curTableName);
            return cursor;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

}
