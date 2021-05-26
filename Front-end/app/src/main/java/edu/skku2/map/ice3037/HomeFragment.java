package edu.skku2.map.ice3037;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private String[] enterpriseList = {
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

    private Integer[] logos = {
            R.drawable.samsung,
            R.drawable.logo_skhynix,
            R.drawable.logo_lg,
            R.drawable.logo_celltrion,
            R.drawable.logo_naver,
            R.drawable.logo_hyundai,
            R.drawable.logo_kakao,
            R.drawable.logo_kia,
            R.drawable.logo_posco
    };

    HomeAdapter mAdapter;
    HomeAdapter2 mAdapter2;
    private ArrayList<Item> mArrayList;
    private ArrayList<Item2> mArrayList2;
    private int count = -1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView budgets;
    private TextView yield;

    private JSONObject obj;
    private ProgressDialog customProgressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        budgets = v.findViewById(R.id.budgets);
        yield = v.findViewById(R.id.yield);

        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager) ;

        RecyclerView mRecyclerView2 = v.findViewById(R.id.recyclerView2);
        LinearLayoutManager mLinearLayoutManager2 = new LinearLayoutManager(v.getContext());
        mRecyclerView2.setLayoutManager(mLinearLayoutManager2) ;

        mArrayList = new ArrayList<>();
        mArrayList2 = new ArrayList<>();
        mAdapter = new HomeAdapter(getActivity().getApplicationContext(), mArrayList);
        mAdapter2 = new HomeAdapter2(getActivity().getApplicationContext(), mArrayList2);

        mRecyclerView.setAdapter(mAdapter) ;
        mRecyclerView2.setAdapter(mAdapter2) ;

        SharedPreferences check = this.getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        String ID = check.getString("userid","");
        Log.d("idid",ID);
        request(ID);

        mAdapter.notifyDataSetChanged() ;
        mAdapter2.notifyDataSetChanged() ;
        return v;
    }


    private void request(String userId){
        /*
         * userId : 사용자 아이디를 입력값으로 요청
         * budgets, yield 에 표시되는 값을 변경
         * mArrayList 에 값을 할당
         * */
        //로딩창
        customProgressDialog = new ProgressDialog(getActivity(), R.style.CustomProgress);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        customProgressDialog.getWindow().setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();
        Call<Post> call = RetrofitClient.getApiService().home(userId);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getActivity().getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();
                    return;
                }
                Post postResponse = response.body();
                if (postResponse.getSuccess()){
                    Log.d("==========", postResponse.getMessage());
                    try {
                        obj = new JSONObject(postResponse.getMessage());
                        for(int i = 0; i < enterpriseList.length; i++){
                            try{
                                JSONObject tmp = (JSONObject) obj.get("stocks");
                                JSONObject enter = (JSONObject) tmp.get(enterpriseList[i]);
                                int size = enter.getInt("size"); // 몇 주
                                int price = enter.getInt("price"); // 산 가격
                                int diff = enter.getInt("diff"); // 현재 가격 - 산 가격
                                int currentPrice = enter.getInt("currentPrice"); // 현재가
                                float frofitRate = (float)obj.getInt("currentDiff")/obj.getInt("currentMoney")*100;

                                budgets.setText(new DecimalFormat("###,### 원").format(obj.getInt("currentMoney")));
                                if(frofitRate > 0){
                                    yield.setText(String.format("+%.2f%%", frofitRate));
                                    yield.setTextColor(0xAAff0000);
                                }
                                else{
                                    yield.setText(String.format("%.2f%%", frofitRate));
                                    yield.setTextColor(0xAA0000ff);
                                }

                                float rate = (float)diff/price*100;
                                Boolean flag;
                                String updown;
                                if(rate>0){
                                    updown = String.format("+%.2f%%", rate);
                                    flag = true;
                                }
                                else{
                                    updown = String.format("%.2f%%", rate);
                                    flag = false;
                                }
                                Item item = new Item(ContextCompat.getDrawable(getContext(), logos[i]), enterpriseList[i], new DecimalFormat("###,### 원").format(currentPrice), new DecimalFormat("###,### 원").format(currentPrice*size), updown, flag, new DecimalFormat("#주").format(size));
                                mArrayList.add(item);
                            }catch (JSONException e){
                            }
                        }
                        for(int i = 0; i < enterpriseList.length; i++){
                            try{
                                JSONObject tmp = (JSONObject) obj.get("active");
                                JSONObject enter = (JSONObject) tmp.get(enterpriseList[i]);
                                int price = enter.getInt("origin");
                                Item2 item = new Item2(ContextCompat.getDrawable(getContext(), logos[i]), enterpriseList[i], new DecimalFormat("###,### 원").format(price));
                                mArrayList2.add(item);
                            }catch (JSONException e){
                            }
                        }
                        //로딩종료
                        customProgressDialog.dismiss();
                        mAdapter.notifyDataSetChanged() ;
                        mAdapter2.notifyDataSetChanged() ;
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
}
