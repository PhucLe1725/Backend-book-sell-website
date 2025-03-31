package com.example.book_sell_website.dto.chatDTO;

public class ChatSendRequestDTO {
    private int senderId;
    private String message;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
