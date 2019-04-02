package com.productions.crackdown.bookingapp.Views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.productions.crackdown.bookingapp.Adapters.AdapterAppointmentsHome;
import com.productions.crackdown.bookingapp.Interface.Home;
import com.productions.crackdown.bookingapp.Model.Appointment;
import com.productions.crackdown.bookingapp.Presenter.HomePresenter;
import com.productions.crackdown.bookingapp.R;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class HomeViewFragment extends Fragment implements Home.View,View.OnClickListener {

    private RapidFloatingActionLayout mFabLayout;
    private RapidFloatingActionHelper mFabHelper;
    private RapidFloatingActionButton mFabBtn;
    private RapidFloatingActionContentLabelList fabBtnContent;
    private Home.Presenter presenter;
    private CalendarView mCalendar;
    private RecyclerView mAppointmentList;
    private ArrayList<Appointment> appointmentArrayList;
    private  AdapterAppointmentsHome adapter;
    private TextView mNoAppointmentText,mAlertText;
    private ImageView mDeleteBtn,mMoveBtn,mSearchBtn;
    private LinearLayout mDeleteView,mMoveView,mSearchView;
    private EditText mDeleteNumber, mMoveNumber,mSearchQuery;
    private Button mDeleteAppointBtn, mDeleteAppointAllBtn,mMoveAppointBtn,mSearchAppointBtn;
    private Calendar selectedCalendar;
    private Appointment movedAppointment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_view_fragment,container,false);
        presenter = new HomePresenter(this,getContext());
        movedAppointment = null;
        mFabLayout = view.findViewById(R.id.home_layout_fab);
        mFabBtn = view.findViewById(R.id.home_btn_fab);
        mCalendar = view.findViewById(R.id.home_calendar);
        mAppointmentList = view.findViewById(R.id.home_recyclerview_appointments);
        mAlertText = view.findViewById(R.id.home_options_text_alert);
        mSearchQuery = view.findViewById(R.id.home_edit_search_number);
        mSearchView = view.findViewById(R.id.home_layout_search_btn_view);
        mSearchAppointBtn = view.findViewById(R.id.home_btn_search_search);
        mSearchAppointBtn.setOnClickListener(this);
        mNoAppointmentText = view.findViewById(R.id.home_text_no_appointments);
        mDeleteBtn = view.findViewById(R.id.home_btn_delete);
        mDeleteView = view.findViewById(R.id.home_layout_btn_view);
        mMoveView = view.findViewById(R.id.home_layout_move_btn_view);
        mMoveAppointBtn = view.findViewById(R.id.home_btn_move_move);
        mMoveBtn = view.findViewById(R.id.home_btn_move);
        mMoveBtn.setOnClickListener(this);
        mMoveAppointBtn.setOnClickListener(this);
        mDeleteAppointBtn = view.findViewById(R.id.home_btn_delete_delete);
        mDeleteAppointBtn.setOnClickListener(this);
        mDeleteAppointAllBtn = view.findViewById(R.id.home_btn_delete_all);
        mDeleteAppointAllBtn.setOnClickListener(this);
        mSearchBtn = view.findViewById(R.id.home_btn_search);
        mSearchBtn.setOnClickListener(this);
        mDeleteNumber = view.findViewById(R.id.home_edit_delete_number);
        mMoveNumber = view.findViewById(R.id.home_edit_move_number);

        selectedCalendar = Calendar.getInstance();

        mDeleteView.setVisibility(View.GONE);
        mNoAppointmentText.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);

        appointmentArrayList = new ArrayList<>();
        mAppointmentList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterAppointmentsHome(appointmentArrayList,getContext()); //setting the adapter for the appointmenst
        mAppointmentList.setAdapter(adapter);

        loadCurrentDate(); //loading the current date into the calendarview

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) { //setting the calendar date change listener
                appointmentArrayList.clear(); //clearing the current selected date appointments
                showNoAppointments(getString(R.string.no_appointments));
                adapter.notifyDataSetChanged();
                selectedCalendar.set(Calendar.YEAR,year);
                selectedCalendar.set(Calendar.MONTH,month);
                selectedCalendar.set(Calendar.DAY_OF_MONTH,day);
                mSearchView.setVisibility(View.GONE);
                mDeleteView.setVisibility(View.GONE);
                mMoveView.setVisibility(View.GONE);
                if(movedAppointment!=null){ //if the user has selected a new date to move an appointment into, this will not be null
                    selectedCalendar.set(Calendar.HOUR_OF_DAY,movedAppointment.getDate().get(Calendar.HOUR));
                    selectedCalendar.set(Calendar.MINUTE,movedAppointment.getDate().get(Calendar.MINUTE));
                    presenter.editAppointment(movedAppointment,selectedCalendar);// editing the date
                    showError("Moved Appointment \""+movedAppointment.getTitle()+"\"");
                    movedAppointment = null;
                    mMoveNumber.setText("");
                    hideAlert();
                    mMoveNumber.clearFocus();//clearing the focus from the edit text
                }
                presenter.loadAppointments(year,month,day);

            }
        });

        mFabBtn.setOnClickListener(this); //setting the fab btn listener
        initDateBtns();
        return view;
    }

    /*
        Selecting the current date into the calendar view
     */
    private void loadCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        presenter.loadAppointments(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        selectedCalendar = calendar;

    }

    private void initDateBtns() {
        mDeleteBtn.setOnClickListener(this);
    }


    /*
        Replacing the current fragment with the fragment to create a new appointment
     */
    @Override
    public void showCreation() {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_placeholder, new CreateViewFragment()).addToBackStack("CREATION").commit();
        }catch (NullPointerException e){
            showError(getString(R.string.please_try_again));
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
    }

    /*
        ADding an appointment into the view
     */
    @Override
    public void addAppointmentToView(Appointment appointment) {
        mNoAppointmentText.setVisibility(View.GONE);
        appointmentArrayList.add(appointment);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearList() {
        appointmentArrayList.clear();
        adapter.notifyDataSetChanged();
    }

    /*
        Showing a no appointments text.
     */
    @Override
    public void showNoAppointments(String s) {
        mNoAppointmentText.setVisibility(View.VISIBLE);
        mNoAppointmentText.setText(s);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_btn_delete:
                showDeleteView();
                break;
            case R.id.home_btn_delete_all:
                showDeleteAllConfirmation();
                break;
            case R.id.home_btn_delete_delete:
                deleteOneAppointment();
                break;
            case R.id.home_btn_fab:
                presenter.createAppointment();
                break;
            case R.id.home_btn_move:
                showMoveAppointment();
                break;
            case R.id.home_btn_move_move:
                moveAppointment();
                break;
            case R.id.home_btn_search:
                showSearchAppointment();
                break;
            case R.id.home_btn_search_search:
                searchAppointment();
        }
    }

    /*
        Showing the option to delete an appointment
     */
    private void showDeleteView() {
        mMoveView.setVisibility(View.GONE);
        mDeleteView.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);
    }

    /*
        Showing the option to search for an appointment
     */
    private void showSearchAppointment() {
        mSearchView.setVisibility(View.VISIBLE);
        mMoveView.setVisibility(View.GONE);
        mDeleteView.setVisibility(View.GONE);
    }

    /*
        Searching for an appointment
     */
    private void searchAppointment() {
        String query = mSearchQuery.getText().toString();
        if(query.isEmpty()){
            showError(getString(R.string.enter_query_error));
            return;
        }else{
            presenter.searchQuery(query);
            mSearchQuery.setText("");
        }
    }

    @Override
    public void showQueryResults(ArrayList<Appointment> appointments) {
    }

    /*
        Showing the options to move an appointment
     */
    private void showMoveAppointment() {
        mDeleteView.setVisibility(View.GONE);
        mMoveView.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);
    }

    /*
        Moving an appointment to a user selected date.
     */
    private void moveAppointment() {
        String moveNumStr = mMoveNumber.getText().toString();
        if(moveNumStr.contains(" ") || moveNumStr.isEmpty()){
            showError(getString(R.string.enter_valid_appointment_error));
        }else {
            try{
                int appointNum = Integer.parseInt(moveNumStr); //getting the appointment number
                Appointment appointment = appointmentArrayList.get(appointNum-1);
                this.movedAppointment = appointment;
                showAlert(getString(R.string.date_selection_error));
            }catch (ClassCastException e){
                showError(getString(R.string.enter_valid_appointment_error));
            }catch (IndexOutOfBoundsException e){
                showError(getString(R.string.enter_valid_appointment_error));
            }
        }

    }

    private void showAlert(String message){
        mAlertText.setVisibility(View.VISIBLE);
        mAlertText.setText(message);
    }

    private void hideAlert(){
        mAlertText.setVisibility(View.GONE);
    }

    /*
        Creating an alert dialog for the user to confirm dleetion all
     */
    private void showDeleteAllConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_question)
                .setMessage(R.string.confirm_delete_all)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteAllAppointments();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    /*
        Deleting any appointment with the user selected date.
     */
    private void deleteAllAppointments() {
        presenter.deleteAllAppointment(selectedCalendar);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Iterator<Appointment> it = appointmentArrayList.iterator();
        while (it.hasNext()) {
            Calendar cal1 = it.next().getDate();
            Calendar cal2 = selectedCalendar; //getting the selected date.
            if(fmt.format(cal1.getTime()).equals(fmt.format(cal2.getTime()))){ //checking the user selected date matches the appointment date for deletion
                it.remove(); //removing the appintment
            }
        }
        adapter.notifyDataSetChanged();
        showNoAppointments(getString(R.string.no_appointment));
    }

    /*
        Deleting one appointment with a user selected date
     */
    private void deleteOneAppointment() {
        String deleteNumStr = mDeleteNumber.getText().toString();
        if(!deleteNumStr.isEmpty() && !deleteNumStr.contains(" ")){
            int deleteNum = Integer.parseInt(deleteNumStr)-1;
            if(deleteNum>=0 && deleteNum<appointmentArrayList.size()) {
                Appointment appointment = appointmentArrayList.get(Integer.parseInt(deleteNumStr) - 1);
                showOneDeleteConfirmation(appointment,Integer.parseInt(deleteNumStr) - 1);
            }else{
                showError("Please enter a valid number.");
            }
        }else{
            showError("Please enter a valid number");
        }

    }

    /*
        Showing the confirmation for the deletion of one appointment
     */
    private void showOneDeleteConfirmation(final Appointment appointment,final int index) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_questions)
                .setMessage(getString(R.string.delete_event_confirm)+appointment.getTitle()+"\"")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        presenter.deleteAppointment(appointmentArrayList.get(index));
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        appointmentArrayList.remove(appointment);
        if(appointmentArrayList.size()==0)
            showNoAppointments(getString(R.string.no_appointmnets_error));
        adapter.notifyDataSetChanged();
    }
}
