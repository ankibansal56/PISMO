package com.pismo.account.domain.enums;

import lombok.Getter;

@Getter
public enum OperationTypeEnum {
    PURCHASE(1L, "PURCHASE", true),
    INSTALLMENT_PURCHASE(2L, "INSTALLMENT PURCHASE", true),
    WITHDRAWAL(3L, "WITHDRAWAL", true),
    PAYMENT(4L, "PAYMENT", false);

    private final Long id;
    private final String description;
    private final boolean isNegative;

    OperationTypeEnum(Long id, String description, boolean isNegative) {
        this.id = id;
        this.description = description;
        this.isNegative = isNegative;
    }

    public static OperationTypeEnum fromId(Long id) {
        for (OperationTypeEnum type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
