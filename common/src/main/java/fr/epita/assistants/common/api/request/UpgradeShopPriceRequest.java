package fr.epita.assistants.common.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpgradeShopPriceRequest {
    private float price;
}
