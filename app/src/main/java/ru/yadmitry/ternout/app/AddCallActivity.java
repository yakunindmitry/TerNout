package ru.yadmitry.ternout.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


public class AddCallActivity extends Activity {

    public final static String CALL_flEdit      = "ru.yadmitry.ternout.CALL_flEdit";
    public final static String CALL_lName       = "ru.yadmitry.ternout.CALL_lName";
    public final static String CALL_fName       = "ru.yadmitry.ternout.CALL_fName";
    public final static String CALL_mName       = "ru.yadmitry.ternout.CALL_mName";
    public final static String CALL_sex         = "ru.yadmitry.ternout.CALL_sex";
    public final static String CALL_temperature = "ru.yadmitry.ternout.CALL_temperature";
    public final static String CALL_StreetName  = "ru.yadmitry.ternout.CALL_StreetName";
    public final static String CALL_pressure    = "ru.yadmitry.ternout.CALL_pressure";
    public final static String CALL_pulse       = "ru.yadmitry.ternout.CALL_pulse";
    public final static String CALL_PeopleCode  = "ru.yadmitry.ternout.CALL_PeopleCode";
    public final static String CALL_StreetCode  = "ru.yadmitry.ternout.CALL_StreetCode";
    public final static String CALL_idCall      = "ru.yadmitry.ternout.CALL_idCall";

    static final private int COMPLAINT_ACTIVITY = 1;
    static final private int STREET_FIO_ACTIVITY = 2;
    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_IMAGE = "image";
    final String ATTRIBUTE_NAME_CODE = "code";
    private static final int CM_DELETE_ID = 1;

