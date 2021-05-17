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
 * Use the {@link BlankFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart chart2;
    private Thread thread;
    private TextView title2;
    private TextView price2;
    private Button auto_trade;
    private TextView price_change;


    public BlankFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment2 newInstance(String param1, String param2) {
        BlankFragment2 fragment = new BlankFragment2();
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
//        View v = inflater.inflate(R.layout.fragment_blank2, container, false);
//
//        chart2 = v.findViewById(R.id.line_chart2);
//        // description text
//        chart2.getDescription().setEnabled(true);
//        Description des2 = chart2.getDescription();
//        des2.setEnabled(true);
//        des2.setText("Real-time Chart");
//        des2.setTextSize(10f);
//        des2.setTextColor(Color.WHITE);
//
//        // touch gestures (false-비활성화)
//        chart2.setTouchEnabled(true);
//        // scaling and dragging (false-비활성화)
//        chart2.setDragEnabled(true);
//        chart2.setScaleEnabled(true);
//        chart2.setDrawGridBackground(false);
//        // if disabled, scaling can be done on x- and y-axis separately
//        chart2.setPinchZoom(true);
//
//        chart2.getAxisLeft().setDrawGridLines(false);
//        chart2.getXAxis().setDrawGridLines(false);
//
//        chart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        chart2.getAxisRight().setEnabled(false);
//        chart2.getLegend().setTextColor(Color.WHITE);
//        chart2.animateXY(2000, 2000);
//        chart2.invalidate();
//        LineData data2 = new LineData();
//        // set data
//        chart2.setData(data2);
//        feedMultiple();
//        return v;
        View v = inflater.inflate(R.layout.fragment_blank2, container, false);

        title2 = v.findViewById(R.id.title2);
        price2 = v.findViewById(R.id.price2);

        title2.setText("대한항공");
        price2.setText("30,000원");
        auto_trade = v.findViewById(R.id.auto_button);
        auto_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), auto_trade.class);
                intent.putExtra("종목",title2.getText());
                startActivity(intent);
            }
        });
        chart2 = v.findViewById(R.id.line_chart2);
        // description text
        chart2.getDescription().setEnabled(true);
        Description des1 = chart2.getDescription();
        des1.setEnabled(true);
        des1.setText("Data Chart");
        des1.setTextSize(10f);
        des1.setTextColor(Color.WHITE);

        // touch gestures (false-비활성화)
        chart2.setTouchEnabled(true);
        // scaling and dragging (false-비활성화)
        chart2.setDragEnabled(true);
        chart2.setScaleEnabled(true);
        chart2.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        chart2.setPinchZoom(true);
        chart2.getAxisLeft().setDrawGridLines(false);
        chart2.getXAxis().setDrawGridLines(false);

        chart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart2.getAxisRight().setEnabled(false);
        chart2.getLegend().setTextColor(Color.BLACK);
        chart2.animateXY(2000, 2000);
        XAxis x = chart2.getXAxis();
        x.setAxisMinimum(40);
        x.setAxisMaximum(200);
        chart2.invalidate();

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
        chart2.setData(data);

        return v;
    }

//    private void addrealEntry(double num) {
//        LineData data = chart2.getData();
//        if (data == null) {
//            data = new LineData();
//            chart2.setData(data);
//        }
//        ILineDataSet set = data.getDataSetByIndex(0);
//        // set.addEntry(...); // can be called as well
//        if (set == null) {
//            set = createSet();
//            data.addDataSet(set);
//        }
//        data.addEntry(new Entry((float) set.getEntryCount(), (float) num), 0);
//        data.notifyDataChanged();
//        // let the chart know it's data has changed
//        chart2.notifyDataSetChanged();
//
//        chart2.setVisibleXRangeMaximum(150);
//        // this automatically refreshes the chart (calls invalidate())
//        chart2.moveViewTo(data.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
//    }
//    private LineDataSet createSet() {
//        LineDataSet set = new LineDataSet(null, "Dynamic Data");
//        set.setFillAlpha(110);
//        set.setFillColor(Color.parseColor("#d7e7fa"));
//        set.setColor(Color.parseColor("#0B80C9"));
//        set.setCircleColor(Color.parseColor("#FFA1B4DC"));
//        set.setCircleHoleColor(Color.BLUE);
//        set.setValueTextColor(Color.WHITE);
//        set.setDrawValues(false);
//        set.setLineWidth(2);
//        set.setCircleRadius(6);
//        set.setDrawCircleHole(false);
//        set.setDrawCircles(false);
//        set.setValueTextSize(9f);
//        set.setDrawFilled(true);
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setHighLightColor(Color.rgb(244, 117, 117));
//        return set;
//    }
//
//    private void feedMultiple() {
//        if (thread != null) thread.interrupt();
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                addrealEntry((float) (Math.random() * 40) + 30f);
//            }
//        };
//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    if(getActivity() != null)
//                        getActivity().runOnUiThread(runnable);
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException ie) {
//                        ie.printStackTrace();
//                    }
//                }
//            }
//        });
//        thread.start();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (thread != null) thread.interrupt();
//    }
}