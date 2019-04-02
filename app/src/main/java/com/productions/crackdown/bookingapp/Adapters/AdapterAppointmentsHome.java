package com.productions.crackdown.bookingapp.Adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.productions.crackdown.bookingapp.Interactor.DatabaseHelper;
import com.productions.crackdown.bookingapp.Model.Appointment;
import com.productions.crackdown.bookingapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Avinath on 4/24/2018.
 * THis is the adapter for the appointments when a user selects a date on the landing page.
 */

public class AdapterAppointmentsHome extends RecyclerView.Adapter<AdapterAppointmentsHome.ViewHolder> {

    private ArrayList<Appointment> appointments;
    private Context context;

    public AdapterAppointmentsHome(ArrayList<Appointment> appointments, Context context){
        this.appointments = appointments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view); //creating a new viewholder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void refreshAdapter(){
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private EditText mTitle, mContent, mDate;
        private TextView mNumber;
        private SimpleDateFormat df;
        private ImageView  mEditBtn;
        private CardView mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            df = new SimpleDateFormat("hh:mm a");
            mTitle = itemView.findViewById(R.id.item_appointments_title);
            mContent = itemView.findViewById(R.id.item_appointments_content);
            mDate = itemView.findViewById(R.id.item_appointments_time);
            mLayout = itemView.findViewById(R.id.item_layout_synonym);
            mEditBtn = itemView.findViewById(R.id.item_appointments_edit_btn);
            mNumber = itemView.findViewById(R.id.item_appointments_number);
        }



        public void bindView(final int position){
            final Appointment appointment = appointments.get(position);
            mTitle.setText(appointment.getTitle());
            mDate.setText(df.format(appointment.getDate().getTime()));
            mContent.setText(appointment.getDetails());
            mNumber.setText(position+1+".");
            mEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { //creating a popup if the user chooses to edit their appointment
                    openEditPopup(appointment,position,true);
                    refreshAdapter();

                }
            });
            mLayout.setOnClickListener(this);
            mTitle.setOnClickListener(this);
            mDate.setOnClickListener(this);
            mContent.setOnClickListener(this);
        }

        /*
            OPening the popup for the user to edit an appointment.
         */
        private void openEditPopup(final Appointment appointment, final int position, boolean isEditable) {
            String myFormat = "dd MMM, yyyy hh:mm a";
            final SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
            final DatabaseHelper databaseHelper = new DatabaseHelper(context);
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.activity_popup_create_appointment);

            Button mEditBtn = dialog.findViewById(R.id.create_btn_create);
            mEditBtn.setText("Save");
            TextView mTitle = dialog.findViewById(R.id.create_text_title);
            final EditText mAppoTitle = dialog.findViewById(R.id.create_edit_title);
            final EditText mAppoContent = dialog.findViewById(R.id.create_edit_details);
            final EditText mAppoDate = dialog.findViewById(R.id.create_edit_date);

            mAppoContent.setText(appointment.getDetails());
            mAppoTitle.setText(appointment.getTitle());
            mAppoDate.setText(sdformat.format(appointment.getDate().getTime()));

            if(isEditable) { //this method is reused for when the user wants to edit and view an appointment
                mTitle.setText("Edit Appointment");
                dialog.setTitle("Edit Appointment");

                final Calendar calendar = Calendar.getInstance();
                setDatePicker(calendar, mAppoDate);

                mEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String details = mAppoContent.getText().toString();
                        String title = mAppoTitle.getText().toString();
                        if (!details.isEmpty() && !title.isEmpty()) {
                            appointment.setDetails(details);
                            appointment.setTitle(title);
                            appointment.setDate(calendar);
                            if (!mDate.getText().toString().isEmpty()) {
                                databaseHelper.editAppointment(appointment);
                                dialog.dismiss();
                                Toast.makeText(context, "Successfully edited!", Toast.LENGTH_LONG).show();
                                appointments.set(position, appointment);
                                refreshAdapter();
                            } else {
                                Toast.makeText(context, "Please enter a valid date", Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(context, "Please enter valid details", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                mEditBtn.setVisibility(View.GONE);
                mAppoContent.setFocusable(false);
                mAppoDate.setFocusable(false);
                mAppoTitle.setFocusable(false);
                mTitle.setText("Appointment");
                dialog.setTitle("Appointment");
            }
            dialog.show();

        }

        /*
            If the user chooses to edit the time of the appointment.
         */
        private void setDatePicker(final Calendar calendar, final EditText mAppoDate) {
            String myFormat = "dd MMM, yyyy hh:mm a";
            final SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }
            };

            final TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                    mAppoDate.setText(sdformat.format(calendar.getTime()));
                }
            };
            mAppoDate.setInputType(InputType.TYPE_NULL);

            mAppoDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(context, date, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();

                    new TimePickerDialog(context,mTimeSetListener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false).show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            openEditPopup(appointments.get(position),position,false);

        }
    }
}
