package com.example.kristenvondrak.dartmouth.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristenvondrak.dartmouth.Parse.ParseAPI;
import com.example.kristenvondrak.dartmouth.Parse.User;
import com.example.kristenvondrak.dartmouth.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

    private EditText m_EmailEditText;
    private EditText m_PasswordEditText;
    private TextView m_LoginTextView;
    private TextView m_ResetTextView;
    private TextView m_SignupTextView;
    public Activity me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        me = this;

        initializeViews();
        initializeListeners();

    }

    private void initializeViews() {
        m_EmailEditText = (EditText) findViewById(R.id.email_edit_text);
        m_PasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        m_LoginTextView = (TextView) findViewById(R.id.login);
        m_ResetTextView = (TextView) findViewById(R.id.reset_password);
        m_SignupTextView = (TextView) findViewById(R.id.signup);
    }

    private void initializeListeners() {

        m_LoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object email = m_EmailEditText.getText();
                Object password = m_PasswordEditText.getText();

                if (email == null || !isValidEmail(email.toString())) {
                    Toast.makeText(me, "Invalid Email", Toast.LENGTH_SHORT).show();
                    // !

                } else if (password == null || !isValidPassword(password.toString())) {
                    Toast.makeText(me, "Invalid Password", Toast.LENGTH_SHORT).show();
                    // !

                }  else {
                    logInParseUser(email.toString(),password.toString());
                }

                // register user/validate
                // show red ! in edittext
            }
        });


        // signup
        m_SignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(me, SignupActivity.class);
                me.startActivity(intent);
            }
        });

        // reset

    }

    private boolean isValidEmail(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean isValidPassword(String password) {
        boolean lowercase = false;
        boolean uppercase = false;
        boolean nonalpha = false;
        if (password.trim().equals(""))
            return false;
        /*
        for (Character c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    lowercase = true;
                } else {
                    uppercase = true;
                }
            } else if ({

            }
        }*/
        return true;

    }

    public static void logOutParseUser() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
    }

    public static ParseUser getCurrentParseUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
        } else {
            // show the signup or login screen
        }
        return currentUser;
    }


    public void logInParseUser(final String email, final String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    me.finish();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    createNewParseUser(email, password);
                }
            }
        });
    }


    public void createNewParseUser(String email, String password) {
        User user = new User();
        // Set core properties
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("Login", "User signup success");
                    me.finish();
                } else {
                    Log.d("Login", "User signup fail " + e.toString());
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
