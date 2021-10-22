package com.openclassrooms.p7.go4lunch.injector;

import com.openclassrooms.p7.go4lunch.service.DummyRestaurantApiService;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

/**
 * Created by lleotraas on 21.
 */
public class DI {
    private static RestaurantApiService service = new DummyRestaurantApiService();

    public static RestaurantApiService getRestaurantApiService(){ return service;}
}
