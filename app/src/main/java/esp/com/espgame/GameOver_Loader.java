package esp.com.espgame;

/**
 * Created by hp on 11-01-2020.
 */


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static esp.com.espgame.Login.Username;
import static esp.com.espgame.MainActivity.selectedIndices;

public class GameOver_Loader extends AsyncTaskLoader<Integer> {
    /**
     * This class is the loader (Async Task) of the GameOver Activity
     * This class fetches and continuously updates the user's score
     * while he is on the GameOver activity
     */
    //return type of loader is set to Integer
    //To get the score count
    private Integer count;
    //Initialize ArrayList to store firebase values
    private ArrayList<HashMap<String,String>> firebasevalues;
    //Pointer to the values of which score is calculated
    public int ptr = 0;



    public GameOver_Loader(Context context, ArrayList<HashMap<String,String>> firebasevalues) {
        //Loader's Constructor call
        super(context);
        //Update the firebase values
        this.firebasevalues = firebasevalues;
        count = Integer.valueOf(0);
        ptr = 0;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public Integer loadInBackground() {
        try {
            //Iterate through all the answers of all the users
            while (ptr < 5) {
                //Check internet connectivity
                if (isInternetAvailable()) {
                    //Get the answers to the current questions in a temp Hashmap
                    HashMap<String,String> serverCards = firebasevalues.get(selectedIndices[ptr]);
                    //Convert that Hashmap's values into a Hashset
                    HashSet<String> serverCardsSet = new HashSet<>(serverCards.values());
                    //Check if the temp hashmap's size is less than 2
                    //If it is less than 2 than only the user has answered that question
                    //No need to check for the further questions
                    if ((serverCards.size()) <= 2)
                        break;
                    //Check if the size of the temp Hashset is 2
                    //If it is true then all the answers answered by different users are same
                    else if (serverCardsSet.size() == 2) {
                        //Increment score count
                        count = new Integer(count.intValue() + 1);
                        ptr += 1;
                    } else {
                        //If the size of the temp hashset is not same, then
                        //do not increment the count
                        ptr += 1;
                    }
                }
            }
        } catch (Exception e) {

        }
        return count;
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
