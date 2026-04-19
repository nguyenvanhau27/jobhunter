package com.jobhunter.jobhunter.service;


public interface PasswordService {


    void changePassword(String email, String oldPassword, String newPassword);


    void sendResetEmail(String email);


    void resetPassword(String token, String newPassword);
}