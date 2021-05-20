package edu.skku2.map.ice3037;

import android.os.Bundle;

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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
    // 이전코드
//    private RecyclerView recyclerView;
    HomeAdapter mAdapter;
    private ArrayList<Item> mArrayList;
    private int count = -1;

    // 새코드
//    RecyclerView mRecyclerView = null ;
//    HomeAdapter mAdapter = null ;
//    ArrayList<Item> mList = new ArrayList<Item>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView budgets;
    private TextView yield;

    private JSONObject obj;



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        // ADD
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        budgets = v.findViewById(R.id.bugets);
        yield = v.findViewById(R.id.yield_info);


        // 이전코드
//        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
//
//        recyclerView.setHasFixedSize(true);
//        adapter = new HomeAdapter(getActivity().getApplicationContext(), list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
//        recyclerView.setAdapter(adapter);
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager) ;

        mArrayList = new ArrayList<>();

        mAdapter = new HomeAdapter(getActivity().getApplicationContext(), mArrayList);

        mRecyclerView.setAdapter(mAdapter) ;
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext())) ;

        request("choi2");

//        Item item1 = new Item("삼성전자", "3000", "+0.02");
//        mArrayList.add(item1);
//        Item item2 = new Item("네이버", "8000 원", "-0.05");
//        mArrayList.add(item2);
//        Item item3 = new Item("SK 하이닉스", "6000 원", "+0.5");
//        mArrayList.add(item3);

        // 두 번째 아이템 추가.
//        addItem(ContextCompat.getDrawable(v.getContext(), R.drawable.logo_naver),
//                "네이버", "8000 원", "-0.05") ;
//        // 세 번째 아이템 추가.
//        addItem(ContextCompat.getDrawable(v.getContext(), R.drawable.logo_skhynix),
//                "SK 하이닉스", "6000 원", "+0.5") ;

        mAdapter.notifyDataSetChanged() ;

        return v;
    }

//    public void addItem(String corp, String price, String updown) {
//        Item item = new Item("", "", "");
//
////        item.setLogo(logo);
//        item.setCorp(corp);
//        item.setPrice(price);
//        item.setUpdown(updown);
//
//        mArrayList.add(item);
//    }

    private void request(String userId){
        /*
         * userId : 사용자 아이디를 입력값으로 요청
         * budgets, yield 에 표시되는 값을 변경
         * mArrayList 에 값을 할당
         * */

        Call<Post> call = RetrofitClient.getApiService().home("choi2");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getActivity().getApplicationContext(),"서버통신에 오류가 발생했습니다.".concat(String.valueOf(response.code())), Toast.LENGTH_SHORT).show();

                    return;
                }
                Post postResponse = response.body();
                if (postResponse.getSuccess()){
//                    Toast.makeText(getActivity().getApplicationContext(), postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("==========", postResponse.getMessage());

//                    JsonParser parser = new JsonParser();
//                    Object obj = parser.parse(postResponse.getMessage());
                    try {
                        obj = new JSONObject(postResponse.getMessage());

                        for(int i = 0; i < enterpriseList.length; i++){
                            try{
                                JSONObject tmp = (JSONObject) obj.get("stocks");
                                JSONObject enter = (JSONObject) tmp.get(enterpriseList[i]);
                                int size = enter.getInt("size");
                                int price = enter.getInt("price");
                                int diff = enter.getInt("diff");

                                budgets.setText(String.format("%s원", new DecimalFormat("###,###").format(obj.getInt("currentMoney"))));
                                yield.setText(String.format("%.2f%%", (float)obj.getInt("currentDiff")/obj.getInt("currentMoney")*100));

                                Item item = new Item(enterpriseList[i], String.valueOf(price), String.format("%.2f%%", (float)diff/price*100));
                                mArrayList.add(item);

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
