package com.ezhevikina.homework14;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ezhevikina.homework14.controller.AccountController;
import com.ezhevikina.homework14.model.Account;
import com.ezhevikina.homework14.repository.Repository;
import com.ezhevikina.homework14.repository.exceptions.NotFoundByIdException;
import com.ezhevikina.homework14.service.AccountService;
import com.ezhevikina.homework14.service.exceptions.NotEnoughMoneyException;
import com.ezhevikina.homework14.service.exceptions.UnknownAccountException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
public class Homework14ApplicationTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AccountService manager;
    @MockBean
    Repository<Account> repository;

    private int accountId = 1;
    private int amount = 100;
    private String accountUri = "/accounts/" + accountId;
    private String invalidAccountUri = "/accounts/aaa";
    private String accountNotFoundMessage = "Account #" + accountId + " not found";
    private String notEnoughMoneyMessage = "Insufficient funds on the account #" + accountId;
    private String successMessage = "Successful operation";

    @Test
    public void testAccountBalance() throws Exception {
        when(manager.balance(accountId)).thenReturn(amount);

        this.mockMvc.perform(get(accountUri + "/balance"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(amount)));
    }

    @Test
    public void testUnknownAccountBalance() throws Exception {
        when(manager.balance(accountId)).thenThrow(new UnknownAccountException(accountNotFoundMessage,
                new NotFoundByIdException()));

        this.mockMvc.perform(get(accountUri + "/balance"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(accountNotFoundMessage));
    }

    @Test
    public void testAccountDeposit() throws Exception {
        doNothing().when(manager).deposit(accountId, amount);

        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/deposit")
                .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    public void testUnknownAccountDeposit() throws Exception {
        doThrow(new UnknownAccountException(accountNotFoundMessage, new NotFoundByIdException()))
                .when(manager).deposit(accountId, amount);

        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/deposit")
                .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(accountNotFoundMessage));
    }

    @Test
    public void testAccountDepositInvalidAmount() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/deposit")
                .content("amount")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDepositInvalidAccount() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(invalidAccountUri + "/deposit")
                .content("amount")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAccountWithdraw() throws Exception {
        doNothing().when(manager).withdraw(accountId, amount);

        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/withdraw")
                .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    public void testAccountNotEnoughMoney() throws Exception {
        doThrow(new NotEnoughMoneyException(notEnoughMoneyMessage)).when(manager).withdraw(accountId, amount);

        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/withdraw")
                .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(notEnoughMoneyMessage));
    }

    @Test
    public void testWithdrawFromUnknownAccount() throws Exception {
        doThrow(new UnknownAccountException(accountNotFoundMessage, new NotFoundByIdException()))
                .when(manager).withdraw(accountId, amount);

        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/withdraw")
                .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(accountNotFoundMessage));
    }

    @Test
    public void testWithdrawInvalidAccount() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(invalidAccountUri + "/withdraw")
                .content("amount")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testTransfer() throws Exception {
        int receiverAccountId = 2;
        doNothing().when(manager).transfer(accountId, receiverAccountId, amount);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/transfer")
                .content(String.format("{\"amount\": %d,\n\"receiverAccountId\": %d\n}",
                        amount, receiverAccountId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    public void testTransferNotEnoughMoneyOnBalance() throws Exception {
        int receiverAccountId = 2;
        doThrow(new NotEnoughMoneyException(notEnoughMoneyMessage))
                .when(manager).transfer(accountId, receiverAccountId, amount);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/transfer")
                .content(String.format("{\"amount\": %d,\n\"receiverAccountId\": %d\n}",
                        amount, receiverAccountId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(notEnoughMoneyMessage));
    }

    @Test
    public void testTransferUnknownAccount() throws Exception {
        int receiverAccountId = 2;
        doThrow(new UnknownAccountException(accountNotFoundMessage, new NotFoundByIdException()))
                .when(manager).transfer(accountId, receiverAccountId, amount);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/transfer")
                .content(String.format("{\"amount\": %d,\n\"receiverAccountId\": %d\n}",
                        amount, receiverAccountId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Requisites are incorrect"));
    }

    @Test
    public void testTransferInvalidAmount() throws Exception {
        int receiverAccountId = 2;
        doThrow(new UnknownAccountException(accountNotFoundMessage, new NotFoundByIdException()))
                .when(manager).transfer(accountId, receiverAccountId, amount);
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders
                .post(accountUri + "/transfer")
                .content(String.format("{\"amount\": %s,\n\"receiverAccountId\": %d\n}",
                        "amount", receiverAccountId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andDo(print())
                .andExpect(status().isBadRequest());
    }
}

