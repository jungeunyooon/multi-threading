package com.banking.multi_threading.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @Schema(description = "금액", example = "10000")
    private Long amount;

    @Schema(description = "스레드 개수", example = "10")
    private int threadCount = 10;

    @Schema(description = "반복 횟수", example = "100")
    private int iterations = 100;
}
