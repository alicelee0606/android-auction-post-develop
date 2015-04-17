
package com.yahoo.mobile.android.auctionpost;

import java.util.List;

public class DetailSellPost extends SimpleSellPost {

    private List<Integer> payment;
    private List<Integer> delivery;
    private String username;
    private String showName;
    private byte[] showData;
    private String showUrl;
    private String email;
    
    public DetailSellPost() {
    }

    // get functions
    public List<Integer> getPayment() {
        return payment;
    }

    public List<Integer> getDelivery() {
        return delivery;
    }

    public String getUserName() {
        return username;
    }

    public String getShowName() {
        return showName;
    }

    public byte[] getShowData() {
        return showData;
    }

    public String getShowUrl() {
        return showUrl;
    }
    
    public String getEmail() {
    	return email;
    }

    // set functions
    public void setPayment(List<Integer> l) {
        this.payment = l;
    }

    public void setDelivery(List<Integer> l) {
        this.delivery = l;
    }

    public void setUserName(String s) {
        this.username = s;
    }

    public void setShowName(String s) {
        this.showName = s;
    }

    public void setShowData(byte[] arr) {
        this.showData = arr;
    }

    public void setShowUrl(String s) {
        this.showUrl = s;
    }
    
    public void setEmail(String s) {
    	this.email = s;
    }

}
