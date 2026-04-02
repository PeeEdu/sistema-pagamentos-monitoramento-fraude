package com.bank_account.validator;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.CreatePixKeyRequestStub;
import com.bank_account.stub.PixKeyStub;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PixKeyValidatorTest {

    private final PixKeyValidator validator = new PixKeyValidator();

    @Test
    void validateBankAccountExists_DeveExecutarSemLancarExcecao_QuandoContaExistir() {
        BankAccount bankAccount = BankAccountStub.buildEntity();

        assertDoesNotThrow(() -> validator.validateBankAccountExists(bankAccount, "user-123"));
    }

    @Test
    void validateBankAccountExists_DeveLancarRuntimeException_QuandoContaNaoExistir() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> validator.validateBankAccountExists(null, "user-123")
        );

        assertEquals("Conta bancária não encontrada", exception.getMessage());
    }

    @Test
    void validatePixKeyNotExists_DeveExecutarSemLancarExcecao_QuandoListaDePixKeysForNula() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(null);

        CreatePixKeyRequest request = CreatePixKeyRequestStub.buildRequest();

        assertDoesNotThrow(() -> validator.validatePixKeyNotExists(bankAccount, request));
    }

    @Test
    void validatePixKeyNotExists_DeveExecutarSemLancarExcecao_QuandoChavePixNaoExistir() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());
        bankAccount.getPixKey().add(PixKeyStub.buildEntity("outra@email.com"));

        CreatePixKeyRequest request = CreatePixKeyRequestStub.buildRequest();

        assertDoesNotThrow(() -> validator.validatePixKeyNotExists(bankAccount, request));
    }

    @Test
    void validatePixKeyNotExists_DeveLancarRuntimeException_QuandoChavePixJaExistir() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>());
        bankAccount.getPixKey().add(PixKeyStub.buildEntity("joao.silva@email.com"));

        CreatePixKeyRequest request = CreatePixKeyRequestStub.buildRequest();

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> validator.validatePixKeyNotExists(bankAccount, request)
        );

        assertEquals("Chave PIX já cadastrada", exception.getMessage());
    }

    @Test
    void initializePixKeyListIfNeeded_DeveInicializarLista_QuandoPixKeyForNula() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(null);

        validator.initializePixKeyListIfNeeded(bankAccount);

        assertNotNull(bankAccount.getPixKey());
        assertTrue(bankAccount.getPixKey().isEmpty());
    }

    @Test
    void initializePixKeyListIfNeeded_DeveManterListaExistente_QuandoPixKeyJaEstiverPreenchida() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        bankAccount.setPixKey(new ArrayList<>(List.of(PixKeyStub.buildEntity())));

        validator.initializePixKeyListIfNeeded(bankAccount);

        assertNotNull(bankAccount.getPixKey());
        assertEquals(1, bankAccount.getPixKey().size());
    }
}