package com.transfer.controller;

import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.response.BaseResponse;
import com.transfer.dto.response.PixResponse;
import com.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/pix")
    public ResponseEntity<?> createTransfer(@RequestBody @Valid CreatePixTransferRequest createPixTransferRequest){
        log.info("iniciando operação de criar transferencia");

        var pix = transferService.createPixTransferRequest(createPixTransferRequest);

        return ResponseEntity.ok().body("ok");
    }
}
