package com.sjcoders.tictactoe;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class App extends Application {

    private String gameChannel_id = "gameChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel1 = new NotificationChannel(gameChannel_id,
                    "Game Channel", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Tic Tac Toe Game Channel");


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel1);

        }

    }


}
