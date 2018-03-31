/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private FastDictionary simpleDictionary;
    private String wordFragment;
    private TextView text;
    private TextView label;
    Button btnch,btnrest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            simpleDictionary = new FastDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        btnch = (Button) findViewById(R.id.button_challenge);
        btnrest = (Button) findViewById(R.id.button_restart);
        text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        wordFragment = text.getText().toString();
        label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {

        // Do computer turn stuff then make it the user's turn again
        String word = text.getText().toString();
        if( word.length() >= 4 && simpleDictionary.isWord(word)){
            label.setText("Alas..You Lost...WORD FOUND");
            newGame();
        }
        String possibleWord = simpleDictionary.getAnyWordStartingWith(word);
        if(possibleWord == null){
            label.setVisibility(View.INVISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    label.setVisibility(View.VISIBLE);
                    label.setText("You Lost..no such word.");
                }
            }, 700);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    newGame();
                }
            }, 2000);
        }
        else {
            if(wordFragment.equals(possibleWord)){
                label.setText("Alas..You Lost...Word found");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newGame();
                    }
                }, 2000);
            }
            else {
                wordFragment = wordFragment + possibleWord.substring(wordFragment.length(), wordFragment.length()+1);
                text.setText(wordFragment);
                userTurn = true;
                label.setText(USER_TURN);

            }
        }

    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getUnicodeChar() >= 97 && event.getUnicodeChar() <= 122 ){
            wordFragment = wordFragment + (char)event.getUnicodeChar();
            text.setText(wordFragment);
            userTurn = false;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();

                }
            }, 1000);
        }
        return super.onKeyUp(keyCode, event);
    }
    public void onChallenge(View view){
        String word = text.getText().toString();
        if( word.length() >= 4 && simpleDictionary.isWord(word)){
            label.setText("Congo...You Won");
            newGame();
        }
        else {
            label.setText("You Lost..no such word.");
            newGame();
        }
    }
    public void newGame(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onStart(null);
            }
        }, 2000);
    }
}
