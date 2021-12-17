package com.openclassrooms.p7.go4lunch.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.TimeOfWeek;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.model.RestaurantData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
@RunWith(MockitoJUnitRunner.class)
public class ServiceTest {
    ApiService service;
    @Mock
    OpeningHours openingHours;

    @Before
    public void setup() {
        service = DI.getRestaurantApiService();

        RestaurantData restaurantDataTest0 = new RestaurantData( "AAAA", "Restaurant le jasmin", true, false);
        RestaurantData restaurantDataTest4 = new RestaurantData( "BBBB", "RESTAURANT le goéland", false, true);
        RestaurantData restaurantDataTest1 = new RestaurantData( "BBBB", "RESTAURANT le goéland", false, true);
        RestaurantData restaurantDataTest2 = new RestaurantData( "CCCC", "restaurant le Kébab", true, false);
        RestaurantData restaurantDataTest3 = new RestaurantData( "AAAA", "Restaurant le jasmin", true, false);
        RestaurantData restaurantDataTest5 = new RestaurantData( "CCCC", "restaurant le Kébab", true, false);
        RestaurantData restaurantDataTest6 = new RestaurantData( "BBBB", "RESTAURANT le goéland", false, false);

        service.getUserAndRestaurant().add(restaurantDataTest0);
        service.getUserAndRestaurant().add(restaurantDataTest1);
        service.getUserAndRestaurant().add(restaurantDataTest2);
        service.getUserAndRestaurant().add(restaurantDataTest3);
        service.getUserAndRestaurant().add(restaurantDataTest4);
        service.getUserAndRestaurant().add(restaurantDataTest5);
        service.getUserAndRestaurant().add(restaurantDataTest6);

        Map<String, RestaurantData> userAndRestaurantMapTest0 = new HashMap<>();
        userAndRestaurantMapTest0.put(restaurantDataTest0.getRestaurantId(), restaurantDataTest0);
        userAndRestaurantMapTest0.put(restaurantDataTest4.getRestaurantId(), restaurantDataTest4);

        Map<String, RestaurantData> userAndRestaurantMapTest1 = new HashMap<>();
        userAndRestaurantMapTest1.put(restaurantDataTest2.getRestaurantId(), restaurantDataTest2);
        userAndRestaurantMapTest1.put(restaurantDataTest6.getRestaurantId(), restaurantDataTest6);

        Map<String, RestaurantData> userAndRestaurantMapTest2 = new HashMap<>();
        userAndRestaurantMapTest2.put(restaurantDataTest1.getRestaurantId(), restaurantDataTest1);
        userAndRestaurantMapTest2.put(restaurantDataTest3.getRestaurantId(), restaurantDataTest3);
        userAndRestaurantMapTest2.put(restaurantDataTest5.getRestaurantId(), restaurantDataTest5);


//        User userTest0 = new User("1111", "one test", "https://i.pravatar.cc/150?u=a042581f4e29026704d", userAndRestaurantMapTest0);
//        User userTest1 = new User("2222", "two test", "https://i.pravatar.cc/150?u=a042581f4e29026704e", userAndRestaurantMapTest1);
//        User userTest2 = new User("3333", "three test", "https://i.pravatar.cc/150?u=a042581f4e29026704f", userAndRestaurantMapTest2);

//        service.getUsers().add(userTest0);
//        service.getUsers().add(userTest1);
//        service.getUsers().add(userTest2);

        LatLng latLngTest = new LatLng(4.5236, 6.7854);
        Restaurant restaurantTest0 = new Restaurant("AAAA", "Restaurant le jasmin", "3 rue St Félicien", "18h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3);
        Restaurant restaurantTest1 = new Restaurant("BBBB", "RESTAURANT le goéland", "5 boulevard du battaillon", "15h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 9);
        Restaurant restaurantTest2 = new Restaurant("CCCC", "restaurant le Kébab", "270 avenue camille blanc", "11h30", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 15);

        service.getRestaurant().add(restaurantTest0);
        service.getRestaurant().add(restaurantTest1);
        service.getRestaurant().add(restaurantTest2);

        Restaurant restaurantTest3 = new Restaurant("DDDD", "le Syrien", "270 rue du voisin", "08h00", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3);

        service.addSearchedRestaurant(restaurantTest3);
    }

    @After
    public void finish() {
        service.getUsers().removeAll(service.getUsers());
        service.getRestaurant().removeAll(service.getRestaurant());
        service.getUserAndRestaurant().removeAll(service.getUserAndRestaurant());
        service.getSearchedRestaurant().removeAll(service.getSearchedRestaurant());
        service = null;
    }

    @Test
    public void getUserWithSuccess() {
        // ARRANGE
        int listExpected = 3;
        // ASSERT
        assertEquals(listExpected, service.getUsers().size());
    }

    @Test
    public void getRestaurantWithSuccess() {
        // ARRANGE
        int listExpected = 3;

        // ASSERT
        assertEquals(listExpected, service.getRestaurant().size());
    }

    @Test
    public void getUserAndRestaurantWithSuccess() {
        // ARRANGE
        int listExpected = 7;

        // ASSERT
        assertEquals(listExpected, service.getUserAndRestaurant().size());
    }

    @Test
    public void getSearchedRestaurantWithSuccess() {
        // ARRANGE
        int listSizeExpected = 1;
        // ASSERT
        assertEquals(listSizeExpected, service.getSearchedRestaurant().size());
    }

    @Test
    public void addUserAndRestaurantWithSuccess() {
        // ARRANGE
        int listSizeExpected = 8;

        // ACT
        RestaurantData restaurantDataToAdd = new RestaurantData( "987654321", "Restaurant le jasmin", true, true);
        service.addUserAndRestaurant(restaurantDataToAdd);

        // ASSERT
        assertEquals(listSizeExpected, service.getUserAndRestaurant().size());
    }

    @Test
    public void addRestaurantWithSuccess() {
        // ARRANGE
        int listSizeExpected = 4;

        // ACT
        Restaurant restaurantToAdd = new Restaurant("965874123", "Restaurant le jasmin", "3 rue St Félicien", "18h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 3);
        service.addRestaurant(restaurantToAdd);

        // ASSERT
        assertEquals(listSizeExpected, service.getRestaurant().size());
    }

    @Test
    public void addUserWithSuccess() {
        // ARRANGE
        int listSizeExpected = 4;

        // ACT
//        User userToAdd = new User("123456789", "test0", "https://i.pravatar.cc/150?u=a042581f4e29026704d", null);
//        service.addUser(userToAdd);
        // ASSERT
        assertEquals(listSizeExpected, service.getUsers().size());
    }

    @Test
    public void addSearchedRestaurantWithSuccess() {
        // ARRANGE
        int listSizeExpected = 2;

        // ACT
        Restaurant restaurantTest3 = new Restaurant("451236987", "le Cyrien", "270 rue du voisin", "08h00", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 3);
        service.addSearchedRestaurant(restaurantTest3);

        // ASSERT
        assertEquals(listSizeExpected, service.getSearchedRestaurant().size());
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
        Map<String, RestaurantData> userAndRestaurantMapToTest = service.makeUserAndRestaurantMap(userIdToTest);

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

//    @Test
//    public void makeStringOpeningHoursShouldReturnTimeBeforeClose() {
//        // ARRANGE
//        LocalTime timeToTest = LocalTime.newInstance(10,0);
//        when(openingHours.getPeriods()).thenReturn(createOpeningHoursForTest().getPeriods());
//        // ACT
//        String hourExpected = service.makeStringOpeningHours(openingHours, "MONDAY", timeToTest);
//        // ASSERT
//        assertEquals(hourExpected, "4");
//    }
//
//    @Test
//    public void makeStringOpeningHoursShouldReturnOpenTime() {
//        // ARRANGE
//        LocalTime timeToTest = LocalTime.newInstance(7,0);
//        when(openingHours.getPeriods()).thenReturn(createOpeningHoursForTest().getPeriods());
//        // ACT
//        String hourExpected = service.makeStringOpeningHours(openingHours, "MONDAY", timeToTest);
//        // ASSERT
//        assertEquals(hourExpected, "8:30");
//    }
//
//    @Test
//    public void makeStringOpeningHoursShouldReturnStillClosedWhenToLate() {
//        // ARRANGE
//        LocalTime timeToTest = LocalTime.newInstance(15,0);
//        when(openingHours.getPeriods()).thenReturn(createOpeningHoursForTest().getPeriods());
//        // ACT
//        String hourExpected = service.makeStringOpeningHours(openingHours, "MONDAY", timeToTest);
//        // ASSERT
//        assertEquals(hourExpected, "still closed");
//    }
//
//    @Test
//    public void makeStringOpeningHoursShouldReturnStillClosedWhenWeekDay() {
//        // ARRANGE
//        LocalTime timeToTest = LocalTime.newInstance(15,0);
//        // ACT
//        when(openingHours.getPeriods()).thenReturn(createOpeningHoursForTest().getPeriods());
//        String expectedString = service.makeStringOpeningHours(openingHours, "SATURDAY", timeToTest);
//        // ASSERT
//        assertEquals("still closed", expectedString);
//    }

    @Test
    public void makeUserFirstNameShouldReturnTest() {
        // ARRANGE
        String userNameToTest = "test one";

        // ACT
        String firstName = service.makeUserFirstName(userNameToTest);

        // ASSERT
        assertEquals("Test", firstName);
    }

    @Test
    public void makeInterestedFriendsStringWithSuccess() {
        // ARRANGE
        String friendInterestedExpected = "One\nTwo\nThree";
        // ACT
        String frienInterestedToTest = service.makeInterestedFriendsString(service.getUsers());
        // ASSERT
        assertEquals(friendInterestedExpected, friendInterestedExpected);
    }

    @Test
    public void removeUselessWordsShouldRemoveRestaurantString() {
        // ARRANGE
        String restaurantNameExpected = "Le jasmin";
        // ACT
        String restaurantNameToTest = service.removeUselessWords(service.getRestaurant().get(0).getName());
        // ASSERT
        assertEquals(restaurantNameExpected, restaurantNameToTest);
    }

    @Test
    public void searchUserByIdWithSuccess() {
        // ARRANGE
        User userExpected = service.getUsers().get(0);
        // ACT
        User userToTest = service.searchUserById("1111");
        // ASSERT
        assertEquals(userExpected, userToTest);
    }

    @Test
    public void searchCurrentRestaurantByIdWithSuccess() {
        // ARRANGE
        Restaurant restaurantExpected = service.getRestaurant().get(0);
        // ACT
        Restaurant restaurantToTest = service.searchCurrentRestaurantById("AAAA");
        // ASSERT
        assertEquals(restaurantExpected, restaurantToTest);
    }

//    @Test
//    public void searchUserAndRestaurantByIdWithSuccess() {
//        // ARRANGE
//        UserAndRestaurant userAndRestaurantExpected = service.getUserAndRestaurant().get(4);
//        // ACT
//        Restaurant userAndRestaurantToTest = service.searchUserAndRestaurantById("BBBB");
//        // ASSERT
//        assertEquals(userAndRestaurantExpected, userAndRestaurantToTest);
//    }

    @Test
    public void searchSelectedUserAndRestaurantToDeselectWithSuccess() {
        // ARRANGE
        RestaurantData restaurantDataTest0 = new RestaurantData( "AAAA", "Restaurant le jasmin", true, false);
        RestaurantData restaurantDataTest4 = new RestaurantData( "BBBB", "RESTAURANT le goéland", false, true);
        Map<String, RestaurantData> userAndRestaurantMapTest0 = new HashMap<>();
        userAndRestaurantMapTest0.put(restaurantDataTest0.getRestaurantId(), restaurantDataTest0);
        userAndRestaurantMapTest0.put(restaurantDataTest4.getRestaurantId(), restaurantDataTest4);
//        User user = new User("1111", "one test", "https://i.pravatar.cc/150?u=a042581f4e29026704d", userAndRestaurantMapTest0);
//        UserAndRestaurant userAndRestaurantSelected = user.getRestaurantDataMap().get("BBBB");
//        String restaurantId = Objects.requireNonNull(user.getRestaurantDataMap().get("BBBB")).getRestaurantId();
//        assert userAndRestaurantSelected != null;
//        assertTrue(userAndRestaurantSelected.isSelected());
//        // ACT
//        service.searchSelectedRestaurant(user);
//        // ASSERT
//        assertFalse(userAndRestaurantSelected.isSelected());
    }

    @Test
    public void likeOrSelectRestaurantLikeWithSuccess() {
        // ARRANGE
        RestaurantData restaurantDataToTest = service.getUserAndRestaurant().get(0);
        assertTrue(restaurantDataToTest.isFavorite());
        String userId = service.getUsers().get(0).getUid();
        String restaurantId = service.getRestaurant().get(0).getId();
        // ACT
//        service.likeOrSelectRestaurant(userId, restaurantId, R.id.activity_detail_like_btn);
        // ASSERT
        assertFalse(restaurantDataToTest.isFavorite());

    }

    @Test
    public void likeOrSelectRestaurantSelectWithSuccess() {
        // ARRANGE
        RestaurantData restaurantDataToTest = service.getUserAndRestaurant().get(0);
        assertFalse(restaurantDataToTest.isSelected());
        String userId = service.getUsers().get(0).getUid();
        String restaurantId = service.getRestaurant().get(0).getId();
        // ACT
//        service.likeOrSelectRestaurant(userId, restaurantId, 12345);
        // ASSERT
        assertTrue(restaurantDataToTest.isSelected());

    }

//    @Test
//    public void searchSelectedRestaurantShouldReturnUserAndRestaurantSelected() {
//        // ARRANGE
//        UserAndRestaurant userAndRestaurantExpected = service.getUserAndRestaurant().get(4);
//        User userToTest = service.getUsers().get(0);
//        // ACT
//        UserAndRestaurant userAndRestaurantToTest = service.searchSelectedRestaurant(userToTest);
//        // ASSERT
//        assertEquals(userAndRestaurantExpected, userAndRestaurantToTest);
//    }
//
//    @Test
//    public void getUsersInterestedAtCurrentRestaurantShouldReturnUserInterestedList() {
//        // ARRANGE
//        String userIdToTest = service.getUsers().get(0).getUid();
//        Restaurant restaurantToTest = service.getRestaurant().get(1);
//        List<User> userListExpected = new ArrayList<>();
//        userListExpected.add(service.getUsers().get(2));
//        // ACT
//        List<User> userListToTest = service.getUsersInterestedAtCurrentRestaurants(userIdToTest, restaurantToTest);
//        // ASSERT
//        assertEquals(userListExpected, userListToTest);
//
//    }
//
//    @Test
//    public void getUsersInterestedAtCurrentRestaurantsForNotificationShouldReturnUserInterestedList() {
//        // ARRANGE
//        List<User> userListExpected = new ArrayList<>();
//        userListExpected.add(service.getUsers().get(2));
//        String userIdToTest = service.getUsers().get(0).getUid();
//        String restaurantIdToTest = service.getRestaurant().get(1).getId();
//        // ACT
//        List<User> userListToTest = service.getUsersInterestedAtCurrentRestaurantForNotification(userIdToTest, restaurantIdToTest);
//        // ASSERT
//        assertEquals(userListExpected, userListToTest);
//    }
//
//    @Test
//    public void getCurrentUserSelectedRestaurantShouldReturnUserAndRestaurantSelected() {
//        // ARRANGE
//        UserAndRestaurant userAndRestaurantExpected = service.getUserAndRestaurant().get(1);
//        User userToTest = service.getUsers().get(2);
//        // ACT
//        UserAndRestaurant userAndRestaurantToTest = service.getCurrentUserSelectedRestaurant(userToTest);
//        // ASSERT
//        assertEquals(userAndRestaurantExpected, userAndRestaurantToTest);
//    }
//
//    @Test
//    public void getCurrentUserSelectedRestaurantShouldReturnNull() {
//        // ARRANGE
//        User userToTest = service.getUsers().get(1);
//        // ACT
//        UserAndRestaurant userAndRestaurantToTest = service.getCurrentUserSelectedRestaurant(userToTest);
//        // ASSERT
//        assertNull(userAndRestaurantToTest);
//    }

    @Test
    public void getRatingShouldNotReturn0() {
        // ARRANGE
        Double ratingExpected = 4.5;
        // ACT
        Double ratingToTest = service.getRating(ratingExpected);
        // ASSERT
        assertEquals(ratingExpected, ratingToTest);
    }

    @Test
    public void getRatingShouldNotReturnNull() {
        // ARRANGE
        Double ratingExpected = 0.0;
        // ACT
        Double ratingToTest = service.getRating(null);
        // ASSERT
        assertEquals(ratingExpected, ratingToTest);
    }

    @Test
    public void setRatingStarsShoudReturnStarBorder() {
        int drawableExpected = R.drawable.baseline_star_border_black_24;
        int indexToTest = 0;
        double ratingToTest = 0.0;
        int drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);

        indexToTest = 2;
        ratingToTest = 3.2;
        drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);

        indexToTest = 1;
        ratingToTest = 1.8;
        drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void setRatingStarsShoudReturnStarHalf() {
        int drawableExpected = R.drawable.baseline_star_half_black_24;
        int indexToTest = 1;
        double ratingToTest = 2;
        int drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);

        indexToTest = 2;
        ratingToTest = 4;
        drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void setRatingStarsShoudReturnStarRate() {
        int drawableExpected = R.drawable.baseline_star_rate_black_24;
        int indexToTest = 2;
        double ratingToTest = 5;
        int drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);

        indexToTest = 1;
        ratingToTest = 3;
        drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);

        indexToTest = 0;
        ratingToTest = 1;
        drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void listViewComparator() {
        //ARRANGE
        //ACT
        service.listViewComparator();
        //ASSERT
        assertEquals("CCCC", service.getRestaurant().get(0).getId());
        assertEquals("BBBB", service.getRestaurant().get(1).getId());
        assertEquals("AAAA", service.getRestaurant().get(2).getId());
    }

//    @Test
//    public void filterUsersInterestedAtCurrentRestaurant() {
//        //ARRANGE
//        //ACT
//        service.filterUsersInterestedAtCurrentRestaurant();
//        //ASSERT
//        assertEquals("1111", service.getUsers().get(0).getUid());
//        assertEquals("3333", service.getUsers().get(1).getUid());
//        assertEquals("2222", service.getUsers().get(2).getUid());
//    }

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
                return periodTest;
            }

            @NonNull
            @Override
            public List<String> getWeekdayText() {
                List<String> weekDayTest = new ArrayList<>();
                weekDayTest.add("Saturday: fermé");
                weekDayTest.add("Sunday: fermé");
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

    private Period createOpenTestPeriod(DayOfWeek dayOfWeek) {
        TimeOfWeek openHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(8,30));
        TimeOfWeek closeHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(14,0));
        return Period.builder()
                .setOpen(openHours)
                .setClose(closeHours)
                .build();
    }
}