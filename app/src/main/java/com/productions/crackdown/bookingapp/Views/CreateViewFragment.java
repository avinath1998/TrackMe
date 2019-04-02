package com.productions.crackdown.bookingapp.Views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.productions.crackdown.bookingapp.Adapters.AdapterSynonyms;
import com.productions.crackdown.bookingapp.Interface.Create;
import com.productions.crackdown.bookingapp.Model.Appointment;
import com.productions.crackdown.bookingapp.Presenter.CreatePresenter;
import com.productions.crackdown.bookingapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CreateViewFragment extends Fragment implements View.OnClickListener, Create.View {

    private EditText mEditDate,mEditTitle,mEditContent,mEditThe;
    private Button mCreateBtn,mTheBtn,mFindBtn;
    private Create.Presenter presenter;
    private Calendar calendar;
    private ProgressBar mTheProgressBar;
    private RecyclerView mSynonymsList;
    private AdapterSynonyms adapterSynonyms;
    private ArrayList<String> synonymList;
    private TextView mNoSyno;
    private int startSelection,endSelection;
    private NestedScrollView mScrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_view_fragment,container,false);
        presenter = new CreatePresenter(this,getContext());
        calendar = Calendar.getInstance();
        mFindBtn = view.findViewById(R.id.create_btn_find);
        mEditDate = view.findViewById(R.id.create_edit_date);
        mEditTitle = view.findViewById(R.id.create_edit_title);
        mEditContent = view.findViewById(R.id.create_edit_details);
        mScrollView = view.findViewById(R.id.create_layout_scroll);
        mCreateBtn = view.findViewById(R.id.create_btn_create);
        mTheBtn = view.findViewById(R.id.create_btn_thesaurus);
        mEditThe = view.findViewById(R.id.create_edit_thesaurus);
        mNoSyno = view.findViewById(R.id.create_text_no_syno);
        mTheProgressBar = view.findViewById(R.id.create_pb_the);
        mSynonymsList = view.findViewById(R.id.create_recycler_synonyms);
        synonymList = new ArrayList<>();
        adapterSynonyms = new AdapterSynonyms(getContext(),synonymList,this);
        mSynonymsList.setAdapter(adapterSynonyms);
        mSynonymsList.setNestedScrollingEnabled(false);
        mSynonymsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mTheBtn.setOnClickListener(this);
        mCreateBtn.setOnClickListener(this);
        mFindBtn.setOnClickListener(this);
        setDatePicker();
        return view;
    }

    /*
        getting the date from the user for the appointment
     */
    private void setDatePicker() {
        String myFormat = "dd MMM, yyyy hh:mm a";
        final SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {//getting the date of the appointment
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };

       final TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) { //getting the time.
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        mEditDate.setText(sdformat.format(calendar.getTime()));
                    }
                };
       mEditDate.setInputType(InputType.TYPE_NULL);
        mEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                new TimePickerDialog(getActivity(),mTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.create_btn_create:
                presenter.createAppointment(new Appointment(mEditTitle.getText().toString(),mEditContent.getText().toString(),calendar)); //creating an appointment
                break;
            case R.id.create_btn_thesaurus:
                getSynonyms();
                break;
            case R.id.create_btn_find:
                getSynonyms();
                break;
        }
    }

    @Override
    public void showError(String message) { //showing a toast to the user
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goBack() {
        try {
            getActivity().onBackPressed(); //going back to the home page after a
        }catch (NullPointerException e){
        }
    }

    /*
        Setting the adapter with a new list of synonyms for the synonym recycler view.
     */
    @Override
    public void setSynonymAdapter(ArrayList<String> list) {
        mNoSyno.setVisibility(View.GONE); //setting the no synonym text visibility t false
        mTheProgressBar.setVisibility(View.GONE);
        mTheBtn.setVisibility(View.VISIBLE);
        mEditThe.setVisibility(View.VISIBLE);
        if(list.isEmpty()){ //if the list is empty, there are no results forom the request
            mNoSyno.setVisibility(View.VISIBLE);
            mSynonymsList.setVisibility(View.GONE);
        }else {
            synonymList.clear();
            mSynonymsList.setVisibility(View.VISIBLE);
            synonymList.addAll(list);
            adapterSynonyms.notifyDataSetChanged();
        }
    }

    /*
        When the user selects the find button, the highlighted text is read
        and a list of synonyms is given with the use of the method below
     */

    @Override
    public void changeText(String word) {
        if(startSelection == 0 && endSelection == 0){
            return; //theres nothing selected
        }
        word = word.replace(getString(R.string.generic_term),""); //replacing any uncessary text.
        word = word.replace(getString(R.string.similar_tem),"");
        word = word.replace(getString(R.string.related_term),"");
        word = word.replace(getString(R.string.autonym),"");
        String detail = mEditContent.getText().toString();
        detail = detail.substring(0,startSelection)+""+word+detail.substring(endSelection,detail.length()); //getting the users text plus the newly generated synonym
        mEditContent.setText(detail);
    }

    public String  getSelectedSynonum(){ //getting the selected synonym
        startSelection = mEditContent.getSelectionStart();
        endSelection = mEditContent.getSelectionEnd();
        return mEditContent.getText().toString().substring(startSelection, endSelection);

    }

    public void getSynonyms() { //getting the synonym
        String word = getSelectedSynonum();
        if(word.equals("")){
            word = mEditThe.getText().toString();
        }
        if(word.isEmpty() || word.contains(" ")){
            showError(getString(R.string.enter_word_the_error));
        }else{
            mTheProgressBar.setVisibility(View.VISIBLE);
            mTheBtn.setVisibility(View.GONE);
            mEditThe.setVisibility(View.GONE);
            mSynonymsList.setVisibility(View.GONE);
            mScrollView.smoothScrollTo(0,(int)mSynonymsList.getY()); //scrolling to the recyclerview with the list of synonyms
            presenter.getSynonyms(word);
        }
    }


}
