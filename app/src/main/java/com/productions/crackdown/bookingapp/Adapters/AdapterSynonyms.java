package com.productions.crackdown.bookingapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.productions.crackdown.bookingapp.Interface.Create;
import com.productions.crackdown.bookingapp.R;

import java.util.ArrayList;

/**
 * Created by Avinath on 4/26/2018.
 * This adapter is for the synonym list when the user is creating an appointment
 */

public class AdapterSynonyms extends RecyclerView.Adapter<AdapterSynonyms.ViewHolder> {

    private Context context;
    private ArrayList<String> synonyms;
    private Create.View viewMain;

    public AdapterSynonyms(Context context, ArrayList<String> synonyms,Create.View view) {
        this.context = context;
        this.synonyms = synonyms;
        this.viewMain = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_create_synonym,parent,false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return synonyms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView synonym;

        public ViewHolder(View itemView) {
            super(itemView);
            synonym = itemView.findViewById(R.id.item_synonym_title);
        }

        public void bindView(final int position){
            synonym.setText(synonyms.get(position));
            synonym.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewMain.changeText(synonyms.get(position)); //changing the text in the details edit text to the selected synonym.
                }
            });
        }

    }
}
