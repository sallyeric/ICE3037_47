package edu.skku2.map.ice3037;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;
import java.util.Date;


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
    private Button on_auto_trade;
    private Button off_auto_trade;

    private JSONArray chartData;
    private JSONArray newsData;

    RecyclerView mRecyclerView;
    InfoNewsAdapter mAdapter;
    private ArrayList<NewsItem> mArrayList;
    private ProgressDialog customProgressDialog;

    private String userId = "choi3";
    private String companyName = "삼성전자";

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
        on_auto_trade = v.findViewById(R.id.onAutoTrade_button);
        on_auto_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), auto_trade.class);
                intent.putExtra("종목",title.getText());
                startActivity(intent);
            }
        });

        SharedPreferences check = this.getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        userId = check.getString("userid","");

        off_auto_trade = v.findViewById(R.id.offAutoTrade_button);
        off_auto_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestOffAutoTrade(userId, companyName);
            }
        });

        // news
        mRecyclerView = v.findViewById(R.id.recyclerView_news);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager) ;

        request(companyNameList[0]);
        chart = v.findViewById(R.id.line_chart);

        return v;
    }
    @Override
    public void onClick(View v) {

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
    }

    public void makeChart(LineChart chart, JSONArray chartData){
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
        chart.getXAxis().setLabelCount(5, true);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setGranularity(1f);
        chart.getLegend().setTextColor(Color.BLACK);

        XAxis x = chart.getXAxis();
        chart.invalidate();

        ArrayList<Entry> values = new ArrayList<>();
        JSONObject element;
        for (int i = 0; i < chartData.length()-1; i++){
            element = (JSONObject) chartData.opt(chartData.length() - i - 1);
            int getDate = Integer.parseInt(element.optString("date"));
            int getPrice = Integer.parseInt(element.optString("price"));
            values.add(new Entry(i, getPrice));
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

        //로딩창
        customProgressDialog = new ProgressDialog(getActivity(), R.style.CustomProgress);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customProgressDialog.getWindow().setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
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

                        float tmp = (float) obj.getInt("diff")/ obj.getInt("price")*100;
                        if(tmp > 0){
                            price_change.setText(String.format("+%s(%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("diff")), tmp));
                            price_change.setTextColor(0xAAff0000);
                        }
                        else{
                            price_change.setText(String.format("%s(%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("diff")), tmp));
                            price_change.setTextColor(0xAA00ff);
                        }

                        price_change.setText(String.format("%s(%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("diff")), (float) obj.getInt("diff")/ obj.getInt("price")*100));
                        if (obj.getInt("diff") < 0){
                            price_change.setTextColor(Color.BLUE);
                        }
                        else{
                            price_change.setTextColor(Color.RED);
                        }

                        chartData = (JSONArray) obj.get("chartData");
                        newsData = (JSONArray) obj.get("newsData");

                        makeChart(chart, chartData);
                        chart.animateXY(1000, 1000);

                        mArrayList = new ArrayList<>();
                        mAdapter = new InfoNewsAdapter(mArrayList);
                        mRecyclerView.setAdapter(mAdapter);

                        JSONObject el;
                        for (int i=0; i < newsData.length()-1; i++){
                            el = (JSONObject) newsData.opt(i);
                            NewsItem item = new NewsItem(el.optString("날짜").toString(), el.optString("시간").toString(), el.optString("기사제목").toString(),el.optString("언론사").toString(),el.optString("링크").toString());
                            mArrayList.add(item);
                        }

                        mAdapter.notifyDataSetChanged();
                        //로딩종료
                        customProgressDialog.dismiss();

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
                //로딩종료
                customProgressDialog.dismiss();
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
                    Toast.makeText(getActivity().getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();

                    return;
                }
                Post postResponse = response.body();
                if (postResponse.getSuccess()){
                    Toast.makeText(getActivity().getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("==========", postResponse.getMessage());
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

    private void makeNewsList(){

    }

}
