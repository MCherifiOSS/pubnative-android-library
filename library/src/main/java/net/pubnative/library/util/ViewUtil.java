/**
 * Copyright 2014 PubNative GmbH
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pubnative.library.util;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.View;

public class ViewUtil {
    /**
     * Gets the percentage value of the given View's visibility.
     * @param view View object, whose visibility percentage to be calculated
     * @return Visible percentage value
     */
    public static int getVisiblePercent(View view) {
        if (view.isShown()) {
            Rect r = new Rect();
            view.getGlobalVisibleRect(r);
            double sVisible = r.width() * r.height();
            double sTotal = view.getWidth() * view.getHeight();
            return (int) (100 * sVisible / sTotal);
        } else {
            return -1;
        }
    }

    /**
     * Gets the full screen size
     * @param context     Context object
     * @param mediaPlayer MediaPlayer object
     * @return Point object
     */
    public static Point getFullSceeenSize(Context context, MediaPlayer mediaPlayer) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int videoW = mediaPlayer.getVideoWidth();
        int videoH = mediaPlayer.getVideoHeight();
        float screenW = dm.widthPixels;
        float screenH = dm.heightPixels;
        float scale = Math.min(screenW / videoW, screenH / videoH);
        Point p = new Point();
        p.x = (int) (videoW * scale);
        p.y = (int) (videoH * scale);
        return p;
    }
}