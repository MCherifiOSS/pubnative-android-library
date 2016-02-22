package net.pubnative.library.models;

import android.content.Context;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdModelTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testGetBeaconWithNullValueReturnsNull() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);

        String url = model.getBeacon(null);

        assertThat(url).isNull();
    }

    @Test
    public void testGetBeaconWithEmptyValueReturnsNull() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);

        String url = model.getBeacon("");

        assertThat(url).isNull();
    }

    @Test
    public void testGetBeaconWithNotContainedValueReturnsNull() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.setBeacons(mock(List.class));

        String url = model.getBeacon("");

        assertThat(url).isNull();
    }

    @Test
    public void testGetBeaconWitContainedValueReturnsBeacon() {

        String testType = "type";
        String testValue = "value";

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        PubnativeBeacon beacon = spy(PubnativeBeacon.class);
        beacon.type = testType;
        beacon.url = testValue;

        List<PubnativeBeacon> beacons = new ArrayList<PubnativeBeacon>();
        beacons.add(beacon);
        model.setBeacons(beacons);

        String url = model.getBeacon(testType);

        assertThat(url).isEqualTo(testValue);
    }

    @Test
    public void testStartTrackingWithValidListener() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = mock(PubnativeAdModel.Listener.class);
        View                        adView      = spy(new View(applicationContext));

        model.startTracking(adView, listener);
        model.invokeOnImpressionConfirmed(adView);
        verify(listener, times(1)).onPubnativeAdModelImpressionConfirmed(eq(model), eq(adView));
    }

    @Test
    public void testStartTrackingWithNullListener() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        View                        adView      = spy(new View(applicationContext));

        model.startTracking(adView, adView, null);
        model.invokeOnImpressionConfirmed(adView);
    }

    @Test
    public void testStartTrackingWithClickableView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = mock(PubnativeAdModel.Listener.class);
        View                        adView      = spy(new View(applicationContext));
        View                        clickableView = spy(new View(applicationContext));

        model.startTracking(adView, clickableView, listener);

        verify(clickableView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithClickableViewForValidClickListener() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = mock(PubnativeAdModel.Listener.class);
        View                        adView      = spy(new View(applicationContext));
        View                        clickableView = spy(new View(applicationContext));

        model.startTracking(adView, clickableView, listener);

        verify(adView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithoutClickableView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = mock(PubnativeAdModel.Listener.class);
        View                        adView      = spy(new View(applicationContext));

        model.startTracking(adView, listener);

        verify(adView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithNullView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = mock(PubnativeAdModel.Listener.class);

        model.startTracking(null, listener);

        verify(listener, times(1)).onPubnativeAdModelImpressionFailed(eq(model), any(Exception.class));
    }

    @Test
    public void testStartTrackingWithNullClickableView() {

        PubnativeAdModel            model       = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener   listener    = mock(PubnativeAdModel.Listener.class);

        View                        adView      = spy(new View(applicationContext));

        model.startTracking(adView, null, listener);
        verify(listener, times(1)).onPubnativeAdModelClickFailed(eq(model), any(Exception.class));
    }
}
