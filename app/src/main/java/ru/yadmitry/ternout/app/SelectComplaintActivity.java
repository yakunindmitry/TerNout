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
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DYakunin on 03.03.2015.
 */
public class SelectComplaintActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String COMPLAINT_idComplaint = "ru.yadmitry.ternout.COMPLAINT_idComplaint";
    public final static String COMPLAINT_ComplaintName = "ru.yadmitry.ternout.COMPLAINT_ComplaintName";
    public final static String COMPLAINT_COMPLAINT_Count = "ru.yadmitry.ternout.COMPLAINT_Count";
    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_IMAGE = "image";
    final String ATTRIBUTE_NAME_CODE = "code";

    String ComplaintName, ComplaintName_p;
    int idComplaint_p, ComplaintCode_p, idComplaint, ComplaintCode;

    ListView lvData,lvParent, lvChild;
    DB db;
    SimpleCursorAdapter scAdapter;
    SimpleAdapter sAdapterSelected, sAdapterChild;
    ArrayList<Map<String, Object>> dataSelected, dataParent, dataChild;
    Map<String, Object> m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_complaint_p);

        db = new DB(this);
        db.open();
        db.curTableName = "Complaint_p";
        db.curFields = db.fieldsComplaint_p;
        db.curTypes = db.typesComplaint_p;
        db.curFileFieldCnt = 3;

        String[] from = new String[]{"img", "name"}; // формируем столбцы сопоставления
        int[] to = new int[]{R.id.ivImg, R.id.tvText};
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0); // scAdapter адаптер, настраиваем список ListView из курсора
        lvData = (ListView) findViewById(R.id.lvComplaint);
        lvData.setAdapter(scAdapter);

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = scAdapter.getCursor();
                cursor.moveToPosition(position);
                idComplaint_p = cursor.getInt(cursor.getColumnIndex("_id"));
                ComplaintCode_p = cursor.getInt(cursor.getColumnIndex("code"));
                ComplaintName_p = cursor.getString(cursor.getColumnIndex("name"));

                Cursor cursorChild = db.getFieldData("Complaint", "code_p", Integer.toString(ComplaintCode_p));
                dataChild.clear();
                while (cursorChild.moveToNext()) {
                    idComplaint = cursorChild.getInt(cursorChild.getColumnIndex("_id"));
                    ComplaintCode = cursorChild.getInt(cursorChild.getColumnIndex("code"));
                    ComplaintName = cursorChild.getString(cursorChild.getColumnIndex("name"));
                    m = new HashMap<String, Object>();
                    m.put(ATTRIBUTE_NAME_CODE, idComplaint);
                    m.put(ATTRIBUTE_NAME_TEXT, ComplaintName);
                    dataChild.add(m);
                }
                sAdapterChild.notifyDataSetChanged();
            }
        });
        getSupportLoaderManager().restartLoader(0, null, this);

        dataChild = new ArrayList<Map<String, Object>>();
        String[] from__ = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE }; // формируем столбцы сопоставления
        int[] to__ = { R.id.tvText, R.id.ivImg };
        sAdapterChild = new SimpleAdapter(this, dataChild, R.layout.item, from__, to__);
        lvChild = (ListView) findViewById(R.id.lvComplaintChild); // определяем список и присваиваем ему адаптер
        lvChild.setAdapter(sAdapterChild);
        lvChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m = (Map<String, Object>) sAdapterChild.getItem(position);
                dataSelected.add(m);
                sAdapterSelected.notifyDataSetChanged();
            }
        });

        dataSelected = new ArrayList<Map<String, Object>>();
        String[] from_ = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE }; // формируем столбцы сопоставления
        int[] to_ = { R.id.tvText, R.id.ivImg };
        sAdapterSelected = new SimpleAdapter(this, dataSelected, R.layout.item_bold, from_, to_);
        lvParent = (ListView) findViewById(R.id.lvComplaints); // определяем список и присваиваем ему адаптер
        lvParent.setAdapter(sAdapterSelected);
    }

    // обработка нажатия кнопок
    public void onButtonClick(View v) {
        Intent intent;
        EditText field;
        Button btnText;
        switch (v.getId()) {
            case R.id.btnCancel:
                intent = new Intent(this, AddCallActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.btnDoneSelComplaint:
                intent = new Intent(this, AddCallActivity.class);
                intent.putExtra(COMPLAINT_COMPLAINT_Count, dataSelected.size());

                for ( int i = 0; i < dataSelected.size(); i++ ){
                    m = dataSelected.get(i);
                    intent.putExtra(COMPLAINT_idComplaint + String.valueOf(i), m.get(ATTRIBUTE_NAME_CODE).toString());
                    intent.putExtra(COMPLAINT_ComplaintName + String.valueOf(i), m.get(ATTRIBUTE_NAME_TEXT).toString());
                }

                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db, ComplaintCode_p);
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
        int ComplaintCode_p;

        public MyCursorLoader(Context context, DB db, int ComplaintCode_p) {
            super(context);
            this.db = db;
            this.ComplaintCode_p = ComplaintCode_p;
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
