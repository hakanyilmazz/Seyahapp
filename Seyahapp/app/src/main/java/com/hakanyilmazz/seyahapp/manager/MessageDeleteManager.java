package com.hakanyilmazz.seyahapp.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MessageDeleteManager {

    private static final String FIREBASE_FIRESTORE_COLLECTION_NAME = "Messages";

    public static void deleteMyMessages(FirebaseUser firebaseUser, FirebaseFirestore firebaseFirestore, Activity from) {
        AlertDialog.Builder alert = new AlertDialog.Builder(from);

        alert.setTitle("Are You Sure?");
        alert.setMessage("Deleting all messages!");
        alert.setCancelable(false);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Task<QuerySnapshot> docRef = firebaseFirestore.collection(FIREBASE_FIRESTORE_COLLECTION_NAME)
                        .whereEqualTo("email", firebaseUser.getEmail())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()) {
                                    documentSnapshot.getReference().delete();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(from.getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(from.getApplicationContext(), "Messages don't deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

}
