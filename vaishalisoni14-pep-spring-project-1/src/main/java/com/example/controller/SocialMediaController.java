//  * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
//  * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
//  * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
//  * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
//  */
package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        try {
            Account newAccount = accountService.registerAccount(account.getUsername(), account.getPassword());
            return ResponseEntity.ok(newAccount);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Username already exists")) {
                return ResponseEntity.status(409).build();
            }
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
        return loggedInAccount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(401).build());
    }
    


    @PostMapping("/messages")
public ResponseEntity<Message> createMessage(@RequestBody Message message) {
    // Check if the user exists
    Optional<Account> account = accountService.getAccountById(message.getPostedBy());
    if (account.isEmpty()) {
        return ResponseEntity.status(400).build();
    }
    // Check if message text is valid
    if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
        return ResponseEntity.status(400).build();
    }
    try {
        Message newMessage = messageService.createMessage(message.getPostedBy(), message.getMessageText(), message.getTimePostedEpoch());
        return ResponseEntity.ok(newMessage);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(400).build();
    }
}


    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(200).build());
    }


    
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessageById(@PathVariable Integer messageId) {
    int rowsModified = messageService.deleteMessageById(messageId);
    if (rowsModified == 0) {
        return ResponseEntity.ok(""); 
    }
    return ResponseEntity.ok(String.valueOf(rowsModified)); 
}


    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getMessagesByUserId(@PathVariable Integer accountId) {
        return messageService.getMessagesByUserId(accountId);
    }




@PatchMapping("/messages/{messageId}")
public ResponseEntity<Integer> updateMessageText(@PathVariable Integer messageId, @RequestBody MessageUpdateRequest request) {
    // Extract the newMessageText from the request object
    String newMessageText = request.getMessageText();

    // Validate newMessageText
    if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
        return ResponseEntity.badRequest().build();
    }

    try {
        int rowsModified = messageService.updateMessage(messageId, newMessageText);
        if (rowsModified == 0) {
            return ResponseEntity.status(400).build(); 
        }
        return ResponseEntity.ok(rowsModified);
    }catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}


public static class MessageUpdateRequest {
    private String messageText;

    // Getters and setters
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}


}
