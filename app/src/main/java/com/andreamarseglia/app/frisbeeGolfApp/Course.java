package com.andreamarseglia.app.frisbeeGolfApp;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joni on 3.9.2014.
 */
public class Course implements Parcelable {
    protected static final int INVALID_PAR = -1;
    private int[] laneList;
    private int numberOfLanes;
    private String name;

    public Course(String [] courseData) {
        name = courseData[0];
        numberOfLanes = Integer.parseInt(courseData[1]);

        laneList = new int[numberOfLanes];

        for(int i = 0; i < numberOfLanes; i++)
        {
            laneList[i] = Integer.valueOf(courseData[i+2]);
        }
    }

    public Course(String name, int numberOfLanes) {
        this.name = name;
        this.numberOfLanes = numberOfLanes;

        laneList = new int[numberOfLanes];

        for(int i = 0; i < numberOfLanes; i++)
        {
            laneList[i] = INVALID_PAR;
        }
    }

    public Course(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);

        this.name = data[0];
        this.numberOfLanes = Integer.parseInt(data[1]);

        laneList = new int[numberOfLanes];

        String tmp[] = data[2].split("\r");
        for(int i = 0; i < numberOfLanes; i++)
        {
            laneList[i] = Integer.parseInt(tmp[i]);
        }


    }

    public void setNumberOfLanes(int numberOfLanes) {
        this.numberOfLanes = numberOfLanes;
    }
    public void setLanePar(int lanePar, int laneId) {
        laneList[laneId] = lanePar;
    }

    int getNumberOfLanes() { return numberOfLanes; }
    String getName() { return  name; }

    /**
     * Get lane par
     * @param i lane number
     * @return lane's par if lane exist if not return -1
     */
    int getLanePar (int i) {
        return laneList[i-1];
    }

    int[] getPars () {
        return laneList;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(name);
        result.append("\r");
        result.append(numberOfLanes);
        result.append("\r");
        for (int j = 0; j < laneList.length; j++) {
            result.append( laneList[j] );
            result.append( "\r" );
        }

        return result.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        StringBuffer tmp = new StringBuffer();
        for (int j = 0; j < laneList.length; j++) {
            tmp.append( laneList[j] );
            tmp.append( "\r" );
        }

        parcel.writeStringArray(new String[] {
                this.name,
                String.valueOf(this.numberOfLanes),
                tmp.toString()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
}
