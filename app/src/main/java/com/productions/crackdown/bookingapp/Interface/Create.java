package com.productions.crackdown.bookingapp.Interface;

import com.productions.crackdown.bookingapp.Model.Appointment;

import java.util.ArrayList;

/**
 * Created by Avinath on 4/22/2018.
 */

public interface Create {
    interface View{
        void showError(String message);
        void goBack();
        void setSynonymAdapter(ArrayList<String> list);
        void changeText(String word);
    }

    interface Presenter{
        void createAppointment(Appointment appointment);
        void getSynonyms(String word);
    }
}
