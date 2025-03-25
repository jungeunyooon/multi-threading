package com.banking.multi_threading.account.service;


import com.banking.multi_threading.account.dto.AccountRequest;
import com.banking.multi_threading.account.dto.AccountResponse;
import com.banking.multi_threading.account.dto.TransactionRequest;
import com.banking.multi_threading.account.entity.Account;
import com.banking.multi_threading.account.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionService transactionService;
    private final Lock mutexLock = new ReentrantLock();
    private final AtomicBoolean spinlock = new AtomicBoolean(false);
    private final Semaphore semaphore = new Semaphore(3); // 단일 계좌 동시 접근 제한

    // 계좌 생성
    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        Account account = Account.builder()
                .balance(request.getBalance())
                .build();
        account = accountRepository.save(account);
        return createTransactionResult(account, 0L, request.getBalance(), 0L, 0L);
    }

    @Transactional
    public AccountResponse processTransaction(Long accountId, TransactionRequest request, String syncMethod) {
        Account account = getAccountById(accountId);
        long initialBalance = account.getBalance();
        int depositThreads = request.getThreadCount() / 2;
        int withdrawThreads = request.getThreadCount() - depositThreads;
        ExecutorService executor = Executors.newFixedThreadPool(request.getThreadCount());
        List<Future<?>> futures = new ArrayList<>();

        long startTime = System.nanoTime();
        long startCpuTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();

        Runnable depositTask = () -> {
            for (int j = 0; j < request.getIterations(); j++) {
                executeTransaction(account, request.getAmount(), syncMethod);
            }
        };
        Runnable withdrawTask = () -> {
            for (int j = 0; j < request.getIterations(); j++) {
                executeTransaction(account, -request.getAmount(), syncMethod);
            }
        };

        for (int i = 0; i < depositThreads; i++) {
            futures.add(executor.submit(depositTask));
        }
        for (int i = 0; i < withdrawThreads; i++) {
            futures.add(executor.submit(withdrawTask));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        long endTime = System.nanoTime();
        long endCpuTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();

        long executionTime = (endTime - startTime) / 1_000_000; // ms
        long cpuTime = (endCpuTime - startCpuTime) / 1_000_000; // ms

        System.out.println("Execution time: " + executionTime + " ms");
        System.out.println("CPU time: " + cpuTime + " ms");
        System.out.println("CPU usage: " + (cpuTime * 100.0 / executionTime) + "%");

        long expectedChange = 0;
        return createTransactionResult(account, initialBalance, expectedChange, executionTime, cpuTime);
    }


    private void executeTransaction(Account account, long amount, String syncMethod) {
        switch (syncMethod) {
            case "mutex":
                mutexLock.lock();
                try {
                    transactionService.updateBalance(account, amount);
                } finally {
                    mutexLock.unlock();
                }
                break;
            case "spinlock":
                while (!spinlock.compareAndSet(false, true)) {}
                try {
                    transactionService.updateBalance(account, amount);
                } finally {
                    spinlock.set(false);
                }
                break;
            case "semaphore":
                try {
                    semaphore.acquire();
                    transactionService.updateBalance(account, amount);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();
                }
                break;
            case "unsafe":
            default:
                account.setBalance(account.getBalance() + amount);
                transactionService.saveTransaction(account, amount);
        }
    }


    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    private AccountResponse createTransactionResult(Account account, long initialBalance, long expectedChange, long executionTime, long cpuTime) {
        long actualChange = account.getBalance() - initialBalance;
        return new AccountResponse(
                account.getId(),
                account.getBalance(),
                account.getHistories(),
                actualChange,
                expectedChange,
                cpuTime
        );
    }
}
