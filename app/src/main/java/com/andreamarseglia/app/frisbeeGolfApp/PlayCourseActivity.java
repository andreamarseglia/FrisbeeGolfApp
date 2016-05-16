package com.andreamarseglia.app.frisbeeGolfApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PlayCourseActivity extends Activity {
    Course course;
    GameCourse gameCourse;

    static final String STATE_COURSE = "course";
    static final String STATE_GAME = "game";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getIntent().getExtras();
        course = data.getParcelable("course");
        gameCourse = data.getParcelable("game");

        setContentView(R.layout.activity_play_course);
    }

    protected void onStart() {
        super.onStart();
        makeUI();
    }

    private void makeUI() {
        // Add 2 textviews for start of screen, which show what course and which hole in that course are currently at playing
        TextView textView = (TextView)findViewById(R.id.course_name);
        textView.setText(course.getName());
        textView = (TextView) findViewById(R.id.hole_information_label);
        textView.setText("#" + (gameCourse.getLaneId()) + " Par: " + course.getLanePar(gameCourse.getLaneId()));

        updateLaneInformation();

        addPlayerScoreFields();

        Button button = (Button)findViewById(R.id.next_lane_button);
        if(course.getNumberOfLanes() == gameCourse.getLaneId()) {
            button.setText("Lopeta");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveCourse();
                    showScore();
                }
            });
        }
        else {
            button.setText(R.string.next_lane);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToNextLane(view);
                }
            });
        }

        button = (Button)findViewById(R.id.prev_lane_button);
        if(gameCourse.getLaneId() > 1 ) {
            button.setEnabled(true);
        }
        else {
            button.setEnabled(false);
        }
    }

    private void showScore() {
        Intent intent = new Intent (this, ResultActivity.class);
        intent.putExtra("game", gameCourse);
        startActivity(intent);
        finish();
    }

    private void saveCourse() {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current state
        savedInstanceState.putParcelable(STATE_COURSE, course);
        savedInstanceState.putParcelable(STATE_GAME, gameCourse);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        course = savedInstanceState.getParcelable(STATE_COURSE);
        gameCourse = savedInstanceState.getParcelable(STATE_GAME);
    }


    /**
     * Dynamically adds play fields, which show what player score
      */
    private void addPlayerScoreFields() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.score_fields);

        if(linearLayout.getChildCount() > 0)
            linearLayout.removeAllViews();

        int playerId = 0;
        for(String name : gameCourse.nameOfPlayers) {
            LinearLayout playerLayout = new LinearLayout(this);
            playerLayout.setOrientation(LinearLayout.HORIZONTAL);
            playerLayout.setGravity(Gravity.CENTER_VERTICAL);

            // Player name
            TextView playerName = new TextView(this);
            playerName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3));
            playerName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
            playerName.setText(name);
            playerLayout.addView(playerName);

            // Player score
            TextView scoreView = new TextView(this);
            scoreView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            scoreView.setGravity(Gravity.CENTER);
            scoreView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
            int score = course.getLanePar(gameCourse.getLaneId()) + gameCourse.getScore(playerId);
            scoreView.setText(String.valueOf(score));
            updateScoreViewColor(scoreView, score);
            playerLayout.addView(scoreView);

            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));

            // Positive button
            ImageButton pos = new ImageButton(this);
            pos.setImageResource(R.drawable.positive);
            pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateScore(view, 1);
                }
            });
            pos.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            buttonLayout.addView(pos);

            // Negative button
            ImageButton neg = new ImageButton(this);
            neg.setImageResource(R.drawable.negative);
            neg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateScore(view, -1);
                }
            });
            neg.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            buttonLayout.addView(neg);

            playerLayout.addView(buttonLayout);

            // Summary score (minus par)
            TextView summary = new TextView(this);
            summary.setGravity(Gravity.CENTER);
            summary.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
            summary.setText(String.valueOf(gameCourse.setSummaryScore(playerId)));
            summary.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            playerLayout.addView(summary);


            playerId++;
            linearLayout.addView(playerLayout);
        }
    }

    /**
     * Update this course result for player by i
     */
    public void updateScore(View view, int i) {
        LinearLayout linearLayout = (LinearLayout)view.getParent().getParent();
        TextView scoreView = (TextView)linearLayout.getChildAt(1);

        // Getting old score and updating it by i
        int score = Integer.parseInt(scoreView.getText().toString()) + i;
        if(score > 0) {
            scoreView.setText(String.valueOf(score));
            updateScoreViewColor(scoreView, score);

            // Update player score for gamecourse object and summary field
            TextView name = (TextView) linearLayout.getChildAt(0);
            TextView summary = (TextView) linearLayout.getChildAt(3);
            int playerId = gameCourse.getPlayerId(name.getText().toString());
            gameCourse.setScore(playerId, score, course.getLanePar(gameCourse.getLaneId()));
            summary.setText(String.valueOf(gameCourse.setSummaryScore(playerId)));
        }
    }

    /**
     * Update score text color
     * @param scoreView object which is score
     * @param score player's result from lane
     */
    private void updateScoreViewColor(TextView scoreView, int score) {
        if(score < course.getLanePar(gameCourse.getLaneId()))
            scoreView.setTextColor(Color.GREEN);
        else if(score == course.getLanePar(gameCourse.getLaneId()))
            scoreView.setTextColor(Color.BLACK);
        else
            scoreView.setTextColor(Color.RED);
    }

    /**
     * Updates lane information (par) if it's -1
     */
    private void updateLaneInformation() {
        int lane = gameCourse.getLaneId();
        int par = course.getLanePar(lane);

        // If there isn't par ask that from user
        if(par == Course.INVALID_PAR) {
            final EditText input = new EditText(getApplicationContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setTextColor(Color.GREEN);

            AlertDialog.Builder builder = new AlertDialog.Builder(PlayCourseActivity.this);
            builder.setCancelable(false)
                    .setTitle("Puuttuva Par")
                    .setMessage("Anna radalle #" + lane + " Par:")
                    .setView(input)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            if(!input.getText().toString().equals(""))
                                updatePar(input.getText().toString());
                            else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(PlayCourseActivity.this);
                                builder1.setCancelable(false)
                                        .setTitle("Radalla pitää olla par")
                                        .setMessage("Syöte oli tyhjä!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(final DialogInterface alert, int id) {
                                                alert.cancel();
                                            }
                                        });
                                AlertDialog alert = builder1.create();
                                alert.show();
                            }
                            dialog.cancel();
                        }});
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     * Updates right bar number to text view if needed
     * @param par lane par
     */
    private void updatePar(String par) {
        int lane =  gameCourse.getLaneId();
        course.setLanePar(Integer.parseInt(par), (lane - 1));
        TextView textView = (TextView) findViewById(R.id.hole_information_label);
        textView.setText("#" + lane + " Par: " + par);


        int i = 0;
        for(String name : gameCourse.nameOfPlayers) {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.score_fields);
            LinearLayout playerLayout = (LinearLayout)linearLayout.getChildAt(i);
            textView = (TextView)playerLayout.getChildAt(1);
            textView.setText(par);

            textView = (TextView)playerLayout.getChildAt(3);
            textView.setText(String.valueOf(gameCourse.setSummaryScore(i)));
            i++;
        }
    }


    public void moveToNextLane(View view) {
        gameCourse.nextLane();
        makeUI();
    }

    public void moveToPrevLane(View view) {
        gameCourse.prevLane();
        makeUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action par if it is present.
        getMenuInflater().inflate(R.menu.play_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action par item clicks here. The action par will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
