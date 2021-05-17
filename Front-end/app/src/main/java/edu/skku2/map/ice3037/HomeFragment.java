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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

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

        // 아이템 추가.
        Item item1 = new Item("삼성전자", "3000", "+0.02");
        mArrayList.add(item1);
        Item item2 = new Item("네이버", "8000 원", "-0.05");
        mArrayList.add(item2);
        Item item3 = new Item("SK 하이닉스", "6000 원", "+0.5");
        mArrayList.add(item3);

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
}
