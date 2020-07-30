package com.sjcoders.tictactoe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MyReceiver extends BroadcastReceiver {

    FirebaseDatabase firebaseDatabase;

    @Override
    public void onReceive(final Context context, Intent intent) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        final int id = intent.getIntExtra("id",-1);
        String key = ""+ intent.getStringExtra("gameKey");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("gameStatus","declined");
        DatabaseReference df = firebaseDatabase.getReference("Game").child(key);
        df.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.cancel(id);
                }
            }
        });

    }
}
