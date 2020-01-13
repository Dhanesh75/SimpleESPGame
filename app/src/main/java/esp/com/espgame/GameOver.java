package esp.com.espgame;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static esp.com.espgame.Login.Username;
import static esp.com.espgame.MainActivity.gameOver;

public class GameOver extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Integer>{

    /**
     * This Activity is called when user has finished answering all his questions
     */
    //ArrayList to store values of the current firebase
    public ArrayList<HashMap<String,String>> firebaseValues;
    //Loader is needed to fetch the score from the background
    //Even the user has played his moves
    //We still need to a loader to update the score when other users update their answers
    final LoaderManager loaderManager = getLoaderManager();
    //Reference to GameOverText
    public TextView GameOverText;
    //Reference to the database needed to connect to the firebase
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        //Initialize GameOverText
        GameOverText = (TextView)findViewById(R.id.gameOverText);
        //Update Count
        GameOverText.setText("Game Over \n Your Score is: " + MainActivity.scoreCount);
        //Loader needed to keep updating the count in background
        //Initialize loader
        loaderManager.initLoader(2, null, this);
        //Update GameOver as true
        gameOver = true;
        //Get Firebase's reference and apply listener to it
        //Invoke loader whenever firebase values are updated
        databaseReference = FirebaseDatabase.getInstance().getReference("Selected_Cards");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseValues = (ArrayList<HashMap<String,String>>) dataSnapshot.getValue();
                loaderManager.restartLoader(2,null,GameOver.this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tag", "Failed to read value.", error.toException());
            }
        });
    }
    @Override
    public void onBackPressed() {
        //Kill app when back is pressed on this activity
        this.finishAffinity();
    }

    public void NewGame(View view){
        /**
         * This function is called when the user presses the button
         * to restart the game
         */
        //Clear all previous the entries of the user
        MainActivity.clearUserEntries();
        //Get Intent to Login Activity
        Intent intent = new Intent(this, Login.class);
        //Start Login Activity
        this.startActivity(intent);
        //Clear the intent stack
        this.finishAffinity();
    }

    @Override
    public Loader<Integer> onCreateLoader(int i, Bundle bundle) {
        return new GameOver_Loader(this,firebaseValues);
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer integer) {
        //Update the scorevalue when loader is finished loading
        GameOverText.setText("Game Over \n Your Score is: " + integer.intValue());
    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {

    }

}
