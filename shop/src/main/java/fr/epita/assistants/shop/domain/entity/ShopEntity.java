package fr.epita.assistants.shop.domain.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopEntity {
    private Integer id;
    private float priceMultiplier;
    private float upgradePrice;
}
