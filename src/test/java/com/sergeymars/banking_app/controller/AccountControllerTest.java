package com.sergeymars.banking_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergeymars.banking_app.dto.AccountDto;
import com.sergeymars.banking_app.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void testAddAccount() throws Exception {
        AccountDto accountDto = new AccountDto(1L, "John Doe", 1000.0);

        when(accountService.createAccount(any(AccountDto.class))).thenReturn(accountDto);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(accountDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountHolderName").value("John Doe"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void testGetAccountById() throws Exception {
        AccountDto accountDto = new AccountDto(1L, "John Doe", 1000.0);

        when(accountService.getAccountById(anyLong())).thenReturn(accountDto);

        mockMvc.perform(get("/api/accounts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountHolderName").value("John Doe"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void testDeleteAccount() throws Exception {

        mockMvc.perform(delete("/api/accounts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Account is deleted successfully!"));
    }

    @Test
    void testAddDeposit() throws Exception {
        AccountDto accountDto = new AccountDto(1L, "John Doe", 1200.0);

        when(accountService.addDeposit(anyLong(), any(Double.class))).thenReturn(accountDto);

        mockMvc.perform(put("/api/accounts/{id}/add_deposit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1200.0));
    }

    @Test
    void testWithdrawDeposit() throws Exception {
        AccountDto accountDto = new AccountDto(1L, "John Doe", 800.0);

        when(accountService.withdrawDeposit(anyLong(), any(Double.class))).thenReturn(accountDto);

        mockMvc.perform(put("/api/accounts/{id}/withdraw_deposit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(800.0));
    }

    @Test
    void testGetAllAccounts() throws Exception {
        AccountDto account1 = new AccountDto(1L, "John Doe", 1000.0);
        AccountDto account2 = new AccountDto(2L, "Jane Smith", 1500.0);
        List<AccountDto> accounts = Arrays.asList(account1, account2);

        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].accountHolderName").value("John Doe"))
                .andExpect(jsonPath("$[1].accountHolderName").value("Jane Smith"));
    }

    @Test
    void testTransferMoney() throws Exception {
        AccountDto accountDto1 = new AccountDto(1L, "John Doe", 1000.0);
        AccountDto accountDto2 = new AccountDto(2L, "Jane Smith", 1500.0);
        List<AccountDto> accounts = Arrays.asList(accountDto1, accountDto2);

        when(accountService.transferMoneyTo(anyLong(), anyLong(), any(Double.class))).thenReturn(accounts);

        mockMvc.perform(put("/api/accounts/{id}/transfer_money", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toAccountId\": 2, \"amount\": 200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].balance").value(1000.0))
                .andExpect(jsonPath("$[1].balance").value(1500.0));
    }
}
