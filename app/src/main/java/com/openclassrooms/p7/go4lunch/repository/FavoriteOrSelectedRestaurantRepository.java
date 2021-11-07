package com.openclassrooms.p7.go4lunch.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.injector.DI;
import com.openclassrooms.p7.go4lunch.model.FavoriteOrSelectedRestaurant;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.service.RestaurantApiService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FavoriteOrSelectedRestaurantRepository {

    private static volatile FavoriteOrSelectedRestaurantRepository INSTANCE;
    private static final String USERS_COLLECTION_NAME = "users";
    private static final String RESTAURANT_COLLECTION_NAME = "favorite_or_selected_restaurants";

    private FavoriteOrSelectedRestaurantRepository(){}

    public static FavoriteOrSelectedRestaurantRepository getInstance() {
        FavoriteOrSelectedRestaurantRepository result = INSTANCE;
        if (result != null) {
            return result;
        }
        synchronized (FavoriteOrSelectedRestaurantRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new FavoriteOrSelectedRestaurantRepository();
            }
            return INSTANCE;
        }
    }
    private CollectionReference getFavoriteOrSelectedRestaurantCollection(String userId) {
        return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME).document(userId).collection(RESTAURANT_COLLECTION_NAME); }

    public void getFavoriteOrRestaurantRestaurantList() {
        RestaurantApiService apiService = DI.getRestaurantApiService();
        for (User user : apiService.getUsers()) {
            this.getFavoriteOrSelectedRestaurantCollection(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    apiService.getFavoriteRestaurant().clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String restaurantId = Objects.requireNonNull(documentSnapshot.get("restaurant_id")).toString();
                        String restaurantName = Objects.requireNonNull(documentSnapshot.get("restaurant_name")).toString();
                        boolean isFavorite = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("is_favorite")).toString());
                        boolean isSelected = Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.get("is_selectable")).toString());
                        FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant = new FavoriteOrSelectedRestaurant(user.getUid(), restaurantId, restaurantName, isFavorite, isSelected);
                        apiService.getFavoriteRestaurant().add(favoriteOrSelectedRestaurant);
                    }
                }
            });
        }
    }

    public Task<QuerySnapshot> getFavoriteOrSelectedRestaurant(String uid) {
            return this.getFavoriteOrSelectedRestaurantCollection(uid).get();
        }

        public Task<DocumentSnapshot> getFavoriteOrSelectedRestaurantToCreate(String uid) {
            return this.getFavoriteOrSelectedRestaurantCollection(uid).document().get();
    }


    public void createFavoriteRestaurant(String userId, FavoriteOrSelectedRestaurant favoriteOrSelectedRestaurant) {
//        this.getFavoriteOrSelectedRestaurantCollection(userId).document(favoriteOrSelectedRestaurant.getRestaurantId()).set(favoriteOrSelectedRestaurant);
        Map<String, Object> userWithRestaurantToCreate = new HashMap<>();
        userWithRestaurantToCreate.put("restaurant_name", favoriteOrSelectedRestaurant.getRestaurantName());
        userWithRestaurantToCreate.put("restaurant_id", favoriteOrSelectedRestaurant.getRestaurantId());
        userWithRestaurantToCreate.put("is_favorite", favoriteOrSelectedRestaurant.isFavorite());
        userWithRestaurantToCreate.put("is_selectable", favoriteOrSelectedRestaurant.isSelected());
        userWithRestaurantToCreate.put("user_id", favoriteOrSelectedRestaurant.getUserId());
        Task<DocumentSnapshot> userWithRestaurantData = getFavoriteOrSelectedRestaurantToCreate(userId);
        userWithRestaurantData.addOnSuccessListener(queryDocumentSnapshots -> {
           this.getFavoriteOrSelectedRestaurantCollection(userId).document(favoriteOrSelectedRestaurant.getRestaurantId()).set(userWithRestaurantToCreate);
        });
    }

    public void deleteFavoriteRestaurant(String userId){

    }

    public void updateFavoriteRestaurant(String userId, String restaurantId, boolean isFavorite) {
        DocumentReference documentReference = getFavoriteOrSelectedRestaurantCollection(userId).document(restaurantId);
        documentReference.update("is_favorite", isFavorite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void updateSelectedRestaurant(String userId, String restaurantId, boolean isSelected) {
        DocumentReference documentReference = getFavoriteOrSelectedRestaurantCollection(userId).document(restaurantId);
        documentReference.update("is_selectable", isSelected).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
}
