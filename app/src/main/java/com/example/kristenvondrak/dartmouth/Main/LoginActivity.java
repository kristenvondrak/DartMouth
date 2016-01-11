package com.example.kristenvondrak.dartmouth.Main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kristenvondrak.dartmouth.R;

public class LoginActivity extends Activity {

    private EditText m_EmailEditText;
    private EditText m_PasswordEditText;
    private TextView m_LoginTextView;
    private TextView m_ResetTextView;
    private TextView m_SignupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Activity me = this;

        m_EmailEditText = (EditText) findViewById(R.id.email_edit_text);
        m_PasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        m_LoginTextView = (TextView) findViewById(R.id.login_text_view);
        m_ResetTextView = (TextView) findViewById(R.id.reset_password_text_view);
        m_SignupTextView = (TextView) findViewById(R.id.signup_text_view);

        m_LoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.finish();
                // register user/validate
                // show red ! in edittext
            }
        });

        // signup

        // reset


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
