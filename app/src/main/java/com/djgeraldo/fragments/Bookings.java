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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.phonemidea.CheckNetwork;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Bookings extends Fragment implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener,View.OnFocusChangeListener  {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    String title;
    ProgressDialog mProgressDialog;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    EditText booking_name,booking_phone,booking_email,booking_date,booking_Time,booking_address,booking_city,booking_state,booking_zipcode,booking_country;
    String bname,bphone,bemail,bdate,btime,badd,bcity,bstate,bzip,bcountry;
    String party_type= null;
    CheckBox night_club,lounge,festival,private_function;
    ImageView backButton1,playerButton1;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    TextView menuListHeading;
    String tag = Bookings.class.getSimpleName();
    Button btn_submit;
    JSONObject object;

    public Bookings() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Bookings(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bookings, container, false);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Bookings");
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("book","Android id: "+androidId);
        Log.d("book","Net is connected: "+isConnected);
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
        booking_name = v.findViewById(R.id.booking_name);
        booking_phone = v.findViewById(R.id.booking_phone);
        booking_email = v.findViewById(R.id.booking_email);
        booking_date = v.findViewById(R.id.booking_date);
        booking_Time = v.findViewById(R.id.booking_Time);
        booking_address = v.findViewById(R.id.booking_address);
        booking_city = v.findViewById(R.id.booking_city);
        booking_state = v.findViewById(R.id.booking_state);
        booking_zipcode = v.findViewById(R.id.booking_zipcode);
        booking_country = v.findViewById(R.id.booking_country);
        btn_submit = v.findViewById(R.id.btn_submit);
        booking_date.setOnFocusChangeListener(this);
        booking_Time.setOnFocusChangeListener(this);

        booking_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                booking_date.setError(null);
                datePick(v.getId());
            }
        });

        booking_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                booking_Time.setError(null);
                String date = booking_date.getText().toString().trim();
                boolean flag = false;
                if (!date.equals(null) && !date.equals("")) {
                    flag = true;
                } else {
                    flag = false;
                }
                timePick(view.getId(), flag);
            }
        });

        night_club = v.findViewById(R.id.night_club);
        lounge = v.findViewById(R.id.lounge);
        festival = v.findViewById(R.id.festival);
        private_function = v.findViewById(R.id.private_function);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("Booking","Android id: "+androidId);
        Log.d("Booking","Net is connected: "+isConnected);
        night_club.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                {
                    party_type = "Night Club";
                    lounge.setChecked(false);
                    festival.setChecked(false);
                    private_function.setChecked(false);}
            }
        });
        lounge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                {
                    party_type = "Lounge";
                    night_club.setChecked(false);
                    festival.setChecked(false);
                    private_function.setChecked(false);}
            }
        });
        festival.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                { party_type = "Festival";
                    night_club.setChecked(false);
                    lounge.setChecked(false);
                    private_function.setChecked(false);}
            }
        });
        private_function.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true)
                {
                    party_type = "Private Function";
                    night_club.setChecked(false);
                    festival.setChecked(false);
                    lounge.setChecked(false);}
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bname = booking_name.getText().toString();
                bphone = booking_phone.getText().toString();
                bemail = booking_email.getText().toString();
                bdate = booking_date.getText().toString();
                btime = booking_Time.getText().toString();
                badd = booking_address.getText().toString();
                bcity = booking_city.getText().toString();
                bstate = booking_state.getText().toString();
                bzip = booking_zipcode.getText().toString();
                bcountry = booking_country.getText().toString();
                Log.d("Bookings","Full Name:"+bname);
                Log.d("Bookings","Phone:"+bphone);
                Log.d("Bookings","Email:"+bemail);
                Log.d("Bookings","date:"+bdate);
                Log.d("Bookings","time:"+btime);
                Log.d("Bookings","address:"+badd);
                Log.d("Bookings","city:"+bcity);
                Log.d("Bookings","state:"+bstate);
                Log.d("Bookings","zip:"+bzip);
                Log.d("Bookings","country:"+bcountry);
                Log.d("Bookings","Party type:"+party_type);
