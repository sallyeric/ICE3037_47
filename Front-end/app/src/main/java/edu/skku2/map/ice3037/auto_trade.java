package edu.skku2.map.ice3037;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class auto_trade extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_trade);
        if(getIntent().getExtras() != null){
            TextView name = (TextView) findViewById(R.id.textView);
            Intent auto_intent = getIntent();
            name.setText(auto_intent.getStringExtra("종목"));
        }
        editText = (EditText)findViewById(R.id.editText);
    }
    public void onButtonClick(View v){
        String ReturnEditText;
        ReturnEditText = editText.getText().toString();
        if (!ReturnEditText.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("금액", Integer.parseInt(ReturnEditText));
            startActivity(intent);
        }
        else{
            Toast.makeText(this.getApplicationContext(),"금액을 적어 주세요",Toast.LENGTH_SHORT).show();
        }
    }

    private void requestOnAutoTrade(String userId, String companyName, int budget){
        /*
         * userId : 사용자 아이디를 입력값으로 요청
         * companyName : 투자할 회사의 이름
         * budget : 투자할 금액
         * 요청에 대한 결과를 화면에 표시하거나 로그로 기록
         * */

        Call<Post> call = RetrofitClient.getApiService().OnAutoTrade(userId, companyName, budget);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();

                    return;
                }
                Post postResponse = response.body();
                if (postResponse.getSuccess()){
//                    Toast.makeText(getActivity().getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("==========", postResponse.getMessage());

                }
                else {
                    Toast.makeText(getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("==========", postResponse.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestOffAutoTrade(String userId, String companyName){
        /*
         * userId : 사용자 아이디를 입력값으로 요청
         * companyName : 투자할 회사의 이름
         * budget : 투자할 금액
         * 요청에 대한 결과를 화면에 표시하거나 로그로 기록
         * */

        Call<Post> call = RetrofitClient.getApiService().OffAutoTrade(userId, companyName);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();

                    return;
                }
                Post postResponse = response.body();
                if (postResponse.getSuccess()){
//                    Toast.makeText(getActivity().getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("==========", postResponse.getMessage());

                }
                else {
                    Toast.makeText(getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("==========", postResponse.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}