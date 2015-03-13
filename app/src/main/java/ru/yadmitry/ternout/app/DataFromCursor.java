package ru.yadmitry.ternout.app;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DYakunin on 06.03.2015.
 */
public class DataFromCursor {

    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_IMAGE = "image";
    final String ATTRIBUTE_NAME_CODE = "code";


    Map<String, Object> m;
    ArrayList<Map<String, Object>> data;

    public ArrayList<Map<String, Object>> getComplaintData(Cursor cursor) {
        data = new ArrayList<Map<String, Object>>();

        while (cursor.moveToNext()) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_CODE, cursor.getInt(cursor.getColumnIndex("code")));
            m.put(ATTRIBUTE_NAME_TEXT, cursor.getString(cursor.getColumnIndex("name")));
            data.add(m);
        }
        return data;
    }

}
