package com.sjcoders.tictactoe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class FireBaseMessgaging extends FirebaseMessagingService {

    private String gameChannel_id = "gameChannel";
    private String isGame4x4;
    String invitedByUser;
    private NotificationManagerCompat notificationManager;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    PendingIntent enterGameIntent;
    DatabaseReference databaseReference,inviteAcceptReference,checkInviteKeyReference;
    ValueEventListener  inviteAcceptvalueEventListener , checkInviteValueEventListener;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth =FirebaseAuth.getInstance();

        Log.i("MSGREC","true");

        notificationManager = NotificationManagerCompat.from(FireBaseMessgaging.this);
        invitedByUser = remoteMessage.getData().get("userName");
        isGame4x4 = remoteMessage.getData().get("isGame4x4");
        String gameKey = remoteMessage.getData().get("key");
        assert gameKey != null;
        Log.i("INVITEKEY",gameKey);

        acceptInvitation(gameKey);

    }

    public void buildNotification(PendingIntent pendingIntent,String myGameKey){

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.notifimg);

        Intent declineIntent = new Intent(FireBaseMessgaging.this,MyReceiver.class);
        declineIntent.putExtra("id",1);
        declineIntent.putExtra("gameKey",myGameKey);
        PendingIntent pendingDeclineIntent = PendingIntent.getBroadcast(FireBaseMessgaging.this,1,declineIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification inviteNotification = new NotificationCompat.Builder(FireBaseMessgaging.this,gameChannel_id)
                .setContentTitle("Game Invitation from "+invitedByUser)
                .setContentText(invitedByUser +" invited you to play Tic Tac Toe...")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(largeIcon)
                .setSound(sound)
                .setColor(Color.GREEN)
                .addAction(R.mipmap.ic_launcher_round,"ACCEPT",pendingIntent)
                .addAction(R.mipmap.ic_launcher_round,"DECLINE",pendingDeclineIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();


        notificationManager.notify(1,inviteNotification);
    }

    public void acceptInvitation(final String gameInviteKey){

        Log.i("GAMEINVITEKEY",gameInviteKey);

            checkInviteKeyReference = firebaseDatabase.getReference("Game").child(gameInviteKey);
            enterTheGame(gameInviteKey);
//            checkInviteValueEventListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.getValue() ==null){
//                        Toast.makeText(FireBaseMessgaging.this, "Wrong Invite Key !", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    else{
//                        Game acceptGame = dataSnapshot.getValue(Game.class);
//                        if(acceptGame!=null && !acceptGame.getGameStatus().equals("COMPLETED")
//                                && !acceptGame.getGameStatus().equals("finished")){
//                            Log.i("HEREOKOK",gameInviteKey);
//                            enterTheGame(gameInviteKey);
//                        } } }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) { }
//            };
//            checkInviteKeyReference.addListenerForSingleValueEvent(checkInviteValueEventListener);
    }

    public void enterTheGame(final String gameKey){

        Log.i("GAMEKEYIS",gameKey);

        HashMap<String,Object> hashMap = new HashMap<>();
       // hashMap.put("bothPlayersReady",true);
        hashMap.put("gameStatus","Started");
        checkInviteKeyReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = null;
                Log.i("ISGAME4x4",isGame4x4);
                if(isGame4x4.equals("true")){
                    intent = new Intent(FireBaseMessgaging.this,Game4x4.class);
                }
                else if(isGame4x4.equals("false")){
                    intent = new Intent(FireBaseMessgaging.this,MainActivity.class);
                }
                Log.i("PUTKEY",gameKey);
                Bundle bundle = new Bundle();
                bundle.putInt("player",2);
                assert intent != null;
                intent.putExtras(bundle);
                intent.putExtra("game",gameKey);
                //intent.putExtra("Pkey",gameKey);
               //intent.putExtra("player",2);
                enterGameIntent = PendingIntent.getActivity(FireBaseMessgaging.this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                Log.i("ACCEPTED","TRUEE");
//                checkInviteKeyReference.removeEventListener(checkInviteValueEventListener);
                buildNotification(enterGameIntent,gameKey);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("ACCEPTED","False");
                ///Toast.makeText(InviteActivity.this, "ERROR"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
