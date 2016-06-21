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

import android.content.Context;

import net.pubnative.library.request.model.PubnativeAdModel;

public class PubnativeVideo {

    private static final String TAG = PubnativeVideo.class.getSimpleName();

    protected Context                   mContext;
    protected PubnativeAdModel          mAdModel;
    protected PubnativeVideo.Listener   mListener;

    /**
     * Interface for callbacks related to the video behaviour
     */
    public interface Listener {

        /**
         * Called whenever the interstitial finished loading an ad
         *
         * @param video video that finished the load
         */
        void onPubnativeVideoLoadFinish(PubnativeVideo video);

        /**
         * Called whenever the interstitial failed loading an ad
         *
         * @param video     video that failed the load
         * @param exception exception with the description of the load error
         */
        void onPubnativeVideoLoadFail(PubnativeVideo video, Exception exception);
    }
}
