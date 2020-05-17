package com.app.baseproject.home;

import android.util.Log;
import android.widget.Toast;

import com.app.baseproject.R;
import com.app.baseproject.baseclasses.BasePresenter;
import com.app.baseproject.baseclasses.SharedMethods;
import com.app.baseproject.baseclasses.WebServices;
import com.app.baseproject.home.modals.CountryInfoModal;
import com.app.baseproject.loaders.JSONFunctions;
import com.app.baseproject.utils.Alert;
import com.app.baseproject.utils.AppData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePresenter extends BasePresenter {
    HomeActivity activity;
    CountryInfoModal userCountryInfo;
    List<CountryInfoModal> countryConstantInfoList = new ArrayList<>();



    public HomePresenter(HomeActivity activity) {
        super(activity);
        this.activity = activity;
    }


    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (getpDialog().isShowing()) {
            getpDialog().dismiss();
        }
        switch (url_no) {
            case WebServices.request_url_no_1:

                responseCountryInfo(result);
                break;
        }
    }

    void requestCountryInfo() {
        if (JSONFunctions.isInternetOn(activity)) {
            getpDialog().setMessage("Loading...");
            getpDialog().show();

            getJfns().makeHttpRequest(WebServices.summary, "GET", WebServices.request_url_no_1);

        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }

    private void responseCountryInfo(String response) {

        //Toast.makeText(activity, ""+response, Toast.LENGTH_SHORT).show();

        try {

            if (response != null) {
                JSONObject info_jo = new JSONObject(response);
                JSONObject global_jo = info_jo.getJSONObject("Global");

                JSONArray country_ja = info_jo.getJSONArray("Countries");

                setData(global_jo, country_ja);

            } else {
                Alert.showError(activity, "No response from server...");
            }


        } catch (Exception ex) {

            Alert.showError(activity, ex.getMessage());


        }
    }

    private void setData(JSONObject global_jo, JSONArray country_ja) {

        //global data
        if (global_jo != null) {
            try {
                activity.tv_total_cases.setText("Total cases \n" + global_jo.getString("TotalConfirmed"));
                activity.tv_total_death.setText("Deaths \n" + global_jo.getString("TotalDeaths"));
                activity.tv_total_recovered.setText("Recovered \n" + global_jo.getString("TotalRecovered"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //country wise data
        if (country_ja != null) {

            activity.countryInfoList.clear();
            countryConstantInfoList.clear();
            for (int i = 0; i < country_ja.length(); i++) {
                try {
                    JSONObject country_jo = country_ja.getJSONObject(i);
                    String country = country_jo.getString("Country");
                    String countryCode = country_jo.getString("CountryCode");
                    int totalConfirmed = country_jo.getInt("TotalConfirmed");
                    int totalDeath = country_jo.getInt("TotalDeaths");
                    int totalRecovered = country_jo.getInt("TotalRecovered");


                    //if the case is zero for any country it would be not added
                    if (totalConfirmed != 0) {
                        activity.countryInfoList.add(new CountryInfoModal(country, countryCode, totalConfirmed, totalDeath, totalRecovered));
                        countryConstantInfoList.add(new CountryInfoModal(country, countryCode, totalConfirmed, totalDeath, totalRecovered));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            sortingFilterTools(activity.countryInfoList,"active", true);
            activity.filterSuggestion("","","");

            //activity.setAdapter(activity.countryInfoList);
        }


    }


    void sortingFilterTools(List<CountryInfoModal> list,String whichColumnSorted, boolean isDescending) {

        int userCountryIdex = -1;
        List<Integer> anyColumnList = new ArrayList<>();


        for (int i = 0; i < (list.size()); i++) {

            if (AppData.COUNTRY_CODE.equals(list.get(i).getCountryCode())) {
                userCountryIdex = i;
            } else {

                if (whichColumnSorted.equals("active")) {
                    Log.d("listx", "sortingFilterTools: active");
                    anyColumnList.add(list.get(i).getTotalConfirmed());
                }
                if (whichColumnSorted.equals("death")) {
                    Log.d("listx", "sortingFilterTools: death");

                    anyColumnList.add(list.get(i).getTotalDeath());

                } else if (whichColumnSorted.equals("recovered")) {
                    Log.d("listx", "sortingFilterTools: recovered");

                    anyColumnList.add(list.get(i).getTotalRecovered());

                }
            }


        }

        if(userCountryIdex!=-1)
        userCountryInfo = list.remove(userCountryIdex);
        if (isDescending) {
            sorting(anyColumnList);
            activity.isActiveDescending = false;
            activity.isRecoverDescending = false;
            activity.isDeathDescending = false;

        } else {
            sortingAscending(anyColumnList);
            activity.isActiveDescending = true;
            activity.isRecoverDescending = true;
            activity.isDeathDescending = true;

        }

        list.add(0, userCountryInfo);

        //new line
        activity.setAdapter(list);


    }

    private void sorting(List<Integer> list) {

        int temp, i, j;
        CountryInfoModal temp2;
        for (i = 0; i < list.size(); i++) {

            for (j = i + 1; j < list.size(); j++) {
                if (list.get(i) < list.get(j)) {
                    temp = list.get(i);
                    temp2 = activity.countryInfoList.get(i);

                    list.set(i, list.get(j));
                    list.set(j, temp);

                    Log.d("sorting", "sorting index: " + i + " " + j);
                    activity.countryInfoList.set(i, activity.countryInfoList.get(j));
                    activity.countryInfoList.set(j, temp2);

                }
            }
        }
    }

    void sortingAscending(List<Integer> list) {

        int temp, i, j;
        CountryInfoModal temp2;
        for (i = 0; i < list.size(); i++) {

            for (j = i + 1; j < list.size(); j++) {
                if (list.get(i) > list.get(j)) {
                    temp = list.get(i);
                    temp2 = activity.countryInfoList.get(i);

                    list.set(i, list.get(j));
                    list.set(j, temp);

                    Log.d("sorting", "sorting index: " + i + " " + j);
                    activity.countryInfoList.set(i, activity.countryInfoList.get(j));
                    activity.countryInfoList.set(j, temp2);

                }
            }
        }
    }


    void filter(int totalConfirm, int totalDeath, int totalRecovered, boolean isConfirmMore, boolean isDeathMore, boolean isRecoverMore) {

        //changed
        List<CountryInfoModal> temp_countryInfoList = new ArrayList<>();


        for (int i = 0; i < /*activity.countryInfoList*/countryConstantInfoList.size(); i++) {
            if (isConfirmMore) {
                if (/*activity.countryInfoList*/countryConstantInfoList.get(i).getTotalConfirmed() >= totalConfirm) {

                    recoverFiltering(totalRecovered, isRecoverMore, temp_countryInfoList, i, totalDeath, isDeathMore);
                }
            } else {
                if (/*ctivity.countryInfoList*/countryConstantInfoList.get(i).getTotalConfirmed() <= totalConfirm) {

                    recoverFiltering(totalRecovered, isRecoverMore, temp_countryInfoList, i, totalDeath, isDeathMore);
                }
            }

        }

        //temp_countryInfoList.add(0, userCountryInfo);
        activity.countryInfoList = temp_countryInfoList;
        activity.setAdapter(activity.countryInfoList);
        sortingFilterTools(activity.countryInfoList,"active",true);

    }

    private void deathMOreFiltering(int totalDeath, int totalRecovered, boolean isDeathMore, List<CountryInfoModal> temp_countryInfoList, int i) {
        if (isDeathMore) {
            if (/*activity.countryInfoList*/countryConstantInfoList.get(i).getTotalDeath() >= totalDeath) {
                temp_countryInfoList.add(/*activity.countryInfoList*/countryConstantInfoList.get(i));


            }
        } else {
            if (/*activity.countryInfoList*/countryConstantInfoList.get(i).getTotalDeath() <= totalDeath) {
                temp_countryInfoList.add(/*activity.countryInfoList*/countryConstantInfoList.get(i));

            }
        }
    }

    private void recoverFiltering(int totalRecovered, boolean isRecoverMore, List<CountryInfoModal> temp_countryInfoList, int i, int totalDeath, boolean isDeathMore) {
        if (isRecoverMore) {
            if (/*activity.countryInfoList*/countryConstantInfoList.get(i).getTotalRecovered() >= totalRecovered) {
                //temp_countryInfoList.add(temp_countryInfoList.get(i));
                deathMOreFiltering(totalDeath, totalRecovered, isDeathMore, temp_countryInfoList, i);
            }
        } else {
            if (/*activity.countryInfoList*/countryConstantInfoList.get(i).getTotalRecovered() <= totalRecovered) {
                //temp_countryInfoList.add(temp_countryInfoList.get(i));
                deathMOreFiltering(totalDeath, totalRecovered, isDeathMore, temp_countryInfoList, i);
            }
        }
    }
}

