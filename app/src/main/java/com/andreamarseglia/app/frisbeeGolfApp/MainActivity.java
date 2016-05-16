package com.andreamarseglia.app.frisbeeGolfApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private CourseList courseList;
    private Spinner courseSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //upDateCourseSpinner();
    }

    private void upDateCourseSpinner() {
        // Add and fills spinner
        courseSpinner = (Spinner)findViewById(R.id.choose_course_spinner);


        List<String> list = new ArrayList<String>();

        // If there are courses on file add them to list if not add info
        if(courseList.getNumberOfCourses() > 0) {
            courseSpinner.setEnabled(true);
            list.add("Valitse rata");
            for (int i = 0; i < courseList.getNumberOfCourses(); i++) {
                list.add(courseList.getCourseName(i));
            }
        }
        else {
            list.add("Ei yhtään rataa, lisää uusi");
            courseSpinner.setEnabled(false);
        }

        // Fill spinner from list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
    }

    /**
     * Moves to add players activity
     */
    public void chooseCourse(View view) {
        Course course = courseList.getCourse(courseSpinner.getSelectedItemPosition());
        if(course != null) {
            // Updates intent and move to next Activity
            Intent intent = new Intent(this, AddPlayersActivity.class);
            intent.putExtra("course", course);
            startActivity(intent);
            finish();
        }
        else {
            // Opens AlertDialog for invalid spinner input
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Valitse rata.").setTitle("Virheellinen syöte");
            builder.setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     * Start add new course activity
     */
    public void addNewCourse(View view) {
        Intent intent = new Intent (this, NewCourseActivity.class);
        intent.putExtra("courselist", courseList);
        startActivity(intent);
        finish();
    }

    public void deleteCourse(View view) {
        Course course = courseList.getCourse(courseSpinner.getSelectedItemPosition());
        final String courseName = course.getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Haluatko varmasti poistaa tämän radan: " + courseName).setTitle("Oletko varma");
        builder.setCancelable(false)
                .setPositiveButton("Kyllä", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        upDateCourseSpinner();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("En", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_acvity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
