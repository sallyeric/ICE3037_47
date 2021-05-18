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

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Holder>{

    private Context context;
    private ArrayList<Item> mData;

    // 생성자에서 데이터 리스트 객체를 전달받음.
//    HomeAdapter(ArrayList<Item> list) {
//        mData = list ;
//    }

    public HomeAdapter(Context context, ArrayList<Item> mData) {
        this.context = context;
        this.mData = mData;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Context context = parent.getContext() ;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
//
//        View view = inflater.inflate(R.layout.home_invest_item, parent, false) ;
//        HomeAdapter.ViewHolder vh = new HomeAdapter.ViewHolder(view) ;
//
//        return vh ;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_invest_item, parent, false);

        Holder viewHolder = new Holder(view);

        return viewHolder;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(Holder holder, int position) {

//        Item item = mData.get(position);
        int itemposition = position;

        holder.logo.setImageDrawable(mData.get(itemposition).getLogo());
        holder.corp.setText(mData.get(itemposition).getCorp());
        holder.price.setText(mData.get(itemposition).getPrice());
        holder.updown.setText(mData.get(itemposition).getUpdown());
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
        TextView updown;

        public Holder(View view) {
            super(view);

            // 뷰 객체에 대한 참조. (hold strong reference)
            logo = (ImageView) view.findViewById(R.id.home_item_logo);
            corp = (TextView) view.findViewById(R.id.home_item_corp);
            price = (TextView) view.findViewById(R.id.home_item_price);
            updown = (TextView) view.findViewById(R.id.home_item_updown);

            Log.d("Contact", "make one");
        }
    }

    // 여기서부터 이전코드
//    private Context context;
//    private List<String> list = new ArrayList<>();
//
//    public HomeAdapter(Context context, List<String> list){
//
//        list.clear();
//
//        for(int i=0;i<10;i++){
//            list.add(i+"번째 아이템");
//        }
//        notifyDataSetChanged();
//    }
//
////    class ViewHolder extends RecyclerView.ViewHolder{
////
////        private TextView item;
////        public ViewHolder(View itemView){
////            super(itemView);
////            item=(TextView)itemView.findViewById(R.id.item);
////        }
////    }
//
//    @NonNull
//    @Override
//    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view= LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.home_invest_item,viewGroup,false);
//        return new Holder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull Holder holder, final int i) {
//
//        int itemposition = i; //position
//        holder.text.setText(list.get(itemposition));
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class Holder extends RecyclerView.ViewHolder{
//        public TextView text;
//
//        public Holder(View view) {
//            super(view);
//            /*TODO: Item Class와 연결되도록 Adapter 수정*/
////            text = (TextView) view.findViewById(R.id.item);
//        }
//    }
}
