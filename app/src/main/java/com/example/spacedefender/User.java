package com.example.spacedefender;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String email;
    private String password;
    private int score;
    private String status;



    public User() {
    }

    public User(String username,String email, String password, int score, String status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.score = score;
        this.status = status;


    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
