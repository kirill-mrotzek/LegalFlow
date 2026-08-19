package de.kirillmrotzek.legalflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContractPageResponse {

    private List<ContractResponse> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}
