package ru.yadmitry.ternout.app;

/**
 * Created by DYakunin on 28.10.2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;

public class DB {

    private static final int DB_VERSION = 38;
    private static final String DB_NAME = "mydb";
    public final String LOG_TAG = "myLogs";
    private final String DIR_SD = "MyFiles";

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private final Context mCtx;

    public DB(Context ctx) { mCtx = ctx;  }

    public String[] data = {"Street", "Address", "People","Call","Complaint_p","Complaint","tb_complaints"};
    public String[] pulse = {"Ритмичный","Одинаковый","Удовлетворительный","Напряженный","СлабогоНаполнения","Нитевидный","Аритмичный"};
    public String[] fieldsTb_complaints = {"call_id","code_complaint"};
    public String[] typesTb_complaints = {"integer", "integer"};
    public String[] fieldsComplaint = {"code","code_p", "name"};
    public String[] typesComplaint = {"integer", "integer", "string"};
    public String[] fieldsComplaint_p = {"code", "name"};
    public String[] typesComplaint_p = {"integer", "string"};
    public String[] fieldsStreet = {"code", "name"};
    public String[] typesStreet = {"integer", "string"};
    public String[] fieldsAddress = {"code", "street_code", "house"};
    public String[] typesAddress = {"integer", "integer", "string"};
    public String[] fieldsPeople = {"code", "last_name", "first_name", "middle_name", "birthday", "flat", "sex","code_street"};
    public String[] typesPeople = {"integer", "string", "string", "string", "string", "string", "integer","integer"};
    public String[] fieldsCall = {"call_date", "code_name", "call_type", "sex", "temperature","pressure","pulse","state"};
    public String[] typesCall = {"datetime", "integer","string","integer","real","string","integer","text"};
    public String curTableName;
    public String curFields[];
    public String curTypes[];
    public int curFileFieldCnt;

    //*************************************************************************************
    // открыть подключение
    //*************************************************************************************
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }   // открыть подключение

    //*************************************************************************************
    // закрыть подключение
    //*************************************************************************************
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }   // закрыть подключение

    //*************************************************************************************
    // показать веосию
    //*************************************************************************************
    public int showVersion() {
        return mDB.getVersion();
    }   // показать веосию

    //*************************************************************************************
    // получить все данные из таблицы DB_TABLE
    //*************************************************************************************
    public Cursor getAllData(String TableName) {
        return mDB.query(TableName, null, null, null, null, null, null);
    }   // получить все данные из таблицы DB_TABLE

    //*************************************************************************************
    // получить все данные из таблицы 'call' с именами и улицами
    //              0            1         2           3     4
    //SELECT c.call_date,c.temperature,c.pressure,c.pulse,c.state,
    //      5            6           7          8       9
    //p.last_name,p.first_name,p.middle_name,c.sex,st.name,
    //   10    11     12      13    14   15
    //p._id,st._id,p.code,st.code,c.img,c._id
    //FROM `call` c, `people` p, `address` a, `street` st
    //WHERE p.code = c.code_name and a.code = p.code_street and st.code = a.street_code
    //*************************************************************************************
    public Cursor getCallData( String s_id) {
        String selectQuery =
                "SELECT c.call_date,c.temperature,c.pressure,c.pulse,c.state," +
                        "p.last_name,p.first_name,p.middle_name,c.sex,st.name," +
                        "p._id,st._id,p.code,st.code,c.img,c._id "+
                "FROM `call` c, `people` p, `address` a, `street` st "+
                "WHERE p.code = c.code_name and a.code = p.code_street and st.code = a.street_code";
        if ( s_id != null ){
            selectQuery = selectQuery + " and c._id = '" + s_id + "'";
        }
        Cursor cursor;
        try {
            cursor = mDB.rawQuery(selectQuery, null);
            return cursor;
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Catch a SQLiteException when select: ", e);
            e.printStackTrace();
        }
        return null;
    }   // получить все данные из таблицы 'call' с именами и улицами

    //*************************************************************************************
    // получить все данные из таблицы 'people' по улицам и подстроке
    //*************************************************************************************
    public Cursor getPeopleData(int code_street, String s_select) {
        String selectQuery =
                "SELECT p._id, p.code, p.sex, p.last_name, p.first_name, p.middle_name, p.img " +
                "FROM people p, address a, street s " +
                "WHERE p.code_street = a.code and a.street_code = s.code";
        if ( code_street != 0 ){
            selectQuery = selectQuery + " and s.code = " + code_street;
        }
        if ( s_select != "" ){
            selectQuery = selectQuery + " and p.last_name LIKE '" + s_select+"%'";
        }

        selectQuery = selectQuery + " ORDER BY p.last_name";

        Cursor cursor;
        //Log.d(LOG_TAG,selectQuery);
        try {
            cursor = mDB.rawQuery(selectQuery, null);
            return cursor;
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Catch a SQLiteException when select: ", e);
            e.printStackTrace();
        }
        return null;
    }   // получить все данные из таблицы 'people' по улицам и подстроке

    //*************************************************************************************
    // получить все данные из таблицы 'people' по улицам и подстроке
    //*************************************************************************************
    public Cursor getComplaintData(int code_complaint) {
        String selectQuery =
                "SELECT c.code, c.name, c.img " +
                "FROM tb_complaints tc, complaint c " +
                "WHERE c._id = tc.code_complaint ";
        if ( code_complaint != 0 ){
            selectQuery = selectQuery + " AND tc.call_id = " + code_complaint;
        }

        Cursor cursor;
        //Log.d(LOG_TAG,selectQuery);
        try {
            cursor = mDB.rawQuery(selectQuery, null);
            return cursor;
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Catch a SQLiteException when select: ", e);
            e.printStackTrace();
        }
        return null;
    }   // получить все данные из таблицы 'people' по улицам и подстроке

    //*************************************************************************************
    // получить все данные из таблицы 'TableName' по полю и значению
    //*************************************************************************************
    public Cursor getFieldData(String TableName, String sField, String argsSQL) {
        return mDB.query(TableName, null, sField + " = ?", new String[] { argsSQL }, null, null, null);
    }   // получить все данные из таблицы 'TableName' по полю и значению


    //*************************************************************************************
    // получить все данные из таблицы 'TableName' по полю и похожему значению
    //*************************************************************************************
    public Cursor getSelData(String TableName, String sField, String argsSQL) {
        return mDB.query(TableName, null, sField + " LIKE ?", new String[] { argsSQL+ "%" }, null, null, null);
    }   // получить все данные из таблицы 'TableName' по полю и похожему значению

    //*************************************************************************************
    // получить id последней вставки
    //*************************************************************************************
    public int last_insert_rowid(String TableName) {
        Cursor c = mDB.rawQuery("SELECT last_insert_rowid()", null);
        //Cursor c = mDB.rawQuery("SELECT max(_id) FROM "+TableName, null);
        c.moveToFirst();
        int id = c.getInt(0);
        return id;
    }  // получить id последней вставки


    //*************************************************************************************
    // добавить запись в DB_TABLE несколько полей
    //*************************************************************************************
    public void addRecFields(String TableName, ArrayList<String> values, String[] fields, Integer flAdd, String whereId) {
        String  sql;
        StringBuilder fieldsStr = new StringBuilder();
        StringBuilder valueStr = new StringBuilder();
        StringBuilder updateStr = new StringBuilder();

        int sexid=0;

        for (int i = 0; i < fields.length; i++) {
            if (i == fields.length - 1) {
                if (TableName == "People" || TableName == "Call"){
                    fieldsStr.append(fields[i] + ",img)");
                    valueStr.append(values.get(i) + "','"+String.valueOf(sexid)+"')");
                    updateStr.append(fields[i] + "='" + values.get(i) + "',img='"+String.valueOf(sexid)+ "'");
                } else {
                    fieldsStr.append(fields[i] + ")");
                    valueStr.append(values.get(i) + "')");
                    updateStr.append(fields[i] + "='" + values.get(i) + "'");
                }
            } else {
                fieldsStr.append(fields[i] + ",");
                valueStr.append(values.get(i) + "','");
                updateStr.append(fields[i] + "='" + values.get(i) + "',");

                if (fields[i] == "sex") {
                    String sex = values.get(i);
                    if (sex.equals("1")) { sexid = R.drawable.women24; }
                    else                 { sexid = R.drawable.men24;   }
                }
            }
        }
        if ( flAdd == 0 )
            sql = "INSERT  INTO " + TableName + " (" + fieldsStr.toString() + " VALUES('" + valueStr.toString();
        else
            sql = "UPDATE " + TableName + " SET " + updateStr.toString() + " WHERE " + whereId;
        try {
            Log.d(LOG_TAG, sql);
            mDB.execSQL(sql);
        } catch (SQLiteException e) {
            Log.d(LOG_TAG, "Catch a SQLiteException when insert: ", e);
            e.printStackTrace();
        }
    }   // добавить запись в DB_TABLE несколько полей

    //*************************************************************************************
    // удалить DB_TABLE
    //*************************************************************************************
    public int delTable(String TableName) {
        return mDB.delete(TableName, null, null);
    }   // удалить DB_TABLE

    //*************************************************************************************
    // удалить ВСЕ записи из DB_TABLE
    //*************************************************************************************
    public void delAllRec(String TableName ) {
        mDB.delete(TableName, null, null);
    }   // удалить ВСЕ записи из DB_TABLE

    //*************************************************************************************
    // удалить 1 запись из DB_TABLE
    //*************************************************************************************
    public void delRec(String TableName, long id) {
        mDB.delete(TableName, "_id = " + id, null);
    }   // удалить 1 запись из DB_TABLE

    //*************************************************************************************
    // Вставить строки в таблицу (список данных, список полей, число полей)
    //*************************************************************************************
    public void InsertToTable(String TableName, ArrayList<String> list, String[] fields, int mod) {
        int i = 0;
        //String s_log = "";
        ArrayList<String> cont = new ArrayList<String>();

        for (String value : list) {
            cont.add(value);
            /*if (i < fields.length - 1) { s_log = s_log + fields[i] + ":" + value + "; "; }*/
            if (i == mod - 1) {
                /*s_log = s_log + fields[fields.length - 1] + ":" + cont.get(fields.length - 1) + "; "; Log.d(LOG_TAG, s_log); s_log = "";*/
                addRecFields(TableName, cont, fields, 0, "");
                i = 0;
                cont.clear();
            } else i++;
        }
    }    // Вставить строки в таблицу (список данных, список полей, число полей)

    //*************************************************************************************
    // Получить массив из CSV файла
    //*************************************************************************************
    public ArrayList getListFromCsvFile(String FileName) {
        ArrayList<String> list = new ArrayList<String>();

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return list;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FileName);

        try {
            Log.d(LOG_TAG, "--- read file ---");
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str, str1 = "";
            int pos = 0;
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                pos = str.indexOf(";");
                while (pos >= 0) {
                    str1 = str.substring(0, pos);
                    str = str.substring(pos + 1);
                    pos = str.indexOf(";");
                    list.add(str1);
                }
                str1 = str;
                list.add(str1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }   // Получить массив из CSV файла

    //*************************************************************************************
    // Получить CREATE SQL строку по списку полей
    //*************************************************************************************
    public String getSQL(String TableName, String[] fields, String[] types) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table " + TableName + " (_id integer primary key, ");
        for (int i = 0; i < fields.length; i++) {
            sql.append(fields[i] + " " + types[i] + ",");
        }

        return sql.toString() + "img integer);";
    }   // Получить CREATE SQL строку по списку полей

    //*************************************************************************************
    // класс по созданию и управлению БД
    //*************************************************************************************
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            ArrayList<String> list;

            Log.d(LOG_TAG, " --- onCreate database ----");
            String sql = getSQL("street", fieldsStreet, typesStreet);
            db.execSQL(sql);
            Log.d(LOG_TAG, sql);
            sql =  getSQL("address", fieldsAddress, typesAddress);
            db.execSQL(sql);
            Log.d(LOG_TAG, sql);
            sql =  getSQL("people", fieldsPeople, typesPeople);
            db.execSQL(sql);
            Log.d(LOG_TAG, sql);
            sql =  getSQL("call", fieldsCall, typesCall);
            db.execSQL(sql);
            Log.d(LOG_TAG, sql);
            sql =  getSQL("complaint", fieldsComplaint, typesComplaint);
            db.execSQL(sql);
            Log.d(LOG_TAG, sql);
            sql =  getSQL("complaint_p", fieldsComplaint_p, typesComplaint_p);
            db.execSQL(sql);
            Log.d(LOG_TAG, sql);
            sql =  getSQL("tb_complaints", fieldsTb_complaints, typesTb_complaints);
            db.execSQL(sql);
            Log.d(LOG_TAG, sql);

            //ContentValues cv = new ContentValues();
            //list = getListFromCsvFile("Street.csv");
            //InsertToTable("street", list, fieldsStreet, 2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(LOG_TAG, " --- onUpgrade database from " + oldVersion
                    + " to " + newVersion + " version --- ");

            if (oldVersion == 37 && newVersion == 38) {

                ArrayList<String> list;
                ContentValues cv = new ContentValues();

                db.beginTransaction();
                Log.d(LOG_TAG, " --- begin transaction ----");
                try {
                    db.execSQL("drop table if exists " + "street" + ";");
                    db.execSQL("drop table if exists " + "address" + ";");
                    db.execSQL("drop table if exists " + "people" + ";");
                    db.execSQL("drop table if exists " + "call" + ";");
                    db.execSQL("drop table if exists " + "complaint_p" + ";");
                    db.execSQL("drop table if exists " + "complaint" + ";");
                    db.execSQL("drop table if exists " + "tb_complaints" + ";");
                    Log.d(LOG_TAG, " --- delete Table ----");
                    // создаем таблицу street //primary key autoincrement
                    String sql;
                    sql = getSQL("street", fieldsStreet, typesStreet);
                    db.execSQL(sql);
                    Log.d(LOG_TAG, sql);
                    sql = getSQL("address", fieldsAddress, typesAddress);
                    db.execSQL(sql);
                    Log.d(LOG_TAG, sql);
                    sql = getSQL("people", fieldsPeople, typesPeople);
                    db.execSQL(sql);
                    Log.d(LOG_TAG, sql);
                    sql = getSQL("call", fieldsCall, typesCall);
                    db.execSQL(sql);
                    Log.d(LOG_TAG, sql);
                    sql = getSQL("complaint_p", fieldsComplaint_p, typesComplaint_p);
                    db.execSQL(sql);
                    Log.d(LOG_TAG, sql);
                    sql = getSQL("complaint", fieldsComplaint, typesComplaint);
                    db.execSQL(sql);
                    Log.d(LOG_TAG, sql);
                    sql = getSQL("tb_complaints", fieldsTb_complaints, typesTb_complaints);
                    db.execSQL(sql);
                    Log.d(LOG_TAG, sql);

                    //list = getListFromCsvFile("Street.csv");
                    //InsertToTable("street", list, fieldsStreet, 2);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }
    }   // класс по созданию и управлению БД

}