package edu.skku2.map.ice3037;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    HomeAdapter mAdapter;
    private ArrayList<Item> mArrayList;
    private int count = -1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView budgets;
    private TextView yield;

    private JSONObject obj;

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

        mArrayList = new ArrayList<>();

        mAdapter = new HomeAdapter(getActivity().getApplicationContext(), mArrayList);

        mRecyclerView.setAdapter(mAdapter) ;

        request("choi3");
//        Item item3 = new Item(ContextCompat.getDrawable(v.getContext(), R.drawable.logo_skhynix),
//                "SK 하이닉스", "6000", "+0.5");
//        mArrayList.add(item3);
//        mAdapter.notifyDataSetChanged() ;
        return v;
    }


    private void request(String userId){
        /*
         * userId : 사용자 아이디를 입력값으로 요청
         * budgets, yield 에 표시되는 값을 변경
         * mArrayList 에 값을 할당
         * */

        Call<Post> call = RetrofitClient.getApiService().home("choi3");
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
                                int price = enter.getInt("price"); // 현재가
                                int diff = enter.getInt("diff"); // 현재 가격 - 산 가격

                                budgets.setText(String.format("%s원", new DecimalFormat("###,###").format(obj.getInt("currentMoney"))));
                                yield.setText(String.format("%.2f%%", (float)obj.getInt("currentDiff")/obj.getInt("currentMoney")*100));

//                                Item item = new Item(enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
//                                mArrayList.add(item);
                                // 로고 설정
                                if(enterpriseList[i] == "삼성전자"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.logo_samsung), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "SK하이닉스"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.logo_skhynix), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "LG화학"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.lgchemi), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "셀트리온"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.celltrion), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "NAVER"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.logo_skhynix), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "현대차"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.naver), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "카카오"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.kakao), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "기아"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.kia), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                                else if (enterpriseList[i] == "POSCO"){
                                    Item item = new Item(ContextCompat.getDrawable(getContext(), R.drawable.posco), enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                    mArrayList.add(item);
                                }
                            }catch (JSONException e){

                            }
                        }

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
            }
        });
    }
}
