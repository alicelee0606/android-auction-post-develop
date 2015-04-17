
package com.yahoo.mobile.android.auctionpost.fragments;

import java.io.IOException;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.yahoo.mobile.android.auctionpost.DetailSellPost;
import com.yahoo.mobile.android.auctionpost.ParseManager;
import com.yahoo.mobile.android.auctionpost.ParseManager.ILoadListener;
import com.yahoo.mobile.android.auctionpost.R;
import com.yahoo.mobile.android.auctionpost.SendMail;

public class ItemPageFragment extends Fragment implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl {

    String objectId;
    TextView tv_name;
    TextView tv_price;
    TextView tv_description;

    TextView tv_delivery_tag;
    TextView tv_delivery;

    TextView tv_seller_tag;
    TextView tv_seller;

    ImageView img;
    TextureView textureView;
    MediaPlayer mediaPlayer;
    MediaController mediaController;

    // Button btnSoldOut, btnDelete;
    Button btnEmailToSeller;

    int vdHeight = 0, vdWidth = 0;
    String vdUrl = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_page, container, false);

        tv_name = (TextView)rootView.findViewById(R.id.item_page_name);
        tv_price = (TextView)rootView.findViewById(R.id.item_page_price);

        tv_description = (TextView)rootView.findViewById(R.id.item_page_description);
        tv_description.setVisibility(View.GONE);

        tv_delivery_tag = (TextView)rootView.findViewById(R.id.item_page_delivery_tag);
        tv_delivery_tag.setVisibility(View.GONE);

        tv_seller_tag = (TextView)rootView.findViewById(R.id.item_page_seller_tag);
        tv_seller = (TextView)rootView.findViewById(R.id.item_page_seller);

        tv_seller_tag.setVisibility(View.GONE);

        tv_delivery = (TextView)rootView.findViewById(R.id.item_page_delivery);
        img = (ImageView)rootView.findViewById(R.id.item_page_image);

        // btnSoldOut = (Button)rootView.findViewById(R.id.btn_sold_out);
        // btnDelete = (Button)rootView.findViewById(R.id.btn_delete);

        textureView = (TextureView)rootView.findViewById(R.id.item_page_texture_view);
        textureView.setSurfaceTextureListener(this);

        btnEmailToSeller = (Button)rootView.findViewById(R.id.btn_email_to_seller);

        // btnDelete.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        // dialog.setTitle("確定要刪除？");
        // dialog.setCancelable(false);
        //
        // dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // // TODO Auto-generated method stub
        //
        // }
        // });
        //
        // dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // // TODO Auto-generated method stub
        //
        // }
        // });
        // dialog.show();
        // }
        // });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        objectId = getActivity().getIntent().getExtras().getString("ObjectId");
        ParseManager parseManager = ParseManager.getInstance(getActivity());
        parseManager.loadSellPost(objectId, new ILoadListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail() {
            }

            @Override
            public void onComplete(final DetailSellPost rDetailSellPost) {
                Log.i("loadSellPost", "onComplete!");
                if (rDetailSellPost.getIsVd()) {
                    img.setVisibility(View.GONE);
                    vdUrl = rDetailSellPost.getShowUrl();
                    onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
                } else {
                    textureView.setVisibility(View.GONE);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(rDetailSellPost.getShowData(), 0, rDetailSellPost.getShowData().length);
                    img.setImageBitmap(bitmap);
                    try {
                        Display display = ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        final double viewWidthToBitmapWidthRatio = (double)size.x / (double)bitmap.getWidth();
                        img.getLayoutParams().height = (int)(bitmap.getHeight() * viewWidthToBitmapWidthRatio);

                    } catch (Exception e) {
                        Log.e("color layout", "");
                    }
                }

                tv_name.setText(rDetailSellPost.getName());
                tv_price.setText(Integer.toString(rDetailSellPost.getPrice()));

                if (rDetailSellPost.getDescription() != null && rDetailSellPost.getDescription().length() != 0) {
                    tv_description.setVisibility(View.VISIBLE);
                    tv_description.setText(rDetailSellPost.getDescription());
                }

                tv_seller_tag.setVisibility(View.VISIBLE);
                tv_seller.setText(rDetailSellPost.getUserName());

                tv_delivery_tag.setVisibility(View.VISIBLE);
                String delivery = "";
                String deliveryMethod[] = getActivity().getResources().getStringArray(R.array.delivery_methods);
                for (int i = 0; i < rDetailSellPost.getDelivery().size(); i++) {
                    delivery += "/" + deliveryMethod[rDetailSellPost.getDelivery().get(i) - 1];
                }
                tv_delivery.setText(delivery);

                btnEmailToSeller.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = "[MaiMai] I'd like to buy '" + rDetailSellPost.getName() + "'!";
                        String text = "Dear " + rDetailSellPost.getUserName() + ",\n\n" + "I'm very interested in your selling item '" + rDetailSellPost.getName() + "'!\n\n"
                                + "Here's my contact infomation, \nCell: \nFacebook: \nLine: \nLooking forward to your reply!";
                        SendMail.sendMail(getActivity(), rDetailSellPost.getEmail(), title, text);
                    }
                });

            }
        });
    }

    // ==================implements TextureView.SurfaceTextureListener
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.i("onSurfaceTextureAvailable", "enter");
        if (vdUrl == null) {
            Log.i("onSurfaceTextureAvailable", "vdUrl is null");
            return;
        }
        if(!textureView.isAvailable()) {
            Log.i("onSurfaceTextureAvailable", "textureView is unavailable");
            return;
        }
        try {
            Log.i("setSurfaceTexture", "start");
            Surface surface = new Surface(surfaceTexture);

            mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(getActivity().findViewById(R.id.item_page_texture_view));

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(vdUrl);
            mediaPlayer.setSurface(surface);

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    if (width != vdWidth && height != vdHeight) {
                        Log.i("OnVideoSizeChangedListener", width + " " + height);
                        vdWidth = width;
                        vdHeight = height;
                        Point size = new Point();
                        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                        textureView.setLayoutParams(new LayoutParams(size.x, (int)(size.x * 1.0 / width * height)));
                    }
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    mediaController.setMediaPlayer(ItemPageFragment.this);
                    mediaController.setEnabled(true);
                    textureView.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {
                            view.performClick();
                            mediaController.show();
                            return false;
                        }
                    });
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
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i("onSurfacetextureSizeChanged", "!!!!! width = " + textureView.getWidth() + " height = " + textureView.getHeight());
        // onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    // ==================end of TextureView.SurfaceTextureListener

    // ==================MediaPlayerControll

    public void start() {
        mediaPlayer.start();
    }

    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public boolean canSeekForward() {
        return false;
    }

    public boolean canSeekBackward() {
        return false;
    }

    public boolean canPause() {
        return true;
    }

    // ==================end of MediaPlayerControll

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // private class PlayVideoTask extends AsyncTask<String, Void, Uri> {
    //
    // protected void onPreExecute() {
    // }
    //
    // @Override
    // protected Uri doInBackground(String... url) {
    // Uri uri = Uri.parse(url[0]);
    // return uri;
    // }
    //
    // protected void onPostExecute(Uri result) {
    // if (result != null) {
    // vd.setVideoURI(result);
    // vd.setMediaController(new MediaController(getActivity()));
    // vd.requestFocus();
    // vd.start();
    // }
    // }
    //
    // };

}
