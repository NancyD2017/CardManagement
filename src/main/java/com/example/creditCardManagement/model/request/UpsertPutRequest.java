package com.example.creditCardManagement.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос для обновления данных")
public class UpsertPutRequest {
    @Schema(description = "Данные для обновления", example = "update_data")
    private String request;
}