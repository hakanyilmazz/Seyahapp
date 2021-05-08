package com.hakanyilmazz.seyahapp.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hakanyilmazz.seyahapp.R;
import com.hakanyilmazz.seyahapp.adapter.MessageRecyclerAdapter;
import com.hakanyilmazz.seyahapp.cryptography.Crypter;
import com.hakanyilmazz.seyahapp.manager.MessageDeleteManager;
import com.hakanyilmazz.seyahapp.manager.SignOutManager;
import com.hakanyilmazz.seyahapp.model.MessageContent;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String FIREBASE_FIRESTORE_COLLECTION_NAME = "Messages";

    private final ArrayList<MessageContent> messages = new ArrayList<>();

    private EditText messageText;
    private RecyclerView chatRecyclerView;

    private MessageRecyclerAdapter messageRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        messageText = findViewById(R.id.chatActivity_messageText);
        chatRecyclerView = findViewById(R.id.chatActivity_chatRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        messageRecyclerAdapter = new MessageRecyclerAdapter(messages);
        chatRecyclerView.setAdapter(messageRecyclerAdapter);

        listenToMessagesChangesFromFirebase();
    }

    private void listenToMessagesChangesFromFirebase() {
        CollectionReference collectionReference = firebaseFirestore.collection(FIREBASE_FIRESTORE_COLLECTION_NAME);
        collectionReference.orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        messages.clear();

                        if (value != null && !value.isEmpty()) {
                            try {
                                for (DocumentSnapshot data : value.getDocuments()) {
                                    String email = (String) data.get("email");
                                    String message = (String) data.get("message");

                                    Timestamp serverDate = (Timestamp) data.get("date");
                                    Date date = serverDate.toDate();

                                    @SuppressLint("SimpleDateFormat")
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm a");

                                    String userMessageDate = simpleDateFormat.format(date);

                                    MessageContent messageContent = new MessageContent(email, userMessageDate, message);

                                    messages.add(messageContent);
                                    messageRecyclerAdapter.notifyDataSetChanged();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_settings, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.chatSettings_signOut) {
            SignOutManager signOutManager = SignOutManager.getInstance();
            signOutManager.askForSignOut(this, LoginActivity.class);

        } else if (item.getItemId() == R.id.chatSettings_deleteMyMessages) {
            MessageDeleteManager.deleteMyMessages(firebaseAuth.getCurrentUser(), firebaseFirestore, this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        String userMessage = messageText.getText().toString().trim();

        if (!userMessage.isEmpty()) {
            messageText.setText("");

            String encryptedMessage = null;
            try {
                encryptedMessage = Crypter.encryptMessage(userMessage);

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                Map<String, Object> message = new HashMap<>();
                message.put("email", currentUser.getEmail());
                message.put("date", FieldValue.serverTimestamp());
                message.put("message", encryptedMessage);

                firebaseFirestore.collection(FIREBASE_FIRESTORE_COLLECTION_NAME)
                        .add(message)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                messageText.setText(userMessage);
                            }
                        });
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
    }

}