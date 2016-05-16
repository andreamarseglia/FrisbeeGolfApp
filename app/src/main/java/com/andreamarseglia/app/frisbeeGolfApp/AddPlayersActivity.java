package com.andreamarseglia.app.frisbeeGolfApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class AddPlayersActivity extends Activity {
    Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        course = (Course)data.getParcelable("course");
        setContentView(R.layout.activity_add_players);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_players, menu);
        return true;
    }

    /**
     * Dynamically adds players input data fields to view
     */
    public void addPlayers(View view) {

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.player_fields);

        // Let's clear layout
        if(linearLayout.getChildCount() > 0)
            linearLayout.removeAllViews();

        final int numberOfPlayers;
        EditText editText = (EditText) findViewById(R.id.amount_of_players);

        if(!editText.getText().toString().equals(""))
            numberOfPlayers = Integer.parseInt(editText.getText().toString());
        else
            numberOfPlayers = -1;

        if(numberOfPlayers < 1) {

                // Opens AlertDialog for invalid input
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPlayersActivity.this);
                builder.setMessage("Pelaajia pitää olla vähintään yksi!")
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
            String hint = "Pelaaja nimi";

            // Create dynamically player name fields
            for (int i = 0; i < numberOfPlayers; i++) {

                EditText playerName = new EditText(this);
                playerName.setTextAppearance(this, R.style.PlayerNameInput);
                playerName.setHint(hint);
                playerName.setId(i);
                linearLayout.addView(playerName);
            }

            // Create button for starting course
            Button addPlayers = new Button(this);
            addPlayers.setText("Aloita kierros");
            addPlayers.setGravity(Gravity.CENTER_HORIZONTAL);
            addPlayers.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                        startGame(numberOfPlayers, linearLayout);
                    }
            });

            linearLayout.addView(addPlayers);
        }

    }

    /**
     * Fetch and checking if input is valid, also start new activity if all is right
     * @param numberOfPlayers user input
     * @param linearLayout view object which include all input fields
     */
    private void startGame(int numberOfPlayers, LinearLayout linearLayout) {
        String[] namesOfPlayers = new String[numberOfPlayers];
        boolean emptyField = false;
        for (int i = 0; i < numberOfPlayers; i++) {
            EditText name = (EditText) linearLayout.getChildAt(i);
            String pn = name.getText().toString();
            if (pn.equals("")) {
                // Opens AlertDialog for invalid input
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPlayersActivity.this);
                builder.setMessage("Pelaajan nimi ei voi olla tyhjä!")
                        .setTitle("Virheellinen syöte")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                emptyField = true;
            } else
                namesOfPlayers[i] = pn;
        }

        if(!emptyField) {
            GameCourse newGame = new GameCourse(namesOfPlayers, course.getNumberOfLanes()+1);
            Intent intent = new Intent(this, PlayCourseActivity.class);
            intent.putExtra("course", course);
            intent.putExtra("game", newGame);
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
