package com.banking.multi_threading.account.entity;

import com.banking.multi_threading.history.entity.History;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id; // PK 

    @Column(name = "balance", nullable = false)
    private Long balance; // 잔액

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<History> histories = new ArrayList<>();

    @Builder
    public Account(Long balance) {
        this.balance = balance;
    }

    // 잔액 초기화 메서드 추가
    public void resetBalance() {
        this.balance = 0L;
    }

    // 잔액 변경 메서드 추가
    public void setBalance(Long balance) {
        this.balance = balance;
    }
}