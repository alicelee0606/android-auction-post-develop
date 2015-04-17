
package com.yahoo.mobile.android.auctionpost;

import com.parse.ParseFile;


public class SimpleSellPost {

	private String objectId;
	private String name;
	private int price;
	private String description;
	private boolean isVd;
	private int tagColor;
	private String coverName;
	private byte[] coverData;
	private String coverUrl;
	private boolean onShelf = true;
	private int numberOfClick;
	
	// add by Jean
	private ParseFile parseFile;
	public void setParseFile(ParseFile f) {
		this.parseFile=f;
	}
	public ParseFile getParseFile() {
		return parseFile;
	}
	
	public SimpleSellPost(){
	}
	
	// get functions
	public String getObjectId(){
	    return objectId;
	}
	
	public String getName(){
	    return name;
	}
	
	public int getPrice(){
	    return price;
	}
	
	public String getDescription(){
	    return description;
	}
	
	public boolean getIsVd(){
		return isVd;
	}
	
	public int getTagColor(){
		return tagColor;
	}
	
	public String getCoverName(){
	    return coverName;
	}
	
	public byte[] getCoverData(){
		return coverData;
	}
	
	public String getCoverUrl(){
		return coverUrl;
	}

	public boolean getOnShelf(){
		return onShelf;
	}
	
	public int getNumberOfClick() {
		return numberOfClick;
	}
	
	// set functions
	public void setObjectId(String s){
		this.objectId = s;
	}
	
	public void setName(String s){
		this.name = s;
	}
	
	public void setPrice(int n){
		this.price = n;
	}
	
	public void setDescription(String s){
		this.description = s;
	}
	
	public void setIsVd(boolean b){
		this.isVd = b;
	}
	
	public void setTagColor(int n){
        this.tagColor = n;
	}
	
	public void setCoverName(String s){
		this.coverName = s;
	}
	
	public void setCoverData(byte[] arr){
		this.coverData = arr;
	}
  
	public void setCoverUrl(String s){
		this.coverUrl = s;
	}
	
	public void setOnShelf(boolean b) {
		this.onShelf = b;
	}
	
	public void setNumberOfClick(int n) {
		this.numberOfClick = n;
	}
}

