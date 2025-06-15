package com.example.demo.DTO;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StatementStatusHistoryDto {
    private Enum status;
    private LocalDateTime time;
    private Enum changeType;
}
