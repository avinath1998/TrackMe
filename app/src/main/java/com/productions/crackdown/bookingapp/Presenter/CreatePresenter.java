package com.productions.crackdown.bookingapp.Presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.productions.crackdown.bookingapp.Interactor.DatabaseHelper;
import com.productions.crackdown.bookingapp.Interface.Create;
import com.productions.crackdown.bookingapp.Model.Appointment;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Avinath on 4/22/2018.
 */

public class CreatePresenter implements Create.Presenter {

    private Create.View view;
    private DatabaseHelper databaseHelper;

    public CreatePresenter(Create.View view, Context context){
        this.view = view;
        databaseHelper = new DatabaseHelper(context);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ArrayList<String> synonyms = (ArrayList) msg.obj;
            setSynonums(synonyms);
        }
    };


    /*
        Creating an appointment
     */
    @Override
    public void createAppointment(Appointment appointment) {
        String title = appointment.getTitle();
        String details = appointment.getDetails();
        Calendar calendar = appointment.getDate();
        if(!title.isEmpty() && !details.isEmpty() && calendar!=null){
            if(databaseHelper.insertAppointment(title,details,calendar,appointment.getId())){
                view.showError("Inserted Successfully");
                view.goBack();
            }else{
                view.showError("Appointment "+title+" already exists, please choose a different event title.");
            }
        }else{
            view.showError("Invalid input!");
        }
    }

    /*
        Creating a new thread in order to get the synonyms
     */
    @Override
    public void getSynonyms(String word) {
        Thread thread = new Thread(new SynonymTask(word,handler));
        thread.start();
    }

    /*
        Setting the synonyms list
     */
    public void setSynonums(ArrayList<String> list){
        view.setSynonymAdapter(list);
    }
}
