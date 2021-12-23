package com.openclassrooms.p7.go4lunch;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.apphosting.datastore.testing.DatastoreTestTrace;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.p7.go4lunch.model.User;
import com.openclassrooms.p7.go4lunch.repository.FirebaseHelper;
import com.openclassrooms.p7.go4lunch.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private FirebaseHelper firebaseHelper;
    @Mock
    private FirebaseFirestore firebaseFirestore ;

    private UserRepository userRepository;
    private Task<AuthResult> addonSuccessListener;
    private Task<AuthResult> addonFailureListener;
    private final String result = "UNDEF";
    private final int SUCCESS = 1;
    private final int FAILURE = -1;
    private final int UNDEF = 0;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userRepository = new UserRepository(firebaseHelper);
    }

    private void getAllUsers() {

    }

    private List<User> getDefaultUser() {
        List<User> userList = new ArrayList<>();
        User userTest1 = new User("1111", "test1", "photo", "restaurant1", "AAAA", true);
        User userTest2 = new User("2222", "test2", "photo", "restaurant2", "BBBB", false);
        User userTest3 = new User("3333", "test3", "photo", "restaurant3", "CCCC", true);

        userList.add(userTest1);
        userList.add(userTest2);
        userList.add(userTest3);

        return userList;
    }
}
