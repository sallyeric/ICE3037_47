package edu.skku2.map.ice3037;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyPageFragment extends Fragment {

    MyPageAdapter mAdapter;
    private ArrayList<ItemMyPage> mArrayList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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

//        RecyclerView recyclerView = v.findViewById(R.id.recyclerView2);
//
//        LinearLayoutManager manager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL,false);
//
//        recyclerView.setLayoutManager(manager); // LayoutManager 등록
//        recyclerView.setAdapter(new MyPageAdapter(mArrayList));  // Adapter 등록

        //
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView2);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager) ;

        mArrayList = new ArrayList<>();

        mAdapter = new MyPageAdapter(mArrayList);

        mRecyclerView.setAdapter(mAdapter) ;
        //

        // 아이템 추가.
        // String corp, String price, String stock, String time, String profit, String percent, int viewType
        ItemMyPage item1 = new ItemMyPage("네이버","8000 원","4 주", "2021-05-25",
                null, null, 0);
        mArrayList.add(item1);
        ItemMyPage item2 = new ItemMyPage("삼성전자","10000 원","8 주", "2021-05-26",
                "2000 원", "5 %", 1);
        mArrayList.add(item2);
        ItemMyPage item3 = new ItemMyPage("SK 하이닉스","20000 원","6 주", "2021-05-27",
                "- 1000 원", "- 5 %", 2);
        mArrayList.add(item3);

        mAdapter.notifyDataSetChanged() ;

        return v;
    }
}
