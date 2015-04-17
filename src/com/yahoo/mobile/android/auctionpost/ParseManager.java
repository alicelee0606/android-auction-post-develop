
package com.yahoo.mobile.android.auctionpost;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class ParseManager {

    private static ParseManager instance;
    private static boolean mIsLogIn;

    private ParseManager(Context app) {
        // initialization with Parse
        String appId = "xzeBie4GRu8TQu2qC02MJpyZ7LT8XAeagmES6vRS";
        String appClientKey = "5FpjKKRdMjiu5q9v6lloIehrzWypnRUHmjvnnqFx";
        Parse.initialize(app, appId, appClientKey);
    }

    public static ParseManager getInstance(Context app) {
        if (instance == null) {
            instance = new ParseManager(app);
        }
        return instance;
    }

    public void signUp(String userName, String passWord, String eMail, final IResultListener listener) {
        // function of creating a new user id
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(passWord);
        user.setEmail(eMail);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    listener.onSuccess();
                } else {
                    listener.onFail();
                }
            }
        });
    }

    public void logIn(String userName, String passWord, final IResultListener listener) {
        // function of logging in
        ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    mIsLogIn = true;
                    listener.onSuccess();
                } else {
                    mIsLogIn = false;
                    listener.onFail();
                }
            }
        });
    }
    
    public String getCurrentUsername() {
    	return ParseUser.getCurrentUser().getUsername();
    }
    
    public void currentLogOut() {
    	ParseUser.logOut();
    	mIsLogIn = false;
    }

    public boolean checkLogIn() {
        return mIsLogIn;
    }

    public void createSellPost(DetailSellPost detailSellPost, final IPostListener listener) {
        // function for creating a ParseObject
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final ParseObject sellPost = new ParseObject("SellPost");
            sellPost.put("name", detailSellPost.getName());
            sellPost.put("price", detailSellPost.getPrice());
            sellPost.put("author", currentUser);

            if (detailSellPost.getDescription() != null) {
                sellPost.put("description", detailSellPost.getDescription());
            }

            sellPost.put("isVd", detailSellPost.getIsVd());
            sellPost.put("tagColor", detailSellPost.getTagColor());
            sellPost.put("payment", detailSellPost.getPayment());
            sellPost.put("delivery", detailSellPost.getDelivery());
            sellPost.put("onShelf", detailSellPost.getOnShelf());
            sellPost.put("numberOfClick", 0);

            ParseFile tempFile = new ParseFile(detailSellPost.getShowName(), detailSellPost.getShowData());
            tempFile.saveInBackground();
            sellPost.put("showFile", tempFile);

            if (detailSellPost.getIsVd()) {
                ParseFile coverFile = new ParseFile(detailSellPost.getCoverName(), detailSellPost.getCoverData());
                coverFile.saveInBackground();
                sellPost.put("coverFile", coverFile);
            }
          
            sellPost.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        listener.onComplete(sellPost.getObjectId());
                    } else {
                        listener.onFail();
                    }
                }
            });

        } else {
            listener.onFail();
        }
    }

    public interface IResultListener {
        public void onSuccess();

        public void onFail();
    }

    public interface ILoadListener extends IResultListener {
        public void onComplete(DetailSellPost resultDetailSellPost);
    }

    public interface IPageListener extends IResultListener {
        public void onComplete(List<SimpleSellPost> sellList);
    }

    public interface IPostListener extends IResultListener {
        public void onComplete(String objectId);
    } 
    
    public void queryPageLoad(int tagColor, int startPoint, int range, final IPageListener listener) {
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("SellPost");
    	query.orderByDescending("createdAt");
    	query.whereEqualTo("onShelf", true);
    	if (tagColor != 0) {
    		query.whereEqualTo("tagColor",tagColor);
    	}
    	query.setSkip(startPoint-1);
    	query.setLimit(range);
    	query.findInBackground(new FindCallback<ParseObject>() {
    	    public void done(List<ParseObject> commentList, ParseException e) {
    	        Log.i("queryPageLoad", "load done!");
    	    	List<SimpleSellPost> sellList = new ArrayList<SimpleSellPost> (); 
    		    for (int i = 0 ; i < commentList.size(); i++){
    		    	SimpleSellPost tempSellPost = new SimpleSellPost();
    		    	tempSellPost.setObjectId(commentList.get(i).getObjectId());
    		    	tempSellPost.setName(commentList.get(i).getString("name"));
    		    	tempSellPost.setPrice(commentList.get(i).getInt("price"));
    		    	tempSellPost.setDescription(commentList.get(i).getString("description"));
    		    	tempSellPost.setIsVd(commentList.get(i).getBoolean("isVd"));
    		    	tempSellPost.setTagColor(commentList.get(i).getInt("tagColor"));
    		    	tempSellPost.setOnShelf(commentList.get(i).getBoolean("onShelf"));
    		    	tempSellPost.setNumberOfClick(commentList.get(i).getInt("numberOfClick"));

    		    	if (commentList.get(i).getBoolean("isVd")) {
    		    		tempSellPost.setCoverUrl(commentList.get(i).getParseFile("coverFile").getUrl());
    		    		
    		    		// add by Jean
    		    		tempSellPost.setParseFile(commentList.get(i).getParseFile("coverFile"));
    		    		
        		      	tempSellPost.setCoverName(commentList.get(i).getParseFile("coverFile").getName());
    	    		    try {
    						tempSellPost.setCoverData(commentList.get(i).getParseFile("coverFile").getData());
    					} catch (ParseException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}  		
    		    	} else {
    		    		tempSellPost.setCoverUrl(commentList.get(i).getParseFile("showFile").getUrl());
    		    		
    		    		// add by Jean
    		    		tempSellPost.setParseFile(commentList.get(i).getParseFile("showFile"));
    		    		
        		      	tempSellPost.setCoverName(commentList.get(i).getParseFile("showFile").getName());
    	    		    try {
    						tempSellPost.setCoverData(commentList.get(i).getParseFile("showFile").getData());
    					} catch (ParseException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}  		
    		    		
    		    	}
    		    	sellList.add(tempSellPost);
    		    }
    		    listener.onComplete(sellList);
                Log.i("queryPageLoad", "sellList done!");
    		    
    		}
    	});
    }

    
    public void queryProfileLoad(int startPoint, int range, final IPageListener listener) {
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("SellPost");
    	query.orderByDescending("createdAt");
    	query.whereEqualTo("author",ParseUser.getCurrentUser());
    	query.setSkip(startPoint-1);
    	query.setLimit(range);
    	query.findInBackground(new FindCallback<ParseObject>() {
    	    public void done(List<ParseObject> commentList, ParseException e) {
    	        Log.i("queryProfileLoad", "load done!");
    	    	List<SimpleSellPost> sellList = new ArrayList<SimpleSellPost> (); 
    		    for (int i = 0 ; i < commentList.size(); i++){
    		    	SimpleSellPost tempSellPost = new SimpleSellPost();
    		    	tempSellPost.setObjectId(commentList.get(i).getObjectId());
    		    	tempSellPost.setName(commentList.get(i).getString("name"));
    		    	tempSellPost.setPrice(commentList.get(i).getInt("price"));
    		    	tempSellPost.setDescription(commentList.get(i).getString("description"));
    		    	tempSellPost.setIsVd(commentList.get(i).getBoolean("isVd"));
    		    	tempSellPost.setTagColor(commentList.get(i).getInt("tagColor"));
    		    	tempSellPost.setOnShelf(commentList.get(i).getBoolean("onShelf"));
    		    	tempSellPost.setNumberOfClick(commentList.get(i).getInt("numberOfClick"));

    		    	if (commentList.get(i).getBoolean("isVd")) {
    		    		tempSellPost.setCoverUrl(commentList.get(i).getParseFile("coverFile").getUrl());
    		    		
    		    		// add by Jean
    		    		tempSellPost.setParseFile(commentList.get(i).getParseFile("coverFile"));
    		    		
        		      	tempSellPost.setCoverName(commentList.get(i).getParseFile("coverFile").getName());
    	    		    try {
    						tempSellPost.setCoverData(commentList.get(i).getParseFile("coverFile").getData());
    					} catch (ParseException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}  		
    		    	} else {
    		    		tempSellPost.setCoverUrl(commentList.get(i).getParseFile("showFile").getUrl());
    		    		
    		    		// add by Jean
    		    		tempSellPost.setParseFile(commentList.get(i).getParseFile("showFile"));
    		    		
        		      	tempSellPost.setCoverName(commentList.get(i).getParseFile("showFile").getName());
    	    		    try {
    						tempSellPost.setCoverData(commentList.get(i).getParseFile("showFile").getData());
    					} catch (ParseException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}  		
    		    		
    		    	}
    		    	sellList.add(tempSellPost);
    		    }
    		    listener.onComplete(sellList);
    		}
    	});
    	
    }
    
    public void manageSellPost(String objectId, final boolean onShelf, final boolean del, final IResultListener listener) {
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("SellPost");
    	query.getInBackground(objectId, new GetCallback<ParseObject>() {
    		@Override
    		public void done(ParseObject sellPost, ParseException e) {
	    	    if (e == null) {
	    	    	if (del) {
	    	    		sellPost.deleteInBackground();
	    	    	} else {
	    	    		sellPost.put("onShelf", onShelf);
	    	    		sellPost.saveInBackground();
	    	    	}
	    	    	listener.onSuccess();
	    	        
	    	    } else {
	    	        listener.onFail();
	    	    }
    	  }
    	});
    }
    
    public void loadSellPost(String objectId, final ILoadListener listener) {
    	Log.d("parse", "loadSellPost");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SellPost");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject sellPost, ParseException e) {
                if (e == null) { // successfully

                    DetailSellPost detailSellPost = new DetailSellPost();
                    detailSellPost.setObjectId(sellPost.getObjectId());
                    detailSellPost.setName(sellPost.getString("name"));
                    detailSellPost.setPrice(sellPost.getInt("price"));
                    detailSellPost.setDescription(sellPost.getString("description"));
                    detailSellPost.setIsVd(sellPost.getBoolean("isVd"));
                    detailSellPost.setTagColor(sellPost.getInt("tagColor"));
                    int tempNumberOfClick = sellPost.getInt("numberOfClick") + 1;
                    sellPost.put("numberOfClick", tempNumberOfClick);
                    sellPost.saveInBackground(new SaveCallback() {
	                        @Override
	                        public void done(ParseException e) {
	                            if (e == null) {
	                       
	                            } else {
	                               
	                            }
	                        }
	                    });
                    detailSellPost.setNumberOfClick(sellPost.getInt("numberOfClick"));
             
                    ParseUser author =  sellPost.getParseUser("author");
                    try {
						author.fetchIfNeeded();
						detailSellPost.setUserName(author.getUsername());
						detailSellPost.setEmail(author.getEmail());
					} catch (ParseException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
                    
                    detailSellPost.setPayment((ArrayList<Integer>)sellPost.get("payment"));
                    detailSellPost.setDelivery((ArrayList<Integer>)sellPost.get("delivery"));

                    detailSellPost.setShowName(sellPost.getParseFile("showFile").getName());
                    try {
                        detailSellPost.setShowData(sellPost.getParseFile("showFile").getData());
                    } catch (ParseException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    detailSellPost.setShowUrl(sellPost.getParseFile("showFile").getUrl());
                    listener.onComplete(detailSellPost); // do notification via callback function*/

                } else {
                    listener.onFail();
                }
            }
        });
    }

}
