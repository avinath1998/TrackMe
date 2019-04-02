package com.productions.crackdown.bookingapp.Interactor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.productions.crackdown.bookingapp.Model.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Avinath on 4/22/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Appointments.db";
    public static final String APPOINTMENT_TABLE_NAME = "Appointment_Table";
    public static final String APPOINTMENT_TITLE_COLUMN = "title";
    public static final String APPOINTMENT_ID_COLUMN = "id";
    public static final String APPOINTMENT_CONTENT_COLUMN = "content";
    public static final String APPOINTMENT_TIME_COLUMN = "time";
    public static final String APPOINTMENT_IS_DONE_COLUMN = "isDone";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ APPOINTMENT_TABLE_NAME +
                "(id VARCHAR(5) PRIMARY KEY,title VARCHAR(20), content VARCHAR(40), time LONG,isDone boolean)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+APPOINTMENT_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertAppointment(String title,String content,Calendar calendar,String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long time = calendar.getTimeInMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APPOINTMENT_ID_COLUMN,id);
        contentValues.put(APPOINTMENT_TIME_COLUMN,time);
        contentValues.put(APPOINTMENT_CONTENT_COLUMN,content);
        contentValues.put(APPOINTMENT_TITLE_COLUMN,title);
        contentValues.put(APPOINTMENT_IS_DONE_COLUMN,false);

        //checking if there are any appointments existing that have the same title and date
        if(checkIfAppointmentExists(calendar,title)) {
            if(db.insert(APPOINTMENT_TABLE_NAME, null, contentValues)==-1){
                return false;
            }else
                return true;
       }else{
            return false;
        }
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE "+ APPOINTMENT_TABLE_NAME +
                "(id VARCHAR(5) PRIMARY KEY,title VARCHAR(20), content VARCHAR(40), time LONG,isDone boolean)"
        );
    }

    /*
    Checking if the appointment already exists by checking the dates and titles.
     */
    private boolean checkIfAppointmentExists(Calendar calendar, String title) {
        Cursor cursor = getAppointments();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        if (cursor.getCount() == 0) {
            return true; //no saved appointments.
        } else {
            while (cursor.moveToNext()) {
                Calendar appointmentCal = Calendar.getInstance();
                appointmentCal.setTimeInMillis(cursor.getLong(3));
                String appointmentTitle = cursor.getString(1);
                if (fmt.format(appointmentCal.getTime()).equals(fmt.format(calendar.getTime()))){
                    if(title.equals(appointmentTitle)){
                        return false; //an appointment already exists with the same date and time.
                    }
                }

            }
            return true;
        }
    }

    /*
        Getting all the appointments
     */
    public Cursor getAppointments(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+APPOINTMENT_TABLE_NAME+" ORDER BY "+APPOINTMENT_TIME_COLUMN,null);
        return cursor;
    }

    /*
        Getting all the apponitments as an arraylist
     */
    public ArrayList<Appointment> getAppointmentsAsArrayList() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        Cursor cursor = getAppointments();
        if(cursor.getCount()==0){ //if the curor count is 0, then the results returned nothing
            return null;
        }else{
            while(cursor.moveToNext()){
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getLong(3)); //getting the time
                Appointment appointment = new Appointment(cursor.getString(0),cursor.getString(1),cursor.getString(2),calendar,cursor.getInt(4) > 0);
                appointments.add(appointment);
            }
            return appointments;
        }
    }

    public boolean deleteAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+APPOINTMENT_TABLE_NAME+" WHERE "+APPOINTMENT_ID_COLUMN+"="+appointment.getId());
        return true;
    }

    public void deleteAllAppointmentsForDate(Calendar calendar){
        SQLiteDatabase db = this.getWritableDatabase();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
        calendar1.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        calendar1.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        calendar1.set(Calendar.HOUR_OF_DAY,0);
        calendar1.set(Calendar.MINUTE,0);
        calendar1.set(Calendar.SECOND,0);

        long lowerBoundTime = calendar1.getTimeInMillis();

        calendar1.set(Calendar.HOUR_OF_DAY,23);
        calendar1.set(Calendar.MINUTE,59);
        calendar1.set(Calendar.SECOND,59);
        long upperBoundTime = calendar1.getTimeInMillis();

        db.execSQL("DELETE FROM "+APPOINTMENT_TABLE_NAME+" WHERE "+APPOINTMENT_TIME_COLUMN+" between "+lowerBoundTime+" and "+upperBoundTime);
    }

    public void editAppointment(Appointment appointment){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE "+APPOINTMENT_TABLE_NAME+
                " SET "+APPOINTMENT_TITLE_COLUMN+
                "=\""+appointment.getTitle()+
                "\", "+APPOINTMENT_CONTENT_COLUMN+"=\""+appointment.getDetails()+
                "\", "+APPOINTMENT_TIME_COLUMN+"="+appointment.getDate().getTimeInMillis()
                +" WHERE "+APPOINTMENT_ID_COLUMN+"="+appointment.getId());
    }

}
