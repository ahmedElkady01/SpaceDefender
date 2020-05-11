package com.example.spacedefender;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
private User u = new User();
    @Test
    public void getStatus() {
        System.out.println("Getting a status");
        String sta = "Active";
        String expected = "Active"; //notActive
        u.setStatus("Active");
        String actual = u.getStatus();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void setStatus() {
        System.out.println("Setting a status");
        String sta = "Active";
        String expected = "Active"; //notActive
        u.setStatus("Active");
        String actual = u.getStatus();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void getScore() {
        System.out.println("Getting a score");
        int score = 10;
        int expected = 10; //notActive
        u.setScore(score);
        int actual = u.getScore();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void setScore() {
        System.out.println("Setting a score");
        int score = 10;
        int expected = 10; //notActive
        u.setScore(score);
        int actual = u.getScore();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void getUsername() {
        System.out.println("Getting a username");
        String username = "Joe";
        String expected = "Joe"; //notActive
        u.setUsername(username);
        String actual = u.getUsername();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void setUsername() {
        System.out.println("Setting a username");
        String username = "Joe";
        String expected = "Joe"; //notActive
        u.setUsername(username);
        String actual = u.getUsername();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void getPassword() {
        System.out.println("Getting a password");
        String password = "1234567";
        String expected = "1234567"; //notActive
        u.setPassword(password);
        String actual = u.getPassword();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void setPassword() {
        System.out.println("Setting a password");
        String password = "1234567";
        String expected = "1234567"; //notActive
        u.setPassword(password);
        String actual = u.getPassword();
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void getEmail() {
        System.out.println("Getting an email");
        String email = "Joe@myserver.com";
        String expected = "Joe@myserver.com"; //notActive
        u.setEmail(email);
        String actual = u.getEmail();
        Assert.assertEquals(expected,actual);

    }

    @Test
    public void setEmail() {
        System.out.println("Setting an email");
        String email = "Joe@myserver.com";
        String expected = "Joe@myserver.com"; //notActive
        u.setEmail(email);
        String actual = u.getEmail();
        Assert.assertEquals(expected,actual);
    }
}