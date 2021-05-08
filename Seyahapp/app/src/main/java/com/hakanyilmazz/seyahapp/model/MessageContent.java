package com.hakanyilmazz.seyahapp.model;

import com.hakanyilmazz.seyahapp.cryptography.Crypter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MessageContent {

    private String email;
    private String date;
    private String message;

    public MessageContent(String email, String date, String message) {
        this.email = email;
        this.date = date;
        setMessage(message);
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        try {
            this.message = Crypter.decryptMessage(message);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
