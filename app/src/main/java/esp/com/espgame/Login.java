package esp.com.espgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    /**
     * This is the Launcher activity of the app
     * This activity takes input the username from the user
     * and starts the main activity
     */

    //Sring for the username
    public static String Username;
    //Reference to the edittext
    public EditText Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Name = (EditText)findViewById(R.id.NameText);
    }
    public void EnterClick(View view){
        //Update username
        Username = Name.getText().toString();
        //If Username is empty send a toast
        if (Username.matches("")) {
            Toast.makeText(this, "Whack! We won't bite ! Please Enter A Name..", Toast.LENGTH_SHORT).show();
            return;
        }
        //Get the intent to main activity
        Intent intent = new Intent(this,MainActivity.class);
        //Start the intent
        this.startActivity(intent);
    }
}
