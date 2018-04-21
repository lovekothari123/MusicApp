package com.djgeraldo.custom;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.djgeraldo.R;

/**
 * Created by Android18 on 28-10-2016.
 */
public class CustomHeader  {
    public static void setOuterFragment(final Activity activity, RelativeLayout relativeLayout) {
        ImageView btn_menu= (ImageView) relativeLayout.findViewById(R.id.btnback);
//        btn_menu.setBackgroundResource(R.drawable.back_arrow);
        relativeLayout.setTag("Outer");
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
//                HomeActivity.visiblePlayer();
            }
        });

    }

    public static void setInnerFragment(final Activity activity, final RelativeLayout relativeLayout){
        final ImageView btn_menu= (ImageView) relativeLayout.findViewById(R.id.btnback);
//        btn_menu.setBackgroundResource(R.drawable.back_arrow);
        relativeLayout.setTag("Inner");

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (relativeLayout.getTag().equals("Inner")) {
                    activity.onBackPressed();
                } else {
//                    btn_menu.setBackgroundResource(R.drawable.menu);

                }

            }
        });

    }

    public static void setOuterFragment_more(final Activity activity, RelativeLayout relativeLayout) {
        ImageView btn_menu= (ImageView) relativeLayout.findViewById(R.id.btnback);
//        btn_menu.setBackgroundResource(R.drawable.back_arrow);
        relativeLayout.setTag("Outer");
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
//                HomeActivity.visiblePlayer();
            }
        });

    }
}
