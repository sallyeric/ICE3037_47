package edu.skku2.map.ice3037;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
        View v = inflater.inflate(R.layout.fragment_blank2, container, false);

        chart2 = v.findViewById(R.id.line_chart2);
        // description text
        chart2.getDescription().setEnabled(true);
        Description des2 = chart2.getDescription();
        des2.setEnabled(true);
        des2.setText("Real-time Chart");
        des2.setTextSize(10f);
        des2.setTextColor(Color.WHITE);

        // touch gestures (false-비활성화)
        chart2.setTouchEnabled(true);
        // scaling and dragging (false-비활성화)
        chart2.setDragEnabled(true);
        chart2.setScaleEnabled(true);
        chart2.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        chart2.setPinchZoom(true);

        chart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart2.getAxisRight().setEnabled(false);
        chart2.getLegend().setTextColor(Color.WHITE);
        chart2.animateXY(2000, 2000);
        chart2.invalidate();
        LineData data2 = new LineData();
        // set data
        chart2.setData(data2);
        feedMultiple();
        return v;
    }

    private void addrealEntry(double num) {
        LineData data = chart2.getData();
        if (data == null) {
            data = new LineData();
            chart2.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well
        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }
        data.addEntry(new Entry((float) set.getEntryCount(), (float) num), 0);
        data.notifyDataChanged();
        // let the chart know it's data has changed
        chart2.notifyDataSetChanged();

        chart2.setVisibleXRangeMaximum(150);
        // this automatically refreshes the chart (calls invalidate())
        chart2.moveViewTo(data.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
    }
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setFillAlpha(110);
        set.setFillColor(Color.parseColor("#d7e7fa"));
        set.setColor(Color.parseColor("#0B80C9"));
        set.setCircleColor(Color.parseColor("#FFA1B4DC"));
        set.setCircleHoleColor(Color.BLUE);
        set.setValueTextColor(Color.WHITE);
        set.setDrawValues(false);
        set.setLineWidth(2);
        set.setCircleRadius(6);
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        return set;
    }

    private void feedMultiple() {
        if (thread != null) thread.interrupt();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addrealEntry((float) (Math.random() * 40) + 30f);
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    getActivity().runOnUiThread(runnable);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null) thread.interrupt();
    }
}