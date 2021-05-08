package com.hakanyilmazz.seyahapp.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;

public class SignOutManager {

    private static SignOutManager signOutManager;

    private SignOutManager() {
    }

    public static SignOutManager getInstance() {
        if (signOutManager == null) {
            signOutManager = new SignOutManager();
        }

        return signOutManager;
    }

    public void askForSignOut(Activity from, Class to) {
        AlertDialog.Builder alert = new AlertDialog.Builder(from);
        alert.setTitle("Are you sure?");
        alert.setMessage("You will sign out!");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(from, "See you again!", Toast.LENGTH_SHORT).show();
                startSignOutTasks(from, to);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(from, "Welcome Again!", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

    private void startSignOutTasks(Activity from, Class to) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        Intent intentToLoginActivity = new Intent(from, to);
        intentToLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        from.finish();

        from.startActivity(intentToLoginActivity);
    }

}
