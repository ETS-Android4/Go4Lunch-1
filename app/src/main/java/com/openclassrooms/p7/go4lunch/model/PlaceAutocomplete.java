package com.openclassrooms.p7.go4lunch.model;

import androidx.annotation.NonNull;

public class PlaceAutocomplete {
     public CharSequence placeId;
     public CharSequence address, area;

     public PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
         this.placeId = placeId;
         this.area = area;
         this.address = address;
     }

     @NonNull
     public String toString() {
         return area.toString();
     }
 }
