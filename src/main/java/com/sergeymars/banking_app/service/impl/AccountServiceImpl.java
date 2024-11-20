package com.sergeymars.banking_app.service.impl;

import com.sergeymars.banking_app.dto.AccountDto;
import com.sergeymars.banking_app.entity.Account;
import com.sergeymars.banking_app.exception.AccountException;
import com.sergeymars.banking_app.mapper.AccountMapper;
import com.sergeymars.banking_app.repository.AccountRepository;
import com.sergeymars.banking_app.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account doesn't exist"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto addDeposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account doesn't exist"));
        double total = account.getBalance() + amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdrawDeposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account doesn't exist"));

        if (account.getBalance() < amount) {
            throw new AccountException("Insufficient money");
        }
        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account)).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<AccountDto> transferMoneyTo(Long fromId, Long toId, double amount) {
        Account fromAccount = accountRepository
                .findById(fromId)
                .orElseThrow(() -> new AccountException("Account from you want transfer money doesn't exist"));
        Account toAccount = accountRepository
                .findById(toId)
                .orElseThrow(() -> new AccountException("Account want you transfer money doesn't exist to"));

        if (fromId == toId){throw new RuntimeException("Ids should be different");}
        if (fromAccount.getBalance() < amount) {throw new AccountException("Insufficient money");}

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        Account savedFromBalance = accountRepository.save(fromAccount);
        Account savedToBalance = accountRepository.save(toAccount);
        List<AccountDto> accounts = new ArrayList<>();
        accounts.add(AccountMapper.mapToAccountDto(savedFromBalance));
        accounts.add(AccountMapper.mapToAccountDto(savedToBalance));
        return accounts;
    }

    @Override
    public void deleteAccount(Long id) {
        if (accountRepository.existsById(id)) {accountRepository.deleteById(id);}
        else {throw new AccountException("Account doesn't exist");}
    }
}
