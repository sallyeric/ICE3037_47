package edu.skku2.map.ice3037;

public class NewsItem {
    private String dateString; // 날짜
    private String timeString; // 시간
    private String titleString; // 제목
    private String linkString; // 링크
    private String mediaString; // 언론사

    public NewsItem(String date, String time, String title,String media, String link) {
        dateString = date;
        timeString = time;
        titleString = title;
        mediaString = media;
        linkString = link;
    }

    public void setDate(String dateString) { this.dateString = dateString; }
    public void setTime(String time) { timeString = time; }
    public void setMedia(String media) { mediaString = media; }
    public void setTitle(String title) { titleString = title; }
    public void setLink(String link) { linkString = link; }

    public String getDate() { return dateString; }
    public String getTime() { return timeString; }
    public String getTitle() { return titleString; }
    public String getMedia() { return mediaString; }
    public String getLink() { return linkString; }
}