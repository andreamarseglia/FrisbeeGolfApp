package com.andreamarseglia.app.frisbeeGolfApp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joni on 3.9.2014.
 */
public class CourseList implements Parcelable {
    private int numberOfCourses;
    private List<Course> listOfCourses;

    public CourseList(Parcel in) {
        String [] data = new String[2];

        in.readStringArray(data);

        this.numberOfCourses = Integer.parseInt(data[0]);

        listOfCourses = new ArrayList<Course>();

        // Split string to array of Course data
        String tmp[] = data[1].split("\n");
        for(int i = 0; i < numberOfCourses; i++) {
            // Add course with Course constructor by giving course data as parameter.
            listOfCourses.add(new Course(tmp[i].split("-")));
        }
    }

    /**
     * Creates empty courselist
     */
    public CourseList() {
        this.numberOfCourses = 0;
        listOfCourses = new ArrayList<Course>();
    }

    // Get methods for courselist class
    protected int getNumberOfCourses() { return  numberOfCourses; }
    protected String getCourseName(int i) {
        return listOfCourses.get(i).getName();
   }

    /**
     * Method which reads input file and gets all saved courses
     */
    private String[] readFile(FileInputStream fis) {
        try {
            String temp="";
            int c;

            while( (c = fis.read()) != -1){
                temp = temp + Character.toString((char)c);

                //string temp contains all the data of the file.
                fis.close();
            }
            return temp.split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add new course to end of list
     * @param newCourse is course what is added
     */
    public void addNewCourse(Course newCourse) {
        numberOfCourses++;
        listOfCourses.add(newCourse);
    }

    public Course getCourse(int pos) {
        if(pos <= numberOfCourses && pos > 0)
            return listOfCourses.get(pos-1);
        else
            return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        StringBuffer tmp = new StringBuffer();
        for (int j = 0; j < numberOfCourses; j++) {
            tmp.append(listOfCourses.get(j).toString());
            tmp.append("\n");
        }

        parcel.writeStringArray(new String[] {
                String.valueOf(this.numberOfCourses),
                tmp.toString()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CourseList createFromParcel(Parcel in) {
            return new CourseList (in);
        }

        public CourseList[] newArray(int size) {
            return new CourseList[size];
        }
    };
}
