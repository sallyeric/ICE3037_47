package edu.skku2.map.ice3037;

public class NewsItem {
    private String timeString; // 날짜
    private String titleString; // 제목
    private String linkString; // 링크
    private String mediaString; // 언론사

    public NewsItem(String time, String title,String media, String link) {
        timeString = time;
        titleString = title;
        mediaString = media;
        linkString = link;
    }
    public void setTime(String time) { timeString = time; }
    public void setMedia(String media) { mediaString = media; }
    public void setTitle(String title) { titleString = title; }
    public void setLink(String link) { linkString = link; }

    public String getTime() { return timeString; }
    public String getTitle() { return titleString; }
    public String getMedia() { return mediaString; }
    public String getLink() { return linkString; }
}