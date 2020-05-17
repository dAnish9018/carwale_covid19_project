package com.app.baseproject.baseclasses;

import android.app.ProgressDialog;
import android.content.Context;

import com.app.baseproject.loaders.JSONFunctions;

public abstract class BasePresenter implements JSONFunctions.OnJSONResponseListener{
    private JSONFunctions jfns;
    private ProgressDialog pDialog;

    public BasePresenter(Context context){
        jfns=new JSONFunctions(BasePresenter.this);
        pDialog=new ProgressDialog(context);
        pDialog.setCancelable(false);
    }

    public JSONFunctions getJfns() {
        return jfns;
    }

    public ProgressDialog getpDialog() {
        return pDialog;
    }
}
