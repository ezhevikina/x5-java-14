package com.ezhevikina.homework14.service;

import com.ezhevikina.homework14.repository.exceptions.DaoException;
import com.ezhevikina.homework14.service.exceptions.NotEnoughMoneyException;
import com.ezhevikina.homework14.service.exceptions.UnknownAccountException;

public interface AccountService {
  void withdraw(int accountId, int amount) throws
          NotEnoughMoneyException, UnknownAccountException, DaoException;

  int balance(int accountId) throws UnknownAccountException, DaoException;

  void deposit(int accountId, int amount) throws UnknownAccountException, DaoException;

  void transfer(int from, int to, int amount) throws
          NotEnoughMoneyException, UnknownAccountException, DaoException;
}
