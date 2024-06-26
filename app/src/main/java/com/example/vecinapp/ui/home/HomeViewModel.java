package com.example.vecinapp.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vecinapp.ModelData.Evento;
import com.example.vecinapp.ModelData.User;
import com.example.vecinapp.singleton.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Evento>> eventsLiveData = new MutableLiveData<>();

    private MutableLiveData<Evento> eventoLiveData = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser actualtUser;
    private User user;
    private LiveData<User> userLiveData;
    private UserSingleton userSingleton;


    public HomeViewModel() {
        actualtUser = FirebaseAuth.getInstance().getCurrentUser();
        userSingleton = UserSingleton.getInstance();
        userLiveData = userSingleton.getUserLiveData();
        userLiveData.observeForever(user -> {
            if (user != null) {
                loadEvents(user.comunidad);
            }
        });
    }

    public void loadEvents(String comunidad) {

        String Idcomunidad = userSingleton.getUser() != null ?userSingleton.getUser().comunidad : comunidad;

        Query query = db.collection("evento").whereEqualTo("comunidad",Idcomunidad);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Evento> eventos = fromQuerySnapshot(queryDocumentSnapshots);
            eventsLiveData.postValue(eventos);
            for (Evento evento : eventos) {
                Log.d("EVENTOS", "Evento encontrado: " + evento.titulo);
            }
        }).addOnFailureListener(e -> {
            eventsLiveData.postValue(new ArrayList<>());
            Log.e("EVENTOS", "Error al obtener eventos", e);
        });
    }

    public void getEventoById(String eventId) {
        DocumentReference docRef = db.collection("evento").document(eventId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Evento evento = fromDocumentSnapshot(documentSnapshot);
                eventoLiveData.setValue(evento);
            } else {
                eventoLiveData.setValue(null);
            }
        }).addOnFailureListener(e -> {
            eventoLiveData.setValue(null);
        });

    }

    public static Evento fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        String descripcion = documentSnapshot.getString("descripcion");
        String titulo = documentSnapshot.getString("titulo");
        String nombreUser = documentSnapshot.getString("nombreUser");
        String apellidoUser = documentSnapshot.getString("apellidoUser");
        String mail = documentSnapshot.getString("mail");
        String ImageUrl = documentSnapshot.getString("ImageUrl");
        String idCategory = documentSnapshot.getString("IdCategoria");
        Timestamp fecha = documentSnapshot.getTimestamp("fecha");
        //GeoPoint direccion = documentSnapshot.getGeoPoint("direccion");

        GeoPoint direccion = null;
        Object direccionObj = documentSnapshot.get("direccion");
        if (direccionObj instanceof GeoPoint) {
            direccion = (GeoPoint) direccionObj;
        } else if (direccionObj instanceof Map) {
            Map<String, Object> direccionMap = (Map<String, Object>) direccionObj;
            if (direccionMap.containsKey("latitude") && direccionMap.containsKey("longitude")) {
                direccion = new GeoPoint((double) direccionMap.get("latitude"), (double) direccionMap.get("longitude"));
            }
        }
        String id = documentSnapshot.getId();
        String comunidad = documentSnapshot.getString("comunidad");

        return new Evento(
                descripcion,
                titulo,
                nombreUser,
                apellidoUser,
                idCategory,
                direccion,
                comunidad,
                id,
                fecha,
                mail,
                ImageUrl
                );
    }

    public static List<Evento> fromQuerySnapshot(QuerySnapshot querySnapshot) {
        List<Evento> eventos = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot) {
            Evento evento = fromDocumentSnapshot(document);
            eventos.add(evento);
        }
        return eventos;
    }

    public LiveData<Evento> getEvent() {
        return eventoLiveData;
    }
    public LiveData<List<Evento>> getEvents() {
        return eventsLiveData;
    }
}