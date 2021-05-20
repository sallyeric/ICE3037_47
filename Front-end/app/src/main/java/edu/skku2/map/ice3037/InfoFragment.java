package edu.skku2.map.ice3037;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String[] companyNameList = {
            "삼성전자",
            "SK하이닉스",
            "LG화학",
            "셀트리온",
            "NAVER",
            "현대차",
            "카카오",
            "기아",
            "POSCO"
    };

    // A class instance
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private CircleImageView bt1;
    private CircleImageView bt2;
    private CircleImageView bt3;
    private CircleImageView bt4;
    private CircleImageView bt5;
    private CircleImageView bt6;
    private CircleImageView bt7;
    private CircleImageView bt8;
    private CircleImageView bt9;
    //    Chart
    private LineChart chart;
    private TextView title;
    private TextView price;
    private TextView price_change;
    private Button auto_trade;

    private JSONObject chartData;
    private JSONArray newsData;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        bt1 = (CircleImageView)v.findViewById(R.id.bt1);
        bt2 = (CircleImageView)v.findViewById(R.id.bt2);
        bt3 = (CircleImageView)v.findViewById(R.id.bt3);
        bt4 = (CircleImageView)v.findViewById(R.id.bt4);
        bt5 = (CircleImageView)v.findViewById(R.id.bt5);
        bt6 = (CircleImageView)v.findViewById(R.id.bt6);
        bt7 = (CircleImageView)v.findViewById(R.id.bt7);
        bt8 = (CircleImageView)v.findViewById(R.id.bt8);
        bt9 = (CircleImageView)v.findViewById(R.id.bt9);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);

        title = v.findViewById(R.id.title);
        price = v.findViewById(R.id.price);
        price_change = v.findViewById(R.id.price_change);
        auto_trade = v.findViewById(R.id.auto_button);
        auto_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), auto_trade.class);
                intent.putExtra("종목",title.getText());
                startActivity(intent);
            }
        });

        request(companyNameList[0]);
        chart = v.findViewById(R.id.line_chart);
        makeChart(chart);
        chart.animateXY(2000, 2000);

        return v;
    }
    @Override
    public void onClick(View v) {

        String companyName = "";
        Integer presentPrice;

        switch (v.getId()){
            case R.id.bt1:
                companyName = companyNameList[0];
                break;
            case R.id.bt2 :
                companyName = companyNameList[1];
                break;
            case R.id.bt3 :
                companyName = companyNameList[2];
                break;
            case R.id.bt4 :
                companyName = companyNameList[3];
                break;
            case R.id.bt5 :
                companyName = companyNameList[4];
                break;
            case R.id.bt6 :
                companyName = companyNameList[5];
                break;
            case R.id.bt7 :
                companyName = companyNameList[6];
                break;
            case R.id.bt8 :
                companyName = companyNameList[7];
                break;
            case R.id.bt9 :
                companyName = companyNameList[8];
                break;
        }

        request(companyName);
        makeChart(chart);
        chart.animateXY(2000, 2000);
    }

    public void makeChart(LineChart chart){
        // description text
        chart.getDescription().setEnabled(true);
        Description des1 = chart.getDescription();
        des1.setEnabled(true);
        des1.setText("Data Chart");
        des1.setTextSize(10f);
        des1.setTextColor(Color.WHITE);
        // touch gestures (false-비활성화)
        chart.setTouchEnabled(true);
        // scaling and dragging (false-비활성화)
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setTextColor(Color.BLACK);
        chart.animateXY(2000, 2000);

        XAxis x = chart.getXAxis();
        x.setAxisMinimum(40);
        x.setAxisMaximum(200);
        chart.invalidate();

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 60; i < 100; i++) {
            float val = (float) (Math.random() * 10);
            values.add(new Entry(i, val));
        }

        LineDataSet set1;
        set1 = new LineDataSet(values, "Price");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // black lines and points
        set1.setFillAlpha(110);
        set1.setFillColor(Color.parseColor("#fae7d7"));
        set1.setColor(Color.parseColor("#FF0000"));
        set1.setCircleColor(Color.parseColor("#FFA1B4DC"));
        set1.setCircleHoleColor(Color.BLUE);
        set1.setValueTextColor(Color.BLACK);
        set1.setDrawValues(false);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setDrawCircles(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setHighLightColor(Color.rgb(244, 117, 117));

        // set data
        chart.setData(data);
    }
    
    private void request(String companyName){
        /*
         * userId : 사용자 아이디를 입력값으로 요청
         * title, price, price_change 에 표시되는 값을 변경
         * chartData, newsData 에 값을 할당
         * */

        title.setText(companyName);

        Call<Post> call = RetrofitClient.getApiService().info(companyName);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getActivity().getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();

                    return;
                }
                Post postResponse = response.body();
                if (postResponse.getSuccess()){

                    try {
                        JSONObject obj = new JSONObject(postResponse.getMessage());
                        Log.d("==========", obj.toString());

                        price.setText(String.format("%s원", new DecimalFormat("###,###").format(obj.getInt("price"))));
                        price_change.setText(String.format("%s(%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("diff")), (float) obj.getInt("diff")/ obj.getInt("price")*100));

                        chartData = (JSONObject) obj.get("chartData");
                        newsData = (JSONArray) obj.get("newsData");

                        Log.d("==========", chartData.toString());
                        Log.d("==========", newsData.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("==========", postResponse.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(),"서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
