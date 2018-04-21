package com.djgeraldo.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Created by sachin on 30-12-2017.
 */

public class IncommingCallReceiver extends BroadcastReceiver
{
    Context mContext;
    @Override
    public void onReceive(Context mContext, Intent intent)
    {
        try
        {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
//                Toast.makeText(mContext, "Phone Is Ringing", Toast.LENGTH_LONG).show();
                if (MediaController.getInstance().getPlayingSongDetail() != null && !MediaController.getInstance().isAudioPaused())
                {
                    MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
                }
            }

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {

//                Toast.makeText(mContext, "Call Recieved", Toast.LENGTH_LONG).show();
                // Your Code
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {

//                Toast.makeText(mContext, "Phone Is Idle", Toast.LENGTH_LONG).show();
                // Your Code

            }
        }
        catch(Exception e)
        {
            //your custom message
        }

    }

}
