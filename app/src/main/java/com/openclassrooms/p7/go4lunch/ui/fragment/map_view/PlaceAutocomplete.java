package com.openclassrooms.p7.go4lunch.ui.fragment.map_view;

public class PlaceAutocomplete {
     public CharSequence placeId;
     public CharSequence address, area;

     PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
         this.placeId = placeId;
         this.area = area;
         this.address = address;
     }

     @Override
     public String toString() {
         return area.toString();
     }
 }
