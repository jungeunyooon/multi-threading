package com.banking.multi_threading.account.service;

import com.banking.multi_threading.account.entity.Account;
import com.banking.multi_threading.account.repository.AccountRepository;
import com.banking.multi_threading.account.util.TransactionType;
import com.banking.multi_threading.history.entity.History;
import com.banking.multi_threading.history.repository.HistoryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

    private final AccountRepository accountRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public void updateBalance(Account account, Long amount) {
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        saveTransaction(account, amount);
    }

    public void saveTransaction(Account account, Long amount) {
        History history = History.builder()
                .amount(amount)
                .transactionTime(LocalDateTime.now())
                .transactionType(amount > 0 ? TransactionType.DEPOSIT : TransactionType.WITHDRAW)
                .account(account)
                .build();
        historyRepository.save(history);
    }
}
