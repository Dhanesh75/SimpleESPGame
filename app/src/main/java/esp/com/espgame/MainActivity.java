package esp.com.espgame;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.registerable.connection.Connectable;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static esp.com.espgame.Login.Username;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Integer> {

    /**
     * This is the main activity of the app which handles the
     * questioning and answering of the user.
     */
    private String TAG = MainActivity.class.getName();
    //Pointer to the current question
    public static int i;
    //Array containing all of the primary questions
    public String primary[];
    //Array containing the indices of the randomly selected questions
    public static int selectedIndices[];
    //Score count to maintain the score
    public static int scoreCount = 0;
    //Array containing the selected questions out of the total questions
    public String selectedPrimary[];
    //2D Array which contains 4 secondary questions for each primary questions
    public String secondary[][];
    //Array containing the choices entered by the user
    public static String selectedSecondary[];
    //Reference to the primary question image
    public TextView PrimaryImage;
    //Reference to the secondary questions
    public TextView SecondaryA;
    public TextView SecondaryB;
    public TextView SecondaryC;
    public TextView SecondaryD;
    //Reference of the server database
    public static DatabaseReference databaseReference;
    //ArrayList to fetch the data from the server
    public static ArrayList<HashMap<String,String>> firebaseValues;
    //Reference to the score counter.
    public TextView ScoreCountText;
    //Merlin object to listen for internet connectivity changes
    private Merlin merlin;
    //Load manager for calling async tasks
    final LoaderManager loaderManager = getLoaderManager();
    //Boolean to set update from client to server
    public static boolean doUpdate = false;
    //Pointer to current question for the loader class (Async Task)
    public static int i_;
    //Boolean to check if any of the secondary questions is selected
    private boolean button_pressed = false;
    //Boolean to check if the game is over
    public static boolean gameOver = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * This function is part of the android activity lifecycle
         * and is called when the activity is created for the first time
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialization of the variables
        PrimaryImage = (TextView)findViewById(R.id.primary_image);
        merlin = new Merlin.Builder().withConnectableCallbacks().build(this);
        SecondaryA = (TextView)findViewById(R.id.secondary_a);
        SecondaryB = (TextView)findViewById(R.id.secondary_b);
        SecondaryC = (TextView)findViewById(R.id.secondary_c);
        SecondaryD = (TextView)findViewById(R.id.secondary_d);
        ScoreCountText = (TextView)findViewById(R.id.scoreCount);
        databaseReference = FirebaseDatabase.getInstance().getReference("Selected_Cards");
        i = 0; ESP_Loader.j = 0; ESP_Loader.ptr = 0;i_ = 0;scoreCount = 0;
        loaderManager.initLoader(1, null, this);
        primary = new String[15];
        selectedPrimary = new String[5];
        secondary = new String[15][4];
        selectedSecondary = new String[5];
        selectedIndices = new int[5];
        gameOver = false;
        doUpdate = false;
        //Random Generator for random indices
        Random rand = new Random();
        for(int i = 0 ; i < 15 ; i++) {
            //Filling primary questions with the numbers from [0,15)
            primary[i] = i + "";
            //4 Choices for each primary question
            secondary[i][0] = "A -" + i + " - "+ 0;
            secondary[i][1] = "A - " + i  + " - "+ 1;
            secondary[i][2] = "A - " + i + " - "+ 2;
            secondary[i][3] = "A - " + i + " - "+ 3;
            if (i < 5){
                //Make all initial choices as -1
                selectedSecondary[i] = -1 + "";
            }
        }

        //Hashset to get Unique Indices
        HashSet<Integer> Temp_SelectedIndices = new HashSet<>();
        while(Temp_SelectedIndices.size() < 5){
            int selectedIndex = rand.nextInt(15);
            Temp_SelectedIndices.add(Integer.valueOf(selectedIndex));
        }

        //Transfer random indices from hashset to selectedIndices[]
        Iterator<Integer> integerIterator = Temp_SelectedIndices.iterator();
        int ind = 0;
        while (integerIterator.hasNext()){
            selectedIndices[ind] = integerIterator.next();
            //Initialize selected primary from the selectedIndices
            selectedPrimary[ind] = primary[selectedIndices[ind]];
            ind++;
        }

        //Initialize the first question and the secondary images on the screen
        PrimaryImage.setText(selectedPrimary[0]);
        SecondaryA.setText(secondary[selectedIndices[0]][0]);
        SecondaryB.setText(secondary[selectedIndices[0]][1]);
        SecondaryC.setText(secondary[selectedIndices[0]][2]);
        SecondaryD.setText(secondary[selectedIndices[0]][3]);

        // Implement a listener which triggers when the firebase is updated
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /**
                 * This method is called once with the initial value and again
                 * whenever data at the specified location is updated.
                 */
                firebaseValues = (ArrayList<HashMap<String,String>>) dataSnapshot.getValue();
                Log.i(TAG,"Firebase data updated");

                //Call loader when game is not over and no button is pressed
                //Secondary values are to be pushed to the server when any button is pressed
                if(!button_pressed && !gameOver) {
                    loaderManager.restartLoader(1, null, MainActivity.this);
                }else
                    button_pressed = false;

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });

        //Set listener to detect network change
        //Call the loader to update scores whenever user comes back online
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                loaderManager.restartLoader(1,null,MainActivity.this);
            }
        });
    }

    public void pressA(View view){
        /**
         * This function is called when the user selects
         * the 1st secondary image of the current question
         */
        if (i < 5) {
            //Update selectedSecondary
            selectedSecondary[i] = secondary[selectedIndices[i]][0];
            //Set doUpdate to true to push the value to the firebase
            doUpdate = true;
            //Set button pressed to true
            button_pressed = true;
            //Set the question pointer for the loader and then update
            //Main thread's question pointer
            i_ = i;
            //Call loader to update the values asynchronously
            loaderManager.restartLoader(1,null,this);
            //Increment the current question pointer to fetch next question
            i += 1;
            // Call Gameover activity once 5 questions are answered
            if(i == 5) {
                Intent intent = new Intent(this, GameOver.class);
                this.startActivity(intent);
            }
            if (i < 5) {
                //Update the primary image and secondary images
                SecondaryA.setText(secondary[selectedIndices[i]][0]);
                SecondaryB.setText(secondary[selectedIndices[i]][1]);
                SecondaryC.setText(secondary[selectedIndices[i]][2]);
                SecondaryD.setText(secondary[selectedIndices[i]][3]);
                PrimaryImage.setText(selectedPrimary[i]);
            }
        }
    }
    public void pressB(View view){
        /**
         * This function is called when the user selects
         * the 2nd secondary image of the current question
         */
        if (i < 5) {
            //Update selectedSecondary
            selectedSecondary[i] = secondary[selectedIndices[i]][1];
            //Set doUpdate to true to push the value to the firebase
            doUpdate = true;
            //Set button pressed to true
            button_pressed = true;
            //Set the question pointer for the loader and then update
            //Main thread's question pointer
            i_ = i;
            //Call loader to update the values asynchronously
            loaderManager.restartLoader(1,null,this);
            //Increment the current question pointer to fetch next question
            i += 1;
            // Call Gameover activity once 5 questions are answered
            if(i == 5) {
                Intent intent = new Intent(this, GameOver.class);
                this.startActivity(intent);
            }
            if(i < 5) {
                //Update the primary image and secondary images
                SecondaryA.setText(secondary[selectedIndices[i]][0]);
                SecondaryB.setText(secondary[selectedIndices[i]][1]);
                SecondaryC.setText(secondary[selectedIndices[i]][2]);
                SecondaryD.setText(secondary[selectedIndices[i]][3]);
                PrimaryImage.setText(selectedPrimary[i]);
            }
        }
    }
    public void pressC(View view){
        /**
         * This function is called when the user selects
         * the 3rd secondary image of the current question
         */
        if (i < 5) {
            //Update selectedSecondary
            selectedSecondary[i] = secondary[selectedIndices[i]][2];
            //Set doUpdate to true to push the value to the firebase
            doUpdate = true;
            //Set button pressed to true
            button_pressed = true;
            //Set the question pointer for the loader and then update
            //Main thread's question pointer
            i_ = i;
            //Call loader to update the values asynchronously
            loaderManager.restartLoader(1,null,this);
            //Increment the current question pointer to fetch next question
            i += 1;
            // Call Gameover activity once 5 questions are answered
            if(i == 5) {
                Intent intent = new Intent(this, GameOver.class);
                this.startActivity(intent);
            }
            if (i < 5) {
                //Update the primary image and secondary images
                SecondaryA.setText(secondary[selectedIndices[i]][0]);
                SecondaryB.setText(secondary[selectedIndices[i]][1]);
                SecondaryC.setText(secondary[selectedIndices[i]][2]);
                SecondaryD.setText(secondary[selectedIndices[i]][3]);
                PrimaryImage.setText(selectedPrimary[i]);
            }
        }
    }
    public void pressD(View view){
        /**
         * This function is called when the user selects
         * the 4th secondary image of the current question
         */
       if (i < 5) {
           //Update selectedSecondary
           selectedSecondary[i] = secondary[selectedIndices[i]][3];
           //Set doUpdate to true to push the value to the firebase
           doUpdate = true;
           //Set button pressed to true
           button_pressed = true;
           //Set the question pointer for the loader and then update
           //Main thread's question pointer
           i_ = i;
           //Call loader to update the values asynchronously
           loaderManager.restartLoader(1,null,this);
           // Call Gameover activity once 5 questions are answered
           i += 1;
           //Update the primary image and secondary images
           if(i == 5) {
               Intent intent = new Intent(this, GameOver.class);
               this.startActivity(intent);
           }
           if (i < 5) {
               //Update the primary image and secondary images
               SecondaryA.setText(secondary[selectedIndices[i]][0]);
               SecondaryB.setText(secondary[selectedIndices[i]][1]);
               SecondaryC.setText(secondary[selectedIndices[i]][2]);
               SecondaryD.setText(secondary[selectedIndices[i]][3]);
               PrimaryImage.setText(selectedPrimary[i]);
           }
       }
    }

    public static void clearUserEntries(){
        /**
         * This function is called when user leavers the program
         * This function clears all the entries made by the user from the firebase
         * This function is called when user presses the log out button, back button or
         * when the game is finished and a new game is to be started
         */
        //Pointer to iterate through the Arraylist
        int ptr = 0;
        //Arraylist to contain the updated values of the firebase after deletion
        ArrayList<HashMap<String,String>> UpdatedValues = new ArrayList<>();

        //Iteration through the ArrayList
        while (ptr < 15) {
            //Delete the entries of the user from all the answered questions
            HashMap<String, String> serverCards = firebaseValues.get(ptr);
            serverCards.remove(Username);
            UpdatedValues.add(serverCards);
            ptr++;
        }
        //Update firebase
        databaseReference.setValue(UpdatedValues);
    }
    public void LogOut(View view){
        /**
         * This function is attached to the "Log Out" Button
         * This button contains the text "Get Me outta Here"
         * This function clears the answered questions by the user
         * and logs the user out
         */
        //Call clear entries
        clearUserEntries();
        //Get an intent to switch to the Login activity
        Intent intent = new Intent(this,Login.class);
        //Switch to the Login Activity
        this.startActivity(intent);
        //Kill current intent
        this.finishAffinity();
    }
    @Override
    public void onBackPressed() {
        /**
         * This function is called when the user presses the back key
         * This function clears the user entries and exits the app
         */
        //clear the user entries
        clearUserEntries();
        //Kill the activity stack and exit the app
        this.finishAffinity();
    }
    @Override
    public Loader<Integer> onCreateLoader(int i, Bundle bundle) {
        /**
         * This function is called once when loader is initialized
         * and everytime when loader is restarted
         * This function starts an async task in the background to
         * Update the values to the firebase and get the values from
         * firebase to update score count
         */
        return new ESP_Loader(this,firebaseValues);
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer integer) {
        /**
         * This function is called when the processing of async task is finished
         */
        //Update the score text on the UI
        ScoreCountText.setText("Score : " + scoreCount+"");
        Log.i(TAG,"Finished Loading");
    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {
        /**
         * This function is called when Loader resets
         * due to bad function calls and other errors
         */
        Log.i(TAG,"Loader Reset");
    }

    @Override
    protected void onResume() {
        /**
         * This function is a part of android activity lifecycle
         * and is called when user resumes the activity from the
         * background
         */
        super.onResume();
        //Bind merlin to listen to network changes
        merlin.bind();
    }

    @Override
    protected void onPause() {
        /**
         * This function is a part of android activity lifecycle
         * and is called when user pauses the activity from the
         * foreground
         */
        //Unbind merlin when activity is pushed to background
        merlin.unbind();
        super.onPause();
    }


}
