package edu.skku2.map.ice3037;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

    private EditText budget;
    private CheckBox lstm;
    private CheckBox macd;
    private CheckBox voli;

    private String userId = "choi3";
    private String companyName;
    private Integer money;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_trade);
        if(getIntent().getExtras() != null){
            TextView name = (TextView) findViewById(R.id.textView);
            Intent auto_intent = getIntent();
            name.setText(auto_intent.getStringExtra("종목"));
            companyName = auto_intent.getStringExtra("종목");
        }
        budget = (EditText)findViewById(R.id.textBudget);
        lstm = (CheckBox)findViewById(R.id.LSTM);
        macd = (CheckBox)findViewById(R.id.MACD);
        voli = (CheckBox)findViewById(R.id.volatility);
        SharedPreferences check = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        userId = check.getString("userid","");
    }

    public void onButtonClick(View v){
        String textBudget;
        textBudget = budget.getText().toString();
        money = Integer.parseInt(textBudget);

        Log.d("sf", String.valueOf(money));

        if (!textBudget.isEmpty()) {
            requestOnAutoTrade(userId, companyName, money, lstm.isChecked(), macd.isChecked(), voli.isChecked());

        }
        else{
            Toast.makeText(this.getApplicationContext(),"금액을 적어 주세요",Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("alarm_channel_id", "알람 테스트", importance);
            channel.setDescription("알람테스트");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void requestOnAutoTrade(String userId, String companyName, int budget, Boolean check1, Boolean check2, Boolean check3){
        /*
         * userId : 사용자 아이디를 입력값으로 요청
         * companyName : 투자할 회사의 이름
         * budget : 투자할 금액
         * 요청에 대한 결과를 화면에 표시하거나 로그로 기록
         * */
        trading_inform t_info = new trading_inform(userId, companyName, budget, check1, check2, check3);

        Call<Post> call = RetrofitClient.getApiService().OnAutoTrade(t_info);
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
                    Log.d("==========", postResponse.getMessage());
                    Intent intent = new Intent(auto_trade.this, MainActivity.class);
                    intent.putExtra("금액", money);
                    startActivity(intent);

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