package com.openclassrooms.p7.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import static java.util.Calendar.getInstance;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.TimeOfWeek;

import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.UserAndRestaurant;
import com.openclassrooms.p7.go4lunch.service.ApiService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class ServiceTest {
    ApiService service;
//    Context context;


    @Before
    public void setup() throws MalformedURLException {
        service = DI.getRestaurantApiService();
//        context = ApplicationProvider.getApplicationContext();

        UserAndRestaurant userAndRestaurantTest0 = new UserAndRestaurant("1111", "AAAA", "Restaurant le jasmin", true, true);
        UserAndRestaurant userAndRestaurantTest1 = new UserAndRestaurant("3333", "BBBB", "RESTAURANT le goéland", false, true);
        UserAndRestaurant userAndRestaurantTest2 = new UserAndRestaurant("2222", "CCCC", "restaurant le Kébab", true, false);
        UserAndRestaurant userAndRestaurantTest3 = new UserAndRestaurant("3333", "AAAA", "Restaurant le jasmin", true, true);
        UserAndRestaurant userAndRestaurantTest4 = new UserAndRestaurant("1111", "BBBB", "RESTAURANT le goéland", false, true);
        UserAndRestaurant userAndRestaurantTest5 = new UserAndRestaurant("3333", "CCCC", "restaurant le Kébab", true, false);

        service.addUserAndRestaurant(userAndRestaurantTest0);
        service.addUserAndRestaurant(userAndRestaurantTest1);
        service.addUserAndRestaurant(userAndRestaurantTest2);
        service.addUserAndRestaurant(userAndRestaurantTest3);
        service.addUserAndRestaurant(userAndRestaurantTest4);
        service.addUserAndRestaurant(userAndRestaurantTest5);

        Map<String, UserAndRestaurant> userAndRestaurantMapTest = new HashMap<>();
        userAndRestaurantMapTest.put(userAndRestaurantTest0.getRestaurantId(), userAndRestaurantTest0);
        userAndRestaurantMapTest.put(userAndRestaurantTest1.getRestaurantId(), userAndRestaurantTest1);
        userAndRestaurantMapTest.put(userAndRestaurantTest2.getRestaurantId(), userAndRestaurantTest2);

        User userTest0 = new User("1111", "test0", "https://i.pravatar.cc/150?u=a042581f4e29026704d", userAndRestaurantMapTest);
        User userTest1 = new User("2222", "test1", "https://i.pravatar.cc/150?u=a042581f4e29026704e", userAndRestaurantMapTest);
        User userTest2 = new User("3333", "test2", "https://i.pravatar.cc/150?u=a042581f4e29026704f", userAndRestaurantMapTest);

        service.addUser(userTest0);
        service.addUser(userTest1);
        service.addUser(userTest2);

        LatLng latLngTest = new LatLng(4.5236, 6.7854);
        Restaurant restaurantTest0 = new Restaurant("AAAA", "Restaurant le jasmin", "3 rue St Félicien", "18h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3);
        Restaurant restaurantTest1 = new Restaurant("BBBB", "RESTAURANT le goéland", "5 boulevard du battaillon", "15h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3);
        Restaurant restaurantTest2 = new Restaurant("CCCC", "restaurant le Kébab", "270 avenue camille blanc", "11h30", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3);

        service.addRestaurant(restaurantTest0);
        service.addRestaurant(restaurantTest1);
        service.addRestaurant(restaurantTest2);

        Restaurant restaurantTest3 = new Restaurant("DDDD", "le Syrien", "270 rue du voisin", "08h00", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3);

        service.addSearchedRestaurant(restaurantTest3);
    }

    @After
    public void finish() {
        service.getUsers().removeAll(service.getUsers());
        service.getRestaurant().removeAll(service.getRestaurant());
        service.getUserAndRestaurant().removeAll(service.getUserAndRestaurant());
        service = null;
    }

    @Test
    public void getUserWithSuccess() {
        // ARRANGE
        int listExpected = 3;

        // ASSERT
        assertEquals(service.getUsers().size(), listExpected);
    }

    @Test
    public void getRestaurantWithSuccess() {
        // ARRANGE
        int listExpected = 3;

        // ASSERT
        assertEquals(service.getRestaurant().size(), listExpected);
    }

    @Test
    public void getUserAndRestaurantWithSuccess() {
        // ARRANGE
        int listExpected = 3;

        // ASSERT
        assertEquals(service.getUserAndRestaurant().size(), listExpected);
    }

    @Test
    public void getSearchedRestaurantWithSuccess() {
        // ASSERT
        assertTrue(service.getSearchedRestaurant().isEmpty());
    }

    @Test
    public void addUserAndRestaurantWithSuccess() {
        // ARRANGE
        int listSizeExpected = 4;

        // ACT
        UserAndRestaurant userAndRestaurantToAdd = new UserAndRestaurant("123456789", "987654321", "Restaurant le jasmin", true, true);
        service.addUserAndRestaurant(userAndRestaurantToAdd);

        // ASSERT
        assertEquals(service.getUserAndRestaurant().size(), listSizeExpected);
    }

    @Test
    public void addRestaurantWithSuccess() {
        // ARRANGE
        int listSizeExpected = 4;

        // ACT
        Restaurant restaurantToAdd = new Restaurant("965874123", "Restaurant le jasmin", "3 rue St Félicien", "18h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 3);
        service.addRestaurant(restaurantToAdd);

        // ASSERT
        assertEquals(service.getRestaurant().size(), listSizeExpected);
    }

    @Test
    public void addUserWithSuccess() {
        // ARRANGE
        int listSizeExpected = 4;

        // ACT
        User userToAdd = new User("123456789", "test0", "https://i.pravatar.cc/150?u=a042581f4e29026704d", null);
        service.addUser(userToAdd);
        // ASSERT
        assertEquals(service.getUserAndRestaurant().size(), listSizeExpected);
    }

    @Test
    public void addSearchedRestaurantWithSuccess() {
        //TODO
        // ARRANGE
        int listSizeExpected = 1;

        // ACT
        Restaurant restaurantTest3 = new Restaurant("451236987", "le Cyrien", "270 rue du voisin", "08h00", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 3);
        service.addSearchedRestaurant(restaurantTest3);

        // ASSERT
        assertEquals(service.getUserAndRestaurant().size(), listSizeExpected);
    }

    @Test
    public void deleteUserWithSuccess() {
        // ARRANGE

        // ACT
        User userToDelete = service.getUsers().get(0);
        service.deleteUser(userToDelete);

        // ASSERT
        assertFalse(service.getUsers().contains(userToDelete));
    }

    @Test
    public void makeUserAndRestaurantMapWithSuccess() {
        // ARRANGE
        String userIdToTest = "1111";

        // ACT
        Map<String, UserAndRestaurant> userAndRestaurantMapToTest = service.makeUserAndRestaurantMap(userIdToTest);

        // ASSERT
        assertEquals(userAndRestaurantMapToTest.size(), 2);
    }

    @Test
    public void getCurrentDayWithSuccess() {
        // ARRANGE
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, 4);
        String dayExpected = "WEDNESDAY";

        // ACT
        String dayTest = service.getCurrentDay(calendar);

        // ASSERT
        assertEquals(dayExpected, dayTest);
    }

    public void makeStringOpeningHoursWithSuccess() {
        // ARRANGE
        OpeningHours openingHoursToTest = createOpeningHoursForTest();
        Calendar calendar = getInstance();
        // ACT
//        openingHoursToTest.getPeriods().
//        service.makeStringOpeningHours();
        // ASSERT
    }

    public OpeningHours createOpeningHoursForTest() {
        return new OpeningHours() {
            @NonNull
            @Override
            public List<Period> getPeriods() {
                List<Period> periodTest = new ArrayList<>();
                periodTest.add(createOpenTestPeriod(DayOfWeek.MONDAY));
                periodTest.add(createOpenTestPeriod(DayOfWeek.TUESDAY));
                periodTest.add(createOpenTestPeriod(DayOfWeek.WEDNESDAY));
                periodTest.add(createOpenTestPeriod(DayOfWeek.THURSDAY));
                periodTest.add(createOpenTestPeriod(DayOfWeek.FRIDAY));
                periodTest.add(createCloseTestPeriod(DayOfWeek.SATURDAY));
                periodTest.add(createCloseTestPeriod(DayOfWeek.SUNDAY));
                return periodTest;
            }

            @NonNull
            @Override
            public List<String> getWeekdayText() {
                List<String> weekDayTest = new ArrayList<>();
                weekDayTest.add("Saturday");
                weekDayTest.add("Sunday");
                return weekDayTest;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }
        };
    }

    private Period createCloseTestPeriod(DayOfWeek dayOfWeek) {
        TimeOfWeek closeDay = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(0,0));
        Period closePeriod = Period.builder()
                .setClose(closeDay)
                .build();
        return closePeriod;
    }

    private Period createOpenTestPeriod(DayOfWeek dayOfWeek) {
        TimeOfWeek openHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(8,30));
        TimeOfWeek closeHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(14,0));
        Period period = Period.builder()
                .setOpen(openHours)
                .setClose(closeHours)
                .build();
        return period;
    }
}