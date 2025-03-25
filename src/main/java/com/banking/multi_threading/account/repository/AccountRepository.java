package com.banking.multi_threading.account.repository;

import com.banking.multi_threading.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
