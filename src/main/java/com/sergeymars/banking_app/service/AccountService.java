package com.sergeymars.banking_app.service;

import com.sergeymars.banking_app.dto.AccountDto;

import java.util.List;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long id);

    AccountDto addDeposit(Long id, double amount);

    AccountDto withdrawDeposit(Long id, double amount);

    List<AccountDto> getAllAccounts();

    List<AccountDto> transferMoneyTo(Long fromId, Long toId, double amount);

    void deleteAccount(Long id);
}
