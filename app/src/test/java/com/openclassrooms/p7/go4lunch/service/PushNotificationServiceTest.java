package com.openclassrooms.p7.go4lunch.service;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.work.WorkerParameters;

import com.openclassrooms.p7.go4lunch.injector.Go4LunchApplication;
import com.openclassrooms.p7.go4lunch.notification.PushNotificationService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Calendar;

@RunWith(AndroidJUnit4.class)
public class PushNotificationServiceTest {

    private PushNotificationService pushNotificationService;
    private Context context = Go4LunchApplication.getContext();
    @Mock
    private WorkerParameters workerParameters;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
//        pushNotificationService = new PushNotificationService(context, workerParameters);
    }

    @Test
    public void setTimeUntilBeginWork_shouldReturn_timeUntil12am() {
        Calendar currentTime = Calendar.getInstance();
        Calendar timeExpected = Calendar.getInstance();
        timeExpected.set(Calendar.HOUR_OF_DAY, 12);
        timeExpected.set(Calendar.MINUTE, 0);
        timeExpected.set(Calendar.SECOND, 0);
        if (timeExpected.before(currentTime)) {
            timeExpected.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeToTest = PushNotificationService.setTimeUntilBeginWork();
        assertEquals(timeExpected.getTimeInMillis(), timeToTest);
    }
}
