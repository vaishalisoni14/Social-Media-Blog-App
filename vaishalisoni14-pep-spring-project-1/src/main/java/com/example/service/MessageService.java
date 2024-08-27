package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message createMessage(Integer postedBy, String messageText, Long timePostedEpoch) {
        if (messageText == null || messageText.isBlank() || messageText.length() > 255) {
            throw new IllegalArgumentException("Invalid message text");
        }

        Message message = new Message(postedBy, messageText, timePostedEpoch);
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    public int deleteMessageById(Integer messageId) {
        if (!messageRepository.existsById(messageId)) {
            return 0; // Message not found
        }
        messageRepository.deleteById(messageId);
        return 1;
    }

    public List<Message> getMessagesByUserId(Integer postedBy) {
        return messageRepository.findByPostedBy(postedBy);
    }

    public int updateMessage(Integer messageId, String newMessageText) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) {
            return 0; // Message not found
        }

        Message message = optionalMessage.get();
        message.setMessageText(newMessageText);
        messageRepository.save(message);
        return 1; // One row updated
    }

    public int updateMessageText(Integer messageId, String newMessageText) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        Message message = optionalMessage.get();
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) {
            throw new IllegalArgumentException("Invalid message text");
        }
        message.setMessageText(newMessageText);
        messageRepository.save(message);
        return 1;
    }

}
