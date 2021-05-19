package edu.skku2.map.ice3037;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    HomeAdapter mAdapter;
    private ArrayList<Item> mArrayList;
    private int count = -1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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

        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager) ;

        mArrayList = new ArrayList<>();

        mAdapter = new HomeAdapter(getActivity().getApplicationContext(), mArrayList);

        mRecyclerView.setAdapter(mAdapter) ;

        // 아이템 추가.
        Item item1 = new Item(ContextCompat.getDrawable(v.getContext(), R.drawable.logo_samsung),
                "삼성전자", "3000", "+0.02");
        mArrayList.add(item1);

        Item item2 = new Item(ContextCompat.getDrawable(v.getContext(), R.drawable.logo_naver),
                "네이버", "8000 원", "-0.05");
        mArrayList.add(item2);

        Item item3 = new Item(ContextCompat.getDrawable(v.getContext(), R.drawable.logo_skhynix),
                "SK 하이닉스", "6000 원", "+0.5");
        mArrayList.add(item3);

        mAdapter.notifyDataSetChanged() ;

        return v;
    }
}
