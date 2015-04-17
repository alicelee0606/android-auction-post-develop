
package com.yahoo.mobile.android.auctionpost.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.yahoo.mobile.android.auctionpost.MediaDecorateActivity;
import com.yahoo.mobile.android.auctionpost.R;

public class CameraColorFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private Bundle mBundle;
    private int tagColor = 0;
    private String path;
    private String imagePath;
    private int type = 0;
    private MediaPlayer mediaPlayer;
    String videoUri = null;
    TextureView textureView;
    ImageView imgPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("fragment", "CameraColorFragment");
        View rootView = inflater.inflate(R.layout.fragment_camera_color, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        RelativeLayout panelLayout = (RelativeLayout)view.findViewById(R.id.camera_panel_container);
        RelativeLayout.LayoutParams panelParams = (RelativeLayout.LayoutParams)panelLayout.getLayoutParams();

        Point size = new Point();
        ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);

        panelParams.height = size.y - size.x - actionBarHeight;

        textureView = (TextureView)view.findViewById(R.id.preview_video);
        textureView.setSurfaceTextureListener(this);
        imgPhoto = (ImageView)view.findViewById(R.id.preview_image);

        mBundle = this.getArguments();

        if (mBundle.getString("videoUri") != null) {
            videoUri = mBundle.getString("videoUri");
            Log.i("videoUri", videoUri);
            imgPhoto.setVisibility(View.GONE);
            onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setVisibility(View.GONE);
            String imageUri = mBundle.getString("imageUri");

            imgPhoto.setImageURI(Uri.parse(imageUri));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(imageUri));
                final double viewWidthToBitmapWidthRatio = (double)size.x / (double)bitmap.getWidth();
                imgPhoto.getLayoutParams().height = (int)(bitmap.getHeight() * viewWidthToBitmapWidthRatio);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        setTagGridView(view, size.x);
    }

    // ==============implement SurfaceTextureListener
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.i("onSurfaceTextureAvailable", "enter");
        if (videoUri == null) {
            Log.i("onSurfaceTextureAvailable", "videoUri is null");
            return;
        }
        if(!textureView.isAvailable()) {
            Log.i("onSurfaceTextureAvailable", "textureView is unavailable");
            return;
        }
        try {
            Log.i("setSurfaceTexture", "start");
            Surface surface = new Surface(surfaceTexture);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(videoUri);
            mediaPlayer.setSurface(surface);

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    Log.i("OnVideoSizeChangedListener", width + " " + height);
                    Point size = new Point();
                    getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                    textureView.setLayoutParams(new LayoutParams(size.x, (int)(size.x * 1.0 / width * height)));
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i("onCompletion", "done, trying to repeat");
                    mp.seekTo(0);
                    mp.start();
                    // setLooping is not working but I don't know why
                }
            });

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ==============implement SurfaceTextureListener end

    private void setTagGridView(View view, final int screen_width) {
        GridView gv = (GridView)view.findViewById(R.id.choose_tag_color_grid);

        int[] images = {
                R.drawable.yellow_rectangle, R.drawable.blue_rectangle, R.drawable.green_rectangle, R.drawable.pink_rectangle, R.drawable.purple_rectangle, R.drawable.gray_rectangle
        };
        String[] tags = getActivity().getResources().getStringArray(R.array.color_tag);

        final List<Map<String, Object>> tagList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", images[i]);
            item.put("text", tags[i]);
            tagList.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), tagList, R.layout.grid_color_button, new String[] {
                "image", "text"
        }, new int[] {
                R.id.color_button_image, R.id.color_button_text
        }) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView colorButton = (ImageView)view.findViewById(R.id.color_button_image);
                colorButton.getLayoutParams().height = screen_width / tagList.size();
                return view;
            }
        };
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                tagColor = pos + 1;
                ImageView imgTag = (ImageView)getActivity().findViewById(R.id.img_item_price);
                if (pos == 0)
                    imgTag.setBackgroundResource(R.drawable.tag_color_1);
                else if (pos == 1)
                    imgTag.setBackgroundResource(R.drawable.tag_color_2);
                else if (pos == 2)
                    imgTag.setBackgroundResource(R.drawable.tag_color_3);
                else if (pos == 3)
                    imgTag.setBackgroundResource(R.drawable.tag_color_4);
                else if (pos == 4)
                    imgTag.setBackgroundResource(R.drawable.tag_color_5);
                else if (pos == 5)
                    imgTag.setBackgroundResource(R.drawable.tag_color_6);

                MediaDecorateActivity activity = (MediaDecorateActivity)getActivity();
                Log.i("if ever in color", "4");
                activity.setTag(tagColor);

            }
        });

    }

}
