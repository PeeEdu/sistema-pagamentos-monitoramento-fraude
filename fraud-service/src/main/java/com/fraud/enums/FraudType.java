package com.fraud.enums;

public enum FraudType {

    // 🔁 Fraudes de Duplicação
    DUPLICATE_TRANSACTION,           // Transação duplicada (mesmo valor, mesmo destino, curto intervalo)
    REPEATED_ATTEMPTS,               // Múltiplas tentativas seguidas

    // 💰 Fraudes de Valor
    HIGH_VALUE,                      // Valor acima do limite permitido
    UNUSUAL_AMOUNT,                  // Valor fora do padrão do usuário
    ROUND_AMOUNT,                    // Valores redondos suspeitos (ex: 1000, 5000)

    // ⏰ Fraudes de Frequência
    HIGH_FREQUENCY,                  // Muitas transações em pouco tempo
    VELOCITY_CHECK_FAILED,           // Velocidade de transações anormal
    UNUSUAL_TIME,                    // Transação em horário atípico (madrugada)

    // 📍 Fraudes de Localização
    LOCATION_MISMATCH,               // Localização diferente do padrão
    IMPOSSIBLE_TRAVEL,               // Transações em locais muito distantes em pouco tempo
    HIGH_RISK_COUNTRY,               // País de alto risco

    // 👤 Fraudes de Conta
    NEW_ACCOUNT,                     // Conta muito nova fazendo transação alta
    ACCOUNT_TAKEOVER,                // Conta possivelmente invadida
    MULTIPLE_ACCOUNTS,               // Múltiplas contas do mesmo dispositivo/IP
    STOLEN_CREDENTIALS,              // Credenciais roubadas

    // 🎯 Fraudes de Padrão
    UNUSUAL_PATTERN,                 // Padrão de uso fora do comum
    BEHAVIORAL_ANOMALY,              // Comportamento diferente do histórico
    SUDDEN_CHANGE,                   // Mudança repentina de comportamento

    // 🔒 Fraudes Técnicas
    IP_BLACKLIST,                    // IP na lista negra
    DEVICE_FINGERPRINT_MISMATCH,     // Dispositivo diferente do habitual
    VPN_TOR_USAGE,                   // Uso de VPN/TOR
    BOT_DETECTED,                    // Comportamento de bot

    // 💳 Fraudes Específicas PIX
    PIX_KEY_FRAUD,                   // Chave PIX suspeita
    FIRST_TIME_RECIPIENT,            // Primeira vez enviando para este destinatário
    MULTIPLE_PIX_KEYS,               // Muitas chaves PIX cadastradas rapidamente

    // 🎭 Fraudes Combinadas
    MULTIPLE_INDICATORS,             // Múltiplos indicadores de fraude
    ML_DETECTED,                     // Detectado por Machine Learning
    MANUAL_REVIEW_REQUIRED,          // Requer revisão manual

    // 🚫 Listas
    BLACKLIST,                       // Usuário/conta na lista negra
    WATCHLIST,                       // Usuário em observação
    SANCTIONS_LIST,                  // Lista de sanções internacionais

    // 🔄 Fraudes de Lavagem
    STRUCTURING,                     // Fracionamento (smurfing)
    LAYERING,                        // Camadas de transações para ocultar origem
    CIRCULAR_TRANSFER,               // Transferências circulares

    // 📱 Fraudes de Dispositivo
    ROOTED_DEVICE,                   // Dispositivo com root/jailbreak
    EMULATOR_DETECTED,               // Emulador detectado
    MULTIPLE_DEVICES,                // Múltiplos dispositivos em curto período

    // 🎲 Outros
    SOCIAL_ENGINEERING,              // Engenharia social
    PHISHING_ATTEMPT,                // Tentativa de phishing
    CARD_TESTING,                    // Teste de cartões roubados
    REFUND_FRAUD,                    // Fraude de reembolso
    FRIENDLY_FRAUD,                  // Fraude amigável (chargeback indevido)

    UNKNOWN                          // Tipo desconhecido
}