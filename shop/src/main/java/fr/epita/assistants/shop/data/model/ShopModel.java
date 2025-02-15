package fr.epita.assistants.shop.data.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Float priceMultiplier;
    private Float upgradePrice;

    public Float getPriceMultiplier() {
        return priceMultiplier;
    }

    public Float getUpgradePrice() {
        return upgradePrice;
    }
}
