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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Holder>{

    private Context context;
    private ArrayList<Item> mData;
    private int mtype;

    public HomeAdapter(Context context, ArrayList<Item> mData) {
        this.context = context;
        this.mData = mData;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_invest_item, parent, false);
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
        holder.eval.setText(mData.get(itemposition).getEval());
        holder.updown.setText(mData.get(itemposition).getUpdown());
        holder.count.setText(mData.get(itemposition).getSize());
        if(mData.get(itemposition).getUp())
            holder.updown.setTextColor(0xAAff0000);
        else
            holder.updown.setTextColor(0xAA0000ff);
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
        TextView eval;
        TextView updown;
        TextView count;
        public Holder(View view) {
            super(view);
            // 뷰 객체에 대한 참조. (hold strong reference)
            logo = (ImageView) view.findViewById(R.id.home_item_logo);
            corp = (TextView) view.findViewById(R.id.home_item_corp);
            price = (TextView) view.findViewById(R.id.home_item_price);
            eval = (TextView) view.findViewById(R.id.home_item_eval);
            updown = (TextView) view.findViewById(R.id.home_item_updown);
            count = (TextView) view.findViewById(R.id.home_item_count);
            Log.d("Contact", "make one");
        }
    }
}
