package com.yahoo.mobile.android.auctionpost.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.yahoo.mobile.android.auctionpost.ItemPageActivity;
import com.yahoo.mobile.android.auctionpost.ParseManager;
import com.yahoo.mobile.android.auctionpost.ParseManager.IPageListener;
import com.yahoo.mobile.android.auctionpost.ParseManager.IResultListener;
import com.yahoo.mobile.android.auctionpost.R;
import com.yahoo.mobile.android.auctionpost.SimpleSellPost;

public class ProfilePageFragment extends Fragment {
	
	TextView tv_username = null;
	GridView gridview = null;
	private boolean mIsUpdating = false;
	private ImageAdapter mImageAdapter;
	private int mBufferSize = 6;
	private int mCount = 1;
	
	OnScrollListener mOnScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView p, int scrollState) {
			
		}
		@Override
		public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (++firstVisibleItem + visibleItemCount > totalItemCount) {
				if (mIsUpdating==false) {
					mIsUpdating=true;
					new DownloadTask().execute();
				}
			}
		}
	};
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_page, container, false);
        tv_username = (TextView) rootView.findViewById(R.id.profile_name);
        gridview = (GridView) rootView.findViewById(R.id.product_grid);
 
        return rootView;
    }

	@Override
	public void onActivityCreated(Bundle savedInsatnceState) {
		super.onActivityCreated(savedInsatnceState);
		
		ParseManager parseManager = ParseManager.getInstance(getActivity().getApplicationContext());
		tv_username.setText(parseManager.getCurrentUsername());
		mImageAdapter = new ImageAdapter(getActivity());
		gridview.setAdapter(mImageAdapter);
		gridview.setOnScrollListener(mOnScrollListener);
	    
	}
	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private List<SimpleSellPost> mSimpleSellPost = new ArrayList<SimpleSellPost>();
	    private LayoutInflater adapterInflater = null;
	    
	    public ImageAdapter(Context c) {
	        mContext = c;
	        adapterInflater = LayoutInflater.from(c);
	    }

	    public int getCount() {
	        return mSimpleSellPost.size();
	    }

	    public Object getItem(int position) {
	        return mSimpleSellPost.get(position);
	    }

	    public long getItemId(int position) {
	        return position;
	    }
	    
	    class ProductHolder {
			ParseImageView viewImage;
			ToggleButton saleSwitch;
			ImageButton delProduct;
		}

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
		    
	    	ProductHolder holder;
			if (convertView==null) {
				convertView = adapterInflater.inflate(R.layout.grid_product, null);
				holder = new ProductHolder();
				holder.viewImage = (ParseImageView) convertView.findViewById(R.id.profile_product_image);
				holder.saleSwitch = (ToggleButton) convertView.findViewById(R.id.sale_state);
				holder.delProduct = (ImageButton) convertView.findViewById(R.id.del_product);
				convertView.setTag(holder);
				
			} else {
				holder = (ProductHolder) convertView.getTag();
			}
			
			final SimpleSellPost objSimpleSellPost = mSimpleSellPost.get(position);

//			Bitmap bitmap = BitmapFactory.decodeByteArray(objSimpleSellPost.getCoverData(), 0, objSimpleSellPost.getCoverData().length);
//			holder.viewImage.setImageBitmap(bitmap);
	        
			holder.viewImage.setTag(objSimpleSellPost.getCoverUrl());
			holder.viewImage.setParseFile(objSimpleSellPost.getParseFile());
			holder.viewImage.loadInBackground();
			
			holder.viewImage.setOnClickListener(new ImageView.OnClickListener() {
				@Override
				public void onClick(View v) {
					String mObjectId = objSimpleSellPost.getObjectId();
			        Log.d("objectId", mObjectId);

			        Bundle bundle = new Bundle();
			        bundle.putString("ObjectId", mObjectId);
			        Intent intent = new Intent(getActivity(), ItemPageActivity.class);
			        intent.putExtras(bundle);
			        Log.d("activity", "from profile page to item page");
			        getActivity().startActivity(intent);
				}
			});
			
			holder.saleSwitch.setChecked(objSimpleSellPost.getOnShelf());
			
			holder.saleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			    	String mObjectId = objSimpleSellPost.getObjectId();
			    	ParseManager parseManager = ParseManager.getInstance(getActivity().getApplicationContext());
			        if (isChecked) {
    					parseManager.manageSellPost(mObjectId, true, false, new IResultListener() {

    						@Override
    						public void onSuccess() {
    						}

    						@Override
    						public void onFail() {
    						}
    						
    					});
			        	
			        } else {
			        	parseManager.manageSellPost(mObjectId, false, false, new IResultListener() {

    						@Override
    						public void onSuccess() {
    						}

    						@Override
    						public void onFail() {
    						}
    						
    					});
			        }
			    }
			});
		
			holder.delProduct.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
	                dialog.setTitle("確定要刪除？");
	                dialog.setCancelable(false);

	                dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    	String mObjectId = objSimpleSellPost.getObjectId();
	    					boolean mOnShelf = objSimpleSellPost.getOnShelf();
	    					ParseManager parseManager = ParseManager.getInstance(getActivity().getApplicationContext());
	    					parseManager.manageSellPost(mObjectId, mOnShelf, true, new IResultListener() {

	    						@Override
	    						public void onSuccess() {
	    						    ProfilePageFragment currentFragment = (ProfilePageFragment) getFragmentManager().findFragmentByTag("Profile_Fragment");
	    						    FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
	    						    fragTransaction.detach(currentFragment);
	    						    fragTransaction.attach(currentFragment);
	    						    fragTransaction.commit();
	    						}

	    						@Override
	    						public void onFail() {
	    						}
	    						
	    					});
	                    }
	                });

	                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        // TODO Auto-generated method stub

	                    }
	                });
	                dialog.show();
					
				}
				
			});
			return convertView;
	    }
	    
		public void refreshItem() {	
			Log.d("refresh", "start refreshing...");
			
			adapterInflater = LayoutInflater.from(getActivity());
			
			ParseManager parseManager = ParseManager.getInstance(getActivity());
            parseManager.queryProfileLoad(mCount, mBufferSize, new IPageListener() {
				
				@Override
				public void onSuccess() {
					
				}
				@Override
				public void onFail() {
					
				}
				@Override
				public void onComplete(List<SimpleSellPost> list) {
					ImageAdapter.this.mSimpleSellPost.addAll(list);
					if (!list.isEmpty()) {
						mImageAdapter.notifyDataSetChanged();
						mCount += list.size();
					}
					mIsUpdating = false;
				}
			});
			
			Log.d("refresh", "finish refreshing!");
		}


	}
	
	private class DownloadTask extends AsyncTask<Void,Void,Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d("profile", "onPreExecute async update");
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mIsUpdating=true;
//			if (mIsScrollingBack==false) {
				Log.d("profile", "doInBackground async update");
				mImageAdapter.refreshItem();
				Log.d("profile", "doInBackground async refresh update");
//			}

			return null;
		}
	}

}
