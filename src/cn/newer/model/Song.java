package cn.newer.model;

public class Song {
	
	 private String title ;  //����������
	 private String artist ; //����������
	 private long durtion ;  //������ʱ��
	 private String data ;  //������·��
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public long getDurtion() {
		return durtion;
	}
	public void setDurtion(long durtion) {
		this.durtion = durtion;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Song [title=" + title + ", artist=" + artist + ", durtion="
				+ durtion + ", data=" + data + "]";
	}
	 
	 


}
