package edu.skku2.map.ice3037;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<ItemMyPage> myDataList = null;

    MyPageAdapter(ArrayList<ItemMyPage> dataList)
    {
        myDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewTypeCode.ViewType.BUY)
        {
            view = inflater.inflate(R.layout.item_buy, parent, false);
            return new BuyViewHolder(view);
        }
//        else if
        if(viewType == ViewTypeCode.ViewType.SELL_LOSS)
        {
            view = inflater.inflate(R.layout.item_sell_loss, parent, false);
            return new SellLossViewHolder(view);
        }
        else
        {
            view = inflater.inflate(R.layout.item_sell_profit, parent, false);
            return new SellProfitViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if(viewHolder instanceof BuyViewHolder)
        {
            ((BuyViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
            ((BuyViewHolder) viewHolder).corp.setText(myDataList.get(position).getCorp());
            ((BuyViewHolder) viewHolder).price.setText(myDataList.get(position).getPrice());
            ((BuyViewHolder) viewHolder).stock.setText(myDataList.get(position).getStock());
        }
        else if(viewHolder instanceof SellLossViewHolder)
        {
            ((SellLossViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
            ((SellLossViewHolder) viewHolder).corp.setText(myDataList.get(position).getCorp());
            ((SellLossViewHolder) viewHolder).price.setText(myDataList.get(position).getPrice());
            ((SellLossViewHolder) viewHolder).stock.setText(myDataList.get(position).getStock());
            ((SellLossViewHolder) viewHolder).profit.setText(myDataList.get(position).getProfit());
            ((SellLossViewHolder) viewHolder).percent.setText(myDataList.get(position).getPercent());
        }
        else
        {
            ((SellProfitViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
            ((SellProfitViewHolder) viewHolder).corp.setText(myDataList.get(position).getCorp());
            ((SellProfitViewHolder) viewHolder).price.setText(myDataList.get(position).getPrice());
            ((SellProfitViewHolder) viewHolder).stock.setText(myDataList.get(position).getStock());
            ((SellProfitViewHolder) viewHolder).profit.setText(myDataList.get(position).getProfit());
            ((SellProfitViewHolder) viewHolder).percent.setText(myDataList.get(position).getPercent());
        }
    }

    @Override
    public int getItemCount()
    {
        return myDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return myDataList.get(position).getViewType();
    }

    public class BuyViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView corp;
        TextView price;
        TextView stock;

        BuyViewHolder(View itemView)
        {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            corp = itemView.findViewById(R.id.corp);
            price = itemView.findViewById(R.id.price);
            stock = itemView.findViewById(R.id.stock);
        }
    }

    public class SellLossViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView corp;
        TextView price;
        TextView stock;
        TextView profit;
        TextView percent;

        SellLossViewHolder(View itemView)
        {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            corp = itemView.findViewById(R.id.corp);
            price = itemView.findViewById(R.id.price);
            stock = itemView.findViewById(R.id.stock);
            profit = itemView.findViewById(R.id.profit);
            percent = itemView.findViewById(R.id.percent);
        }
    }

    public class SellProfitViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView corp;
        TextView price;
        TextView stock;
        TextView profit;
        TextView percent;

        SellProfitViewHolder(View itemView)
        {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            corp = itemView.findViewById(R.id.corp);
            price = itemView.findViewById(R.id.price);
            stock = itemView.findViewById(R.id.stock);
            profit = itemView.findViewById(R.id.profit);
            percent = itemView.findViewById(R.id.percent);
        }
    }

}
