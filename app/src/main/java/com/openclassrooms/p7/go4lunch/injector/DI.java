package com.openclassrooms.p7.go4lunch.injector;

import com.openclassrooms.p7.go4lunch.service.DummyApiService;
import com.openclassrooms.p7.go4lunch.service.ApiService;

/**
 * Created by lleotraas on 21.
 */
public class DI {
    private static ApiService service = new DummyApiService();

    public static ApiService getRestaurantApiService(){ return service;}
}
