package edu.skku2.map.ice3037;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    String uid="", pw="";
    EditText usernameET,passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET=(EditText) findViewById(R.id.userid);
        passwordET=(EditText) findViewById(R.id.password);

        if(getIntent().getExtras() != null){
            EditText username = (EditText)findViewById(R.id.userid);
            Intent signupIntent = getIntent();
            username.setText(signupIntent.getStringExtra("Username"));
        }

        TextView login = (TextView) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에서 값 비교
                uid = usernameET.getText().toString();
                pw = passwordET.getText().toString();

                /*TODO: write Login process*/

                // Intent: Go to Main Activity
                EditText username = (EditText) findViewById(R.id.userid);
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                loginIntent.putExtra("Username", username.getText().toString());
                startActivity(loginIntent);
            }
        });

        TextView signup = (TextView)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent: Go to Sign Up Activity
                Intent signupIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signupIntent);
            }
        });
    }
}