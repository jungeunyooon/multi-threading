package com.banking.multi_threading.account.controller;

import com.banking.multi_threading.account.dto.AccountRequest;
import com.banking.multi_threading.account.dto.AccountResponse;
import com.banking.multi_threading.account.dto.TransactionRequest;
import com.banking.multi_threading.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "계좌 API", description = "은행 계좌 관련 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    // 계좌 생성
    @Operation(summary = "계좌 생성", description = "새로운 계좌를 생성합니다.")
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request
    ) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    // 동기화 없는 입출금
    @Operation(summary = "동기화 없는 입출금", description = "동기화 없이 입출금을 처리합니다.")
    @PostMapping("/{accountId}/transactions/unsafe")
    public ResponseEntity<Void> unsafeTransaction(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request
    ) {
        accountService.processTransaction(accountId, request, "unsafe");
        return ResponseEntity.ok().build();
    }

    // Mutex 기반 입출금
    @Operation(summary = "뮤텍스 입출금", description = "Mutex를 사용한 동기화 처리")
    @PostMapping("/{accountId}/transactions/mutex")
    public ResponseEntity<Void> mutexTransaction(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request
    ) {
        accountService.processTransaction(accountId, request, "mutex");
        return ResponseEntity.ok().build();
    }

    // Spinlock 기반 입출금
    @Operation(summary = "스핀락 입출금", description = "Spinlock을 사용한 동기화 처리")
    @PostMapping("/{accountId}/transactions/spinlock")
    public ResponseEntity<Void> spinlockTransaction(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request
    ) {
        accountService.processTransaction(accountId, request, "spinlock");
        return ResponseEntity.ok().build();
    }

    // Semaphore 기반 입출금
    @Operation(summary = "세마포어 입출금", description = "Semaphore를 사용한 동기화 처리")
    @PostMapping("/{accountId}/transactions/semaphore")
    public ResponseEntity<Void> semaphoreTransaction(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request
    ) {
        accountService.processTransaction(accountId, request, "semaphore");
        return ResponseEntity.ok().build();
    }


}