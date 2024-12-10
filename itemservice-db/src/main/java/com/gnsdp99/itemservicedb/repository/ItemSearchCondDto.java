package com.gnsdp99.itemservicedb.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchCondDto {

    private String itemName;
    private Integer maxPrice;
}
