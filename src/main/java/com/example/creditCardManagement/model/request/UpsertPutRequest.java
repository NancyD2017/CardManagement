package com.example.creditCardManagement.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Запрос для обновления данных")
@Data
public class UpsertPutRequest {
    @Schema(description = "Данные для обновления", example = "update_data")
    private String request;
}