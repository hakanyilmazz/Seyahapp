package com.hakanyilmazz.seyahapp.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hakanyilmazz.seyahapp.R;
import com.hakanyilmazz.seyahapp.adapter.PlaceAdapter;
import com.hakanyilmazz.seyahapp.manager.SignOutManager;
import com.hakanyilmazz.seyahapp.model.Place;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity {

    private MenuInflater menuInflater;
    private ListView listView;
    private PlaceAdapter placeAdapter;

    private SQLiteDatabase database;

    private ArrayList<Place> placeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        listView = findViewById(R.id.listView);
        getDataFromSQLite();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings_signOut) {
            SignOutManager signOutManager = SignOutManager.getInstance();
            signOutManager.askForSignOut(this, LoginActivity.class);

        } else if (item.getItemId() == R.id.settings_chatting) {
            startChatActivity();

        } else if (item.getItemId() == R.id.settings_newPlace) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("info", "new");
            startActivity(intent);

        } else if (item.getItemId() == R.id.settings_notification) {
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // Intent from this activity to Chat Activity
    private void startChatActivity() {
        Intent intentToChatActivity = new Intent(PlacesActivity.this, ChatActivity.class);
        startActivity(intentToChatActivity);
    }

    public void getDataFromSQLite() {
        placeAdapter = new PlaceAdapter(this, placeList);

        try {
            database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM places", null);

            int nameIndex = cursor.getColumnIndex("name");
            int latitudeIndex = cursor.getColumnIndex("latitude");
            int longitudeIndex = cursor.getColumnIndex("longitude");

            while (cursor.moveToNext()) {
                String nameFromDatabase = cursor.getString(nameIndex);
                String latitudeFromDatabase = cursor.getString(latitudeIndex);
                String longitudeFromDatabase = cursor.getString(longitudeIndex);

                Place place = new Place(nameFromDatabase, Double.parseDouble(latitudeFromDatabase), Double.parseDouble(longitudeFromDatabase));

                placeList.add(place);
            }

            placeAdapter.notifyDataSetChanged();
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setAdapter(placeAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(PlacesActivity.this, MapsActivity.class);
            intent.putExtra("info", "old");
            intent.putExtra("place", placeList.get(position));
            startActivity(intent);
        });
    }

}