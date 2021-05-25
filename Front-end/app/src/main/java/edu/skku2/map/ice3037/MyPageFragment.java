package edu.skku2.map.ice3037;

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

//        mArrayList = new ArrayList<>();
//
//        mAdapter = new MyPageAdapter(mArrayList);
//
//        mRecyclerView.setAdapter(mAdapter) ;

        /*TODO
        *  [Backend] 여기 코드 수정하면 될 것 같습니다.
        * 아이템 추가.
        * viewType에 따라 item의 색깔이 다르게 나타납니다. (회색/빨강/파랑)
        * 수익/손실에 따라 viewType을 설정해주면 됩니다. (0/1/2)
        * 매수: 회사명, 매수금액, 몇 주, 시간, viewType(0)
        * 매도 (+): 회사명, 매수금액, 몇 주, 시간,  수익/손실, 수익률/손실률, viewType(1)
        * 매도 (-): 회사명, 매수금액, 몇 주, 시간,  수익/손실, 수익률/손실률, viewType(2) */

        // 내 자산과 수익률
        budgets = v.findViewById(R.id.budget_info);
        yield = v.findViewById(R.id.yield_info);

//        request("choi3");

//        ItemMyPage item1 = new ItemMyPage("네이버","8000 원","4 주", "2021-05-25",
//                null, null, 0);
//        mArrayList.add(item1);
//        ItemMyPage item2 = new ItemMyPage("삼성전자","10000 원","8 주", "2021-05-26",
//                "2000 원", "5 %", 1);
//        mArrayList.add(item2);
//        ItemMyPage item3 = new ItemMyPage("SK 하이닉스","20000 원","6 주", "2021-05-27",
//                "- 1000 원", "- 5 %", 2);
//        mArrayList.add(item3);
//
//        mAdapter.notifyDataSetChanged() ;

        return v;
    }

    private void request(String userId){
    /*
    * userId : 사용자 아이디를 입력값으로 요청
    * budgets_info, yield_info 에 표시되는 값을 변경
    * stocks, history 에 각각의 JSONObject, JSONArray 를 할당
    * */
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

<<<<<<< HEAD
                        budgets.setText(new DecimalFormat("###,###원").format(obj.getInt("currentMoney")));
                        yield.setText(String.format("%s원 (%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("currentDiff")), (float) 1.1));
=======
                        budgets.setText(String.format("%s원", new DecimalFormat("###,###").format(obj.getInt("currentMoney"))));
                        yield.setText(String.format("%s(%.2f%%)", new DecimalFormat("###,###").format(obj.getInt("currentDiff")), (float) obj.getInt("currentDiff")/ obj.getInt("money")*100));
//                        yield.setText(String.format("%.2f%%", (float)obj.getInt("currentDiff")/obj.getInt("currentMoney")*100));

                        /* 매수: 회사명, 매수금액, 몇 주, 시간, viewType(0)
                        * 매도 (+): 회사명, 매수금액, 몇 주, 시간,  수익/손실, 수익률/손실률, viewType(1)
                        * 매도 (-): 회사명, 매수금액, 몇 주, 시간,  수익/손실, 수익률/손실률, viewType(2) */
>>>>>>> JSH_tab1tab2

                        stocks = (JSONObject) obj.get("stocks");
                        history = (JSONArray) obj.get("history");

<<<<<<< HEAD
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
                            if(type == 1){
                                if(diff > 0)
                                    type = 1;
                                else
                                    type = 2;
                            }

                            ItemMyPage item = new ItemMyPage(name, new DecimalFormat("###,###원").format(price), String.format("%d주", size),
                                    dateToString(date), new DecimalFormat("###,###원").format(diff), String.format("%.2f%%", (float) diff/price*100), type);
                            mArrayList.add(item);
                        }

                        mAdapter.notifyDataSetChanged() ;
=======
                        for(int n = 0; n < history.length(); n++)
                        {
                            JSONObject object = history.getJSONObject(n);
                            String name = object.getString("name"); // 회사명
                            int price = object.getInt("price"); // 메수금액
                            int size = object.getInt("size"); // 몇 주
                            int date = object.getInt("date"); // 시간
                            int type = object.getInt("type"); // 매수 / 매도
                            int diff = object.getInt("diff"); // 수익
                        }
>>>>>>> JSH_tab1tab2

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
