package fr.epita.assistants.common.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopRequest {
    private float priceMultiplier;
    private float upgradePrice;
}
