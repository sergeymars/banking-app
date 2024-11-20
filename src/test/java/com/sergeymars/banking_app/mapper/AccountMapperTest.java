package com.sergeymars.banking_app.mapper;


import com.sergeymars.banking_app.dto.AccountDto;
import com.sergeymars.banking_app.entity.Account;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountMapperTest {

    @Test
    void testMapToAccount() {
        AccountDto accountDto = new AccountDto(1L, "John Doe", 1000.0);

        Account account = AccountMapper.mapToAccount(accountDto);

        assertNotNull(account, "Account should not be null");
        assertEquals(accountDto.id(), account.getId(), "Account ID should match");
        assertEquals(accountDto.accountHolderName(), account.getAccountHolderName(), "Account holder name should match");
        assertEquals(accountDto.balance(), account.getBalance(), "Account balance should match");
    }

    @Test
    void testMapToAccountDto() {
        Account account = new Account(1L, "John Doe", 1000.0);

        AccountDto accountDto = AccountMapper.mapToAccountDto(account);

        assertNotNull(accountDto, "AccountDto should not be null");
        assertEquals(account.getId(), accountDto.id(), "AccountDto ID should match");
        assertEquals(account.getAccountHolderName(), accountDto.accountHolderName(), "AccountDto holder name should match");
        assertEquals(account.getBalance(), accountDto.balance(), "AccountDto balance should match");
    }
}
