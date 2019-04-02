package com.productions.crackdown.bookingapp.Presenter;

import android.content.Context;

import com.productions.crackdown.bookingapp.Interactor.DatabaseHelper;
import com.productions.crackdown.bookingapp.Interface.Home;
import com.productions.crackdown.bookingapp.Model.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Avinath on 4/21/2018.
 */

public class HomePresenter implements Home.Presenter {

    private Home.View view;
    private DatabaseHelper databaseHelper;

    public HomePresenter(Home.View view, Context context){
        this.view = view;
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void createAppointment() {
        view.showCreation();
    }


    /*
        Loading the appointments that correspond with the users selected dates.
     */
    @Override
    public void loadAppointments(int year, int month, int day) {
        ArrayList<Appointment> appointments = databaseHelper.getAppointmentsAsArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        if(appointments!=null) {
            for (Appointment appointment : appointments) {
                Calendar appointmentCal = appointment.getDate();
                if (fmt.format(appointmentCal.getTime()).equals(fmt.format(calendar.getTime()))) { //checking if the appointment time matches the users selected dat
                    view.addAppointmentToView(appointment);
                }
            }
        }else {
            view.showNoAppointments("No Appointments");
        }
    }

    /*
        Deleting an appointment
     */
    @Override
    public void deleteAppointment(Appointment appointment) {
        if(databaseHelper.deleteAppointment(appointment)){
            view.removeAppointment(appointment);
            view.showError("Deleted Appointment \""+appointment.getTitle()+"\"");

        }
    }

    /*
        Deleting all appointments
     */
    @Override
    public void deleteAllAppointment(Calendar calendar) {
        databaseHelper.deleteAllAppointmentsForDate(calendar);
    }

    /*
        Editing an appointment
     */
    @Override
    public void editAppointment(Appointment movedAppointment, Calendar selectedCalendar) {
        movedAppointment.setDate(selectedCalendar);
        databaseHelper.editAppointment(movedAppointment);
    }

    /*
        Query the database for a user inputted query
     */
    @Override
    public void searchQuery(String query) {
        ArrayList<Appointment> appointments = databaseHelper.getAppointmentsAsArrayList(); //getting the appoints.
        view.clearList();
        int counter = 0;
        for(Appointment appointment:appointments){
            String title = appointment.getTitle().toLowerCase();
            String details = appointment.getDetails().toLowerCase();
            if(title.contains(query)){
                if(appointment.getDate().getTime().compareTo(new Date())>0) {
                    view.addAppointmentToView(appointment);
                    counter++;
                }
            }else if(details.contains(query)){ //checking if the details contains the query.
                if(appointment.getDate().getTime().compareTo(new Date())>0) {
                    view.addAppointmentToView(appointment);
                    counter++;
                }
            }
        }
        if(counter==0)
            view.showNoAppointments("No Appointments Found."); //no appointments have been found
        else
            view.showNoAppointments("Appointments Found: "+counter); //appointments have been found
    }
}
