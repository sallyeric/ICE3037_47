package edu.skku2.map.ice3037;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

// 회사명, 매도/매수금액, 몇 주, 시간, 수익/손실, 수익률/손실률

public class ItemMyPage {
    private String timeString; // 시간
    private String corpString; // 회사명
    private String priceString; // 매수금액
    private String stockString; // 몇 주
    private String profitString; // 수익/손실
    private String percentString; // 수익률/손실률
    private int viewTypeInt;

    public ItemMyPage(String corp, String price, String stock, String time, String profit, String percent, int viewType) {
        timeString = time;
        corpString = corp;
        priceString = price;
        stockString = stock;
        profitString = profit;
        percentString = percent;
        viewTypeInt = viewType;
    }

    public void setTime(String time) { timeString = time; }
    public void setCorp(String corp) { corpString = corp; }
    public void setPrice(String price) { priceString = price; }
    public void setStock(String stock) { stockString = stock; }
    public void setProfit(String profit) { profitString = profit; }
    public void setPercent(String percent) { percentString = percent; }

    public String getTime() { return timeString; }
    public String getCorp() { return corpString; }
    public String getPrice() { return priceString; }
    public String getStock() { return stockString; }
    public String getProfit() { return profitString; }
    public String getPercent() { return percentString; }
    public int getViewType() { return viewTypeInt; }
}
