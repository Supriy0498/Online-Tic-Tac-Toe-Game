package com.sjcoders.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteActivity extends AppCompatActivity {

    Button BtnGame4x4,BtnEnter;
    TextView textViewInviteCode;
    EditText editTextEnterCode;
    RequestQueue mRequestQueue;
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference,inviteAcceptReference,checkInviteKeyReference;
    ValueEventListener  inviteAcceptvalueEventListener , checkInviteValueEventListener;
    String key="";
    private boolean isGame4x4 = false;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA7bhoFtQ:APA91bFAWSXLtVUGL8UumN33COS-w5QCT_o0FyhBQKknN7O-RFp2pwiOjbQXQ357vdqLwu3RjVIi2qBwgLZMOlNf0VSJaeqSIA1FzbIpB6ZDQhoHG9bT-nt7c2KoPbk2Enh7J5TteCcD";
    final private String contentType = "application/json";
    private boolean isClick = false;

    public void showHelp(View view){

        new AlertDialog.Builder(InviteActivity.this)
                .setTitle("4x4 Tic Tac Toe")
                .setIcon(getResources().getDrawable(R.drawable.ic_live_help_black))
                .setMessage("The 4x4 Tic Tac Toe Game has following winning conditions:\n" +
                        "4 in a row (vertical,horizontal,diagonal) \nand also\n 4 squares anywhere on the board ")
                .setCancelable(true)
                .setPositiveButton("OK",null)
                .show();

    }

    public  void game4x4(View view){
       // startActivity(new Intent(InviteActivity.this,Game4x4.class));
        String name = sharedPreferences.getString("userName","null");
        String playerName = editTextEnterCode.getText().toString().trim();

        if(playerName.equals("")){
            Toast.makeText(InviteActivity.this, "Please enter the userName of player to be invited !", Toast.LENGTH_SHORT).show();
        }
        else if(playerName.equals(name)){
            Toast.makeText(InviteActivity.this, "Please check the userName !", Toast.LENGTH_SHORT).show();
        }
        else if(!name.equals("null")) {
            progressDialog.show();
            isGame4x4 = true;
            createGame();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        mRequestQueue = Volley.newRequestQueue(InviteActivity.this);
        BtnEnter = findViewById(R.id.BtnEnter);
        BtnGame4x4 = findViewById(R.id.BtnGame4x4);
        textViewInviteCode = findViewById(R.id.textViewCode);
        editTextEnterCode = findViewById(R.id.editTexEnter);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(InviteActivity.this);
        progressDialog.setTitle("Waiting For Player");
        progressDialog.setMessage("Please wait while your opponent accepts the invitation !");
        progressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        Log.d("USERNAME: ",sharedPreferences.getString("userName","null"));
        checkUserName();


        BtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = sharedPreferences.getString("userName","null");
                String playerName = editTextEnterCode.getText().toString().trim();

                if(playerName.equals("")){
                    Toast.makeText(InviteActivity.this, "Please enter the userName of player to be invited !", Toast.LENGTH_SHORT).show();
                }
                else if(playerName.equals(name)){
                    Toast.makeText(InviteActivity.this, "Please check the userName !", Toast.LENGTH_SHORT).show();
                }
                else if(!name.equals("null")) {
                    progressDialog.show();
                    createGame();
                }
            }
        });

    }

    public void createGame(){
        databaseReference = firebaseDatabase.getReference("Game");
        key = getAlphaNumericString(6);
        Game game = new Game("Player1","Player2","Not Started",key);
        databaseReference.child(key).setValue(game).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               // textViewInviteCode.setText(key);
                Log.i("GAMEKEYIS",key);
                sendNotify(key);
                Toast.makeText(InviteActivity.this, "Game Created", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(InviteActivity.this, "Unable to invite player !"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserName(){
        if(sharedPreferences.getString("userName","null").equals("null")){
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            final AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder =new AlertDialog.Builder(InviteActivity.this);
            alertDialogBuilder.setTitle("Game UserName")
                    .setMessage("Please select a user name.. ")
                    .setView(input)
                    .setCancelable(false)
                    .setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String userName = input.getText().toString().trim();
                            if(userName.equals("")){
                                Toast.makeText(InviteActivity.this, "Please enter your userName", Toast.LENGTH_SHORT).show();
                                checkUserName();
                            }
                            else{
                                subscribeUser(userName,dialog);
                            }
                        }
                    }).create();
            alertDialogBuilder.show();
        }
        else{
            String name = textViewInviteCode.getText().toString();
            textViewInviteCode.setText(name + sharedPreferences.getString("userName","null"));
        }
    }

    private void subscribeUser(final String topicName, final DialogInterface dialogInterface){
        Log.i("SUBHERE","true");
        FirebaseMessaging.getInstance().subscribeToTopic(topicName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i("SUBHERE","true1");
                    Toast.makeText(InviteActivity.this, "Subscribed to topic: "+topicName, Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putString("userName",topicName).apply();
                    String name = textViewInviteCode.getText().toString();
                    textViewInviteCode.setText(name + topicName);
                    dialogInterface.cancel();
                }
                else{
                    Log.i("SUBHERE","true2");
                    Toast.makeText(InviteActivity.this, "Failed try again ..", Toast.LENGTH_SHORT).show();
                    checkUserName();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("SUBHERE","true3");
                Toast.makeText(InviteActivity.this, "Error try again", Toast.LENGTH_SHORT).show();
                checkUserName();
            }
        });
    }

    private void UnsubscribeUser(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("supriy").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(InviteActivity.this, "Unsubcribed from topic: supriy", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(InviteActivity.this, "Failed ..", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InviteActivity.this, "Error " +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void waitForAccept() {
        inviteAcceptReference = firebaseDatabase.getReference("Game").child(key);
            inviteAcceptvalueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Game myGame = dataSnapshot.getValue(Game.class);
                    assert myGame != null;
                    if (MainActivity.isGameStarted==0 && myGame.getPlayer1() != null && myGame.isBothPlayersReady()
                            && !myGame.getGameStatus().equals("finished") && !myGame.getGameStatus().equals("declined")) {
                            Log.i("INTSTARTED", "true");
                            Intent intent;
                            if(isGame4x4){
                                intent = new Intent(InviteActivity.this, Game4x4.class);
                            }
                            else{
                                 intent = new Intent(InviteActivity.this, MainActivity.class);
                            }
                            intent.putExtra("key", key);
                            intent.putExtra("player", 1);
                            //textViewInviteCode.setText("Your Invite Code");
                            inviteAcceptReference.removeEventListener(inviteAcceptvalueEventListener);
                            progressDialog.cancel();
                            startActivity(intent);

                    }
                    if(myGame.getGameStatus().equals("declined"))
                    {
                        progressDialog.cancel();
                        Toast.makeText(InviteActivity.this, "Opponent Player Declined the invitation :(", Toast.LENGTH_LONG).show();

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.cancel();
                    Toast.makeText(InviteActivity.this, "Unable to create game " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
        inviteAcceptReference.addValueEventListener(inviteAcceptvalueEventListener);
    }


    public String getAlphaNumericString(int n)
    {
        String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index= (int)(randomString.length()* Math.random());
            sb.append(randomString.charAt(index));
        }
        return sb.toString();
    }

    public void sendNotify(String game_key){

        String topic =editTextEnterCode.getText().toString();
        JSONObject payload = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        JSONObject notifcation = new JSONObject();
        int flag=0;
        try {
            String name = sharedPreferences.getString("userName","null");
            if(!name.equals("null") && !topic.equals(name)) {
                flag=1;
                notifcationBody.put("userName", name);
                notifcationBody.put("key", game_key);
                if(isGame4x4){
                    notifcationBody.put("isGame4x4","true");
                }
                else{
                    notifcationBody.put("isGame4x4","false");
                }
                payload.put("to", "/topics/" + topic);
                payload.put("data", notifcationBody);
            }
        } catch (JSONException e) {
            Log.e("TAGERROR", "onCreate: " + e.getMessage() );
        }
        if(flag==1) {
            sendNotification(payload);
        }
        else{
            Toast.makeText(this, "Please check the user name !", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        waitForAccept();
                        Toast.makeText(InviteActivity.this, "Invitation sent Successfully", Toast.LENGTH_LONG).show();
                        //progressDialog.cancel();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel();
                        Toast.makeText(InviteActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        //progressDialog.cancel();
                        //Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        mRequestQueue.add(jsonObjectRequest);

    }
}
