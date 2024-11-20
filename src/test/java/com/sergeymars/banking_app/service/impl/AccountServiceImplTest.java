package com.sergeymars.banking_app.service.impl;
import com.sergeymars.banking_app.dto.AccountDto;
import com.sergeymars.banking_app.entity.Account;
import com.sergeymars.banking_app.exception.AccountException;
import com.sergeymars.banking_app.mapper.AccountMapper;
import com.sergeymars.banking_app.repository.AccountRepository;
import com.sergeymars.banking_app.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountHolderName("John Doe");
        account.setBalance(100.0);

        accountDto = new AccountDto(1L, "John Doe", 100.0);
    }

    @Test
    void createAccount_ShouldReturnAccountDto() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto result = accountService.createAccount(accountDto);

        assertNotNull(result);
        assertEquals(accountDto, result);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountById_AccountExists_ShouldReturnAccountDto() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        AccountDto result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(accountDto, result);

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAccountById_AccountDoesNotExist_ShouldThrowException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class, () -> accountService.getAccountById(1L));

        assertEquals("Account doesn't exist", exception.getMessage());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void addDeposit_ShouldIncreaseBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountDto result = accountService.addDeposit(1L, 50.0);

        assertEquals(new AccountDto(1L, "John Doe", 150.0), result);
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void withdrawDeposit_InsufficientFunds_ShouldThrowException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountException exception = assertThrows(AccountException.class, () -> accountService.withdrawDeposit(1L, 200.0));

        assertEquals("Insufficient money", exception.getMessage());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAllAccounts_ShouldReturnAccountDtoList() {
        List<Account> accounts = Arrays.asList(account);
        when(accountRepository.findAll()).thenReturn(accounts);

        List<AccountDto> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(List.of(accountDto), result);
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void transferMoneyTo_InsufficientFunds_ShouldThrowException() {
        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setAccountHolderName("Jane Smith");
        toAccount.setBalance(50.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        AccountException exception = assertThrows(AccountException.class, () -> accountService.transferMoneyTo(1L, 2L, 200.0));

        assertEquals("Insufficient money", exception.getMessage());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findById(2L);
    }

    @Test
    void deleteAccount_AccountExists_ShouldDeleteAccount() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).existsById(1L);
        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAccount_AccountDoesNotExist_ShouldThrowException() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        AccountException exception = assertThrows(AccountException.class, () -> accountService.deleteAccount(1L));

        assertEquals("Account doesn't exist", exception.getMessage());
        verify(accountRepository, times(1)).existsById(1L);
    }
}