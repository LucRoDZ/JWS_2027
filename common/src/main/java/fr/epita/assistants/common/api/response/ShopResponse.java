package fr.epita.assistants.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShopResponse {
    private Integer id;
    private float priceMultiplier;
    private float upgradePrice;
}
