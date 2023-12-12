package org.techmarket.service;

public interface IEmailService {

     void sendMessage(String to, String subject, String text);
}
