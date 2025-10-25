package com.pismo.account.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operation_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationType {

    @Id
    @Column(name = "operation_type_id")
    private Long operationTypeId;

    @Column(name = "description", nullable = false)
    private String description;
}