//        if (!u_id.isEmpty() && !full_name.isEmpty() && Utils.getInstance().isValidEmail(email) && !date.isEmpty() && !time.isEmpty() && !address.isEmpty() && !city.isEmpty() && !state.isEmpty() && !zip.isEmpty() && !country.isEmpty() && party_type != null) {
                if (!bname.isEmpty() && !bphone.isEmpty() && !bemail.isEmpty() && isValidEmail(bemail) && !bdate.isEmpty() && !btime.isEmpty() && !badd.isEmpty() && !bcity.isEmpty() && !bstate.isEmpty() && !bzip.isEmpty() && !bcountry.isEmpty() && party_type != null) {
                    object = new JSONObject();
                    try {
                        object.put("name", bname);
                        object.put("mob_no", bphone);
                        object.put("email", bemail);
                        object.put("date", bdate);
                        object.put("time", btime);
                        object.put("address", badd);
                        object.put("city", bcity);
                        object.put("state", bstate);
                        object.put("country", bcountry);
                        object.put("zip", bzip);
                        object.put("party_type", party_type);
                        object.put("user_id", androidId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (CheckNetwork.isInternetAvailable(context)) {
                        new bookAsyncTask(object).execute();
                    }
                    else
                    {
                        Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    if (bname.isEmpty()) {
                        booking_name.setError("Please Enter Full Name");
                        booking_name.requestFocus();
                    } else if (!isValidEmail(bemail)) {
                        booking_email.setError("Please Enter valid Email");
                        booking_email.requestFocus();
                    } else if (bdate.isEmpty()) {
                        booking_date.setError("Please Enter valid Date");
                        booking_date.requestFocus();
                    } else if (btime.isEmpty()) {
                        booking_Time.setError("Please Enter Time");
                        booking_Time.requestFocus();
                    } else if (badd.isEmpty()) {
                        booking_address.setError("Please Enter Address");
                        booking_address.requestFocus();
                    } else if (bcity.isEmpty()) {
                        booking_city.setError("Please Enter City");
                        booking_city.requestFocus();
                    } else if (bstate.isEmpty()) {
                        booking_state.setError("Please Enter State");
                        booking_state.requestFocus();
                    } else if (bzip.isEmpty()) {
                        booking_zipcode.setError("Please Enter Zip Code");
                        booking_zipcode.requestFocus();
                    } else if (bcountry.isEmpty()) {
                        booking_country.setError("Please Enter Country");
                        booking_country.requestFocus();
                    } else if (party_type == null) {
                        Toast.makeText(getActivity(), "please select party type", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        return v;   
    }


    private static boolean isValidEmail(String email) {
        if(!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        else
            return false;

    }

    private void clearAllEdditText()
    {
        booking_name.requestFocus();
        booking_name.getText().clear();
        booking_phone.getText().clear();
        booking_email.getText().clear();
        booking_date.getText().clear();
        booking_Time.getText().clear();
        booking_address.getText().clear();
        booking_city.getText().clear();
        booking_state.getText().clear();
        booking_zipcode.getText().clear();
        booking_country.getText().clear();
        night_club.setChecked(false);
        lounge.setChecked(false);
        festival.setChecked(false);
        private_function.setChecked(false);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        booking_date.setText(null);
        int day = dayOfMonth;
        int yy = year;
        int month = ++monthOfYear;
        String str_day = day < 10 ? "0" + day : "" + day;
        String str_month = month < 10 ? "0" + month : "" + month;
        booking_date.setText(String.valueOf(yy) + "/" + str_month + "/" + str_day);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        booking_Time.setText(hourString + ":" + minuteString);
    }

    private void timePick(int id, boolean flag) {
//        id_flag = id;
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance((TimePickerDialog.OnTimeSetListener) context,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        if (flag == true) {
            String selected_date = booking_date.getText().toString().trim();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            Date date2 = new Date(System.currentTimeMillis());
            String current_date = formatter.format(date2);
            if (selected_date.equals(current_date)) {
                tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE) + 1, now.get(Calendar.SECOND));
            }
        }
        tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
        tpd.dismissOnPause(true);
        tpd.setOnTimeSetListener(this);
    }

    private void datePick(int id) {
//        id_flag = id;
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog =  DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog");
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setOnDateSetListener(this);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
        {
            switch (v.getId())
            {
                case R.id.booking_date:
                    booking_date.setError(null);
                    datePick(v.getId());
                    break;
                case R.id.booking_Time:
                    booking_Time.setError(null);
                    String date = booking_date.getText().toString().trim();
                    boolean flag = false;
                    if (!date.equals(null) && !date.equals("")) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                    timePick(v.getId(), flag);
                    break;
            }
        }
    }

    //    http://durisimomobileapps.net/djsisko/api/booking
    private class bookAsyncTask extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;
        public bookAsyncTask(JSONObject object)
        {
            this.jsonObject = object;
        }

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
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/booking/add";
                String url = "http://durisimomobileapps.net/djgeraldo/api/booking/add";
                Log.d("mytag",String.valueOf(jsonObject));
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
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
            Log.d("mytag", "Bookings Response : " + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                String msg = jsonObject.getString("msg");
                Log.d("Bookings","status is:"+status);
                if (status == 1)
                {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                    clearAllEdditText();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


}
