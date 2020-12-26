package com.ezhevikina.homework14.service;

import com.ezhevikina.homework14.model.Account;
import com.ezhevikina.homework14.repository.FileAccountRepository;
import com.ezhevikina.homework14.repository.exceptions.DaoException;
import com.ezhevikina.homework14.repository.exceptions.NotFoundByIdException;
import com.ezhevikina.homework14.service.exceptions.NotEnoughMoneyException;
import com.ezhevikina.homework14.service.exceptions.UnknownAccountException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagerTest {
    private AccountManager manager;
    @Mock
    private FileAccountRepository dao;
    private Account account;

    @BeforeEach
    public void initAccountManager() {
        manager = new AccountManager(dao);
        account = new Account(1, "Holder", 100);
    }

    @Test
    public void testAccountBalance() throws DaoException, UnknownAccountException, NotFoundByIdException {
        when(dao.getById(1)).thenReturn(account);
        assertEquals(manager.balance(1), 100);
    }

    @Test
    public void testAccountBalanceThrowsUnknownAccountException() throws DaoException, NotFoundByIdException {
        doThrow(new NotFoundByIdException()).when(dao).getById(1);
        Assertions.assertThrows(UnknownAccountException.class, () -> manager.balance(1));
    }

    @Test
    public void testAccountDeposit() throws DaoException, NotFoundByIdException, UnknownAccountException {
        when(dao.getById(1)).thenReturn(account);
        manager.deposit(1, 100);
        verify(dao, times(1)).update(account);
    }

    @Test
    public void testAccountDepositThrowsUnknownAccountException()
            throws DaoException, NotFoundByIdException {
        doThrow(new NotFoundByIdException()).when(dao).getById(1);
        Assertions.assertThrows(UnknownAccountException.class, () -> {
            manager.deposit(1, 100);
        });
    }

    @Test
    public void testAccountDepositThrowsDaoException_WhenDaoThrowsDaoException()
            throws NotFoundByIdException, DaoException {
        doThrow(new DaoException("Exception in DAO layer")).when(dao).getById(1);
        Assertions.assertThrows(DaoException.class, () -> {
            manager.deposit(1, 100);
        });
    }

    @Test
    public void testAccountWithdrawAll() throws UnknownAccountException,
            DaoException, NotEnoughMoneyException, NotFoundByIdException {
        when(dao.getById(1)).thenReturn(account);
        manager.withdraw(1, 100);
        verify(dao).update(account);
    }

    @Test
    public void testAccountWithdrawLessThanBalance() throws UnknownAccountException,
            DaoException, NotEnoughMoneyException, NotFoundByIdException {
        when(dao.getById(1)).thenReturn(account);
        manager.withdraw(1, 10);
        verify(dao).update(account);
    }

    @Test
    public void testAccountWithdrawMoreThanBalance_NotEnoughMoneyException() throws DaoException,
            NotFoundByIdException {
        when(dao.getById(1)).thenReturn(account);
        Assertions.assertThrows(NotEnoughMoneyException.class, () -> {
            manager.withdraw(1, 1000);
        });
    }

    @Test
    public void testAccountWithdrawThrowsUnknownAccountException() throws DaoException, NotFoundByIdException {
        doThrow(new NotFoundByIdException()).when(dao).getById(1);
        assertThrows(UnknownAccountException.class, () -> {
            manager.withdraw(1, 100);
        });
    }

    @Test
    public void testTransfer() throws DaoException,
            NotFoundByIdException, UnknownAccountException, NotEnoughMoneyException {
        when(dao.getById(1)).thenReturn(account);
        when(dao.getById(2)).thenReturn(account);
        manager.transfer(1, 2, 100);
        verify(dao, times(2)).getById(anyInt());
        verify(dao, times(2)).update(account);
    }
}