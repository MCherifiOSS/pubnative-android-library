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

package net.pubnative.library.request.model;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.pubnative.URLDriller;
import net.pubnative.library.request.PubnativeAsset;
import net.pubnative.library.request.model.api.PubnativeAPIV3AdModel;
import net.pubnative.library.request.model.api.PubnativeAPIV3DataModel;
import net.pubnative.library.tracking.PubnativeImpressionTracker;
import net.pubnative.library.tracking.PubnativeTrackingManager;
import net.pubnative.library.utils.SystemUtils;
import net.pubnative.library.widget.PubnativeWebView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PubnativeAdModel implements PubnativeImpressionTracker.Listener,
                                         URLDriller.Listener,
                                         Serializable {

    private static      String                     TAG                    = PubnativeAdModel.class.getSimpleName();
    //Generic Fields
    protected transient Listener                   mListener              = null;
    protected           boolean                    mUseClickLoader        = true;
    protected           boolean                    mUseBackgroundClick    = true;
    protected           PubnativeAPIV3AdModel      mData                  = null;
    protected           List<String>               mUsedAssets            = null;
    //Tracking
    private transient   PubnativeImpressionTracker mPubnativeAdTracker    = null;
    private transient   boolean                    mIsImpressionConfirmed = false;
    private transient   View                       mClickableView         = null;
    private transient   View                       mAdView                = null;
    //Loading View
    private transient   RelativeLayout             loadingView            = null;

    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface definition for callbacks to be invoked when impression confirmed/failed, ad clicked/clickfailed
     */
    public interface Listener {

        /**
         * Called when impression is confirmed
         *
         * @param pubnativeAdModel PubnativeAdModel impression that was confirmed
         * @param view             The view where impression confirmed
         */
        void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view);

        /**
         * Called when click is confirmed
         *
         * @param pubnativeAdModel PubnativeAdModel that detected the click
         * @param view             The view that was clicked
         */
        void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view);

        /**
         * Called before the model opens the offer
         *
         * @param pubnativeAdModel PubnativeAdModel which's offer will be opened
         */
        void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel);
    }

    public static PubnativeAdModel create(PubnativeAPIV3AdModel data) {

        PubnativeAdModel model = new PubnativeAdModel();
        model.mData = data;
        return model;
    }

    //==============================================================================================
    // Generic Fields
    //==============================================================================================

    /**
     * Gets the specified meta field raw data
     *
     * @param meta meta field type name
     *
     * @return valid PubnativeAPIV3DataModel if present, null if not
     */
    public PubnativeAPIV3DataModel getMeta(String meta) {

        Log.v(TAG, "getMeta");
        PubnativeAPIV3DataModel result = null;
        if (mData == null) {
            Log.w(TAG, "getMeta - Error: ad data not present");
        } else {
            result = mData.getAsset(meta);
        }
        return result;
    }

    /**
     * Gets the specified asset field raw data
     *
     * @param asset asset field type name
     *
     * @return valid PubnativeAPIV3DataModel if present, null if not
     */
    public PubnativeAPIV3DataModel getAsset(String asset) {

        return getAsset(asset, true);
    }

    protected PubnativeAPIV3DataModel getAsset(String asset, Boolean trackAsset) {

        Log.v(TAG, "getAsset");
        PubnativeAPIV3DataModel result = null;
        if (mData == null) {
            Log.w(TAG, "getAsset - Error: ad data not present");
        } else {
            result = mData.getAsset(asset);
            if (result != null) {
                recordAsset(result.getTracking());
            }
        }
        return result;
    }

    protected void recordAsset(String url) {

        Log.v(TAG, "recordAsset");
        if (!TextUtils.isEmpty(url)) {
            if (mUsedAssets == null) {
                mUsedAssets = new ArrayList<String>();
            }
            if (!mUsedAssets.contains(url)) {
                mUsedAssets.add(url);
            }
        }
    }

    //==============================================================================================
    // Fields
    //==============================================================================================

    /**
     * Gets the title string of the ad
     *
     * @return String representation of the ad title, null if not present
     */
    public String getTitle() {

        Log.v(TAG, "getTitle");
        String result = null;
        PubnativeAPIV3DataModel data = getAsset(PubnativeAsset.TITLE);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the description string of the ad
     *
     * @return String representation of the ad Description, null if not present
     */
    public String getDescription() {

        Log.v(TAG, "getDescription");
        String result = null;
        PubnativeAPIV3DataModel data = getAsset(PubnativeAsset.DESCRIPTION);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the call to action string of the ad
     *
     * @return String representation of the call to action value, null if not present
     */
    public String getCtaText() {

        Log.v(TAG, "getCtaText");
        String result = null;
        PubnativeAPIV3DataModel data = getAsset(PubnativeAsset.CALL_TO_ACTION);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the icon image url of the ad
     *
     * @return valid String with the url value, null if not present
     */
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");
        String result = null;
        PubnativeAPIV3DataModel data = getAsset(PubnativeAsset.ICON);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    public String getVast() {

        Log.v(TAG, "getVast");
        String result = null;
        PubnativeAPIV3DataModel data = getAsset(PubnativeAsset.VAST);
        if (data != null) {
            result = data.getStringField("tag");
        }
        return result;
    }

    /**
     * Gets the banner image url of the ad
     *
     * @return valid String with the url value, null if not present
     */
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");
        String result = null;
        PubnativeAPIV3DataModel data = getAsset(PubnativeAsset.BANNER);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    /**
     * Gets the click url of the ad
     *
     * @return String value with the url of the click, null if not present
     */
    public String getClickUrl() {

        Log.v(TAG, "getClickUrl");
        String result = null;
        if (mData == null) {
            Log.w(TAG, "getClickUrl - Error: ad data not present");
        } else {
            result = mData.link;
        }
        return result;
    }

    /**
     * Gets rating of the app in a value from 0 to 5
     *
     * @return int value, 0 if not present
     */
    public int getRating() {

        Log.v(TAG, "getRating");
        int result = 0;
        PubnativeAPIV3DataModel data = getAsset(PubnativeAsset.RATING);
        if (data != null) {
            Integer rating = data.getNumber();
            if (rating != null) {
                result = rating.intValue();
            }
        }
        return result;
    }

    /**
     * Gets the type of the ad "native" or "video"
     *
     * @return valid String native/video depending on the ad type
     * @deprecated There are no longer differencies about video/native ad, it's implicit to the request
     * so you should stop using this method
     */
    @Deprecated
    public String getType() {

        Log.v(TAG, "getType");
        String result = "native";
        if (getAsset(PubnativeAsset.VAST, false) != null) {
            result = "video";
        }
        return result;
    }

    /**
     * Gets the portrait banner asset of the ad
     *
     * @return String with the url value
     * @deprecated This resource is no longer served, so it will always return null
     */
    @Deprecated
    public String getPortraitBannerUrl() {

        Log.v(TAG, "getPortraitBannerUrl");
        return null;
    }

    /**
     * Gets all the present beacons in the ad
     *
     * @return Returns the list of all present beacons of the ad
     * @deprecated Beacons returned by this method won't contain all the data, and will
     * return null in the future, direct access to beacons will be stopped.
     */
    @Deprecated
    public List<PubnativeBeacon> getBeacons() {

        Log.v(TAG, "getBeacons");
        List<PubnativeBeacon> result = new ArrayList<PubnativeBeacon>();
        if (mData == null) {
            Log.w(TAG, "getBeacons - Error: ad data not present");
        } else {
            result.addAll(createBeacons(PubnativeAPIV3AdModel.Beacon.IMPRESSION));
            result.addAll(createBeacons(PubnativeAPIV3AdModel.Beacon.CLICK));
        }
        return result;
    }

    protected List<PubnativeBeacon> createBeacons(String beaconType) {

        List<PubnativeBeacon> result = null;
        if (mData == null) {
            Log.w(TAG, "getBeacons - Error: ad data not present");
        } else {
            List<PubnativeAPIV3DataModel> beacons = mData.getBeacons(beaconType);
            if (beacons != null && beacons.size() > 0) {
                result = new ArrayList<PubnativeBeacon>();
                for (PubnativeAPIV3DataModel data : beacons) {
                    PubnativeBeacon beacon = new PubnativeBeacon();
                    beacon.js = data.getStringField("js");
                    beacon.type = beaconType;
                    beacon.url = data.getURL();
                }
            }
        }
        return result;
    }

    //==============================================================================================
    // Helpers
    //==============================================================================================

    /**
     * This will enable / disable the spin that takes the screen on click. Default behaviour is enabled
     *
     * @param enabled true will show a spinner on top of the screen, false will disable the click spin view
     */
    public void setUseClickLoader(boolean enabled) {

        Log.v(TAG, "setUseClickLoader");
        mUseClickLoader = enabled;
    }

    public void setUseBackgroundClick(boolean enabled) {

        Log.v(TAG, "setUseBackgroundClick");
        mUseBackgroundClick = enabled;
    }

    /**
     * This function will return the first present beacon URL of the specified type
     *
     * @param beaconType type of beacon
     *
     * @return return Beacon URL or null if not present.
     * @deprecated Beacons are multiple now, so this method will not cover all options,
     * IT could even return a beacon with null url, since there are other type of beacons supported.
     */
    @Deprecated
    public String getBeacon(String beaconType) {

        Log.v(TAG, "getBeacon");
        String beaconUrl = null;
        if (TextUtils.isEmpty(beaconType)) {
            Log.e(TAG, "getBeacon - Error: beacon type is null or empty");
        } else {
            for (PubnativeBeacon beacon : getBeacons()) {
                if (beaconType.equalsIgnoreCase(beacon.type)) {
                    beaconUrl = beacon.url;
                    break;
                }
            }
        }
        return beaconUrl;
    }

    //==============================================================================================
    // Tracking
    //==============================================================================================

    /**
     * Start tracking of ad view to auto confirm impressions and handle clicks
     *
     * @param view     ad view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, Listener listener) {

        Log.v(TAG, "startTracking: both ad view & clickable view are same");
        startTracking(view, view, listener);
    }

    /**
     * Start tracking of ad view to auto confirm impressions and handle clicks
     *
     * @param view          ad view
     * @param clickableView clickable view
     * @param listener      listener for callbacks
     */
    public void startTracking(View view, View clickableView, Listener listener) {

        Log.v(TAG, "startTracking");
        mListener = listener;
        mAdView = view;
        mClickableView = clickableView;
        // 1. Impression tracking
        if (mAdView == null) {
            Log.w(TAG, "startTracking - ad view is null, cannot start tracking");
        } else if (mIsImpressionConfirmed) {
            Log.v(TAG, "startTracking - impression is already confirmed, dropping impression tracking");
        } else {
            if (mPubnativeAdTracker == null) {
                mPubnativeAdTracker = new PubnativeImpressionTracker();
            }
            mPubnativeAdTracker.startTracking(mAdView, this);
        }
        // 2. Start tracking clicks
        if (TextUtils.isEmpty(getClickUrl())) {
            Log.w(TAG, "startTracking - Error: click url is empty, clicks won't be tracked");
        } else if (mClickableView == null) {
            Log.w(TAG, "startTracking - Error: click view is null, clicks won't be tracked");
        } else {
            mClickableView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Log.v(TAG, "onClick detected");
                    invokeOnClick(view);
                    confirmClickBeacons(view);
                    if (mUseBackgroundClick) {
                        if (mUseClickLoader) {
                            showLoadingView();
                        }
                        URLDriller driller = new URLDriller();
                        driller.setUserAgent(SystemUtils.getWebViewUserAgent(view.getContext()));
                        driller.setListener(PubnativeAdModel.this);
                        driller.drill(getClickUrl());
                    } else {
                        openURL(getClickUrl());
                    }
                }
            });
        }
    }

    /**
     * stop tracking of ad view
     */
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        mPubnativeAdTracker.stopTracking();
        if (mClickableView != null) {
            mClickableView.setOnClickListener(null);
        }
    }

    protected void openURL(String urlString) {

        Log.v(TAG, "openURL: " + urlString);
        if (TextUtils.isEmpty(urlString)) {
            Log.w(TAG, "Error: ending URL cannot be opened - " + urlString);
        } else if (mClickableView == null) {
            Log.w(TAG, "Error: clickable view not set");
        } else {
            try {
                Uri uri = Uri.parse(urlString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mClickableView.getContext().startActivity(intent);
                invokeOnOpenOffer();
            } catch (Exception ex) {
                Log.w(TAG, "openURL: Error - " + ex.getMessage());
            }
        }
    }

    protected void confirmImpressionBeacons(View view) {

        Log.v(TAG, "confirmImpressionBeacons");
        // 1. Track assets
        if (mUsedAssets != null) {
            for (String asset : mUsedAssets) {
                PubnativeTrackingManager.track(view.getContext(), asset);
            }
        }
        // 2. Track impressions
        confirmBeacons(PubnativeAPIV3AdModel.Beacon.IMPRESSION, view);
    }

    protected void confirmClickBeacons(View view) {

        Log.v(TAG, "confirmClickBeacons");
        confirmBeacons(PubnativeAPIV3AdModel.Beacon.CLICK, view);
    }

    protected void confirmBeacons(String beaconType, View view) {

        Log.v(TAG, "confirmBeacons: " + beaconType);
        if (mData == null) {
            Log.w(TAG, "confirmBeacons - Error: ad data not present");
        } else {
            List<PubnativeAPIV3DataModel> beacons = mData.getBeacons(beaconType);
            if (beacons != null) {
                for (PubnativeAPIV3DataModel beaconData : beacons) {
                    String beaconURL = beaconData.getURL();
                    if (TextUtils.isEmpty(beaconURL)) {
                        // JAVASCRIPT
                        String beaconJS = beaconData.getStringField("js");
                        if (!TextUtils.isEmpty(beaconJS)) {
                            try {
                                new PubnativeWebView(view.getContext()).loadBeacon(beaconJS);
                            } catch (Exception e) {
                                Log.e(TAG, "confirmImpressionBeacons - JS Error: " + e);
                            }
                        }
                    } else {
                        // URL
                        PubnativeTrackingManager.track(view.getContext(), beaconURL);
                    }
                }
            }
        }
    }

    //==============================================================================================
    // LoadingView
    //==============================================================================================

    protected void showLoadingView() {

        Log.v(TAG, "showLoadingView");
        if (getRootView() == null) {
            Log.w(TAG, "showLoadingView - Error: impossible to retrieve root view");
        } else {
            hideLoadingView();
            getRootView().addView(getLoadingView(),
                                  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                             ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    protected void hideLoadingView() {

        Log.v(TAG, "hideLoadingView");
        if (getRootView() == null) {
            Log.w(TAG, "hideLoadingView - Error: impossible to retrieve root view");
        } else {
            getRootView().removeView(getLoadingView());
        }
    }

    protected ViewGroup getRootView() {

        Log.v(TAG, "getRootView");
        ViewGroup result = null;
        if (mAdView == null) {
            Log.w(TAG, "getRootView - Error: not assigned ad view, cannot retrieve root view");
        } else {
            result = (ViewGroup) mAdView.getRootView();
        }
        return result;
    }

    protected RelativeLayout getLoadingView() {

        Log.v(TAG, "getLoadingView");
        if (loadingView == null) {
            loadingView = new RelativeLayout(mAdView.getContext());
            loadingView.setGravity(Gravity.CENTER);
            loadingView.setBackgroundColor(Color.argb(77, 0, 0, 0));
            loadingView.setClickable(true);
            loadingView.addView(new ProgressBar(mAdView.getContext()));
        }
        return loadingView;
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================

    protected void invokeOnImpression(View view) {

        Log.v(TAG, "invokeOnImpression");
        mIsImpressionConfirmed = true;
        if (mListener != null) {
            mListener.onPubnativeAdModelImpression(PubnativeAdModel.this, view);
        }
    }

    protected void invokeOnClick(View view) {

        Log.v(TAG, "invokeOnClick");
        if (mListener != null) {
            mListener.onPubnativeAdModelClick(PubnativeAdModel.this, view);
        }
    }

    protected void invokeOnOpenOffer() {

        Log.v(TAG, "invokeOnOpenOffer");
        if (mListener != null) {
            mListener.onPubnativeAdModelOpenOffer(this);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeImpressionTracker.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onImpressionDetected(View view) {

        Log.v(TAG, "onImpressionDetected");
        confirmImpressionBeacons(view);
        invokeOnImpression(view);
    }

    //----------------------------------------------------------------------------------------------
    // URLDriller.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onURLDrillerStart(String url) {

        Log.v(TAG, "onURLDrillerStart: " + url);
    }

    @Override
    public void onURLDrillerRedirect(String url) {

        Log.v(TAG, "onURLDrillerRedirect: " + url);
    }

    @Override
    public void onURLDrillerFinish(String url) {

        Log.v(TAG, "onURLDrillerFinish: " + url);
        openURL(url);
        hideLoadingView();
    }

    @Override
    public void onURLDrillerFail(String url, Exception exception) {

        Log.v(TAG, "onURLDrillerFail: " + exception);
        openURL(url);
        hideLoadingView();
    }
}
