package edu.skku2.map.ice3037;

import java.util.ArrayList;

public class Item {
    private int logo; // 회사 로고 이미지
    private String corp; // 회사명
    private String price; // 시가총액
    private String updown; // 변화량(%)

    public Item () { }

    public Item(int logo, String corp, String price, String updown) {
        this.logo = logo;
        this.corp = corp;
        this.price = price;
        this.updown = updown;
    }

    public int getLogo() {
        return logo;
    }

    public String getCorp() {
        return corp;
    }

    public String getPrice() {
        return price;
    }

    public String getUpdown() {
        return updown;
    }

    public static ArrayList<Item> createItemsList(int items) {
        ArrayList<Item> item_list = new ArrayList<Item>();

        for (int i = 1; i <= items; i++) {
            /*TODO: 로고 이미지 불러올 수 있도록 수정*/
//            item_list.add(new Item(logo, " ", " ", " "));
        }

        return item_list;
    }
}
