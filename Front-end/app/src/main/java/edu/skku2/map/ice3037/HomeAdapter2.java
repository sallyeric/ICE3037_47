package edu.skku2.map.ice3037;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter2 extends RecyclerView.Adapter<HomeAdapter2.Holder>{

    private Context context;
    private ArrayList<Item2> mData;

    public HomeAdapter2(Context context, ArrayList<Item2> mData) {
        this.context = context;
        this.mData = mData;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_trade_item, parent, false);
            Holder viewHolder = new Holder(view);
            return viewHolder;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(Holder holder, int position) {

        int itemposition = position;
            holder.logo.setImageDrawable(mData.get(itemposition).getLogo());
            holder.corp.setText(mData.get(itemposition).getCorp());
            holder.price.setText(mData.get(itemposition).getPrice());
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class Holder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView corp;
        TextView price;
        public Holder(View view) {
            super(view);
                logo = (ImageView) view.findViewById(R.id.home_item_logo);
                corp = (TextView) view.findViewById(R.id.home_item_corp);
                price = (TextView) view.findViewById(R.id.home_item_price);
        }
    }
}
