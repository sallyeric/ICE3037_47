package edu.skku2.map.ice3037;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NewsItem> myDataList = null;
    InfoNewsAdapter(ArrayList<NewsItem> dataList)
    {
        myDataList = dataList;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView title;
        TextView media;
        ViewHolder(View itemView) {
            super(itemView) ;
            date = itemView.findViewById(R.id.news_date);
            time = itemView.findViewById(R.id.news_time);
            title = itemView.findViewById(R.id.news_title);
            media = itemView.findViewById(R.id.media);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.news_info, parent, false);
        return new InfoNewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder mholder =(ViewHolder)holder;
        mholder.date.setText(myDataList.get(position).getDate());
        mholder.time.setText(myDataList.get(position).getTime());
        mholder.title.setText(myDataList.get(position).getTitle());
        mholder.media.setText(myDataList.get(position).getMedia());

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter(){
            @Override
            public String transformUrl(Matcher match, String url){
                return "";
            }
        };
        String tmp = mholder.title.getText().toString();
        Pattern tmpp = Pattern.compile("\\[");
        Pattern tmpp2 = Pattern.compile("]");
        Matcher matcher = tmpp.matcher(tmp);
        String result = matcher.replaceAll("<");
        Matcher matcher2 = tmpp2.matcher(result);
        String result2 = matcher2.replaceAll(">");
        Pattern mPattern = Pattern.compile(result2);
        mholder.title.setText(result2);
        Linkify.addLinks(mholder.title, mPattern, myDataList.get(position).getLink(),null, mTransform);
        mholder.title.setLinkTextColor(Color.parseColor("#000000"));
        stripUnderlines(mholder.title);

    }
    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }
    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }
}
