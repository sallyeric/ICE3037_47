package edu.skku2.map.ice3037;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
}