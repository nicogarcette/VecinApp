package com.example.vecinapp.ui.evento;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vecinapp.CompleteProfileActivity;
import com.example.vecinapp.MainActivity;
import com.example.vecinapp.ModelData.Evento;
import com.example.vecinapp.ModelData.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;import com.google.firebase.firestore.CollectionReference;
import java.util.HashMap;
import java.util.Map;
public class EventoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventosRef = db.collection("evento");
    private String currentPhotoPath;
    private MutableLiveData<Boolean> eventoCreado = new MutableLiveData<>();

    public EventoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is eventoooo fragment");
    }

    public void agregarEvento(Evento newEvent) {

        Map<String, Object> evento = new HashMap<>();
        evento.put("nombreUser",newEvent.userName );
        evento.put("descripcion", newEvent.description);
        evento.put("direccion", newEvent.direction);
        evento.put("titulo", newEvent.title);
        evento.put("fecha", Timestamp.now());
        evento.put("IdCategoria", newEvent.category);   // nombre de categoria de una
        evento.put("apellidoUser", newEvent.userLastName);
        evento.put("comunidad", newEvent.comunity);
        evento.put("verificado", false);

        eventosRef.add(evento)
                .addOnSuccessListener(documentReference -> {
                    Log.d("EVENTO", "Evento agregado con ID: " + documentReference.getId());
                    eventoCreado.setValue(true);
                })
                .addOnFailureListener(e -> {
                    eventoCreado.setValue(false);
                });
    }

    public LiveData<Boolean> getEventoCreado() {
        return eventoCreado;
    }



    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public LiveData<String> getText() {
        return mText;
    }
}