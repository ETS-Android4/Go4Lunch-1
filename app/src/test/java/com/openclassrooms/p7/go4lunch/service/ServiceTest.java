package com.openclassrooms.p7.go4lunch.service;

import static org.junit.Assert.assertEquals;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TimeOfWeek;
import com.openclassrooms.p7.go4lunch.R;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.Restaurant;
import com.openclassrooms.p7.go4lunch.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RunWith(JUnit4.class)
public class ServiceTest {
    private ApiService service;

    @Before
    public void setup() {
        service = DI.getRestaurantApiService();
    }

    @After
    public void finish() {

        service = null;
    }

    @Test
    public void getCurrentDay_shouldReturn_dayExpected() {
        // ARRANGE
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        String dayExpected = "SUNDAY";
        // ACT
        String dayToTest = service.getCurrentDay(calendar);
        // ASSERT
        assertEquals(dayExpected, dayToTest);

        // ARRANGE
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        dayExpected = "MONDAY";
        // ACT
        dayToTest = service.getCurrentDay(calendar);
        // ASSERT
        assertEquals(dayExpected,dayToTest);

        // ARRANGE
        calendar.set(Calendar.DAY_OF_WEEK, 3);
        dayExpected = "TUESDAY";
        // ACT
        dayToTest = service.getCurrentDay(calendar);
        // ASSERT
        assertEquals(dayExpected,dayToTest);

        // ARRANGE
        calendar.set(Calendar.DAY_OF_WEEK, 4);
        dayExpected = "WEDNESDAY";
        // ACT
        dayToTest = service.getCurrentDay(calendar);
        // ASSERT
        assertEquals(dayExpected,dayToTest);

        // ARRANGE
        calendar.set(Calendar.DAY_OF_WEEK, 5);
        dayExpected = "THURSDAY";
        // ACT
        dayToTest = service.getCurrentDay(calendar);
        // ASSERT
        assertEquals(dayExpected,dayToTest);

        // ARRANGE
        calendar.set(Calendar.DAY_OF_WEEK, 6);
        dayExpected = "FRIDAY";
        // ACT
        dayToTest = service.getCurrentDay(calendar);
        // ASSERT
        assertEquals(dayExpected,dayToTest);

        // ARRANGE
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        dayExpected = "SATURDAY";
        // ACT
        dayToTest = service.getCurrentDay(calendar);
        // ASSERT
        assertEquals(dayExpected,dayToTest);
    }

    @Test
    public void getOpeningHours_shouldReturn_5() {
        String shouldBe5 = service.getOpeningHours(null);
        assertEquals(shouldBe5, "5");
    }

    @Test
    public void makeStringOpeningHours_shouldReturn_1141() {
        String codeExpected = "1141";
        String dayToTest = "WEDNESDAY";
        LocalTime timeToTest = LocalTime.newInstance(13, 25);
        String codeToTest = service.makeStringOpeningHours(createOpeningHoursForTest(), dayToTest, timeToTest);
        assertEquals(codeExpected, codeToTest);
    }

    @Test
    public void makeStringOpeningHours_shouldReturn_08300() {
        String codeExpected = "08:300";
        String dayToTest = "WEDNESDAY";
        LocalTime timeToTest = LocalTime.newInstance(7, 0);
        String codeToTest = service.makeStringOpeningHours(createOpeningHoursForTest(), dayToTest, timeToTest);
        assertEquals(codeExpected, codeToTest);
    }

    @Test
    public void makeStringOpeningHours_shouldReturn_1140() {
        String codeExpected = "1140";
        String dayToTest = "WEDNESDAY";
        LocalTime timeToTest = LocalTime.newInstance(11, 45);
        String codeToTest = service.makeStringOpeningHours(createOpeningHoursForTest(), dayToTest, timeToTest);
        assertEquals(codeExpected, codeToTest);
    }

    @Test
    public void makeStringOpeningHours_shouldReturn_4() {
        String codeExpected = "4";
        String dayToTest = "WEDNESDAY";
        LocalTime timeToTest = LocalTime.newInstance(16, 30);
        String codeToTest = service.makeStringOpeningHours(createOpeningHoursForTest(), dayToTest, timeToTest);
        assertEquals(codeExpected, codeToTest);
    }

    @Test
    public void formatUserFirstName_ShouldReturn_Test() {
        // ARRANGE
        String userNameToTest = "test one";
        // ACT
        String firstName = service.formatUserFirstName(userNameToTest);
        // ASSERT
        assertEquals("Test", firstName);

        // ARRANGE
        userNameToTest = "test";
        // ACT
        firstName = service.formatUserFirstName(userNameToTest);
        // ASSERT
        assertEquals("test", firstName);
    }

