package com.example.transportenligne;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RunServiceOnBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
      if(intent.getAction().equals((Intent.ACTION_BOOT_COMPLETED)))
      {
          Intent websocketIntent=new Intent(context,WebsocketConnector.class);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              context.startForegroundService(websocketIntent);
          }else {
              context.startService(websocketIntent);
          }
      }
    }


}
