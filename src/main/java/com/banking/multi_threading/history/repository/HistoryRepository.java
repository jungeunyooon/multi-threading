package com.banking.multi_threading.history.repository;

import com.banking.multi_threading.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
