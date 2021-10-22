package com.openclassrooms.p7.go4lunch.service;

import com.openclassrooms.p7.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lleotraas on 21.
 */
public abstract class DummyRestaurant {
    public static List<Restaurant> DUMMY_RESTAURANT =
            new ArrayList<Restaurant>();
//            Arrays.asList(
//                    new Restaurant("Le Zinc", "", "9 Rue Faubourg Saint Honoré", "8h00/19h00", 245, 4, 3.5,"https://i.pravatar.cc/150?u=a042581f4e29026704d"),
//                    new Restaurant("Le Zinc", "", "9 Rue Faubourg Saint Honoré", "8h00/19h00", 245, 4, 3.5,"https://i.pravatar.cc/150?u=a042581f4e29026704b"),
//                    new Restaurant("Le Zinc", "", "9 Rue Faubourg Saint Honoré", "8h00/19h00", 245, 4, 3.5,"https://i.pravatar.cc/150?u=a042581f4e29026703d"),
//                    new Restaurant("Le Zinc", "", "9 Rue Faubourg Saint Honoré", "8h00/19h00", 245, 4, 3.5,"https://i.pravatar.cc/150?u=a042581f4e29026706d"),
//                    new Restaurant("Le Zinc", "", "9 Rue Faubourg Saint Honoré", "8h00/19h00", 245, 4, 3.5,"https://i.pravatar.cc/150?u=a042581f4e29026704f")
//            );
    static List<Restaurant> generateRestaurant(){ return new ArrayList<Restaurant>(DUMMY_RESTAURANT);}
}
