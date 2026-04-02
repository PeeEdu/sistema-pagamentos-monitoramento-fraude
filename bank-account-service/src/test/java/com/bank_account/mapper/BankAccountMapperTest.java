package com.bank_account.mapper;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.dto.response.PixKeyResponse;
import com.bank_account.dto.response.PixKeysListResponse;
import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.PixKeyCreatedEvent;
import com.bank_account.event.PixKeyDeletedEvent;
import com.bank_account.event.UserCreatedEvent;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.CreatePixKeyRequestStub;
import com.bank_account.stub.PixKeyStub;
import com.bank_account.stub.UserCreatedEventStub;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountMapperTest {

    private final BankAccountMapper bankAccountMapper = Mappers.getMapper(BankAccountMapper.class);

    @Test
    void toEntity_DeveRetornarBankAccount_QuandoReceberUserCreatedEvent() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        Instant before = Instant.now();
        BankAccount response = bankAccountMapper.toEntity(event);
        Instant after = Instant.now();

        assertNotNull(response);
        assertNull(response.getId());
        assertEquals(event.getUserId(), response.getUserId());
        assertNull(response.getAccountNumber());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals("CORRENTE", response.getAccountType());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("BRL", response.getCurrency());
        assertNotNull(response.getCreatedAt());
        assertFalse(response.getCreatedAt().isBefore(before));
        assertFalse(response.getCreatedAt().isAfter(after));
        assertNull(response.getUpdatedAt());
    }

    @Test
    void toResponse_DeveRetornarBankAccountResponse_QuandoReceberBankAccount() {
        BankAccount bankAccount = BankAccountStub.buildEntity();

        BankAccountResponse response = bankAccountMapper.toResponse(bankAccount);

        assertNotNull(response);
        assertEquals(bankAccount.getId(), response.id());
        assertEquals(bankAccount.getUserId(), response.userId());
        assertEquals(bankAccount.getAccountNumber(), response.accountNumber());
        assertEquals(bankAccount.getBalance(), response.balance());
        assertEquals(bankAccount.getAccountType(), response.accountType());
        assertEquals(bankAccount.getStatus(), response.status());
        assertEquals(bankAccount.getCurrency(), response.currency());
        assertEquals(bankAccount.getCreatedAt(), response.createdAt());
    }

    @Test
    void toPixKeyEntity_DeveRetornarPixKey_QuandoReceberCreatePixKeyRequest() {
        CreatePixKeyRequest request = CreatePixKeyRequestStub.buildRequest();

        PixKey response = bankAccountMapper.toPixKeyEntity(request);

        assertNotNull(response);
        assertEquals(request.type(), response.getType());
        assertEquals(request.key(), response.getKey());
    }

    @Test
    void toPixKeyResponse_DeveRetornarCreatePixKeyResponse_QuandoReceberPixKeyEUserId() {
        PixKey pixKey = PixKeyStub.buildEntity();

        CreatePixKeyResponse response = bankAccountMapper.toPixKeyResponse(pixKey, "user-123");

        assertNotNull(response);
        assertEquals("user-123", response.userId());
        assertEquals(pixKey.getType(), response.type());
        assertEquals(pixKey.getKey(), response.key());
    }

    @Test
    void toPixKeyResponse_DeveRetornarPixKeyResponse_QuandoReceberPixKey() {
        PixKey pixKey = PixKeyStub.buildEntity();

        PixKeyResponse response = bankAccountMapper.toPixKeyResponse(pixKey);

        assertNotNull(response);
        assertEquals(pixKey.getKey(), response.key());
        assertEquals(pixKey.getType(), response.type());
    }

    @Test
    void toPixKeyResponseList_DeveRetornarListaDePixKeyResponse_QuandoReceberListaDePixKey() {
        List<PixKey> pixKeys = List.of(
                PixKeyStub.buildEntity("joao.silva@email.com"),
                PixKeyStub.buildEntity("11999999999")
        );

        List<PixKeyResponse> response = bankAccountMapper.toPixKeyResponseList(pixKeys);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("joao.silva@email.com", response.get(0).key());
        assertEquals("11999999999", response.get(1).key());
    }

    @Test
    void toPixKeysListResponse_DeveRetornarPixKeysListResponse_QuandoReceberBankAccountComPixKeys() {
        BankAccount bankAccount = BankAccountStub.buildEntity();

        PixKeysListResponse response = bankAccountMapper.toPixKeysListResponse(bankAccount);

        assertNotNull(response);
        assertEquals(bankAccount.getUserId(), response.userId());
        assertEquals(bankAccount.getAccountNumber(), response.accountNumber());
        assertNotNull(response.pixKeys());
        assertEquals(2, response.pixKeys().size());
        assertEquals(2, response.totalKeys());
        assertEquals(bankAccount.getPixKey().get(0).getKey(), response.pixKeys().get(0).key());
    }

    @Test
    void toPixKeysListResponse_DeveRetornarTotalZero_QuandoBankAccountNaoPossuirPixKeys() {
        BankAccount bankAccount = BankAccountStub.buildEntityWithoutPixKeys();

        PixKeysListResponse response = bankAccountMapper.toPixKeysListResponse(bankAccount);

        assertNotNull(response);
        assertEquals(bankAccount.getUserId(), response.userId());
        assertEquals(bankAccount.getAccountNumber(), response.accountNumber());
        assertNull(response.pixKeys());
        assertEquals(0, response.totalKeys());
    }

    @Test
    void toPixKeyStringList_DeveRetornarListaVazia_QuandoListaForNula() {
        List<String> response = bankAccountMapper.toPixKeyStringList(null);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void toPixKeyStringList_DeveRetornarListaDeStrings_QuandoListaPossuirPixKeys() {
        List<PixKey> pixKeys = List.of(
                PixKeyStub.buildEntity("joao.silva@email.com"),
                PixKeyStub.buildEntity("11999999999")
        );

        List<String> response = bankAccountMapper.toPixKeyStringList(pixKeys);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("joao.silva@email.com", response.get(0));
        assertEquals("11999999999", response.get(1));
    }

    @Test
    void toPixKeyCreatedEvent_DeveRetornarPixKeyCreatedEvent_QuandoReceberBankAccountEPixKey() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        PixKey pixKey = PixKeyStub.buildEntity();

        Instant before = Instant.now();
        PixKeyCreatedEvent response = bankAccountMapper.toPixKeyCreatedEvent(bankAccount, pixKey);
        Instant after = Instant.now();

        assertNotNull(response);
        assertEquals(bankAccount.getUserId(), response.getUserId());
        assertEquals(bankAccount.getAccountNumber(), response.getAccountNumber());
        assertEquals(pixKey.getKey(), response.getPixKey());
        assertEquals(pixKey.getType().name(), response.getPixKeyType());
        assertNotNull(response.getCreatedAt());
        assertFalse(response.getCreatedAt().isBefore(before));
        assertFalse(response.getCreatedAt().isAfter(after));
    }

    @Test
    void toPixKeyDeletedEvent_DeveRetornarPixKeyDeletedEvent_QuandoReceberBankAccountEPixKey() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        String pixKey = "joao.silva@email.com";

        Instant before = Instant.now();
        PixKeyDeletedEvent response = bankAccountMapper.toPixKeyDeletedEvent(bankAccount, pixKey);
        Instant after = Instant.now();

        assertNotNull(response);
        assertEquals(bankAccount.getUserId(), response.getUserId());
        assertEquals(bankAccount.getAccountNumber(), response.getAccountNumber());
        assertEquals(pixKey, response.getPixKey());
        assertNotNull(response.getDeletedAt());
        assertFalse(response.getDeletedAt().isBefore(before));
        assertFalse(response.getDeletedAt().isAfter(after));
    }

    @Test
    void toEntity_DeveRetornarNulo_QuandoUserCreatedEventForNulo() {
        BankAccount response = bankAccountMapper.toEntity(null);

        assertNull(response);
    }

    @Test
    void toResponse_DeveRetornarNulo_QuandoBankAccountForNulo() {
        BankAccountResponse response = bankAccountMapper.toResponse(null);

        assertNull(response);
    }

    @Test
    void toPixKeyEntity_DeveRetornarNulo_QuandoCreatePixKeyRequestForNulo() {
        PixKey response = bankAccountMapper.toPixKeyEntity(null);

        assertNull(response);
    }

    @Test
    void toPixKeyResponse_DeveRetornarNulo_QuandoPixKeyForNulo() {
        PixKeyResponse response = bankAccountMapper.toPixKeyResponse((PixKey) null);

        assertNull(response);
    }

    @Test
    void toPixKeyResponse_DeveRetornarNulo_QuandoPixKeyEUserIdForemNulos() {
        CreatePixKeyResponse response = bankAccountMapper.toPixKeyResponse(null, null);

        assertNull(response);
    }

    @Test
    void toPixKeysListResponse_DeveRetornarNulo_QuandoBankAccountForNulo() {
        PixKeysListResponse response = bankAccountMapper.toPixKeysListResponse(null);

        assertNull(response);
    }

    @Test
    void toPixKeyCreatedEvent_DeveRetornarNulo_QuandoBankAccountEPixKeyForemNulos() {
        PixKeyCreatedEvent response = bankAccountMapper.toPixKeyCreatedEvent(null, null);

        assertNull(response);
    }

    @Test
    void toPixKeyDeletedEvent_DeveRetornarNulo_QuandoBankAccountEPixKeyForemNulos() {
        PixKeyDeletedEvent response = bankAccountMapper.toPixKeyDeletedEvent(null, null);

        assertNull(response);
    }
}