package edu.skku2.map.ice3037;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Holder>{

    private Context context;
    private List<String> list = new ArrayList<>();

    public HomeAdapter(Context context, List<String> list){

        list.clear();

        for(int i=0;i<10;i++){
            list.add(i+"번째 아이템");
        }
        notifyDataSetChanged();
    }

//    class ViewHolder extends RecyclerView.ViewHolder{
//
//        private TextView item;
//        public ViewHolder(View itemView){
//            super(itemView);
//            item=(TextView)itemView.findViewById(R.id.item);
//        }
//    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.home_invest_item,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {

        int itemposition = i; //position
        holder.text.setText(list.get(itemposition));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        public TextView text;

        public Holder(View view) {
            super(view);
            /*TODO: Item Class와 연결되도록 Adapter 수정*/
//            text = (TextView) view.findViewById(R.id.item);
        }
    }
}
