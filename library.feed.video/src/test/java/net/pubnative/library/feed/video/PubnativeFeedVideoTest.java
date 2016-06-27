package net.pubnative.library.feed.video;

import android.os.Handler;

import net.pubnative.library.feed.video.PubnativeFeedVideo;
import net.pubnative.library.request.PubnativeRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = net.pubnative.library.BuildConfig.class,
        sdk = 21)
public class PubnativeFeedVideoTest {
    @Test
    public void load_withNullContext_pass() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.load(null, "app_token");

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void load_withInvalidAppToken_pass() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.load(RuntimeEnvironment.application.getApplicationContext(), "");

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void load_withNullAppToken_pass() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.load(RuntimeEnvironment.application.getApplicationContext(), null);

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestSuccess_withoutAds_invokesLoadFail() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        video.onPubnativeRequestSuccess(request, null);
        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestFailed_invokesLoadFailed() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        video.onPubnativeRequestFailed(request, mock(Exception.class));
        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void invokeLoadFail_withNullListener_shouldPass() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        video.invokeLoadFail(mock(Exception.class));
    }

    @Test
    public void invokeLoadFail_withValidListener_invokesLoadFailed() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        Exception exception = mock(Exception.class);
        video.invokeLoadFail(exception);

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), eq(exception));
    }

    @Test
    public void invokeLoadFinish_withNullListener_shouldPass() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        video.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFinish_withValidListener_invokesLoadFinish() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.invokeLoadFinish();

        verify(listener).onPubnativeFeedVideoLoadFinish(eq(video));
    }

    @Test
    public void invokeShow_withNullListener_shouldPass() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        video.invokeShow();
    }

    @Test
    public void invokeShow_withValidListener_invokesShow() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.mHandler = new Handler();
        video.invokeShow();

        verify(listener).onPubnativeFeedVideoShow(eq(video));
    }

    @Test
    public void invokeClick_withNullListener_shouldPass() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        video.invokeVideoClick();
    }

    @Test
    public void invokeClick_withValidListener_invokesClick() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.mHandler = new Handler();
        video.invokeVideoClick();

        verify(listener).onPubnativeFeedVideoClick(eq(video));
    }
}