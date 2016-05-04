package net.pubnative.library.banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;

public class PubnativeBanner implements PubnativeRequest.Listener,
                                        PubnativeAdModel.Listener {

    public static final String TAG = PubnativeBanner.class.getSimpleName();
    protected Context               mContext;
    protected String                mAppToken;
    protected int                   mBannerSize;

    public interface Size {
        public static final int BANNER_50 = 0;
        public static final int BANNER_90 = 1;
    }

    public PubnativeBanner(Context context, String appToken, int bannerSize) {
        RelativeLayout banner = null;

        mContext = context.getApplicationContext();
        mAppToken = appToken;
        mBannerSize = bannerSize;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (bannerSize) {
            case Size.BANNER_90:
                banner = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_banner_tablet, null);
                break;
            case Size.BANNER_50:
            default:
                banner = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_banner_phone, null);
                break;

        }

    }

    public void load() {
        //TODO: load method implementation
    }

    public void show() {
        //TODO: show method implementation
    }

    public void destroy() {
        //TODO: destroy method implementation
    }

    protected void hide() {
        //TODO: hide method implementation
    }

    protected void render() {
        //TODO: render method implementation
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

    }

    //----------------------------------------------------------------------------------------------
    // PubnativeAdModel.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view) {

    }

    @Override
    public void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view) {

    }

    @Override
    public void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel) {

    }
}
