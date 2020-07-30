package com.sjcoders.tictactoe;

public class Game {

    private String player1;
    private String player2;
    private String gameStatus;
    private String gameKey;
    private boolean bothPlayersReady;
    private int whoseTurn;
    private int whoWon;
    private int player1Move;
    private int player2Move;

    public Game(){

    }

    public Game(String player1, String player2, String gameStatus,String gameKey) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameStatus = gameStatus;
        this.gameKey = gameKey;
        this.player1Move = -1;
        this.player2Move = -1;
        this.bothPlayersReady = false;
        this.whoseTurn = 1;
        this.whoWon = -1;
    }

    public int getWhoWon() {
        return whoWon;
    }

    public void setWhoWon(int whoWon) {
        this.whoWon = whoWon;
    }

    public int getPlayer1Move() {
        return player1Move;
    }

    public void setPlayer1Move(int player1Move) {
        this.player1Move = player1Move;
    }

    public int getPlayer2Move() {
        return player2Move;
    }

    public void setPlayer2Move(int player2Move) {
        this.player2Move = player2Move;
    }

    public int getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(int whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public boolean isBothPlayersReady() {
        return bothPlayersReady;
    }

    public void setBothPlayersReady(boolean bothPlayersReady) {
        this.bothPlayersReady = bothPlayersReady;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
