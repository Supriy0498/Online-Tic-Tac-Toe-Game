package com.sjcoders.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class Game4x4 extends AppCompatActivity implements View.OnClickListener {
    boolean isCross = true;
    String key;
    String result;
    static int isGameStarted = 0;
    AlertDialog.Builder alertDialog;
    int myPlayerNumber,whoseTurn=-1;
    ValueEventListener gameStatusValueEventListener,whoseTurnValueEventListener,playerMoveValueEventListener,whoWonReferenceValueEventListener;
    DatabaseReference exitGameReference,databaseReference,gameReference,gameStatusReference,moveReference,playerReference,whoWonReference;
    FirebaseDatabase firebaseDatabase;
    private  int c1=0 , c2=0, c3=0 , c4=0 , c5=0 , c6=0 ,c7=0 ,c8=0 ,c9=0,stop=0,isdraw=0;
    ImageView cell1 , cell2 ,cell3,cell4,cell5,cell6,cell7,cell8,cell9,cell10 , cell12 ,cell13,cell14,cell15,cell16,cell11 ;
    TextView textViewTurn;
    private int[][] winningPositions = {{0,1,2,3},{4,5,6,7},{8,9,10,11},{12,13,14,15},{0,5,10,15},{3,6,9,12},{0,4,8,12},{1,5,9,13},{2,6,10,14},{3,7,11,15},
                                 {0,1,4,5},{1,2,5,6},{2,3,6,7},{4,5,8,9},{5,6,9,10},{6,7,10,11},{8,9,12,13},{9,10,13,14},{10,11,14,15}};
    private int[] gameStates = {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};



    public void play4x4(View v) {

        if (stop == 0 && whoseTurn == myPlayerNumber) {

            ImageView item = (ImageView) v;
            int position = Integer.parseInt(item.getTag().toString());
            position = position - 1;

            if (gameStates[position] == 2) {

                isdraw += 1;
                if (isCross) {
                    isCross = false;
                    whoseTurn=2;
                    textViewTurn.setText("Opponent's Turn");
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("whoseTurn",whoseTurn);
                    hashMap.put("player1Move",position+1);
                    gameReference = firebaseDatabase.getReference("Game");
                    gameReference.child(key).updateChildren(hashMap);
                    gameStates[position] = 0;
                    item.setImageResource(R.drawable.crossnew);
                } else {
                    isCross = true;
                    whoseTurn=1;
                    textViewTurn.setText("Opponent's Turn");
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("whoseTurn",whoseTurn);
                    hashMap.put("player2Move",position+1);
                    gameReference = firebaseDatabase.getReference("Game");
                    gameReference.child(key).updateChildren(hashMap);
                    gameStates[position] = 1;
                    item.setImageResource(R.drawable.circlenew);
                }

                int isDone=0;
                for (int[] winState : winningPositions) {
                    Log.i("WINSTATE", "->" + gameStates[winState[0]] + " " + gameStates[winState[1]] + " " + gameStates[winState[2]]+" "+gameStates[winState[3]]);
                    if (gameStates[winState[0]] == gameStates[winState[1]] &&
                            gameStates[winState[1]] == gameStates[winState[2]] &&
                            gameStates[winState[2]] == gameStates[winState[3]]&& gameStates[winState[0]] != 2) {
                        int winner;
                        if (gameStates[winState[0]] == 0) {
                            winner = 1;
                        }
                        else {
                            winner = 2;
                        }
                        HashMap<String,Object> whoWon = new HashMap<>();
                        whoWon.put("whoWon",winner);
                        firebaseDatabase.getReference("Game").child(key).updateChildren(whoWon);
                        if(myPlayerNumber == winner)
                            result = "You Won !!! ";
                        else
                            result="You Lost :( Better luck next time !";
                        stop = 1;
                        //te.setText(result);
                        Log.i("WINN", "->" + winner);
                        isDone=1;
                        break;
                    }
                }
                Log.i("OKOK", "-------------------------------------------");

                boolean isGameDraw = true;
                for (int i : gameStates) {
                    if (i == 2)
                        isGameDraw = false;
                }

                if (isGameDraw && isDone==0) {
                    Log.i("DRAWW", "->" + Arrays.toString(gameStates));
                    stop = 1;
                    isDone=1;
                    //wonStatus.setText("ITS A DRAW");
                    HashMap<String,Object> whoWon = new HashMap<>();
                    whoWon.put("whoWon",0);
                    firebaseDatabase.getReference("Game").child(key).updateChildren(whoWon);
                    Toast.makeText(this, "ITS A DRAW", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }
    public void initArray(){
        Arrays.fill(gameStates, 2);
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setCancelable(false)
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        gameStatusReference.removeEventListener(gameStatusValueEventListener);
                        moveReference.removeEventListener(whoseTurnValueEventListener);
                        playerReference.removeEventListener(playerMoveValueEventListener);
                        whoWonReference.removeEventListener(whoWonReferenceValueEventListener);
                        exitGameReference = firebaseDatabase.getReference("Game");
                        // if(myPlayerNumber==1){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("gameStatus","finished");
                        hashMap.put("bothPlayersReady",false);
                        exitGameReference.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Game4x4.this, "Game Finished", Toast.LENGTH_SHORT).show();
                                isGameStarted =0;
                                alertDialog = null;
                                if(isTaskRoot()) {
                                    Intent intent = new Intent(Game4x4.this,InviteActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog = null;
                                finish();
                                Toast.makeText(Game4x4.this, "ERROR "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).create().show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        cell1 = findViewById(R.id.image4x4View1);
        cell2 = findViewById(R.id.image4x4View2);
        cell3 = findViewById(R.id.image4x4View3);
        cell4 = findViewById(R.id.image4x4View4);
        cell5 = findViewById(R.id.image4x4View5);
        cell6 = findViewById(R.id.image4x4View6);
        cell7 = findViewById(R.id.image4x4View7);
        cell8 = findViewById(R.id.image4x4View8);
        cell9 = findViewById(R.id.image4x4View9);
        cell10 = findViewById(R.id.image4x4View10);
        cell11 = findViewById(R.id.image4x4View11);
        cell12 = findViewById(R.id.image4x4View12);
        cell13 = findViewById(R.id.image4x4View13);
        cell14 = findViewById(R.id.image4x4View14);
        cell15 = findViewById(R.id.image4x4View15);
        cell16 = findViewById(R.id.image4x4View16);
        textViewTurn = findViewById(R.id.textViewTurn);

        alertDialog =  new AlertDialog.Builder(Game4x4.this);
        Intent myIntent = getIntent();
        isGameStarted = 1;
        firebaseDatabase = FirebaseDatabase.getInstance();
        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            myPlayerNumber = getIntent().getIntExtra("player",0);
        }
        else {
            myPlayerNumber = bundle.getInt("player");
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Game4x4.this);
            notificationManagerCompat.cancel(1);
            Log.i("PLAYERNO: ",String.valueOf(myPlayerNumber));
        }

        if(myPlayerNumber==1) {
            textViewTurn.setText("Your Turn");
            key = getIntent().getStringExtra("key");
            whoseTurn = 1;
        }
        else if(myPlayerNumber==2){
            textViewTurn.setText("Opponent's Turn");
            key = myIntent.getStringExtra("game");
            Log.i("PLAYER2KEY: ","->"+key);
            // }
            assert key != null;
            // Log.i("PLAYER2KEY: ","->"+key);
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("bothPlayersReady",true);
            DatabaseReference df = firebaseDatabase.getReference("Game").child(key);
            df.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // progressDialog.cancel();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("ACCEPTED","False");
                }
            });

        }

        databaseReference = firebaseDatabase.getReference("Game").child(key);
        gameStatusReference = databaseReference.child("gameStatus");
        gameStatusValueEventListener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String myFinalGame = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                if(myFinalGame!=null){
                    if(myFinalGame.equals("finished")){
                        Log.i("GAMELEFT","PLAYER left");
                        isGameStarted = 0;
                        gameStatusReference.removeEventListener(gameStatusValueEventListener);
                        moveReference.removeEventListener(whoseTurnValueEventListener);
                        playerReference.removeEventListener(playerMoveValueEventListener);
                        whoWonReference.removeEventListener(whoWonReferenceValueEventListener);
                        alertDialog = null;
                        Toast.makeText(Game4x4.this, "Your Opponent Left the Game :/", Toast.LENGTH_LONG).show();
                        if(isTaskRoot()) {
                            Intent intent = new Intent(Game4x4.this,InviteActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            finish();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        gameStatusReference.addValueEventListener(gameStatusValueEventListener);

        moveReference = databaseReference.child("whoseTurn");
        whoseTurnValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int whoseTurnIsIt = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                if(whoseTurnIsIt==1) {
                    isCross = true;
                    whoseTurn=1;
                }
                else {
                    isCross = false;
                    whoseTurn=2;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        moveReference.addValueEventListener(whoseTurnValueEventListener);


        final String whichMove;
        if(myPlayerNumber==1)
            whichMove = "player2Move";
        else
            whichMove = "player1Move";
        playerReference = databaseReference.child(whichMove);
        playerMoveValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int position = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                if(position!=-1){
                    textViewTurn.setText("Your Turn");
                    ImageView playerMoveImageView;
                    playerMoveImageView = findViewById(R.id.gameContainer4x4).findViewWithTag(String.valueOf(position));
                    if (myPlayerNumber == 1) {
                        playerMoveImageView.setImageResource(R.drawable.circlenew);
                        gameStates[position - 1] = 1;
                    } else if (myPlayerNumber == 2) {
                        gameStates[position - 1] = 0;
                        playerMoveImageView.setImageResource(R.drawable.crossnew);
                    }
                    Log.i("MYGAMESTATE", "-> " + Arrays.toString(gameStates));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        playerReference.addValueEventListener(playerMoveValueEventListener);


        whoWonReference = databaseReference.child("whoWon");
        whoWonReferenceValueEventListener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int whoIsWinner = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                if(whoIsWinner!=-1) {
                    stop=1;
                    final String gameresult;
                    if (myPlayerNumber == whoIsWinner)
                        gameresult = "You Won !!! ";
                    else if (whoIsWinner == 0)
                        gameresult = "It's A Draw ,Good Game !!";
                    else
                        gameresult = "You Lost :( Better luck next time !";

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            showAlert(gameresult);
                            playAgain();
                        }
                    };
                    Handler handler = new Handler();
                    handler.postDelayed(runnable,2000);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        whoWonReference.addValueEventListener(whoWonReferenceValueEventListener);


    }

    public void playAgain(){
        if(myPlayerNumber==1)
            textViewTurn.setText("Your Turn");
        else
            textViewTurn.setText("Opponent's Turn");
        stop=0;
        isdraw=0;
        isCross = true;
        initArray();
        cell1.setImageResource(0);
        cell2.setImageResource(0);
        cell3.setImageResource(0);
        cell4.setImageResource(0);
        cell5.setImageResource(0);
        cell6.setImageResource(0);
        cell7.setImageResource(0);
        cell8.setImageResource(0);
        cell9.setImageResource(0);
        cell10.setImageResource(0);
        cell11.setImageResource(0);
        cell12.setImageResource(0);
        cell13.setImageResource(0);
        cell14.setImageResource(0);
        cell15.setImageResource(0);
        cell16.setImageResource(0);
        DatabaseReference playAgainReference = firebaseDatabase.getReference("Game");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("gameStatus","Started");
        hashMap.put("bothPlayersReady",true);
        hashMap.put("player1Move",-1);
        hashMap.put("player2Move",-1);
        hashMap.put("whoWon",-1);
        hashMap.put("whoseTurn",1);
        playAgainReference.child(key).updateChildren(hashMap);
    }

    public void showAlert(String gameresult){

        alertDialog.setTitle("Game Over")
                .setCancelable(false)
                .setMessage(gameresult)
                .setPositiveButton("Play Again", null)
                .setNegativeButton("EXIT",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        onBackPressed();
                    }

                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {


    }
}