    @Test
    public void formatRestaurantName_ShouldRemove_Restaurant() {
        // ARRANGE
        String restaurantNameExpected = "Le jasmin";
        String restaurantNameEmptyExpected = "";
        // ACT
        String restaurantNameToTest1 = service.formatRestaurantName("Restaurant le jasmin");
        String restaurantNameToTest2 = service.formatRestaurantName("RESTAURANT le jasmin");
        String restaurantNameToTest3 = service.formatRestaurantName("restaurant le jasmin");
        String restaurantNameToTest4 = service.formatRestaurantName("");
        // ASSERT
        assertEquals(restaurantNameExpected, restaurantNameToTest1);
        assertEquals(restaurantNameExpected, restaurantNameToTest2);
        assertEquals(restaurantNameExpected, restaurantNameToTest3);
        assertEquals(restaurantNameEmptyExpected, restaurantNameToTest4);
    }

    @Test
    public void formatInterestedFriends_shouldReturn_stringInList() {
        // ARRANGE
        String friendInterestedExpected = "Three\nTwo\nOne\n";
        // ACT
        String friendInterestedToTest = service.formatInterestedFriends(getDefaultUserList());
        // ASSERT
        assertEquals(friendInterestedExpected, friendInterestedToTest);
    }

    @Test
    public void getInterestedFriends_shouldReturn_2Users() {
        // ARRANGE
        List<User> userInterestedToTest = service.getInterestedFriend(getDefaultUserList(), "AAAA");
        // ACT

        // ASSERT
        assertEquals(2, userInterestedToTest.size());
    }

    @Test
    public void getRating_shouldNotReturn_0() {
        // ARRANGE
        Double ratingExpected = 4.5;
        // ACT
        Double ratingToTest = service.getRating(ratingExpected);
        // ASSERT
        assertEquals(ratingExpected, ratingToTest);
    }

    @Test
    public void getRating_shouldReturn_0() {
        // ARRANGE
        Double ratingExpected = 0.0;
        // ACT
        Double ratingToTest = service.getRating(null);
        // ASSERT
        assertEquals(ratingExpected, ratingToTest);
    }

    @Test
    public void getRectangularBound_shouldReturn_rectangularBound() {
        // ARRANGE
        double latitude = 43.4073612;
        double longitude = 3.6997723;
        LatLng currentLocation = new LatLng(latitude, longitude);
        LatLng southWest = new LatLng(latitude - 0.050000, longitude + 0.050000);
        LatLng northEast = new LatLng(latitude + 0.050000, longitude + 0.050000);
        RectangularBounds rectangularBoundsExpected = RectangularBounds.newInstance(
                new LatLng(southWest.latitude, southWest.longitude),
                new LatLng(northEast.latitude, northEast.longitude));
        // ACT
        RectangularBounds rectangularBoundsToTest = service.getRectangularBound(currentLocation);
        // ASSERT
        assertEquals(rectangularBoundsExpected, rectangularBoundsToTest);
    }

