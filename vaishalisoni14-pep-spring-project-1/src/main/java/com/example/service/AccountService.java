package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account registerAccount(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.length() < 4) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        if (accountRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        Account account = new Account(username, password);
        return accountRepository.save(account);
    }

    public Optional<Account> login(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            return Optional.of(account);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Account> getAccountById(Integer accountId) {
        return accountRepository.findById(accountId);
    }

}
