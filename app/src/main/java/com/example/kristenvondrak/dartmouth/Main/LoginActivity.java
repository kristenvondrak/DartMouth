package com.example.kristenvondrak.dartmouth.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    protected EditText m_EmailEditText;
    protected EditText m_PasswordEditText;
    protected ImageView m_EmailError;
    protected ImageView m_PasswordError;
    protected ProgressBar m_ProgressSpinner;

    private TextView m_LoginTextView;
    private TextView m_ResetTextView;
    private TextView m_SignupTextView;

    public Activity m_Me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        m_Me = this;

        initializeViews();
        initializeListeners();
    }

    private void initializeViews() {
        m_EmailEditText = (EditText) findViewById(R.id.email_edit_text);
        m_PasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        m_EmailError = (ImageView) findViewById(R.id.email_error_icon);
        m_PasswordError = (ImageView) findViewById(R.id.password_error_icon);
        m_LoginTextView = (TextView) findViewById(R.id.login);
        m_ResetTextView = (TextView) findViewById(R.id.reset_password);
        m_SignupTextView = (TextView) findViewById(R.id.signup);
        m_ProgressSpinner = (ProgressBar) findViewById(R.id.progress_spinner);
    }

    private void initializeListeners() {

        m_LoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object email = m_EmailEditText.getText();
                Object password = m_PasswordEditText.getText();

                if (email == null || !isValidEmail(email.toString().trim())) {
                    showInvalidFieldDialog(Constants.Validation.InvalidEmailTitle, Constants.Validation.InvalidEmailMessage);
                    m_EmailError.setVisibility(View.VISIBLE);

                } else if (password == null || !isValidPassword(password.toString().trim())) {
                    showInvalidFieldDialog(Constants.Validation.InvalidPasswordTitle, Constants.Validation.InvalidPasswordMessage);
                    m_PasswordError.setVisibility(View.VISIBLE);

                } else {
                    logInParseUser(email.toString().trim(), password.toString().trim());
                }
            }
        });


        // signup
        m_SignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_Me, SignupActivity.class);
                m_Me.startActivity(intent);
            }
        });

        // reset

    }

    protected boolean isValidEmail(String email) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(Constants.Validation.EmailRegex);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    protected boolean isValidPassword(String password) {
        return (password.length() >= Constants.Validation.MinimumPasswordLength &&
                password.length() <= Constants.Validation.MaximumPasswordLength);
    }

    protected boolean isValidConfirmation(String first, String second) {
        return first.equalsIgnoreCase(second);
    }

    protected void showInvalidFieldDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(m_Me);
        builder .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // close
                    }
                });
        builder.create().show();
    }


    public void logInParseUser(final String email, final String password) {
        m_ProgressSpinner.setVisibility(View.VISIBLE);
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Intent intent = new Intent(m_Me, MainActivity.class);
                    m_Me.startActivity(intent);
                } else {
                    // Signin failed
                    // TODO: look at parse messages
                    m_ProgressSpinner.setVisibility(View.GONE);
                    showInvalidFieldDialog(Constants.Validation.SigninErrorTitle, e.getMessage());
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

    @Override
    public void onBackPressed(){
        // disable back button
        //Intent intent = new Intent(m_Me, SignupActivity.class);
        //m_Me.startActivity(intent);
    }
}
