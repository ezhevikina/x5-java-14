package com.ezhevikina.homework14.service;

import com.ezhevikina.homework14.model.Account;
import com.ezhevikina.homework14.repository.Repository;
import com.ezhevikina.homework14.repository.exceptions.DaoException;
import com.ezhevikina.homework14.repository.exceptions.NotFoundByIdException;
import com.ezhevikina.homework14.service.exceptions.NotEnoughMoneyException;
import com.ezhevikina.homework14.service.exceptions.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountManager implements AccountService {
  private final Repository<Account> repository;

  @Autowired
  public AccountManager(Repository<Account> repository) {
    this.repository = repository;
  }

  @Override
  public void withdraw(int accountId, int amount) throws NotEnoughMoneyException,
          UnknownAccountException, DaoException {
    Account account;

    try {
      account = repository.getById(accountId);

      if (account.getAmount() < amount) {
        throw new NotEnoughMoneyException(String.format(
                "Insufficient funds on the account #%d", accountId));
      }

      account.setAmount(account.getAmount() - amount);
      repository.update(account);
    } catch (NotFoundByIdException e) {
      throw new UnknownAccountException(String.format(
              "Account #%d not found", accountId), e);
    }
  }

  @Override
  public int balance(int accountId) throws UnknownAccountException, DaoException {
    try {
      return repository.getById(accountId).getAmount();
    } catch (NotFoundByIdException e) {
      throw new UnknownAccountException(String.format(
              "Account #%d not found", accountId), e);
    }
  }

  @Override
  public void deposit(int accountId, int amount) throws UnknownAccountException, DaoException {
    try {
      Account account = repository.getById(accountId);
      account.setAmount(account.getAmount() + amount);
      repository.update(account);

    } catch (NotFoundByIdException e) {
      throw new UnknownAccountException(String.format(
              "Account #%d not found", accountId), e);
    }
  }

  @Override
  public void transfer(int from, int to, int amount) throws NotEnoughMoneyException,
          UnknownAccountException, DaoException {
    withdraw(from, amount);
    deposit(to, amount);
  }
}
