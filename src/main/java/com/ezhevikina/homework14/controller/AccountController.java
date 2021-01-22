package com.ezhevikina.homework14.controller;

import com.ezhevikina.homework14.model.Transfer;
import com.ezhevikina.homework14.repository.exceptions.DaoException;
import com.ezhevikina.homework14.service.AccountService;
import com.ezhevikina.homework14.service.exceptions.NotEnoughMoneyException;
import com.ezhevikina.homework14.service.exceptions.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    private AccountService manager;

    @Autowired
    public AccountController(AccountService manager) {
        this.manager = manager;
    }

    @GetMapping(value = "/accounts/{id}/balance")
    @ResponseBody
    public ResponseEntity<?> getBalance(@PathVariable("id") int id) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(manager.balance(id));

        } catch (UnknownAccountException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(String.format("Account #%d not found", id));

        } catch (DaoException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping(value = "/accounts/{id}/deposit", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deposit(@PathVariable("id") int id,
                                     @RequestBody int amount) {
        try {
            manager.deposit(id, amount);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Successful operation");

        } catch (UnknownAccountException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(String.format("Account #%d not found", id));

        } catch (DaoException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping(value = "/accounts/{id}/withdraw", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> withdraw(@PathVariable("id") int id,
                                     @RequestBody int amount) {
        try {
            manager.withdraw(id, amount);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Successful operation");

        } catch (UnknownAccountException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(String.format("Account #%d not found", id));

        } catch (DaoException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());

        } catch (NotEnoughMoneyException e) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(String.format("Insufficient funds on the account #%d", id));
        }
    }

    @PostMapping(value = "/accounts/{id}/transfer", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> transfer(@PathVariable("id") int id,
                                      @RequestBody Transfer transfer) {
        try {
            manager.transfer(id, transfer.getReceiverAccountId(), transfer.getAmount());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Successful operation");

        } catch (UnknownAccountException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Requisites are incorrect");

        } catch (DaoException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());

        } catch (NotEnoughMoneyException e) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(String.format("Insufficient funds on the account #%d", id));
        }
    }


}
