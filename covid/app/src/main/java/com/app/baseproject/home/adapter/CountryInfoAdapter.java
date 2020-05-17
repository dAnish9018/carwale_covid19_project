package com.app.baseproject.home.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.baseproject.R;
import com.app.baseproject.home.HomeActivity;
import com.app.baseproject.home.modals.CountryInfoModal;
import com.app.baseproject.utils.AppData;

import java.util.ArrayList;
import java.util.List;

public class CountryInfoAdapter extends RecyclerView.Adapter<CountryInfoAdapter.MyViewHolder> {

    List<CountryInfoModal> infoList = new ArrayList<>();
    HomeActivity activity;

    public CountryInfoAdapter(List<CountryInfoModal> infoList, HomeActivity activity) {
        this.infoList = infoList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CountryInfoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_country_info,viewGroup,false);
        WindowManager windowManager = (WindowManager)viewGroup.getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.WRAP_CONTENT));
      return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryInfoAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.setIsRecyclable(false);

        CountryInfoModal countryInfo = infoList.get(i);

        if(AppData.COUNTRY_CODE.equals(countryInfo.getCountryCode()))
        {
            myViewHolder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.silver));
        }

      myViewHolder.setData(countryInfo.getCountry(),countryInfo.getTotalConfirmed(),countryInfo.getTotalDeath(),countryInfo.getTotalRecovered());

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_country,tv_cases,tv_death,tv_recovered;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_country = itemView.findViewById(R.id.tv_country);
            tv_cases = itemView.findViewById(R.id.tv_cases);
            tv_death = itemView.findViewById(R.id.tv_death);
            tv_recovered = itemView.findViewById(R.id.tv_recovered);
        }

        void setData(String country,int cases,int death,int recovered){

            tv_country.setText(country);
            tv_cases.setText(""+cases);
            tv_death.setText(""+death);
            tv_recovered.setText(""+recovered);
        }
    }
}
