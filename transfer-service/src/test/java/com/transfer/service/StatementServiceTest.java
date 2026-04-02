package com.transfer.service;

import com.transfer.entity.AccountPixKey;
import com.transfer.entity.Transfer;
import com.transfer.repository.AccountPixKeyRepository;
import com.transfer.repository.TransferRepository;
import com.transfer.stub.AccountPixKeyStub;
import com.transfer.stub.TransferStub;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatementServiceTest {

    private final TransferRepository transferRepository;
    private final AccountPixKeyRepository accountPixKeyRepository;
    private final StatementService statementService;

    StatementServiceTest() {
        this.transferRepository = mock(TransferRepository.class);
        this.accountPixKeyRepository = mock(AccountPixKeyRepository.class);
        this.statementService = new StatementService(transferRepository, accountPixKeyRepository);
    }

    @Test
    void getAccountStatementByUserId_DeveRetornarPageVazia_QuandoUsuarioNaoPossuirChavesPix() {
        when(accountPixKeyRepository.findByUserId("user-123")).thenReturn(List.of());

        Page<Transfer> response = statementService.getAccountStatementByUserId("user-123", 0, 20);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(accountPixKeyRepository).findByUserId("user-123");
        verify(transferRepository, never()).findSmartStatement(anyString(), anyList(), any(Pageable.class));
    }

    @Test
    void getAccountStatementByUserId_DeveRetornarExtrato_QuandoUsuarioPossuirChavesPix() {
        AccountPixKey pixKey1 = AccountPixKeyStub.buildEntity("joao@email.com");
        AccountPixKey pixKey2 = AccountPixKeyStub.buildEntity("11999999999");
        Page<Transfer> page = new PageImpl<>(List.of(TransferStub.buildEntity()));

        when(accountPixKeyRepository.findByUserId("user-123")).thenReturn(List.of(pixKey1, pixKey2));
        when(transferRepository.findSmartStatement(eq("123456"), eq(List.of("joao@email.com", "11999999999")), any(Pageable.class)))
                .thenReturn(page);

        Page<Transfer> response = statementService.getAccountStatementByUserId("user-123", 0, 20);

        assertNotNull(response);
        assertEquals(page, response);

        verify(accountPixKeyRepository).findByUserId("user-123");
        verify(transferRepository).findSmartStatement(
                eq("123456"),
                eq(List.of("joao@email.com", "11999999999")),
                any(Pageable.class)
        );
    }

    @Test
    void getAccountStatementByAccountNumber_DeveRetornarExtrato_QuandoContaPossuirChavesPix() {
        AccountPixKey pixKey1 = AccountPixKeyStub.buildEntity("joao@email.com");
        AccountPixKey pixKey2 = AccountPixKeyStub.buildEntity("11999999999");
        Page<Transfer> page = new PageImpl<>(List.of(TransferStub.buildEntity()));

        when(accountPixKeyRepository.findByAccountNumber("123456")).thenReturn(List.of(pixKey1, pixKey2));
        when(transferRepository.findSmartStatement(eq("123456"), eq(List.of("joao@email.com", "11999999999")), any(Pageable.class)))
                .thenReturn(page);

        Page<Transfer> response = statementService.getAccountStatementByAccountNumber("123456", 0, 20);

        assertNotNull(response);
        assertEquals(page, response);

        verify(accountPixKeyRepository).findByAccountNumber("123456");
        verify(transferRepository).findSmartStatement(
                eq("123456"),
                eq(List.of("joao@email.com", "11999999999")),
                any(Pageable.class)
        );
    }

    @Test
    void getAllTransfersByUserId_DeveRetornarPageVazia_QuandoUsuarioNaoPossuirChavesPix() {
        when(accountPixKeyRepository.findByUserId("user-123")).thenReturn(List.of());

        Page<Transfer> response = statementService.getAllTransfersByUserId("user-123", 0, 20);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(accountPixKeyRepository).findByUserId("user-123");
        verify(transferRepository, never()).findByFromAccountNumberOrPixKeyIn(anyString(), anyList(), any(Pageable.class));
    }

    @Test
    void getAllTransfersByUserId_DeveRetornarTodasAsTransferencias_QuandoUsuarioPossuirChavesPix() {
        AccountPixKey pixKey1 = AccountPixKeyStub.buildEntity("joao@email.com");
        AccountPixKey pixKey2 = AccountPixKeyStub.buildEntity("11999999999");
        Page<Transfer> page = new PageImpl<>(List.of(TransferStub.buildEntity()));

        when(accountPixKeyRepository.findByUserId("user-123")).thenReturn(List.of(pixKey1, pixKey2));
        when(transferRepository.findByFromAccountNumberOrPixKeyIn(
                eq("123456"),
                eq(List.of("joao@email.com", "11999999999")),
                any(Pageable.class)
        )).thenReturn(page);

        Page<Transfer> response = statementService.getAllTransfersByUserId("user-123", 0, 20);

        assertNotNull(response);
        assertEquals(page, response);

        verify(accountPixKeyRepository).findByUserId("user-123");
        verify(transferRepository).findByFromAccountNumberOrPixKeyIn(
                eq("123456"),
                eq(List.of("joao@email.com", "11999999999")),
                any(Pageable.class)
        );
    }
}