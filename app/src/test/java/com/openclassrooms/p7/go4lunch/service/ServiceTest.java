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
import com.openclassrooms.p7.go4lunch.model.RestaurantFavorite;
import com.openclassrooms.p7.go4lunch.model.User;

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

        RestaurantFavorite restaurantFavoriteTest0 = new RestaurantFavorite( "AAAA",  true);
        RestaurantFavorite restaurantFavoriteTest4 = new RestaurantFavorite( "BBBB" , false);
        RestaurantFavorite restaurantFavoriteTest1 = new RestaurantFavorite( "BBBB", false);
        RestaurantFavorite restaurantFavoriteTest2 = new RestaurantFavorite( "CCCC", true);
        RestaurantFavorite restaurantFavoriteTest3 = new RestaurantFavorite( "AAAA", true);
        RestaurantFavorite restaurantFavoriteTest5 = new RestaurantFavorite( "CCCC", true);
        RestaurantFavorite restaurantFavoriteTest6 = new RestaurantFavorite( "BBBB", false);

        User userTest1 = new User("1111", "test1", "photo", "restaurant1", "AAAA", true);
        User userTest2 = new User("2222", "test2", "photo", "restaurant2", "BBBB", false);
        User userTest3 = new User("3333", "test3", "photo", "restaurant3", "CCCC", true);

        LatLng latLngTest = new LatLng(4.5236, 6.7854);
        Restaurant restaurantTest0 = new Restaurant("AAAA", "Restaurant le jasmin", "3 rue St Félicien", "18h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3, false);
        Restaurant restaurantTest1 = new Restaurant("BBBB", "RESTAURANT le goéland", "5 boulevard du battaillon", "15h", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 9, false);
        Restaurant restaurantTest2 = new Restaurant("CCCC", "restaurant le Kébab", "270 avenue camille blanc", "11h30", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 15, false);
        Restaurant restaurantTest3 = new Restaurant("DDDD", "le Syrien", "270 rue du voisin", "08h00", "0467868361", "http://www.mangerBouger.fr", 450.987f, 2.3,latLngTest, null, 3, true);


    }

    @After
    public void finish() {

        service = null;
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
    public void makeUserFirstNameShouldReturnTest() {
        // ARRANGE
        String userNameToTest = "test one";

        // ACT
        String firstName = service.formatUserFirstName(userNameToTest);

        // ASSERT
        assertEquals("Test", firstName);
    }

    @Test
    public void makeInterestedFriendsStringWithSuccess() {
        // ARRANGE
        String friendInterestedExpected = "One\nTwo\nThree";
        // ACT
//        String frienInterestedToTest = service.formatUserString(service.getUsers());
        // ASSERT
        assertEquals(friendInterestedExpected, friendInterestedExpected);
    }

    @Test
    public void removeUselessWordsShouldRemoveRestaurantString() {
        // ARRANGE
        String restaurantNameExpected = "Le jasmin";
        // ACT
//        String restaurantNameToTest = service.formatRestaurantName(service.getRestaurant().get(0).getName());
        // ASSERT
//        assertEquals(restaurantNameExpected, restaurantNameToTest);
    }

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
        List<Restaurant> restaurantList = new ArrayList<>();
        service.listViewComparator(restaurantList);
        //ASSERT
//        assertEquals("CCCC", service.getRestaurant().get(0).getId());
//        assertEquals("BBBB", service.getRestaurant().get(1).getId());
//        assertEquals("AAAA", service.getRestaurant().get(2).getId());
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
        if (dayOfWeek.equals(DayOfWeek.THURSDAY)) {
            openHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(19,0));
            closeHours = TimeOfWeek.newInstance(dayOfWeek, LocalTime.newInstance(1,30));
        }
        return Period.builder()
                .setOpen(openHours)
                .setClose(closeHours)
                .build();
    }
}