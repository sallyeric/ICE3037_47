package edu.skku2.map.ice3037;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment5#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment5 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //    Chart
    private LineChart chart5;
    private TextView title5;
    private TextView price5;
    private Button auto_trade;
    private TextView price_change;

    public BlankFragment5() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment5.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment5 newInstance(String param1, String param2) {
        BlankFragment5 fragment = new BlankFragment5();
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
        View v = inflater.inflate(R.layout.fragment_blank5, container, false);

        title5 = v.findViewById(R.id.title5);
        price5 = v.findViewById(R.id.price5);

        title5.setText("엔씨소프트");
        price5.setText("900,000원");
        auto_trade = v.findViewById(R.id.auto_button);
        auto_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), auto_trade.class);
                intent.putExtra("종목",title5.getText());
                startActivity(intent);
            }
        });
        chart5 = v.findViewById(R.id.line_chart5);
        // description text
        chart5.getDescription().setEnabled(true);
        Description des1 = chart5.getDescription();
        des1.setEnabled(true);
        des1.setText("Data Chart");
        des1.setTextSize(10f);
        des1.setTextColor(Color.WHITE);

        // touch gestures (false-비활성화)
        chart5.setTouchEnabled(true);
        // scaling and dragging (false-비활성화)
        chart5.setDragEnabled(true);
        chart5.setScaleEnabled(true);
        chart5.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        chart5.setPinchZoom(true);
        chart5.getAxisLeft().setDrawGridLines(false);
        chart5.getXAxis().setDrawGridLines(false);

        chart5.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart5.getAxisRight().setEnabled(false);
        chart5.getLegend().setTextColor(Color.BLACK);
        chart5.animateXY(2000, 2000);
        XAxis x = chart5.getXAxis();
        x.setAxisMinimum(40);
        x.setAxisMaximum(200);
        chart5.invalidate();

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
        chart5.setData(data);

        return v;

    }
}