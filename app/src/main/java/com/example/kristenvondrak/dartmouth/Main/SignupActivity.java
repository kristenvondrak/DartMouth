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
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends LoginActivity {

    private EditText m_ConfirmEditText;
    private ImageView m_ConfirmError;
    private TextView m_SignupTextView;
    private TextView m_LoginTextView;


    public Activity m_Me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        m_Me = this;

        if (ParseAPI.getCurrentParseUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }

        initializeViews();
        initializeListeners();
    }

    private void initializeViews() {
        m_EmailEditText = (EditText) findViewById(R.id.email_edit_text);
        m_PasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        m_ConfirmEditText = (EditText) findViewById(R.id.confirm_password_edit_text);
        m_EmailError = (ImageView) findViewById(R.id.email_error_icon);
        m_PasswordError = (ImageView) findViewById(R.id.password_error_icon);
        m_ConfirmError = (ImageView) findViewById(R.id.confirm_password_error_icon);
        m_LoginTextView = (TextView) findViewById(R.id.login);
        m_SignupTextView = (TextView) findViewById(R.id.signup);
        m_ProgressSpinner = (ProgressBar) findViewById(R.id.progress_spinner);
    }

    private void initializeListeners() {

        m_LoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_Me, LoginActivity.class);
                m_Me.startActivity(intent);
            }
        });

        m_SignupTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m_EmailError.setVisibility(View.GONE);
                m_PasswordError.setVisibility(View.GONE);
                m_ConfirmError.setVisibility(View.GONE);

                Object email = m_EmailEditText.getText();
                Object password = m_PasswordEditText.getText();
                Object confirm = m_ConfirmEditText.getText();

                if (email == null || !isValidEmail(email.toString().trim())) {
                    showInvalidFieldDialog(Constants.Validation.InvalidEmailTitle, Constants.Validation.InvalidEmailMessage);
                    m_EmailError.setVisibility(View.VISIBLE);

                } else if (password == null || !isValidPassword(password.toString().trim())) {
                    showInvalidFieldDialog(Constants.Validation.InvalidPasswordTitle, Constants.Validation.InvalidPasswordMessage);
                    m_PasswordError.setVisibility(View.VISIBLE);

                } else if (confirm == null || !isValidConfirmation(password.toString().trim(), confirm.toString().trim())) {
                    showInvalidFieldDialog(Constants.Validation.NoMatchPasswordsTitle, Constants.Validation.NoMatchPasswordsMessage);
                    m_ConfirmError.setVisibility(View.VISIBLE);

                } else {
                    createNewParseUser(email.toString().trim(), password.toString().trim());
                }
            }
        });


    }



    public void createNewParseUser(String email, String password) {
        m_ProgressSpinner.setVisibility(View.VISIBLE);

        User user = new User();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(m_Me, MainActivity.class);
                    m_Me.startActivity(intent);
                } else {
                    m_ProgressSpinner.setVisibility(View.GONE);
                    showInvalidFieldDialog(Constants.Validation.SignupErrorTitle, e.toString());
                    Log.d("Signup", "User signup fail " + e.toString());
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
    }
}
