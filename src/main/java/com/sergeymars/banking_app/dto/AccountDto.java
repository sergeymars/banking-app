package com.sergeymars.banking_app.dto;

public record AccountDto(
        Long id,
        String accountHolderName,
        double balance
) {
}
