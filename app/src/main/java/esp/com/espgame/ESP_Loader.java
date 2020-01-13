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

import static esp.com.espgame.MainActivity.databaseReference;
import static esp.com.espgame.MainActivity.doUpdate;
import static esp.com.espgame.MainActivity.i_;
import static esp.com.espgame.MainActivity.selectedIndices;
import static esp.com.espgame.MainActivity.selectedSecondary;
import static esp.com.espgame.Login.Username;

public class ESP_Loader extends AsyncTaskLoader<Integer> {
    /**
     * This is the Async Task Class which contains the loader functions
     * for the Main activity which loads the data from background asynchronously
     */
    
    //TAG to print the logs corresponding to this class
    private String TAG = ESP_Loader.class.getName();
    //return type of the loader is set to Integer
    private Integer count;
    //ArrayList to get the firebase values
    private ArrayList<HashMap<String,String>> firebasevalues;
    //Pointer to the values of which score is calculated
    public static int ptr = 0;
    //Pointer to the values which are being pushed to the firebase
    public static int j = 0;


    public ESP_Loader(Context context, ArrayList<HashMap<String,String>> firebasevalues) {
        /**
         * This is the constructor of ESP_Loader class
         * We initialize the ArrayList of the firebase data
         */
        super(context);
        this.firebasevalues = firebasevalues;
        count = Integer.valueOf(0);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }



    private void updateData(){
        /**
         * This function pushes data to the firebase
         */
        try {
            //Get the current data of the firebase in a temp variable
            HashMap<String,String> serverCards_ = firebasevalues.get(selectedIndices[i_]);
            //Check if Internet Connectivity is available
            if (isInternetAvailable()) {
                //Iterate and push all the values that are not updated in the firebase
                while (j <= i_) {
                    //Put the current value in the temp variable
                    serverCards_.put(Username,selectedSecondary[j]);
                    Log.i(TAG,"Data Pushed to the firebase");
                    //Push the data to the firebase
                    databaseReference.child(selectedIndices[i_] + "").setValue(serverCards_);
                    j += 1;
                }
            }
        }catch (Exception e){
            Log.w("Tag","Error while updating " + e.toString());
        }
    }

    @Override
    public Integer loadInBackground() {
        /**
         * This function is called whenever the loader is initialized or restarted
         * from the Main Activity.
         * This function is responsible to call the function that pushes the values
         * to firebase and update the score count.
         */
        //Check if the data is to be updated
        if(doUpdate) {
            updateData();
            //doUpdate = false;
        }
        try {
            /**
             * This is the code to update the score count
             */
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
                    Log.i("TAG",serverCardsSet + "");
                    if ((serverCards.size()) <= 2) {
                        break;
                    }
                    //Check if the size of the temp Hashset is 2
                    //If it is true then all the answers answered by different users are same
                    else if (serverCardsSet.size() == 2 ) {
                        //Increment score count
                        MainActivity.scoreCount += 1;
                        ptr += 1;
                    } else {
                        //If the size of the temp hashset is not same, then
                        //do not increment the count
                        ptr += 1;
                    }
                }
            }
        } catch (Exception e) {
            Log.i("Tag","Error While Counting " + e.toString());
        }
        return count;
    }
    public boolean isInternetAvailable() {
        /**
         * This function checks the availibility of internet connectivity
         * by trying to connect to "www.google.com"
         */
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
