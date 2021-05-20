package edu.skku2.map.ice3037;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                if(uid.isEmpty() || pw.isEmpty()){
                    Toast.makeText(getApplicationContext(),"아이디와 비밀번호를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    request(uid, pw);
                }

//                EditText username = (EditText) findViewById(R.id.userid);
//                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
//                loginIntent.putExtra("Username", username.getText().toString());
//                startActivity(loginIntent);
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

    private void request(String userId, String pw){
        Call<Post> call = RetrofitClient.getApiService().login(uid, pw);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();
                    return;
                }
                Post postResponse = response.body();
                if (postResponse.getSuccess()){
                    Toast.makeText(getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    // Intent: Go to Main Activity
                    EditText username = (EditText) findViewById(R.id.userid);
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    loginIntent.putExtra("Username", username.getText().toString());
                    startActivity(loginIntent);
                }
                else {
                    Toast.makeText(getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    usernameET.setText("");
                    passwordET.setText("");
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}