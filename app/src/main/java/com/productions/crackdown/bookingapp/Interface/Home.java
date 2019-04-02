package com.productions.crackdown.bookingapp.Interface;

import com.productions.crackdown.bookingapp.Model.Appointment;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Avinath on 4/21/2018.
 */

public interface Home {
    interface View{
        void showCreation();
        void showError(String message);
        void addAppointmentToView(Appointment appointment);
        void removeAppointment(Appointment appointment);
        void showQueryResults(ArrayList<Appointment> appointments);
        void clearList();
        void showNoAppointments(String s);
    }

    interface Presenter{
        void createAppointment();
        void loadAppointments(int year, int month, int day);
        void deleteAppointment(Appointment appointment);
        void deleteAllAppointment(Calendar calendar);
        void editAppointment(Appointment movedAppointment, Calendar selectedCalendar);
        void searchQuery(String query);
    }
}
