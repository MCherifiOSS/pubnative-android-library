// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.pubnative.library.request.PubnativeAsset;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;
import net.pubnative.player.VASTParser;
import net.pubnative.player.VASTPlayer;
import net.pubnative.player.model.VASTModel;

import java.util.List;

public class PubnativeVideo implements PubnativeRequest.Listener,
                                        VASTPlayer.Listener {

    private static final String TAG = PubnativeVideo.class.getSimpleName();
    private final static String VIDEO_STATIC = "<VAST version=\"2.0\"><Ad id=\"3295422\"><InLine><AdTitle>UC Browser for Android</AdTitle><Description>Searching, Browsing, Downloading, Video, Gaming, Shopping and Social Sharing experience - get it now!</Description><AdSystem version=\"1\">PubNative</AdSystem><Impression>http://tr.pubnative.net/impression?aid=4&amp;t=KOvJvnhz15cd5PH0h0O2FncynlRwDjwlqWwIHaOBrq2SRX5AOQgY7YituLubawk1xK24Hb9ezCXDYryRaHFQfPd_GKm4VGnTKSipeli3KXRwoxmKzmrKpyBYSvA6393NVZHhdfgK1zf4yaR5Xx-6CG2JGEFErLE8UhLdSQAyc3-r29we5FVn1KJ1I22aiVkk6uWuFXW6cTDzTleFhLdhoyvyVD1LkmlgxesgjE1l5H1Hyv-JbFYToYxP7NcIJjDXYYi2_xOwMprXrH6Tl4j2KGTmTkDikHNKuiuV8imocl5UzuneEPmr9cuKGxF0Cze8YWJtPp14fycltRhISvNQ</Impression><Creatives><Creative sequence=\"1\"><Linear><Duration></Duration><VideoClicks><ClickThrough>http://tr.pubnative.net/click/native?aid=4&amp;t=4_g5FZRpf-Vid0u-47zNXWFQUIQcrEpPsCdScUjtFX5bafoKg8kqoaXmKX_PNfQFXenanHoClQ-MXjL7Em9amEx0SVPmXwV2apg7rGiUBwCywpRPvLKBzovhygB24ADXNxINjI-i-4uy0SrllOyrV-v7NRu1QBsHYHDW65RUiwCryYD3pY8yWsrmpU_uMuk2h9lT5h87r-5B3hSJzQT066eyX5vmfHzTrm2XD2Xtm_eduNpre4uWvIu5vO6rxP0PRsDWjXEh0wwsSAFWMDPettyp_-UyMP0deacNLTKfDjO7y3eyEF0D4RV9QvpM2r-yE3qzQzHNWNdTW9_gTivkegbaJSJItSGJTikW57g8lLs53T2drp5NmbAFcx6lo0tsH83RXjD4Rp3V3J180G3g4dY5ECThw0Jfja4i5o_55xmNagTV2YYCzvmoppy0ijNYeEFoyHpIFVCvnNKpVBoIbOimbFo2ePoa0DOH3V6tIu0BX_AWI0vYK5hi-Q70YP_40xoBeH8w45XiJeE4CA2zDZiTg0JaJOhlIqDcBA</ClickThrough></VideoClicks><MediaFiles><MediaFile delivery=\"progressive\" type=\"video/mp4\" width=\"0\" height=\"0\">http://cdn.pubnative.net/games/videos/1003679/en.mp4</MediaFile></MediaFiles></Linear></Creative></Creatives></InLine></Ad></VAST>";

    //==============================================================================================
    // Properties
    //==============================================================================================
    protected Handler                 mHandler;
    protected Context                 mContext;
    protected PubnativeAdModel        mAdModel;
    protected PubnativeVideo.Listener mListener;
    protected String                  mAppToken;
    protected boolean                 mIsLoading;
    protected boolean                 mIsShown;
    protected WindowManager           mWindowManager;

    // Video view
    protected RelativeLayout          mContainer;
    protected VASTPlayer              mVASTPlayer;

    /**
     * Interface for callbacks related to the video behaviour
     */
    public interface Listener {

        /**
         * Called whenever the video finished loading an ad
         *
         * @param video video that finished the load
         */
        void onPubnativeVideoLoadFinish(PubnativeVideo video);

        /**
         * Called whenever the video failed loading an ad
         *
         * @param video     video that failed the load
         * @param exception exception with the description of the load error
         */
        void onPubnativeVideoLoadFail(PubnativeVideo video, Exception exception);

        /**
         * Called when the video was just shown on the screen
         *
         * @param video video that was shown in the screen
         */
        void onPubnativeVideoShow(PubnativeVideo video);

        /**
         * Called whenever the video was removed from the screen
         *
         * @param video video that was hidden
         */
        void onPubnativeVideoHide(PubnativeVideo video);

        /**
         * Called whenever the video starts
         *
         * @param video video that has been started
         */
        void onPubnativeVideoStart(PubnativeVideo video);

        /**
         * Called whenever the video finishes
         *
         * @param video video that has been stopped
         */
        void onPubnativeVideoFinish(PubnativeVideo video);

        /**
         * Called whenever the video is being clicked
         *
         * @param video video that has been clicked
         */
        void onPubnativeVideoClick(PubnativeVideo video);
    }

    /**
     * Sets a callback listener for this video object
     *
     * @param listener valid PubnativeVideo.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Starts loading an ad for this video
     * @param context valid Context
     * @param appToken valid App token where to request the ad from
     */
    public void load(Context context, String appToken) {

        Log.v(TAG, "load");
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (mListener == null) {
            Log.w(TAG, "load - The ad hasn't a listener");
        }
        if (TextUtils.isEmpty(appToken)) {
            invokeLoadFail(new Exception("PubnativeVideo - load error: app token is null or empty"));
        } else if (context == null) {
            invokeLoadFail(new Exception("PubnativeVideo - load error: context is null or empty"));
        } else if (mIsLoading) {
            Log.w(TAG, "load - The ad is loaded or being loaded, dropping this call");
        } else if (isReady()) {
            invokeLoadFinish();
        } else {
            mContext = context;
            mAppToken = appToken;
            mIsShown = false;
            mIsLoading = true;
            initialize();
            PubnativeRequest request = new PubnativeRequest();
            request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, mAppToken);
            String[] assets = new String[] { //TODO this array must be changed according to VAST
                    PubnativeAsset.TITLE,
                    PubnativeAsset.DESCRIPTION,
                    PubnativeAsset.BANNER/*,
                    PubnativeAsset.VAST*/
            };
            request.setParameterArray(PubnativeRequest.Parameters.ASSET_FIELDS, assets);
            request.start(mContext, this);
        }
    }

    /**
     * Method that checks if the video is ready to be shown in the screen
     *
     * @return true if the video can be shown false if not
     */
    public boolean isReady() {

        Log.v(TAG, "setListener");
        return mAdModel != null;
    }

    /**
     * Shows, if not shown and ready the cached video over the current activity
     */
    public void show() {

        Log.v(TAG, "show");
        if (mIsShown) {
            Log.w(TAG, "show - the ad is already shown, ");
        } else if (isReady()) {
            render();
        } else {
            Log.e(TAG, "show - Error: the video is not yet loaded");
        }
    }

    /**
     * Hides video ad
     */
    public void hide() {

        Log.v(TAG, "hide");
        if (mIsShown) {
            mWindowManager.removeView(mContainer);
            mIsShown = false;
            mVASTPlayer.clean();
            ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            invokeHide();
        }
    }

    //==============================================================================================
    // Helpers
    //==============================================================================================
    protected void render() {

        Log.v(TAG, "render");
        new VASTParser(mContext).setListener(new VASTParser.Listener() {

            @Override
            public void onVASTParserError(int error) {

                Log.v(TAG, "onVASTParserError");
                invokeLoadFail(new Exception("PubnativeVideo - render error: error loading resources"));
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {
                //TODO this needs to be modified after correct server response
                Log.v(TAG, "onVASTParserFinished");
                mVASTPlayer.setCampaignType(VASTPlayer.CampaignType.CPC);
                mVASTPlayer.setSkipName("Skip");
                mVASTPlayer.setSkipTime(5);
                Picasso.with(mContext).load(mAdModel.getBannerUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mVASTPlayer.setBannerImage(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                mVASTPlayer.load(model);

                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                mWindowManager.addView(mContainer, params);
                ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mIsShown = true;
            }

        }).execute(VIDEO_STATIC);
    }

    protected void initialize() {

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout rootView = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_video, null);
        mVASTPlayer = (VASTPlayer) rootView.findViewById(R.id.player);
        mVASTPlayer.setListener(this);
        mContainer = new RelativeLayout(mContext) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                Log.v(TAG, "dispatchKeyEvent");
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }

            @Override
            protected void onWindowVisibilityChanged(int visibility) {

                Log.v(TAG, "onWindowVisibilityChanged");
                if (visibility != View.VISIBLE) {
                    hide();
                }
            }
        };
        mContainer.addView(rootView);
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeVideoLoadFinish(PubnativeVideo.this);
                }
            }
        });
    }

    protected void invokeLoadFail(final Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeVideoLoadFail(PubnativeVideo.this, exception);
                }
            }
        });
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeVideoShow(PubnativeVideo.this);
                }
            }
        });
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        mIsShown = false;
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeVideoHide(PubnativeVideo.this);
                }
            }
        });
    }

    protected void invokeVideoStart() {

        Log.v(TAG, "invokeVideoStart");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeVideoStart(PubnativeVideo.this);
                }
            }
        });
    }

    protected void invokeVideoFinish() {

        Log.v(TAG, "invokeVideoFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeVideoFinish(PubnativeVideo.this);
                }
            }
        });
    }

    protected void invokeVideoClick() {

        Log.v(TAG, "invokeVideoFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeVideoClick(PubnativeVideo.this);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

        Log.v(TAG, "onPubnativeRequestSuccess");
        if (ads == null || ads.size() == 0) {
            invokeLoadFail(new Exception("PubnativeVideo - load error: error loading resources"));
        } else {
            mAdModel = ads.get(0);
            Picasso.with(mContext).load(mAdModel.getBannerUrl()).fetch(new Callback() {

                @Override
                public void onSuccess() {
                    invokeLoadFinish();
                }

                @Override
                public void onError() {
                    // do nothing
                }
            });
        }
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

        Log.v(TAG, "onPubnativeRequestFailed");
        invokeLoadFail(ex);
    }

    //----------------------------------------------------------------------------------------------
    // VASTPlayer.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onVASTPlayerLoadFinish() {

        Log.v(TAG, "onVASTPlayerLoadFinish");
        invokeShow();
        mVASTPlayer.play();
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {

        Log.v(TAG, "onVASTPlayerFail");
        invokeLoadFail(new Exception("PubnativeVideo - show error: error loading player"));
        hide();
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

        Log.v(TAG, "onVASTPlayerPlaybackStart");
        invokeVideoStart();
    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

        Log.v(TAG, "onVASTPlayerPlaybackFinish");
        invokeVideoFinish();
    }

    @Override
    public void onVASTPlayerClick() {

        Log.v(TAG, "onVASTPlayerClick");
        invokeVideoClick();
    }
}
