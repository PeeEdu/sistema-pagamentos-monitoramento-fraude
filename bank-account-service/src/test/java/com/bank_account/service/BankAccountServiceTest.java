package com.bank_account.service;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.request.DepositRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.dto.response.PixKeysListResponse;
import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.UserCreatedEvent;
import com.bank_account.mapper.BankAccountMapper;
import com.bank_account.producer.PixKeyEventProducer;
import com.bank_account.repository.BankAccountRepository;
import com.bank_account.stub.BankAccountResponseStub;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.CreatePixKeyRequestStub;
import com.bank_account.stub.CreatePixKeyResponseStub;
import com.bank_account.stub.DepositRequestStub;
import com.bank_account.stub.PixKeyStub;
import com.bank_account.stub.PixKeysListResponseStub;
import com.bank_account.stub.UserCreatedEventStub;
import com.bank_account.validator.PixKeyValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final PixKeyValidator pixKeyValidator;
    private final PixKeyEventProducer pixKeyEventProducer;
    private final BankAccountService bankAccountService;

    BankAccountServiceTest() {
        this.bankAccountRepository = mock(BankAccountRepository.class);
        this.bankAccountMapper = mock(BankAccountMapper.class);
        this.pixKeyValidator = mock(PixKeyValidator.class);
        this.pixKeyEventProducer = mock(PixKeyEventProducer.class);

        this.bankAccountService = new BankAccountService(
                bankAccountRepository,
                bankAccountMapper,
                pixKeyValidator,
                pixKeyEventProducer
        );
    }

    @Test
    void getBankAccountByUserId_DeveRetornarBankAccountResponse_QuandoUserIdExistir() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        BankAccountResponse response = BankAccountResponseStub.buildResponse();

        when(bankAccountRepository.findByUserId("user-123")).thenReturn(bankAccount);
        when(bankAccountMapper.toResponse(bankAccount)).thenReturn(response);

        BankAccountResponse result = bankAccountService.getBankAccountByUserId("user-123");

        assertNotNull(result);
        assertEquals(response, result);

        verify(bankAccountRepository).findByUserId("user-123");
        verify(bankAccountMapper).toResponse(bankAccount);
    }

    @Test
    void createAccountForUser_DeveCriarContaESalvar_QuandoEventoForValido() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();
        BankAccount bankAccount = BankAccountStub.buildEntity();

        bankAccount.setAccountNumber(null);

        when(bankAccountMapper.toEntity(event)).thenReturn(bankAccount);
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        assertDoesNotThrow(() -> bankAccountService.createAccountForUser(event));

        assertNotNull(bankAccount.getAccountNumber());
        assertFalse(bankAccount.getAccountNumber().isBlank());

        verify(bankAccountMapper).toEntity(event);
        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    void createPixKey_DeveCriarSalvarEnviarEventoERetornarResponse_QuandoDadosForemValidos() {
        String userId = "user-123";
        CreatePixKeyRequest request = CreatePixKeyRequestStub.buildRequest();
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());
        PixKey pixKey = PixKeyStub.buildEntity();
        CreatePixKeyResponse response = CreatePixKeyResponseStub.buildResponse();

        when(bankAccountRepository.findByUserId(userId)).thenReturn(bankAccount);
        when(bankAccountMapper.toPixKeyEntity(request)).thenReturn(pixKey);
        when(bankAccountMapper.toPixKeyResponse(pixKey, userId)).thenReturn(response);

        CreatePixKeyResponse result = bankAccountService.createPixKey(userId, request);

        assertNotNull(result);
        assertEquals(response, result);
        assertTrue(bankAccount.getPixKey().contains(pixKey));

        verify(bankAccountRepository).findByUserId(userId);
        verify(pixKeyValidator).validateBankAccountExists(bankAccount, userId);
        verify(pixKeyValidator).validatePixKeyNotExists(bankAccount, request);
        verify(pixKeyValidator).initializePixKeyListIfNeeded(bankAccount);
        verify(bankAccountMapper).toPixKeyEntity(request);
        verify(bankAccountRepository).save(bankAccount);
        verify(pixKeyEventProducer).sendPixKeyCreatedEvent(bankAccount, pixKey);
        verify(bankAccountMapper).toPixKeyResponse(pixKey, userId);
    }

    @Test
    void createPixKey_DeveContinuarOperacao_QuandoEnvioDeEventoFalhar() {
        String userId = "user-123";
        CreatePixKeyRequest request = CreatePixKeyRequestStub.buildRequest();
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());
        PixKey pixKey = PixKeyStub.buildEntity();
        CreatePixKeyResponse response = CreatePixKeyResponseStub.buildResponse();

        when(bankAccountRepository.findByUserId(userId)).thenReturn(bankAccount);
        when(bankAccountMapper.toPixKeyEntity(request)).thenReturn(pixKey);
        when(bankAccountMapper.toPixKeyResponse(pixKey, userId)).thenReturn(response);

        doThrow(new RuntimeException("Erro ao enviar evento"))
                .when(pixKeyEventProducer)
                .sendPixKeyCreatedEvent(bankAccount, pixKey);

        CreatePixKeyResponse result = bankAccountService.createPixKey(userId, request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(bankAccountRepository).save(bankAccount);
        verify(pixKeyEventProducer).sendPixKeyCreatedEvent(bankAccount, pixKey);
    }

    @Test
    void deletePixKey_DeveRemoverChaveSalvarContaEEnviarEvento_QuandoDadosForemValidos() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());
        bankAccount.getPixKey().add(PixKeyStub.buildEntity("joao.silva@email.com"));

        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(bankAccount);

        assertDoesNotThrow(() -> bankAccountService.deletePixKey("123456", "joao.silva@email.com"));

        assertTrue(bankAccount.getPixKey().isEmpty());

        verify(bankAccountRepository).findByAccountNumber("123456");
        verify(bankAccountRepository).save(bankAccount);
        verify(pixKeyEventProducer).sendPixKeyDeletedEvent(bankAccount, "joao.silva@email.com");
    }

    @Test
    void deletePixKey_DeveContinuarOperacao_QuandoEnvioDoEventoFalhar() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());
        bankAccount.getPixKey().add(PixKeyStub.buildEntity("joao.silva@email.com"));

        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(bankAccount);

        doThrow(new RuntimeException("Erro ao enviar evento"))
                .when(pixKeyEventProducer)
                .sendPixKeyDeletedEvent(bankAccount, "joao.silva@email.com");

        assertDoesNotThrow(() -> bankAccountService.deletePixKey("123456", "joao.silva@email.com"));

        verify(bankAccountRepository).save(bankAccount);
        verify(pixKeyEventProducer).sendPixKeyDeletedEvent(bankAccount, "joao.silva@email.com");
    }

    @Test
    void deletePixKey_DeveLancarRuntimeException_QuandoContaNaoExistir() {
        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankAccountService.deletePixKey("123456", "joao.silva@email.com")
        );

        assertEquals("Conta bancária não encontrada: 123456", exception.getMessage());

        verify(bankAccountRepository).findByAccountNumber("123456");
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    void deletePixKey_DeveLancarRuntimeException_QuandoContaNaoPossuirChavesPix() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());

        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(bankAccount);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankAccountService.deletePixKey("123456", "joao.silva@email.com")
        );

        assertEquals("Nenhuma chave PIX encontrada para a conta: 123456", exception.getMessage());

        verify(bankAccountRepository).findByAccountNumber("123456");
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    void deletePixKey_DeveLancarRuntimeException_QuandoChavePixNaoForEncontrada() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());
        bankAccount.getPixKey().add(PixKeyStub.buildEntity("outra@email.com"));

        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(bankAccount);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankAccountService.deletePixKey("123456", "joao.silva@email.com")
        );

        assertEquals("Chave PIX não encontrada: joao.silva@email.com", exception.getMessage());

        verify(bankAccountRepository).findByAccountNumber("123456");
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    void deposit_DeveDepositarValorSalvarContaERetornarResponse_QuandoDadosForemValidos() {
        DepositRequest request = DepositRequestStub.buildRequest();
        BankAccount bankAccount = BankAccountStub.buildEntity();
        BankAccountResponse response = BankAccountResponseStub.buildResponse();

        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(bankAccount);
        when(bankAccountMapper.toResponse(bankAccount)).thenReturn(response);

        BankAccountResponse result = bankAccountService.deposit("123456", request);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(new BigDecimal("1100.00"), bankAccount.getBalance());

        verify(bankAccountRepository).findByAccountNumber("123456");
        verify(bankAccountRepository).save(bankAccount);
        verify(bankAccountMapper).toResponse(bankAccount);
    }

    @Test
    void getPixKeysByAccountNumber_DeveRetornarPixKeysListResponse_QuandoContaExistir() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        PixKeysListResponse response = PixKeysListResponseStub.buildResponse();

        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(bankAccount);
        when(bankAccountMapper.toPixKeysListResponse(bankAccount)).thenReturn(response);

        PixKeysListResponse result = bankAccountService.getPixKeysByAccountNumber("123456");

        assertNotNull(result);
        assertEquals(response, result);

        verify(bankAccountRepository).findByAccountNumber("123456");
        verify(bankAccountMapper).toPixKeysListResponse(bankAccount);
    }

    @Test
    void getPixKeysByAccountNumber_DeveLancarRuntimeException_QuandoContaNaoExistir() {
        when(bankAccountRepository.findByAccountNumber("123456")).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankAccountService.getPixKeysByAccountNumber("123456")
        );

        assertEquals("Conta bancária não encontrada: 123456", exception.getMessage());

        verify(bankAccountRepository).findByAccountNumber("123456");
        verify(bankAccountMapper, never()).toPixKeysListResponse(any());
    }
}