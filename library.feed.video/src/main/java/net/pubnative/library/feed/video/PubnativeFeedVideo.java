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

package net.pubnative.library.feed.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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

public class PubnativeFeedVideo implements PubnativeRequest.Listener,
                                        VASTPlayer.Listener {

    private static final String TAG = PubnativeFeedVideo.class.getSimpleName();

    //==============================================================================================
    // Properties
    //==============================================================================================
    protected Handler                     mHandler;
    protected Context                     mContext;
    protected PubnativeAdModel            mAdModel;
    protected PubnativeFeedVideo.Listener mListener;
    protected String                      mAppToken;
    protected boolean                     mIsLoading;
    protected boolean                     mIsShown;

    // Video view
    protected ViewGroup                   mContainer;
    protected VASTPlayer                  mVASTPlayer;
    protected RelativeLayout              mFeedVideo;

    /**
     * Interface for callbacks related to the video behaviour
     */
    public interface Listener {

        /**
         * Called whenever the video finished loading an ad
         *
         * @param video video that finished the load
         */
        void onPubnativeFeedVideoLoadFinish(PubnativeFeedVideo video);

        /**
         * Called whenever the video failed loading an ad
         *
         * @param video     video that failed the load
         * @param exception exception with the description of the load error
         */
        void onPubnativeFeedVideoLoadFail(PubnativeFeedVideo video, Exception exception);

        /**
         * Called when the video was just shown on the screen
         *
         * @param video video that was shown in the screen
         */
        void onPubnativeFeedVideoShow(PubnativeFeedVideo video);

        /**
         * Called whenever the video was removed from the screen
         *
         * @param video video that was hidden
         */
        void onPubnativeFeedVideoHide(PubnativeFeedVideo video);

        /**
         * Called whenever the video starts
         *
         * @param video video that has been started
         */
        void onPubnativeFeedVideoStart(PubnativeFeedVideo video);

        /**
         * Called whenever the video finishes
         *
         * @param video video that has been stopped
         */
        void onPubnativeFeedVideoFinish(PubnativeFeedVideo video);

        /**
         * Called whenever the video is being clicked
         *
         * @param video video that has been clicked
         */
        void onPubnativeFeedVideoClick(PubnativeFeedVideo video);
    }

    /**
     * Sets a callback listener for this video object
     *
     * @param listener valid PubnativeFeedVideo.Listener object
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
            invokeLoadFail(new Exception("PubnativeFeedVideo - load error: app token is null or empty"));
        } else if (context == null) {
            invokeLoadFail(new Exception("PubnativeFeedVideo - load error: context is null or empty"));
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
            String[] assets = new String[] {
                    PubnativeAsset.BANNER,
                    PubnativeAsset.VAST
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
    public void show(ViewGroup container) {

        Log.v(TAG, "show");
        if (mIsShown) {
            Log.w(TAG, "show - the ad is already shown, ");
        } else if (isReady()) {
            mContainer = container;
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
            mIsShown = false;
            mVASTPlayer.clean();
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
                invokeLoadFail(new Exception("PubnativeFeedVideo - render error: error loading resources"));
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {

                Log.v(TAG, "onVASTParserFinished");
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
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mContainer.addView(mFeedVideo, params);
            }

        }).execute(mAdModel.getVast());
    }

    protected void initialize() {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFeedVideo = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_video, null);
        mVASTPlayer = (VASTPlayer) mFeedVideo.findViewById(R.id.player);
        mVASTPlayer.setListener(this);
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
                    mListener.onPubnativeFeedVideoLoadFinish(PubnativeFeedVideo.this);
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
                    mListener.onPubnativeFeedVideoLoadFail(PubnativeFeedVideo.this, exception);
                }
            }
        });
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        mIsShown = true;
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeFeedVideoShow(PubnativeFeedVideo.this);
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
                    mListener.onPubnativeFeedVideoHide(PubnativeFeedVideo.this);
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
                    mListener.onPubnativeFeedVideoStart(PubnativeFeedVideo.this);
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
                    mListener.onPubnativeFeedVideoFinish(PubnativeFeedVideo.this);
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
                    mListener.onPubnativeFeedVideoClick(PubnativeFeedVideo.this);
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
            invokeLoadFail(new Exception("PubnativeFeedVideo - load error: error loading resources"));
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
        invokeLoadFail(new Exception("PubnativeFeedVideo - show error: error loading player"));
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
