package edu.skku2.map.ice3037;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

// 현재가, 평가금액, 수익률

public class Item {
    private Drawable logoDrawable; // 회사 로고 이미지
    private String corpString; // 회사명
    private String priceString; // 현재가
    private String evalString; // 평가금액
    private String updownString; // 변화량(%)
    private Boolean up;
    private String sizeString;
//    public Item(Drawable logo, String corp, String price, String eval, String updown) {
    public Item(Drawable logo, String corp, String price, String eval, String updown, Boolean flag, String size) {
        logoDrawable = logo;
        corpString = corp;
        priceString = price;
        evalString = eval;
        updownString = updown;
        sizeString = size;
        up = flag;
    }

    public void setLogo(Drawable logo) { logoDrawable = logo ; }
    public void setCorp(String corp) {
        corpString = corp ;
    }
    public void setPrice(String price) {
        priceString = price ;
    }
    public void setEval(String eval) {
        evalString = eval ;
    }
    public void setUpdown(String updown) {
        updownString = updown ;
    }
    public void setUp(Boolean up) { this.up = up; }

    public Drawable getLogo() { return logoDrawable; }
    public String getCorp() { return corpString; }
    public String getPrice() { return priceString; }
    public String getEval() { return evalString; }
    public String getUpdown() { return updownString; }
    public String getSize() {return sizeString;}
    public Boolean getUp() { return up; }
}
