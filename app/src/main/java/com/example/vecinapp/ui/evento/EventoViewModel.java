package com.example.vecinapp.ui.evento;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.vecinapp.ModelData.Evento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventosRef = db.collection("evento");
    private Uri currentPhoto;

    private MutableLiveData<Boolean> eventoCreado = new MutableLiveData<>();

    private MutableLiveData<String> imageUrlLiveData = new MutableLiveData<>();

    public LiveData<String> getImageUrlLiveData() {
        return imageUrlLiveData;
    }

    public EventoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is eventoooo fragment");
    }

    public void agregarEvento(Evento newEvent) {
        LiveData<String> imageUrlLiveData = SubirImagenStorage();

        imageUrlLiveData.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String imageUrl) {
                if (imageUrl != null) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    Map<String, Object> evento = new HashMap<>();
                    evento.put("nombreUser", newEvent.nombreUser);
                    evento.put("descripcion", newEvent.descripcion);
                    evento.put("direccion", newEvent.direccion);
                    evento.put("titulo", newEvent.titulo);
                    evento.put("fecha", Timestamp.now());
                    evento.put("IdCategoria", newEvent.IdCategoria);
                    evento.put("apellidoUser", newEvent.apellidoUser);
                    evento.put("mail", currentUser.getEmail());
                    evento.put("comunidad", newEvent.comunidad);
                    evento.put("verificado", false);
                    evento.put("ImageUrl", imageUrl);

                    eventosRef.add(evento)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("EVENTO", "Evento agregado con ID: " + documentReference.getId());
                                eventoCreado.setValue(true);
                            })
                            .addOnFailureListener(e -> {
                                eventoCreado.setValue(false);
                            });

                    imageUrlLiveData.removeObserver(this);
                }
            }
        });
    }

    private LiveData<String> SubirImagenStorage() {
        MutableLiveData<String> imageUrlLiveData = new MutableLiveData<>();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");

        imagesRef.putFile(currentPhoto)
                .addOnSuccessListener(taskSnapshot -> {
                    imagesRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri.toString();
                        imageUrlLiveData.setValue(imageUrl);
                    });
                })
                .addOnFailureListener(exception -> {
                    imageUrlLiveData.setValue(null);
                });

        return imageUrlLiveData;
    }

    public LiveData<Boolean> getEventoCreado() {
        return eventoCreado;
    }

    public void setCurrentPhoto(Uri currentPhoto) {
        this.currentPhoto = currentPhoto;
    }

    public LiveData<String> getText() {
        return mText;
    }
}