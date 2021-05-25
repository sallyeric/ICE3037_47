package edu.skku2.map.ice3037;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

// 현재가, 평가금액, 수익률

public class Item2 {
    private Drawable logoDrawable; // 회사 로고 이미지
    private String corpString; // 회사명
    private String priceString; // 현재가

    //    public Item(Drawable logo, String corp, String price, String eval, String updown) {
    public Item2(Drawable logo, String corp, String price) {
        logoDrawable = logo;
        corpString = corp;
        priceString = price;
    }

    public void setLogo(Drawable logo) { logoDrawable = logo ; }
    public void setCorp(String corp) {
        corpString = corp ;
    }
    public void setPrice(String price) {
        priceString = price ;
    }

    public Drawable getLogo() { return logoDrawable; }
    public String getCorp() { return corpString; }
    public String getPrice() { return priceString; }
}
