package com.djgeraldo.phonemidea;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jaimin patel / jaimin2305@gmail.comon 1/1/2016.
 */
public class Utils {

    //Single Instance object
    private static Utils instance = null;

    //
    private Utils() {
    }

    //Single Instance get
    public static Utils getInstance() {
        if (instance == null)
            instance = new Utils();

        return instance;
    }

    /**
     * @return General Method For Debug Call from Anywhere in Application
     */
    private static final String TAG = "mytag";
    //        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.stegowl.djmusic", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

    public void d(String message) {
        Log.i(TAG, message);
    }

    public void toast(Activity caller, String message) {
        Toast.makeText(caller, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isValidEmail(String emailId) {

        if (!TextUtils.isEmpty(emailId) && Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
            return true;
        else
            return false;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public String getRealPathFromURI2(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void showFailAlert(Context context,String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Failed");
                        alertDialog.setMessage(msg);

                        alertDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
    }
    public  long strToMilli(String strTime) {
        long retVal = 0;
        String hour = strTime.substring(0, 2);
        String min = strTime.substring(3, 5);
        String sec = strTime.substring(6, 8);
//        String milli = strTime.substring(9, 12);
        int h = Integer.parseInt(hour);
        int m = Integer.parseInt(min);
        int s = Integer.parseInt(sec);
//        int ms = Integer.parseInt(milli);

        String strDebug = String.format("%02d:%02d:%02d", h, m, s);
        //System.out.println(strDebug);
        long lH = h * 60 * 60 * 1000;
        long lM = m * 60 * 1000;
        long lS = s * 1000;

        retVal = lH + lM + lS ;
        return retVal;
    }

    public   String milliToString(long millis) {

        long hrs = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long min = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        //millis = millis - (hrs * 60 * 60 * 1000); //alternative way
        //millis = millis - (min * 60 * 1000);
        //millis = millis - (sec * 1000);
        //long mls = millis ;
//        long mls = millis % 1000;
        String toRet = String.format("%02d:%02d:%02d", hrs, min, sec);
        //System.out.println(toRet);
        return toRet;
    }
    public String date_time() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("MMM-dd HH:mm", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public void loadGlide(Context context, ImageView iv, String url) {
        try {
            Glide.with(context)
                    .load(url)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .centerCrop()
//                    .placeholder(R.drawable.app_logo1)
                    .crossFade()
                    .into(iv);



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean isValidPassword(String pass) {


        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[@#$%!]).{6,20})";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);


        if (!pass.isEmpty() && pattern.matcher(pass).matches())
            return true;
        else
            return false;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public  String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

//        long now = getCurrentTime();
//
//        long now= System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        Date curDate = calendar.getTime();
        long now=curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "min ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "hrs ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + "day ago";
        }
    }
    private long getCurrentTime(){
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);

        long sinceMidnight = (rightNow.getTimeInMillis() + offset) %
                (24 * 60 * 60 * 1000);

        System.out.println(sinceMidnight + " milliseconds since midnight");
        return offset;
    }

//    public void loadImageCircle(Context context, CircleImageView iv, String url) {
//        try {
//            Picasso.with(context)
//                    .load(url)
//                    .skipMemoryCache()
//                    .placeholder(context.getResources().getDrawable(R.drawable.users))
//                    .error(context.getResources().getDrawable(R.drawable.users))
//                    .into(iv);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void loadImage(Context context, ImageView iv, String url) {
//        try {
//            Picasso.with(context)
//                    .load(url)
//                    .placeholder(context.getResources().getDrawable(R.drawable.img))
//                    .error(context.getResources().getDrawable(R.drawable.img))
//                    .into(iv);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public boolean isValidAmount(String val) {
//        final String Amount_Pattern="^\\d{1,3}(\\.\\d{2})?$";
//        final String price="\\b\\d{1,3}(\\.\\d{2})?\\b";

        final String amount = "^\\d{1,4}(\\.\\d{2})$";

        Pattern pattern = Pattern.compile(amount);
        if (!val.isEmpty() && pattern.matcher(val).matches()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isValidAmount2(String val) {
//        final String Amount_Pattern="^\\d{1,3}(\\.\\d{2})?$";
//        final String price="\\b\\d{1,3}(\\.\\d{2})?\\b";

        DecimalFormat df=new DecimalFormat("0.00");
        float flt_val=Float.parseFloat(val);
        final String amount = "^\\d{1,4}(\\.\\d{2})$";

        Pattern pattern = Pattern.compile(amount);
        if (!val.isEmpty() && pattern.matcher(val).matches() && flt_val>=0.5) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isMobileNumbervalid(String phoneNumber) {
        //^[0-9]{10}$
        //Pattern pattern = Pattern.compile("^\\+[0-9]{10,13}$");
        Pattern pattern = Pattern.compile("^[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber.trim());
        //
        if (matcher.matches() && !phoneNumber.isEmpty())
            return true;
        else
            return false;
    }

    /***
     * @param context
     * @return Check whether internet connectivity is on or off.
     */
    public boolean isConnectivity(Context context) {
        //
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * Check only accepts AlphaBet Character.
     */
    public boolean isAlphabetCheck(String charCheck) {
        Pattern pattern = Pattern.compile("[a-zA-Z'-]+");
        Matcher matcher = pattern.matcher(charCheck);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    //image convert in string
    public String bitmapToString(Bitmap bitmap) {

        String temp = null;
        try {
            ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ByteStream);
            byte[] b = ByteStream.toByteArray();
            temp = Base64.encodeToString(b, Base64.NO_WRAP);

            return temp;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            return temp;
        }

    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        return false;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return true;
        }
        return false;
    }

    //string data convert in bitmapimage
    public Bitmap stringToBitmap(String imageData) {
        try {
            byte[] encodeByte = Base64.decode(imageData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    String response = null;

    public AlertDialog.Builder showDialogBox(Context context, String title, String msg) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        response = "0";
                    }
                });
        alertDialog.show();
        return alertDialog;
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }


    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }



}
