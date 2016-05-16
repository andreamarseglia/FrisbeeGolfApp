package com.andreamarseglia.app.frisbeeGolfApp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joni on 5.9.2014.
 */
public class GameCourse implements Parcelable {
    String [] nameOfPlayers;
    int [][] scoreBoard;
    int laneId;

    // Basic Constructor
    public GameCourse (String [] namesOfPlayers, int numberOfHoles) {
        this.nameOfPlayers = namesOfPlayers;
        scoreBoard = new int[namesOfPlayers.length][numberOfHoles + 1];
        laneId = 1;
    }

    // Parcel Constructor
    public GameCourse (Parcel in) {
        String [] data = new String[3];
        in.readStringArray(data);

        laneId = Integer.parseInt(data[0]);
        String [] tempNames = data[1].split("\r");
        String [] tempScores = data[2].split("\n");

        nameOfPlayers = new String[tempNames.length];
        scoreBoard = new int[tempNames.length][tempScores[0].split("\r").length + 1];

        for(int i = 0; i < tempNames.length; i++){
            nameOfPlayers [i] = tempNames[i];
            String [] playerScore = tempScores[i].split("\r");
            for (int j = 0; j < playerScore.length; j++) {
                scoreBoard[i][j] = Integer.parseInt(playerScore[j]);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        StringBuffer tmpNames = new StringBuffer();
        StringBuffer tmpScores = new StringBuffer();

        for(int j = 0; j < nameOfPlayers.length; j++) {
            tmpNames.append(nameOfPlayers[j]);
            tmpNames.append("\r");
            for (int k = 0; k < scoreBoard[j].length; k++) {
                tmpScores.append(String.valueOf(scoreBoard[j][k]));
                tmpScores.append("\r");
            }
            tmpScores.append("\n");
        }

        parcel.writeStringArray(new String[] {
                String.valueOf(laneId),
                tmpNames.toString(),
                tmpScores.toString()
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GameCourse createFromParcel(Parcel in) {
            return new GameCourse(in);
        }

        public GameCourse[] newArray(int size) {
            return new GameCourse[size];
        }
    };

    /**
     * Moves to next lane
     */
    public void nextLane() {laneId++;}

    /**
     * Simple lane id getter
     */
    public int getLaneId() {return laneId;}

    /**
     * Set score for lane
     * @param playerId
     * @param score
     */
    public void setScore(int playerId, int score, int par) {
        scoreBoard[playerId][laneId] = score - par;
    }

    /**
     * Simple getter for player score on lane
     */
    public int getScore(int playerId) {
        return scoreBoard[playerId][laneId];
    }
    /**
     *
     * Set and get summary code for player
     * @param playerId
     * @return
     */
    public int setSummaryScore(int playerId) {
        scoreBoard[playerId][0] = 0;
        for(int i = 1; i < scoreBoard[playerId].length; i++) {
            scoreBoard[playerId][0] += scoreBoard[playerId][i];
        }

        return scoreBoard[playerId][0];
    }

    /**
     * Simple player name getter
     */
    public String getName(int i) {return nameOfPlayers[i];}

    /**
     * Get player id by player name
     */
    public int getPlayerId(String name) {
        int i = 0;
        for(String n : nameOfPlayers) {
            if(n.equals(name))
                return i;
            else
                i++;
        }
        return -1;
    }

    public void prevLane() {
        laneId--;
    }

    public int getNumberPlayer() {
        return nameOfPlayers.length;
    }

    public int getResult(int i) {
        return scoreBoard[i][0];
    }
}
