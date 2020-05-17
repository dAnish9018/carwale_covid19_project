package com.app.baseproject.home;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.baseproject.services.RefreshInterface;
import com.app.baseproject.services.RereshCastReceiver;
import com.app.baseproject.utils.Alert;
import com.app.baseproject.utils.Animator;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.baseproject.R;
import com.app.baseproject.home.adapter.CountryInfoAdapter;
import com.app.baseproject.home.modals.CountryInfoModal;
import com.app.baseproject.services.RequestCallingService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements RefreshInterface {

    RecyclerView rv_country_list;
    Handler handler;
    Runnable runnable;
    HomePresenter presenter;
    TextView tv_total_cases, tv_total_death, tv_total_recovered;
    TextView tv_case_more, tv_death_more, tv_recover_more;
    List<CountryInfoModal> countryInfoList = new ArrayList<>();

    ConstraintLayout layoutBottomSheet, cl_profile;
    BottomSheetBehavior sheetBehavior;
    boolean isCaseMore = true, isDeathMore = true, isRecoverMore = true, isActiveDescending = true, isDeathDescending = true, isRecoverDescending = true;
    View mViewBg;

    TextView tv_filter_case, tv_filter_death, tv_filter_recovery;
    RereshCastReceiver rereshCastReceiver;
    public static final String REFRESH = "REFRESH";
    PendingIntent pintent;
    AlarmManager alarm;


    //https://developer.android.com/training/location/retrieve-current#java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setToolbar();
        initUI();
        setUpRecyclerview();

        presenter = new HomePresenter(this);
        presenter.requestCountryInfo();

        sheetListner();
        registerReceiver();
        startAlarmManager();


    }

    private void setToolbar() {

        findViewById(R.id.iv_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenter.requestCountryInfo();

            }
        });

    }

    private void registerReceiver() {
        rereshCastReceiver = new RereshCastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HomeActivity.REFRESH);

        registerReceiver(rereshCastReceiver, intentFilter);
    }


    private void sheetListner() {

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mViewBg.setClickable(false);
                        break;


                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //  btnBottomSheet.setText("Close Sheet");
                        // cl_profile.setBackgroundColor(getResources().getColor(R.color.transparentBlack));


                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        // btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                Log.d("bottom_Sheet", "onSlide: slideOffset" + slideOffset + "");
                mViewBg.setVisibility(View.VISIBLE);
                mViewBg.setAlpha(slideOffset);
                mViewBg.setClickable(true);


            }
        });


    }

    private void setUpRecyclerview() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_country_list.setLayoutManager(mLayoutManager);
    }

    void setAdapter(List<CountryInfoModal> list) {
        CountryInfoAdapter countryInfoAdapter = new CountryInfoAdapter(list, this);
        rv_country_list.setAdapter(countryInfoAdapter);
    }

    private void initUI() {
        rv_country_list = findViewById(R.id.rv_country_list);
        tv_total_cases = findViewById(R.id.tv_total_cases);
        tv_total_death = findViewById(R.id.tv_total_death);
        tv_total_recovered = findViewById(R.id.tv_total_recovered);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        tv_case_more = findViewById(R.id.case_more);
        tv_recover_more = findViewById(R.id.recover_more);
        tv_death_more = findViewById(R.id.death_more);
        mViewBg = findViewById(R.id.bg);

        tv_filter_case = findViewById(R.id.tv_filter_case);
        tv_filter_death = findViewById(R.id.tv_filter_death);
        tv_filter_recovery = findViewById(R.id.tv_filter_recovery);
    }


    public void onUpClicked(View view) {
        rv_country_list.smoothScrollBy(0, -300);
    }

    public void onDownClicked(View view) {
        rv_country_list.smoothScrollBy(0, 300);
    }


    private void startAlarmManager() {

        //import reference
        //https://stackoverflow.com/questions/8321443/how-to-start-service-using-alarm-manager-in-an
        //https://stackoverflow.com/questions/34585381/setrepeating-of-alarmmanager-repeats-after-1-minute-no-matter-what-the-time-is
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.SECOND, 60*3);

        Intent intent = new Intent(HomeActivity.this, RequestCallingService.class);
        pintent = PendingIntent.getService(HomeActivity.this, 0, intent, 0);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(),
                1000 * 60 * 3, pintent);
    }


    void openSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }
    }

    void closeSheet() {

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        }
    }

    public void onFilterClicked(View view) {

        if (countryInfoList.size() > 0) {
            Animator.buttonAnim(this, view);

            if (view.getId() != R.id.img_close) {

                openSheet();
            } else {
                closeSheet();
            }
        }
    }

    public void onSubmittedClick(View view) {

        int totalcases, totalDeath, totalRecovered;

        EditText et_case = findViewById(R.id.et_case);
        String cases = et_case.getText().toString();

        EditText et_death = findViewById(R.id.et_death);
        String death = et_death.getText().toString();

        EditText et_recover = findViewById(R.id.et_recover);
        String recover = et_recover.getText().toString();

        if (cases.equals(""))
            if (isCaseMore)
                totalcases = 0;
            else
                totalcases = 100000000;

        else {
            totalcases = Integer.valueOf(cases);
        }

        if (death.equals(""))
            if (isDeathMore)
                totalDeath = 0;
            else
                totalDeath = 100000000;
        else {
            totalDeath = Integer.valueOf(death);
        }
        if (recover.equals(""))
            if (isRecoverMore)
                totalRecovered = 0;
            else
                totalRecovered = 100000000;
        else {
            totalRecovered = Integer.valueOf(recover);
        }


        if (verifyEachField(totalcases, totalDeath, totalRecovered, cases, death, recover)) {
            presenter.filter(totalcases, totalDeath, totalRecovered, isCaseMore, isDeathMore, isRecoverMore);
            closeSheet();
            filterSuggestion(cases, death, recover);
        }


    }

    private boolean verifyEachField(int totalcases, int totalDeath, int totalRecovered, String cases, String death, String recover) {
        if (cases.isEmpty() & death.isEmpty() & recover.isEmpty()) {
            Alert.showMessage(this, "Atleast one filter must be applied");
            return false;
        }
        if ((!cases.isEmpty() & !death.isEmpty()) | (!cases.isEmpty() & !recover.isEmpty())) {
            if (totalDeath > totalcases | totalRecovered > totalcases) {
                Alert.showMessage(this, "Case must be greater from  death and recovery");
                return false;

            }

        }
        return true;
    }

    void filterSuggestion(String cases, String death, String recover) {


        findViewById(R.id.text_applied_filter).setVisibility(View.VISIBLE);
        findViewById(R.id.text_clear_filter).setVisibility(View.VISIBLE);

        if (!cases.equals("")) {
            tv_filter_case.setVisibility(View.VISIBLE);
            tv_filter_case.setText("cases\n" + cases + tv_case_more.getText().toString());
        } else
            tv_filter_case.setVisibility(View.GONE);
        if (!death.equals("")) {
            tv_filter_death.setVisibility(View.VISIBLE);
            tv_filter_death.setText("deaths\n" + death + tv_death_more.getText().toString());
        } else
            tv_filter_death.setVisibility(View.GONE);

        if (!recover.equals("")) {
            tv_filter_recovery.setVisibility(View.VISIBLE);
            tv_filter_recovery.setText("recovered\n" + recover + tv_recover_more.getText().toString());
        } else
            tv_filter_recovery.setVisibility(View.GONE);


        if (cases.equals("") && death.equals("") && recover.equals("")) {
            findViewById(R.id.text_applied_filter).setVisibility(View.GONE);
            findViewById(R.id.text_clear_filter).setVisibility(View.GONE);


        }
    }

    public void moreCaseClicked(View view) {
        if (tv_case_more.getText().toString().equals("& more")) {
            isCaseMore = false;
            tv_case_more.setText("& less");
        } else {
            isCaseMore = true;
            tv_case_more.setText("& more");
        }

    }

    public void moreDeathClicked(View view) {
        if (tv_death_more.getText().toString().equals("& more")) {
            isDeathMore = false;
            tv_death_more.setText("& less");
        } else {
            isDeathMore = true;
            tv_death_more.setText("& more");
        }

    }

    public void moreRecoverClicked(View view) {
        if (tv_recover_more.getText().toString().equals("& more")) {
            isRecoverMore = false;
            tv_recover_more.setText("& less");
        } else {
            isRecoverMore = true;
            tv_recover_more.setText("& more");
        }

    }

    @Override
    public void onBackPressed() {

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else {
            exitAlertBox();
        }
        //  super.onBackPressed();

    }

    public void onClearFilteredClicked(View view) {

        presenter.filter(0, 0, 0, true, true, true);
        filterSuggestion("", "", "");
        presenter.sortingFilterTools(countryInfoList, "active", true);
    }

    public void onSortingClick(View view) {

        if (countryInfoList.size() > 0) {
            Animator.buttonAnim(this, view);

            if (view.getId() == R.id.tv_sorting_active) {
                presenter.sortingFilterTools(countryInfoList, "active", isActiveDescending);

            }
            if (view.getId() == R.id.tv_sorting_death) {
                presenter.sortingFilterTools(countryInfoList, "death", isDeathDescending);

            }
            if (view.getId() == R.id.tv_sorting_recovered) {
                presenter.sortingFilterTools(countryInfoList, "recovered", isRecoverDescending);

            }
        }
    }

    @Override
    public void onRefresh() {

        presenter.requestCountryInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarm.cancel(pintent);
        unregisterReceiver(rereshCastReceiver);
    }

    private void exitAlertBox() {

        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Setting Alert Dialog Title

        alertDialogBuilder.setTitle("Confirm Exit..!!!");

        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("Are you sure,You want to exit");
        alertDialogBuilder.setCancelable(false);


        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
