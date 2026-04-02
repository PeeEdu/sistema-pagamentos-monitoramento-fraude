package com.bank_account.controller;

import com.bank_account.service.BankAccountService;
import com.bank_account.stub.BankAccountResponseStub;
import com.bank_account.stub.CreatePixKeyRequestStub;
import com.bank_account.stub.CreatePixKeyResponseStub;
import com.bank_account.stub.DepositRequestStub;
import com.bank_account.stub.PixKeysListResponseStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BankAccountControllerTest {

    private static final String BASE_URL = "/api/bank-account";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final BankAccountService bankAccountService;
    private final MockMvc mockMvc;

    BankAccountControllerTest() {
        this.bankAccountService = mock(BankAccountService.class);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new BankAccountController(bankAccountService))
                .build();
    }

    @Test
    void getBankAccountByUserId_DeveRetornarStatus200ComContaBancaria_QuandoUserIdForInformado() throws Exception {
        var response = BankAccountResponseStub.buildResponse();

        when(bankAccountService.getBankAccountByUserId("user-123")).thenReturn(response);

        mockMvc.perform(
                        get(BASE_URL + "/{userId}", "user-123")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Conta bancaria encontrada com sucesso")))
                .andExpect(jsonPath("$.data.id", is(response.id())))
                .andExpect(jsonPath("$.data.userId", is(response.userId())))
                .andExpect(jsonPath("$.data.accountNumber", is(response.accountNumber())))
                .andExpect(jsonPath("$.data.accountType", is(response.accountType())))
                .andExpect(jsonPath("$.data.status", is(response.status())))
                .andExpect(jsonPath("$.data.currency", is(response.currency())));

        verify(bankAccountService).getBankAccountByUserId("user-123");
    }

    @Test
    void depositValue_DeveRetornarStatus200ComContaAtualizada_QuandoDepositoForInformado() throws Exception {
        var request = DepositRequestStub.buildRequest();
        var response = BankAccountResponseStub.buildResponse();

        when(bankAccountService.deposit("123456", request)).thenReturn(response);

        mockMvc.perform(
                        patch(BASE_URL + "/deposit/{accountNumber}", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Deposito feito com sucesso")))
                .andExpect(jsonPath("$.data.id", is(response.id())))
                .andExpect(jsonPath("$.data.userId", is(response.userId())))
                .andExpect(jsonPath("$.data.accountNumber", is(response.accountNumber())));

        verify(bankAccountService).deposit("123456", request);
    }

    @Test
    void createPixKey_DeveRetornarStatus200ComChavePix_QuandoRequestForValido() throws Exception {
        var request = CreatePixKeyRequestStub.buildRequest();
        var response = CreatePixKeyResponseStub.buildResponse();

        when(bankAccountService.createPixKey("user-123", request)).thenReturn(response);

        mockMvc.perform(
                        patch(BASE_URL + "/pix/{userId}", "user-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Chave PIX criada com sucesso")))
                .andExpect(jsonPath("$.data.userId", is(response.userId())))
                .andExpect(jsonPath("$.data.type", is(response.type().name())))
                .andExpect(jsonPath("$.data.key", is(response.key())));

        verify(bankAccountService).createPixKey("user-123", request);
    }

    @Test
    void getPixKeysByAccountNumber_DeveRetornarStatus200ComListaDeChaves_QuandoContaForInformada() throws Exception {
        var response = PixKeysListResponseStub.buildResponse();

        when(bankAccountService.getPixKeysByAccountNumber("123456")).thenReturn(response);

        mockMvc.perform(
                        get(BASE_URL + "/pix/{accountNumber}", "123456")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Chaves PIX encontradas com sucesso")))
                .andExpect(jsonPath("$.data.userId", is(response.userId())))
                .andExpect(jsonPath("$.data.accountNumber", is(response.accountNumber())))
                .andExpect(jsonPath("$.data.totalKeys", is(response.totalKeys())))
                .andExpect(jsonPath("$.data.pixKeys", hasSize(2)))
                .andExpect(jsonPath("$.data.pixKeys[0].key", is(response.pixKeys().get(0).key())))
                .andExpect(jsonPath("$.data.pixKeys[0].type", is(response.pixKeys().get(0).type().name())));

        verify(bankAccountService).getPixKeysByAccountNumber("123456");
    }

    @Test
    void deletePixKey_DeveRetornarStatus200ComMensagem_QuandoChavePixForInformada() throws Exception {
        mockMvc.perform(
                        delete(BASE_URL + "/pix/{accountNumber}/{pixKey}", "123456", "joao.silva@email.com")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Chave PIX excluída com sucesso")))
                .andExpect(jsonPath("$.data", is("Chave PIX removida")));

        verify(bankAccountService).deletePixKey("123456", "joao.silva@email.com");
    }

    @Test
    void createPixKey_DeveRetornarStatus400_QuandoRequestForInvalido() throws Exception {
        String requestInvalido = """
                {
                  "type": null,
                  "key": ""
                }
                """;

        mockMvc.perform(
                        patch(BASE_URL + "/pix/{userId}", "user-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestInvalido)
                )
                .andExpect(status().isBadRequest());

        verify(bankAccountService, never()).createPixKey(anyString(), any());
    }
}