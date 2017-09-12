package it.angelic.noobpoolstats.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MyTimeStampTypeAdapter extends TypeAdapter<Calendar> {
    @Override
    public void write(JsonWriter out, Calendar value) throws IOException {
        if (value == null)
            out.nullValue();
        else
            out.value(value.getTime().toString());
    }

    @Override
    public Calendar read(JsonReader in) throws IOException {
        if (in != null) {
            Calendar ret = Calendar.getInstance();
            ret.setTime(new Date(in.nextLong()));
            return ret;
        }else
            return null;
    }
}