    int idComplaint, StreetCode, PeopleCode, flEdit, idCall, sex;
    String ComplaintName;
    DateDialogFragment frag;
    TextView tvDate;
    Calendar now;
    DB db;
    DataFromCursor dfc;
    ListView lvComplaint;
    SimpleAdapter sComplaintAdapter;
    ArrayList<Map<String, Object>> dataComplaint;
    Map<String, Object> m, el;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_call);
        // открываем подключение к БД
        db = new DB(this);
        db.open();

        now = Calendar.getInstance();
        tvDate = (TextView)findViewById(R.id.tvCurDate);
        tvDate.setText(String.valueOf(now.get(Calendar.DAY_OF_MONTH))+"-"+String.valueOf(now.get(Calendar.MONTH)+1)+"-"+String.valueOf(now.get(Calendar.YEAR)));

        Intent intent = getIntent();
        idCall = Integer.valueOf(intent.getStringExtra(CALL_idCall));
        sex = Integer.valueOf(intent.getStringExtra(CALL_sex));

        Cursor cursor = db.getComplaintData(idCall);
        dataComplaint = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_CODE, cursor.getInt(cursor.getColumnIndex("code")));
            m.put(ATTRIBUTE_NAME_TEXT, cursor.getString(cursor.getColumnIndex("name")));
            dataComplaint.add(m);
        }
        //dfc = new DataFromCursor();
        //dataComplaint = dfc.getComplaintData(db.getComplaintData(idCall));

        String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE }; // формируем столбцы сопоставления
        int[] to = { R.id.tvText, R.id.ivImg };
        sComplaintAdapter = new SimpleAdapter(this, dataComplaint, R.layout.item, from, to);
        lvComplaint = (ListView) findViewById(R.id.lvComplaints); // определяем список и присваиваем ему адаптер
        lvComplaint.setAdapter(sComplaintAdapter);

        // Заполнение полей
        TextView tvCurStreet = (TextView) findViewById(R.id.tvCurStreet);
        TextView tvCurFio = (TextView) findViewById(R.id.tvCurFio);
        TextView editTemperature = (TextView) findViewById(R.id.editTemperature);
        TextView editPressure = (TextView) findViewById(R.id.editPressure);
        TextView editPulse = (TextView) findViewById(R.id.editPulse);

        flEdit = intent.getIntExtra(CALL_flEdit, 0);
        if ( flEdit == 1 ){
            tvCurStreet.setText(intent.getStringExtra(CALL_StreetName));
            tvCurFio.setText(intent.getStringExtra(CALL_lName)+" "+intent.getStringExtra(CALL_fName)+" "+intent.getStringExtra(CALL_mName));
            editTemperature.setText(intent.getStringExtra(CALL_temperature));
            editPressure.setText(intent.getStringExtra(CALL_pressure));
            editPulse.setText(intent.getStringExtra(CALL_pulse));
            PeopleCode = Integer.valueOf(intent.getStringExtra(CALL_PeopleCode));
            StreetCode = Integer.valueOf(intent.getStringExtra(CALL_StreetCode));
            idCall = Integer.valueOf(intent.getStringExtra(CALL_idCall));
        }

        // адаптер spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, db.pulse);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinPuls);
        spinner.setAdapter(adapter);

        // добавляем контекстное меню к списку ListView
        registerForContextMenu(lvComplaint);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int ItemId = item.getItemId();

        // получаем из пункта контекстного меню данные по пункту списка
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (ItemId == CM_DELETE_ID) {
            dataComplaint.remove(info.position);
            sComplaintAdapter.notifyDataSetChanged();
        }
        return true;
    }

    void AddComplaint(int id) {
        ArrayList<String> list = new ArrayList<String>();
        for ( int i = 0; i < sComplaintAdapter.getCount(); i++ ) {
            list.clear();
            el = (Map<String, Object>) sComplaintAdapter.getItem(i);
            list.add(String.valueOf(id));
            list.add(el.get(ATTRIBUTE_NAME_CODE).toString());

            db.addRecFields("tb_complaints", list, db.fieldsTb_complaints, 0, "");
        }
    }

    // обработка нажатия кнопок
    public void onButtonClick(View v) {
        Intent intent;
        EditText field;
        ArrayList<String> list = new ArrayList<String>();
        switch (v.getId()) {
            case R.id.saveCall:
                tvDate = (TextView)findViewById(R.id.tvCurDate);
                list.add(tvDate.getText().toString());
                list.add(String.valueOf(PeopleCode));
                list.add("*");

                list.add(Integer.toString(sex));
                field = (EditText)findViewById(R.id.editTemperature);
                list.add(field.getText().toString());
                field = (EditText)findViewById(R.id.editPressure);
                list.add(field.getText().toString());
                field = (EditText)findViewById(R.id.editPulse);
                list.add(field.getText().toString());
                Spinner spinner = (Spinner) findViewById(R.id.spinPuls);
                list.add(spinner.getSelectedItem().toString());
                list.add("1");

                if ( flEdit == 0 ) {    // Добавление новай записи
                    db.addRecFields("Call", list, db.fieldsCall, 0, "");

                    int cur_id = db.last_insert_rowid("calls");
                    AddComplaint(cur_id);
                } else {    // Редактирование существующей записи
                    db.addRecFields("Call", list, db.fieldsCall, 1, "_id = '" + idCall + "'");
                    db.delRec("tb_complaints", idCall);
                    AddComplaint(idCall);
                }

                intent = new Intent(this, MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnCancel:
                intent = new Intent(this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.pickDate:
                showDialog();
                break;
            case R.id.btnFio:
                intent = new Intent(this, SelectStreetFIOActivity.class);
                startActivityForResult(intent, STREET_FIO_ACTIVITY);
                break;
            case R.id.btnComplaint:
                intent = new Intent(this, SelectComplaintActivity.class);
                startActivityForResult(intent, COMPLAINT_ACTIVITY);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        super.onActivityResult(requestCode, resultCode, intentData);

        if (requestCode == STREET_FIO_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                StreetCode = intentData.getIntExtra(SelectStreetFIOActivity.STREET_FIO_codeSTREET, 0);
                TextView tvCurStreet = (TextView) findViewById(R.id.tvCurStreet);
                tvCurStreet.setText(intentData.getStringExtra(SelectStreetFIOActivity.STREET_FIO_nameSTREET));

                PeopleCode = intentData.getIntExtra(SelectStreetFIOActivity.STREET_FIO_PeopleCode, 0);
                sex = intentData.getIntExtra(SelectStreetFIOActivity.STREET_FIO_sex, 0);
                TextView tvCurFio = (TextView) findViewById(R.id.tvCurFio);
                tvCurFio.setText(intentData.getStringExtra(SelectStreetFIOActivity.STREET_FIO_lName)+" "+
                        intentData.getStringExtra(SelectStreetFIOActivity.STREET_FIO_fName)+" "+
                        intentData.getStringExtra(SelectStreetFIOActivity.STREET_FIO_mName));
            }
        }

        if (requestCode == COMPLAINT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Integer ComplaintCount = intentData.getIntExtra(SelectComplaintActivity.COMPLAINT_COMPLAINT_Count, 0);
                for ( int i = 0; i < ComplaintCount; i++ ){
                    String s1 = SelectComplaintActivity.COMPLAINT_idComplaint + String.valueOf(i);
                    String s2 = SelectComplaintActivity.COMPLAINT_ComplaintName + String.valueOf(i);
                    idComplaint = Integer.valueOf(intentData.getStringExtra(s1));
                    ComplaintName = intentData.getStringExtra(s2);

                    int flag = 0;
                    for ( int j = 0; j < dataComplaint.size(); j++ ){
                        el = dataComplaint.get(j);
                        String sn = el.get(ATTRIBUTE_NAME_CODE).toString();
                        if( sn != null && sn == String.valueOf(idComplaint) ) {
                            flag = 1;
                        }
                    }
                    if ( flag == 0 ) {
                        m = new HashMap<String, Object>();
                        m.put(ATTRIBUTE_NAME_CODE, idComplaint);
                        m.put(ATTRIBUTE_NAME_TEXT, ComplaintName);
                        dataComplaint.add(m);
                    }
                }
                sComplaintAdapter.notifyDataSetChanged();
            }
            else {
                ; // стираем текст
            }
        }
    }

    public void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction(); //get the fragment
        frag = DateDialogFragment.newInstance(this, new DateDialogFragmentListener(){
            public void updateChangedDate(int year, int month, int day){
                tvDate.setText(String.valueOf(day)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year));
                now.set(year, month, day);
            }
        }, now);

        frag.show(ft, "DateDialogFragment");
    }

    public interface DateDialogFragmentListener{
        //this interface is a listener between the Date Dialog fragment and the activity to update the buttons date
        public void updateChangedDate(int year, int month, int day);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

}
