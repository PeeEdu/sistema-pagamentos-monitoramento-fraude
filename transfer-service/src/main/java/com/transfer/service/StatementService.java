package com.transfer.service;

import com.transfer.entity.AccountPixKey;
import com.transfer.entity.Transfer;
import com.transfer.enums.TransferStatus;
import com.transfer.repository.AccountPixKeyRepository;
import com.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementService {

    private final TransferRepository transferRepository;
    private final AccountPixKeyRepository accountPixKeyRepository;

    public Page<Transfer> getAccountStatementByUserId(String userId, int page, int size) {
        log.info("📊 Buscando extrato inteligente para userId: {}", userId);

        List<AccountPixKey> accountPixKeys = accountPixKeyRepository.findByUserId(userId);

        if (accountPixKeys.isEmpty()) {
            log.warn("⚠️ Nenhuma chave PIX encontrada para userId: {}", userId);
            return Page.empty();
        }

        String accountNumber = accountPixKeys.getFirst().getAccountNumber();
        List<String> pixKeys = accountPixKeys.stream()
                .map(AccountPixKey::getPixKey)
                .toList();

        log.info("🔍 Buscando extrato inteligente para conta: {} com {} chaves PIX",
                accountNumber, pixKeys.size());

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return transferRepository.findSmartStatement(accountNumber, pixKeys, pageable);
    }

    public Page<Transfer> getAccountStatementByAccountNumber(String accountNumber, int page, int size) {
        log.info("📊 Buscando extrato inteligente para conta: {}", accountNumber);

        List<AccountPixKey> accountPixKeys = accountPixKeyRepository.findByAccountNumber(accountNumber);
        List<String> pixKeys = accountPixKeys.stream()
                .map(AccountPixKey::getPixKey)
                .toList();

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return transferRepository.findSmartStatement(accountNumber, pixKeys, pageable);
    }

    public Page<Transfer> getAllTransfersByUserId(String userId, int page, int size) {
        log.info("📊 Buscando TODAS as transferências (admin/debug) para userId: {}", userId);

        List<AccountPixKey> accountPixKeys = accountPixKeyRepository.findByUserId(userId);

        if (accountPixKeys.isEmpty()) {
            return Page.empty();
        }

        String accountNumber = accountPixKeys.getFirst().getAccountNumber();
        List<String> pixKeys = accountPixKeys.stream()
                .map(AccountPixKey::getPixKey)
                .toList();

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return transferRepository.findByFromAccountNumberOrPixKeyIn(accountNumber, pixKeys, pageable);
    }
}