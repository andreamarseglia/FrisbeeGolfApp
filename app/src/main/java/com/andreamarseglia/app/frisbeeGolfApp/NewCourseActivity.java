package com.andreamarseglia.app.frisbeeGolfApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class NewCourseActivity extends Activity {
    CourseList courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        courseList = (CourseList) data.getParcelable("courselist");
        setContentView(R.layout.activity_new_course);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_course, menu);
        return true;
    }

    /**
     * Basic button method which starts newly added course
     */
    public void createNewCourse(View view) {
        String courseName = "";
        int numberOfLanes = -1;

        EditText editText = (EditText)findViewById(R.id.new_course_name);
        if(!editText.getText().toString().equals(""))
            courseName = editText.getText().toString();
        editText = (EditText)findViewById(R.id.new_course_holes);
        if(!editText.getText().toString().equals(""))
            numberOfLanes = Integer.parseInt(editText.getText().toString());

        if(courseName.equals("") || numberOfLanes < 1) {
            // Opens AlertDialog for invalid input
            AlertDialog.Builder builder = new AlertDialog.Builder(NewCourseActivity.this);
            builder.setMessage(numberOfLanes < 1 ? "Virheellinen lukumäärä reikiä!" : "Tyhjä radan nimi!")
                    .setTitle("Virheellinen syöte")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            // Makes new course and add it to list
            Course newCourse = new Course(courseName, numberOfLanes);
            courseList.addNewCourse(newCourse);

            // Updates intent and move to next Activity
            Intent intent = new Intent(this, AddPlayersActivity.class);
            intent.putExtra("course", newCourse);
            startActivity(intent);
            finish();
        }
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
