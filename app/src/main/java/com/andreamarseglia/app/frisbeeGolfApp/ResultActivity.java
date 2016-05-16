package com.andreamarseglia.app.frisbeeGolfApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ResultActivity extends Activity {
    GameCourse gameCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        gameCourse = data.getParcelable("game");
        setContentView(R.layout.activity_result);
    }

    protected void onStart() {
        super.onStart();

        String [][] result = new String[gameCourse.getNumberPlayer()][2];
        int i = 0;
        for(String name : gameCourse.nameOfPlayers) {
            result[i][0] = name;
            result[i][1] = String.valueOf(gameCourse.getResult(i));
            i++;
        }

        result = sort(result);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.result_layout);

        if(linearLayout.getChildCount() > 0)
            linearLayout.removeAllViews();

        for(i = 0; i < result.length; i++) {
            LinearLayout playerField = new LinearLayout(this);
            playerField.setOrientation(LinearLayout.HORIZONTAL);

            TextView place = new TextView(this);
            place.setText(String.valueOf(i+1));
            place.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
            place.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            TextView name = new TextView(this);
            name.setText(result[i][0]);
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
            name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
            playerField.addView(name);

            TextView score = new TextView(this);
            score.setText(result[i][1]);
            score.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
            score.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            playerField.addView(score);

            linearLayout.addView(playerField);
        }
    }

    private String[][] sort(String[][] result) {
        int tmpScore;
        String tmpName;
        int place;
        int littleScore;
        String littleName;

        for(int i = 0; i < result.length; i++) {
            place = i;
            littleName = result[i][0];
            littleScore = Integer.parseInt(result[i][1]);
            for(int j = i+1; j < result.length; j++) {
                if(littleScore > Integer.parseInt(result[j][1])) {
                    place = j;
                    littleScore = Integer.parseInt(result[j][1]);
                    littleName = result[j][0];
                }
            }
            if(place != i) {
                tmpName = result[i][0];
                tmpScore = Integer.parseInt(result[i][1]);
                result[i][0] = littleName;
                result[i][1] = String.valueOf(littleScore);
                result[place][0] = tmpName;
                result[place][1] = String.valueOf(tmpScore);
            }
        }
        return result;
    }

    public void startMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
