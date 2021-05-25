package edu.skku2.map.ice3037;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

public class MyPageFragment extends Fragment {

    MyPageAdapter mAdapter;
    private ArrayList<ItemMyPage> mArrayList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView budgets;
    private TextView yield;

    private JSONObject stocks;
    private JSONArray history;

    private RecyclerView mRecyclerView;
    private ProgressDialog customProgressDialog;

    public MyPageFragment() {
        // Required empty public constructor
    }

    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
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

        View v = inflater.inflate(R.layout.fragment_my_page, container, false);

        mRecyclerView = v.findViewById(R.id.recyclerView2);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager) ;

        // 내 자산과 수익률
        budgets = v.findViewById(R.id.budget_info);
        yield = v.findViewById(R.id.yield_info);

        request("choi3");

        return v;
    }

    private void request(String userId){
    /*
    * userId : 사용자 아이디를 입력값으로 요청
    * budgets_info, yield_info 에 표시되는 값을 변경
    * stocks, history 에 각각의 JSONObject, JSONArray 를 할당
    * */
        //로딩창
        customProgressDialog = new ProgressDialog(getActivity());
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customProgressDialog.getWindow().setGravity(Gravity.CENTER);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
        Call<Post> call = RetrofitClient.getApiService().myInfo(userId);
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

                        budgets.setText(new DecimalFormat("###,###원").format(obj.getInt("currentMoney")));

                        float tmp = (float) 1.1;
                        if(tmp > 0){
                            yield.setText(String.format("+%s원 (%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("currentDiff")), tmp));
                            yield.setTextColor(0xAAff5555);
                        }
                        else{
                            yield.setText(String.format("%s원 (%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("currentDiff")), tmp));
                            yield.setTextColor(0xAA0000ff);
                        }

                        /* 매수: 회사명, 매수금액, 몇 주, 시간, viewType(0)
                        * 매도 (+): 회사명, 매수금액, 몇 주, 시간,  수익/손실, 수익률/손실률, viewType(1)
                        * 매도 (-): 회사명, 매수금액, 몇 주, 시간,  수익/손실, 수익률/손실률, viewType(2) */


                        stocks = (JSONObject) obj.get("stocks");
                        history = (JSONArray) obj.get("history");

                        mArrayList = new ArrayList<>();
                        mAdapter = new MyPageAdapter(mArrayList);
                        mRecyclerView.setAdapter(mAdapter) ;


                        for(int n = 0; n < history.length(); n++)
                        {
                            JSONObject object = (JSONObject) history.opt(n);
                            String name = object.getString("name"); // 회사명
                            int price = object.getInt("price"); // 메수금액
                            int size = object.getInt("size"); // 몇 주
                            String date = object.getString("date"); // 시간
                            int type = object.getInt("type"); // 매수 / 매도
                            int diff = object.getInt("diff"); // 수익


                            ItemMyPage item;
                            if(type == 1){
                                if(diff > 0){
                                    item = new ItemMyPage(name, new DecimalFormat("###,###원").format(price), String.format("%d주", size),
                                            dateToString(date), new DecimalFormat("+###,###원").format(diff), String.format("+%.2f%%", (float) diff/price*100), 1);
                                }
                                else {
                                    item = new ItemMyPage(name, new DecimalFormat("###,###원").format(price), String.format("%d주", size),
                                            dateToString(date), new DecimalFormat("###,###원").format(diff), String.format("%.2f%%", (float) diff/price*100), 2);
                                }
                            }
                            else{
                                item = new ItemMyPage(name, new DecimalFormat("###,###원").format(price), String.format("%d주", size),
                                        dateToString(date), new DecimalFormat("###,###원").format(diff), String.format("%.2f%%", (float) diff/price*100), 0);
                            }

                            mArrayList.add(item);
                        }

                        //로딩종료
                        customProgressDialog.dismiss();
                        mAdapter.notifyDataSetChanged() ;


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
                Log.d("Tab3", "Success");
            }
        });
    }

    String dateToString(String str){
        String res = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8) + "." + str.substring(8, 10) + ":" + str.substring(10);
        return res;
    }
}
