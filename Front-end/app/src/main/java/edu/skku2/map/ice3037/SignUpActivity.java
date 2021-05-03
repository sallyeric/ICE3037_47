package edu.skku2.map.ice3037;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    String signupUsername="", signupPassword="", signupPwConfirm="", signupCreonAccount="";
    EditText usernameET,passwordET,pwconfirmET,creonaccountET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameET=(EditText) findViewById(R.id.signupUsername);
        passwordET=(EditText) findViewById(R.id.signupPassword);
        pwconfirmET=(EditText) findViewById(R.id.signupPwConfirm);
        creonaccountET=(EditText) findViewById(R.id.signupCreonAccount);

        TextView signup = (TextView) findViewById(R.id.signupButton);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // value parse
                signupUsername = usernameET.getText().toString();
                signupPassword = passwordET.getText().toString();
                signupPwConfirm = pwconfirmET.getText().toString();
                signupCreonAccount = creonaccountET.getText().toString();

                /*TODO: Write Sign Up Verification Process*/

                // Intent: Go to Login Activity
                EditText username = (EditText) findViewById(R.id.signupUsername);
                Intent signupIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                signupIntent.putExtra("Username", username.getText().toString());
                startActivity(signupIntent);
            }
        });
    }
}