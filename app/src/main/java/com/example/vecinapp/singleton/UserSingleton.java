package com.example.vecinapp.singleton;



import com.example.vecinapp.ModelData.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserSingleton {

    private static UserSingleton instance;
    private FirebaseFirestore db;
    private User user;
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private FirebaseUser actualtUser;

    private UserSingleton() {
        db = FirebaseFirestore.getInstance();
        actualtUser = FirebaseAuth.getInstance().getCurrentUser();
        loadDataFromFirebase();
    }

    public static synchronized UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    private void loadDataFromFirebase() {
        Query query = db.collection("usuario").whereEqualTo("mail", actualtUser.getEmail()).limit(1);;

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                User userDB = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                user = userDB;
                userLiveData.setValue(userDB);

            }
        }).addOnFailureListener(e -> {
        });
    }

    public void refresh(){
        loadDataFromFirebase();
    }
    public User getUser() {
        return user;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

}
