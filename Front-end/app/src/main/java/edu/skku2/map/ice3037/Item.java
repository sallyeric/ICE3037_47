package edu.skku2.map.ice3037;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Item {
    private Drawable logoDrawable; // 회사 로고 이미지
    private String corpString; // 회사명
    private String priceString; // 시가총액
    private String updownString; // 변화량(%)

//    public Item () { }
//
    public Item(Drawable logo, String corp, String price, String updown) {
        logoDrawable = logo;
        corpString = corp;
        priceString = price;
        updownString = updown;
    }

    public void setLogo(Drawable logo) { logoDrawable = logo ; }
    public void setCorp(String corp) {
        corpString = corp ;
    }
    public void setPrice(String price) {
        priceString = price ;
    }
    public void setUpdown(String updown) {
        updownString = updown ;
    }

    public Drawable getLogo() { return logoDrawable; }

    public String getCorp() { return corpString; }

    public String getPrice() { return priceString; }

    public String getUpdown() { return updownString; }

//    public static ArrayList<Item> createItemsList(int items) {
//        ArrayList<Item> item_list = new ArrayList<Item>();
//
//        for (int i = 1; i <= items; i++) {
//            /*TODO: 로고 이미지 불러올 수 있도록 수정*/
//            item_list.add(new Item(" ", " ", " "));
//        }
//
//        return item_list;
//    }
}
