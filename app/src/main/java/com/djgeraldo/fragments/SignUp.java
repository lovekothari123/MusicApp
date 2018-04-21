package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.phonemidea.CheckNetwork;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUp extends Fragment implements DatePickerDialog.OnDateSetListener,View.OnFocusChangeListener {


    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    String title;
    ProgressDialog mProgressDialog;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    EditText fname,lname,business_name,business_address,business_type,mob,email,office,birth_date,description;
    String first,last,bname,badd,btype,mobile,emailAdd,officePh,bdate,descr;
    Button btn_submit;
    JSONObject object;
    ImageView backButton1,playerButton1;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    String tag = SignUp.class.getSimpleName();
    Calendar myCalendar = Calendar.getInstance();


    public SignUp() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SignUp(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Sign Up");
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getMenuFragment();
//            }
//        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
//            }
//        });
        fname = v.findViewById(R.id.fname);
        lname = v.findViewById(R.id.lname);
        business_name = v.findViewById(R.id.business_name);
        business_address = v.findViewById(R.id.business_address);
        business_type = v.findViewById(R.id.business_type);
        mob = v.findViewById(R.id.mob);
        email = v.findViewById(R.id.email);
        office = v.findViewById(R.id.office);
        birth_date = v.findViewById(R.id.birth_date);
        description = v.findViewById(R.id.description);
        btn_submit = v.findViewById(R.id.btn_submit);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("SignUp","Android id: "+androidId);
        Log.d("SignUp","Net is connected: "+isConnected);
        object = new JSONObject();
        birth_date.setOnFocusChangeListener(this);
        birth_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                new DatePickerDialog(context, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                birth_date.setError(null);
                datePick(v.getId());
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first = fname.getText().toString();
                last = lname.getText().toString();
                bname = business_name.getText().toString();
                badd = business_address.getText().toString();
                btype = business_type.getText().toString();
                mobile = mob.getText().toString();
                emailAdd = email.getText().toString();
                officePh = office.getText().toString();
                bdate = birth_date.getText().toString();
                descr = description.getText().toString();
                Log.d("SignUp","fname is:"+first);
                Log.d("SignUp","lname is:"+last);
                Log.d("SignUp","bname is:"+bname);
                Log.d("SignUp","b address is:"+badd);
                Log.d("SignUp","btype is:"+btype);
                Log.d("SignUp","mobile is:"+mobile);
                Log.d("SignUp","emailAdd is:"+emailAdd);
                Log.d("SignUp","office Ph is:"+officePh);
                Log.d("SignUp","bdate is:"+bdate);
                Log.d("SignUp","descr is:"+descr);
                if (!first.isEmpty() && !last.isEmpty() && !bname.isEmpty() && !badd.isEmpty() && !btype.isEmpty() && !mobile.isEmpty() && isValidEmail(emailAdd) && !emailAdd.isEmpty() && !officePh.isEmpty() && !bdate.isEmpty() && !descr.isEmpty() && btn_submit.isPressed())
                {
                    try {
                        object.put("fname",first);
                        object.put("lname",last);
                        object.put("b_name",bname);
                        object.put("b_address",badd);
                        object.put("b_type",btype);
                        object.put("mob_no",mobile);
                        object.put("office",officePh);
                        object.put("email",emailAdd);
                        object.put("bdate",bdate);
                        object.put("description",descr);

                        Log.d("mytag",object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (CheckNetwork.isInternetAvailable(context)){
                        new signUpAsyncTask().execute();

//                    fname,lname,business_name,business_address,business_type,mob,email,office,birth_date,description
                    }
                    else
                    {
                        Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //first,last,bname,badd,btype,mobile,emailAdd,officePh,bdate,descr
                    //                    fname,lname,business_name,business_address,business_type,mob,email,office,birth_date,description
                    if (first.isEmpty()) {
                        fname.setError("Please Enter First Name");
                        fname.requestFocus();
                    }
                    else if (bname.isEmpty())
                    {
                        business_name.setError("Please Enter Business Name");
                        business_name.requestFocus();
                    }
                    else if (last.isEmpty()) {
                        lname.setError("Please Enter Last Name");
                        lname.requestFocus();
                    }
                    else if (badd.isEmpty()) {
                        business_address.setError("Please Enter Address");
                        business_address.requestFocus();
                    }
                    else if (btype.isEmpty()) {
                        business_type.setError("Please Enter Business Type");
                        business_type.requestFocus();
                    }
                    else if (mobile.isEmpty()) {
                        mob.setError("Please Enter Mobile Number");
                        mob.requestFocus();
                    }
                    else if (!isValidEmail(emailAdd)) {
                        email.setError("Please Enter valid Email");
                        email.requestFocus();
                    }  else if (officePh.isEmpty()) {
                        office.setError("Please Enter Office Number");
                        office.requestFocus();
                    } else if (bdate.isEmpty()) {
                        birth_date.setError("Please Enter Birth date");
                        birth_date.requestFocus();
                    } else if (descr.isEmpty()) {
                        description.setError("Please Enter Description");
                        description.requestFocus();
                    }

                }

            }
        });
        return v;
    }

    public void clearAll()
    {
        fname.requestFocus();
        fname.getText().clear();
        lname.getText().clear();
        business_name.getText().clear();
        business_address.getText().clear();
        business_type.getText().clear();
        mob.getText().clear();
        email.getText().clear();
        office.getText().clear();
        birth_date.getText().clear();
        description.getText().clear();
    }

    private void datePick(int id) {
//        id_flag = id;
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog =  DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(Calendar.getInstance());
        datePickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog");
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setOnDateSetListener(this);

    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        birth_date.setText(null);
        int day = dayOfMonth;
        int yy = year;
        int month = ++monthOfYear;
        String str_day = day < 10 ? "0" + day : "" + day;
        String str_month = month < 10 ? "0" + month : "" + month;
        birth_date.setText(String.valueOf(yy) + "-" + str_month + "-" + str_day);
    }

    private static boolean isValidEmail(String email) {
        if(!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        else
            return false;

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
        {
            switch (v.getId())
            {
                case R.id.birth_date:
                    birth_date.setError(null);
                    datePick(v.getId());
                    break;
            }
        }
    }


    //http://durisimomobileapps.net/djsisko/api/signup
    private class signUpAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            Log.d("mytag",object.toString());
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/vip_list/add";
                String url = "http://durisimomobileapps.net/djgeraldo/api/vip_list/add";
                RequestBody body = RequestBody.create(JSON, object.toString());
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                return result;
            } catch (Exception e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            try {
                Log.d("mytag", "Sign Up Response : " + s);
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                String msg = jsonObject.getString("msg");
                if (status == 1)
                {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                    clearAll();

                }
                Log.d("Sign Up","Status is:"+status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
