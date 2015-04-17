
package com.yahoo.mobile.android.auctionpost.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.yahoo.mobile.android.auctionpost.ItemPageActivity;
import com.yahoo.mobile.android.auctionpost.ParseManager;
import com.yahoo.mobile.android.auctionpost.ParseManager.IPageListener;
import com.yahoo.mobile.android.auctionpost.R;
import com.yahoo.mobile.android.auctionpost.SimpleSellPost;

public class StreamFragment extends ListFragment {
	
	private ListViewAdapter mListAdapter;
	
	private int mBufferSize = 1;
	private int mCount = 1;
	private boolean mIsUpdating = false;
	private String mObjectId;

	
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
	public void onActivityCreated(Bundle savedInsatnceState) {
		super.onActivityCreated(savedInsatnceState);	
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.i("Stream onResume", "setListAdapter");
		mCount = 1;
		mListAdapter = new ListViewAdapter(getActivity());
		setListAdapter(mListAdapter);
		getListView().setOnScrollListener(mOnScrollListener);
	}
	
	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Log.d("click", "onListItemClick");
        SimpleSellPost objSimpleSellPost = (SimpleSellPost)l.getItemAtPosition(position);
        mObjectId = objSimpleSellPost.getObjectId();
        Log.d("objectId", mObjectId);

        Bundle bundle = new Bundle();
        bundle.putString("ObjectId", mObjectId);
        Intent intent = new Intent(getActivity(), ItemPageActivity.class);
        intent.putExtras(bundle);
        Log.d("activity", "from stream page to item page");
        getActivity().startActivity(intent);
    }

	private class ListViewAdapter extends BaseAdapter {
		
		private List<SimpleSellPost> mListSimpleSellPost = new ArrayList<SimpleSellPost>();
		private LayoutInflater adapterInflater = null;
	
		public ListViewAdapter(Context context) {
			adapterInflater = LayoutInflater.from(context);
			Log.d("list", "list constructor");
			this.refreshItem();
			mIsUpdating = true;
		}
		
		@Override
		public int getCount() {
			return mListSimpleSellPost.size();
		}
		
		@Override
		public Object getItem(int position) {
			return mListSimpleSellPost.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		class ViewHolder {
			TextView viewName;
			ParseImageView viewImage;
			ImageView imgPrice;
			TextView textPrice;
			TextView textBrowse;
			TextView viewDescription;
			RelativeLayout viewLayout;
		}
		
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView==null) {
				convertView = adapterInflater.inflate(R.layout.list_stream_item, null);
				holder = new ViewHolder();
				holder.viewImage = (ParseImageView) convertView.findViewById(R.id.img_item);
				holder.imgPrice = (ImageView) convertView.findViewById(R.id.img_item_price);
				holder.textPrice = (TextView) convertView.findViewById(R.id.text_item_price);
				holder.textBrowse = (TextView) convertView.findViewById(R.id.number_browse);
				holder.viewLayout = (RelativeLayout) convertView.findViewById(R.id.layout_listview);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			SimpleSellPost objSimpleSellPost = mListSimpleSellPost.get(position);
			
			holder.viewImage.setPlaceholder(getResources().getDrawable(R.drawable.img_placeholder));
			holder.viewImage.setTag(objSimpleSellPost.getCoverUrl());
			holder.viewImage.setParseFile(objSimpleSellPost.getParseFile());
			holder.viewImage.loadInBackground(new GetDataCallback() {
			   @Override
			   public void done(byte[] data, ParseException e) {
//			     Log.i("ParseImageView",
//			         "Fetched! Data length: " + data.length + ", or exception: " + e.getMessage());
			   }
			 });
			
			byte[] data = objSimpleSellPost.getCoverData();
			Log.d("Stream", "obj Name="+objSimpleSellPost.getCoverName());
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//			holder.viewImage.setImageBitmap(bitmap);
			Log.d("size", "stream data size = "+data.length);
			final double viewWidthToBitmapWidthRatio = (double)holder.viewImage.getWidth() / (double)bitmap.getWidth();
			holder.viewImage.getLayoutParams().height = (int) (bitmap.getHeight() * viewWidthToBitmapWidthRatio);
			
			switch (objSimpleSellPost.getTagColor()+1) {
			case 1:
				holder.imgPrice.setImageDrawable(getResources().getDrawable(R.drawable.tag_color_1));
				break;
			case 2:
				holder.imgPrice.setImageDrawable(getResources().getDrawable(R.drawable.tag_color_2));
				break;
			case 3:
				holder.imgPrice.setImageDrawable(getResources().getDrawable(R.drawable.tag_color_3));
				break;
			case 4:
				holder.imgPrice.setImageDrawable(getResources().getDrawable(R.drawable.tag_color_4));
				break;
			case 5:
				holder.imgPrice.setImageDrawable(getResources().getDrawable(R.drawable.tag_color_5));
				break;
			case 6:
				holder.imgPrice.setImageDrawable(getResources().getDrawable(R.drawable.tag_color_6));
				break;
				
			default:
				holder.imgPrice.setImageDrawable(getResources().getDrawable(R.drawable.tag_color_1));
			}
			holder.textPrice.setText(Integer.toString(objSimpleSellPost.getPrice()));
			holder.textBrowse.setText(Integer.toString(objSimpleSellPost.getNumberOfClick()));
			
			holder.imgPrice.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("click", "price is clicked");
				}
			});
			
			holder.viewImage.setOnClickListener(new ImageView.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					Log.d("click", "one item is clicked in onListItemClick");
					onListItemClick(StreamFragment.this.getListView(), v, position, position);
				}
			});
			
			return convertView;
		}
		
		public void refreshItem() {	
			Log.d("refresh", "start refreshing...");
			
			adapterInflater = LayoutInflater.from(getActivity());
			
			ParseManager parseManager = ParseManager.getInstance(getActivity());
			
			parseManager.queryPageLoad(0, mCount, mBufferSize, new IPageListener() {
				
				@Override
				public void onSuccess() {
					
				}
				@Override
				public void onFail() {
					
				}
				@Override
				public void onComplete(List<SimpleSellPost> list) {
					ListViewAdapter.this.mListSimpleSellPost.addAll(list);
					if (!list.isEmpty()) {
						Log.i("kk query",Integer.toString(mCount));
						mListAdapter.notifyDataSetChanged();
						mCount += list.size();
					}
					mIsUpdating = false;
					Log.d("refresh", "finish refreshing!");
				}
			});
			
			
		}
	}


	private class DownloadTask extends AsyncTask<Void,Void,Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d("stream", "onPreExecute async update");
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mIsUpdating=true;
//			if (mIsScrollingBack==false) {
				Log.d("stream", "doInBackground async update");
				mListAdapter.refreshItem();
				Log.d("stream", "doInBackground async refresh update");
//			}

			return null;
		}
	}
}
