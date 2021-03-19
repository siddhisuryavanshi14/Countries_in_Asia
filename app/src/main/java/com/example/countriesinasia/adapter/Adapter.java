package com.example.countriesinasia.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countriesinasia.R;
import com.example.countriesinasia.helper.Country;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<Country> countryList;
    Context context;

    public Adapter(List<Country> countryList, Context context) {
        this.countryList = countryList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_country,
                parent, false);
        return new Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Country country=countryList.get(position);
        holder.countryName.setText(country.getName());
        GlideToVectorYou
                .init()
                .with(context)
                .withListener(new GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {
                    }

                    @Override
                    public void onResourceReady() {
                    }
                })
                .load(Uri.parse(country.getFlag()), holder.flag);


        // Clicked on content
        holder.llContent.setOnClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogBox = inflater.inflate(R.layout.country_info, null);

            // FIND DIALOG BOX VIEWS

            ImageView flag = dialogBox.findViewById(R.id.flag);
            TextView countryName = dialogBox.findViewById(R.id.countryName);
            TextView capital = dialogBox.findViewById(R.id.capitalName);
            TextView region = dialogBox.findViewById(R.id.regionName);
            TextView subregion = dialogBox.findViewById(R.id.subregionName);
            TextView population = dialogBox.findViewById(R.id.populationCount);
            TextView borders = dialogBox.findViewById(R.id.bordersName);
            TextView languages = dialogBox.findViewById(R.id.languagesName);

            // SHOWING DATA IN POPUP

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setView(dialogBox);

            // Country Name
            countryName.setText(country.getName());

            //Country flag
            GlideToVectorYou
                    .init()
                    .with(context)
                    .withListener(new GlideToVectorYouListener() {
                        @Override
                        public void onLoadFailed() {
                        }

                        @Override
                        public void onResourceReady() {
                        }
                    })
                    .load(Uri.parse(country.getFlag()), flag);

            //Capital
            if (country.getCapital().isEmpty()|| country.getCapital()==null) {
                borders.setText("---");
            } else {
                capital.setText(country.getCapital());
            }

            //Region
            region.setText(country.getRegion());

            //Subregion
            subregion.setText(country.getSubregion());

            //Populaation
            population.setText(String.valueOf(country.getPopulation()));

            //Borders
            if (country.getBorders().toString().isEmpty()|| country.getBorders().toString()==null) {
                borders.setText("---");
            } else {
                borders.setText(country.getBorders());
            }

            //Languages
            languages.setText(country.getLanguages());

            AlertDialog mDialog = dialog.create();
            Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView countryName;
        ImageView flag;
        LinearLayout llContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryName=itemView.findViewById(R.id.countryName);
            flag=itemView.findViewById(R.id.flag);
            llContent=itemView.findViewById(R.id.llContent);
        }
    }

    public void filterList(ArrayList<Country> filteredNames) {
        countryList = filteredNames;
        notifyDataSetChanged();
    }
}
