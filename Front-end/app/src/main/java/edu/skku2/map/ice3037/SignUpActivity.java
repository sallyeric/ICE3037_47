package edu.skku2.map.ice3037;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                // 통신
                if(signupUsername.isEmpty() || signupPassword.isEmpty() || signupCreonAccount.isEmpty() || signupPwConfirm.isEmpty()){
                    Toast.makeText(getApplicationContext(),"필수입력사항을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(signupPassword.equals(signupPwConfirm)){
                    request(signupUsername, signupPassword);
                }
                else{
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    passwordET.setText("");
                    pwconfirmET.setText("");
                }

            }
        });
    }

    private void request(String userId, String pw){
        Call<Post> call = RetrofitClient.getApiService().signUp(signupUsername, signupPassword, signupCreonAccount);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();
                    return;
                }

                Post postResponse = response.body();

                Log.v("",postResponse.getSuccess().toString());

                if (postResponse.getSuccess()){
                    Toast.makeText(getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    // Intent: Go to Login Activity
                    EditText username = (EditText) findViewById(R.id.signupUsername);
                    Intent signupIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                    signupIntent.putExtra("Username", username.getText().toString());
                    startActivity(signupIntent);

                }
                else {
                    Toast.makeText(getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}