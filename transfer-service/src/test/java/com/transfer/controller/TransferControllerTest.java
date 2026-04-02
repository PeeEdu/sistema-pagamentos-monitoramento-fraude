package com.transfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.response.BaseResponse;
import com.transfer.dto.response.PixResponse;
import com.transfer.entity.Transfer;
import com.transfer.service.StatementService;
import com.transfer.service.TransferService;
import com.transfer.stub.CreatePixTransferRequestStub;
import com.transfer.stub.TransferStub;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransferControllerTest {

    private static final String BASE_URL = "/api/transfer";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TransferService transferService;
    private final StatementService statementService;
    private final TransferController transferController;
    private final MockMvc mockMvc;

    TransferControllerTest() {
        this.transferService = mock(TransferService.class);
        this.statementService = mock(StatementService.class);
        this.transferController = new TransferController(transferService, statementService);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(transferController)
                .build();
    }

    @Test
    void createTransfer_DeveRetornarStatus200ComOk_QuandoRequisicaoForValida() throws Exception {
        CreatePixTransferRequest request = CreatePixTransferRequestStub.buildRequest();
        PixResponse response = mock(PixResponse.class);

        when(transferService.createPixTransferRequest(any(CreatePixTransferRequest.class))).thenReturn(response);

        mockMvc.perform(
                        post(BASE_URL + "/pix")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        verify(transferService).createPixTransferRequest(any(CreatePixTransferRequest.class));
    }

    @Test
    void getExtractByAccountNumber_DeveRetornarStatus200ComExtrato_QuandoContaForInformada() {
        Transfer transfer = TransferStub.buildEntity();
        Page<Transfer> page = new PageImpl<>(List.of(transfer));

        when(statementService.getAccountStatementByAccountNumber("123456", 0, 20)).thenReturn(page);

        ResponseEntity<BaseResponse<Page<Transfer>>> response =
                transferController.getExtractByAccountNumber("123456", 0, 20);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Extrato encontrado com sucesso", response.getBody().message());
        assertEquals(page, response.getBody().data());

        verify(statementService).getAccountStatementByAccountNumber("123456", 0, 20);
    }

    @Test
    void getSmartExtractByUserId_DeveRetornarStatus200ComExtratoInteligente_QuandoUserIdForInformado() {
        Transfer transfer = TransferStub.buildEntity();
        Page<Transfer> page = new PageImpl<>(List.of(transfer));

        when(statementService.getAccountStatementByUserId("user-123", 0, 20)).thenReturn(page);

        ResponseEntity<BaseResponse<Page<Transfer>>> response =
                transferController.getSmartExtractByUserId("user-123", 0, 20);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Extrato inteligente encontrado com sucesso", response.getBody().message());
        assertEquals(page, response.getBody().data());

        verify(statementService).getAccountStatementByUserId("user-123", 0, 20);
    }

    @Test
    void getAllExtractByUserId_DeveRetornarStatus200ComExtratoCompleto_QuandoUserIdForInformado() {
        Transfer transfer = TransferStub.buildEntity();
        Page<Transfer> page = new PageImpl<>(List.of(transfer));

        when(statementService.getAllTransfersByUserId("user-123", 0, 20)).thenReturn(page);

        ResponseEntity<BaseResponse<Page<Transfer>>> response =
                transferController.getAllExtractByUserId("user-123", 0, 20);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Extrato completo encontrado com sucesso", response.getBody().message());
        assertEquals(page, response.getBody().data());

        verify(statementService).getAllTransfersByUserId("user-123", 0, 20);
    }
}