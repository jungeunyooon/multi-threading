package com.banking.multi_threading.account.dto;

import com.banking.multi_threading.history.entity.History;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    @Schema(description = "계좌 ID", example = "1")
    private Long id;

    @Schema(description = "잔액", example = "1234567890")
    private Long balance;

    @Schema(description = "거래 내역")
    private List<History> histories;

    @Schema(description = "변경된 금액", example = "10000")
    private Long changedAmount;

    @Schema(description = "예상 변경 금액", example = "10000")
    private Long expectedChangedAmount;

    @Schema(description = "CPU 사용량", example = "100")
    private Long cpuUsage;

}
