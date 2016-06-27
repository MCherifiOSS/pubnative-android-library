package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.pubnative.library.demo.utils.Settings;
import net.pubnative.library.feed.banner.PubnativeFeedBanner;
import net.pubnative.library.feed.video.PubnativeFeedVideo;


public class FeedVideoActivity extends Activity implements PubnativeFeedVideo.Listener {

    private static final String TAG = FeedVideoActivity.class.getName();
    private RelativeLayout mLoaderContainer;
    private RelativeLayout mFeedBannerContainer;

    private PubnativeFeedVideo mFeedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infeed_banner);
        mLoaderContainer = (RelativeLayout) findViewById(R.id.activity_infeed_banner_container_loader);
        mFeedBannerContainer = (RelativeLayout) findViewById(R.id.activity_infeed_banner_container);
    }

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        if(mFeedVideo != null) {
            mFeedVideo.hide();
        }
        mFeedVideo = new PubnativeFeedVideo();
        mFeedVideo.setListener(this);
        mFeedVideo.load(this, Settings.getAppToken());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeFeedBanner.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeFeedVideoLoadFinish(PubnativeFeedVideo video) {

        Log.v(TAG, "onPubnativeFeedVideoLoadFinish");
        showToast("Video load finished");
        mFeedVideo.show(mFeedBannerContainer);
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeFeedVideoLoadFail(PubnativeFeedVideo video, Exception exception) {

        Log.v(TAG, "onPubnativeFeedVideoLoadFail");
        showToast("Video load failed");
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeFeedVideoShow(PubnativeFeedVideo video) {

        Log.v(TAG, "onPubnativeFeedVideoShow");
        showToast("Video show");
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeFeedVideoHide(PubnativeFeedVideo video) {

        Log.v(TAG, "onPubnativeFeedVideoHide");
        showToast("Video hide");
    }

    @Override
    public void onPubnativeFeedVideoStart(PubnativeFeedVideo video) {

        Log.v(TAG, "onPubnativeFeedVideoStart");
        showToast("Video start");
    }

    @Override
    public void onPubnativeFeedVideoFinish(PubnativeFeedVideo video) {

        Log.v(TAG, "onPubnativeFeedVideoFinish");
        showToast("Video finish");
    }

    @Override
    public void onPubnativeFeedVideoClick(PubnativeFeedVideo video) {

        Log.v(TAG, "onPubnativeFeedVideoClick");
        showToast("Video click");
    }
}
