package cnedu.ustcjd.helloworld;

import android.content.Context;
import android.icu.util.TimeZone;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    private static final String TAG = "AppTest";
    @Test
    public void contextTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        //assertEquals("cnedu.ustcjd.helloworld", appContext.getPackageName());
    }
}