    @Test
    public void setRatingStars_shouldReturn_starBorder() {
        int drawableExpected = R.drawable.baseline_star_border_24;
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
    public void setRatingStars_shouldReturn_starHalf() {
        int drawableExpected = R.drawable.baseline_star_half_24;
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
    public void setRatingStars_shouldReturn_starRate() {
        // ARRANGE
        int drawableExpected = R.drawable.baseline_star_rate_24;
        int indexToTest = 2;
        double ratingToTest = 5;
        // ACT
        int drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        // ASSERT
        assertEquals(drawableExpected, drawableToTest);

        // ARRANGE
        indexToTest = 1;
        ratingToTest = 3;
        // ACT
        drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        // ASSERT
        assertEquals(drawableExpected, drawableToTest);

        // ARRANGE
        indexToTest = 0;
        ratingToTest = 1;
        // ACT
        drawableToTest = service.setRatingStars(indexToTest, ratingToTest);
        // ASSERT
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void setFavoriteImage_shouldReturn_ic_star() {
        // ARRANGE
        int drawableExpected = R.drawable.ic_star;
        // ACT
        int drawableToTest = service.setFavoriteImage(true);
        // ASSERT
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void setFavoriteImage_shouldReturn_ic_star_outline() {
        // ARRANGE
        int drawableExpected = R.drawable.ic_star_outline;
        // ACT
        int drawableToTest = service.setFavoriteImage(false);
        // ASSERT
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void setSelectedImage_shouldReturn_baseline_check_circle_24() {
        // ARRANGE
        int drawableExpected = R.drawable.baseline_check_circle_24;
        // ACT
        int drawableToTest = service.setSelectedImage(true);
        // ASSERT
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void setSelectedImage_shouldReturn_baseline_check_circle_outline_24() {
        // ARRANGE
        int drawableExpected = R.drawable.baseline_check_circle_outline_24;
        // ACT
        int drawableToTest = service.setSelectedImage(false);
        // ASSERT
        assertEquals(drawableExpected, drawableToTest);
    }

    @Test
    public void listViewComparator_shouldReturn_cbda() {
        //ARRANGE
        List<Restaurant> restaurantListToTest = new ArrayList<>();
        restaurantListToTest = getDefaultRestaurantList();
        //ACT
        service.listViewComparator(restaurantListToTest);
        //ASSERT
        assertEquals("CCCC", restaurantListToTest.get(0).getId());
        assertEquals("BBBB", restaurantListToTest.get(1).getId());
        assertEquals("DDDD", restaurantListToTest.get(2).getId());
        assertEquals("AAAA", restaurantListToTest.get(3).getId());
    }

    @Test
    public void workmatesViewComparator_shouldReturn() {
        // ARRANGE
        List<User> userListToTest = new ArrayList<>();
        userListToTest = getDefaultUserList();
        // ACT
        service.workmatesViewComparator(userListToTest);
        // ASSERT
        assertEquals("restaurant3", userListToTest.get(0).getRestaurantName());
        assertEquals("restaurant2", userListToTest.get(1).getRestaurantName());
        assertEquals("restaurant1", userListToTest.get(2).getRestaurantName());
    }

    public OpeningHours createOpeningHoursForTest() {
        return new OpeningHours() {
            @NonNull
            @Override
            public List<Period> getPeriods() {
                List<Period> periodTest = new ArrayList<>();
                periodTest.add(createMorningOpenTestPeriod(DayOfWeek.MONDAY));
                periodTest.add(createMorningOpenTestPeriod(DayOfWeek.TUESDAY));
                periodTest.add(createMorningOpenTestPeriod(DayOfWeek.WEDNESDAY));
                periodTest.add(createMorningOpenTestPeriod(DayOfWeek.THURSDAY));
                periodTest.add(createMorningOpenTestPeriod(DayOfWeek.FRIDAY));
                return periodTest;
            }

            @NonNull
            @Override
            public List<String> getWeekdayText() {
                List<String> weekDayTest = new ArrayList<>();
                weekDayTest.add("Saturday: closed");
                weekDayTest.add("Sunday: closed");
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

    private Period createMorningOpenTestPeriod(DayOfWeek dayOfWeek) {
        TimeOfWeek openHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(8,30));
        TimeOfWeek closeHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(14,0));
        return Period.builder()
                .setOpen(openHours)
                .setClose(closeHours)
                .build();
    }

    private List<User> getDefaultUserList() {
        List<User> defaultList = new ArrayList<>();
        User userTest1 = new User("1111", "one test", "photo", "restaurant3", "AAAA", true);
        User userTest2 = new User("2222", "two test", "photo", "restaurant1", "", false);
        User userTest3 = new User("3333", "three test", "photo", "restaurant2", "AAAA", true);

        defaultList.add(userTest1);
        defaultList.add(userTest2);
        defaultList.add(userTest3);
        return defaultList;
    }

    private List<Restaurant> getDefaultRestaurantList() {
        List<Restaurant> defaultRestaurantList = new ArrayList<>();
        Restaurant restaurantTest0 = new Restaurant("AAAA", "Restaurant le jasmin", "3 rue St Félicien", "18h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 3, false);
        Restaurant restaurantTest1 = new Restaurant("BBBB", "RESTAURANT le goéland", "5 boulevard du battaillon", "15h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 9, false);
        Restaurant restaurantTest2 = new Restaurant("CCCC", "restaurant le Kébab", "270 avenue camille blanc", "11h30", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 15, false);
        Restaurant restaurantTest3 = new Restaurant("DDDD", "le Syrien", "270 rue du voisin", "08h00", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,null, null, 4, true);

        defaultRestaurantList.add(restaurantTest0);
        defaultRestaurantList.add(restaurantTest1);
        defaultRestaurantList.add(restaurantTest2);
        defaultRestaurantList.add(restaurantTest3);

        return defaultRestaurantList;
    }
}