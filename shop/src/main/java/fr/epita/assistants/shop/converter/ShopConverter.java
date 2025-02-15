package fr.epita.assistants.shop.converter;

import fr.epita.assistants.common.api.response.ShopResponse;
import fr.epita.assistants.common.api.request.ShopRequest;
import fr.epita.assistants.shop.domain.entity.ShopEntity;

public class ShopConverter {

    public static ShopResponse entityToResponse(ShopEntity shop) {
        return new ShopResponse(shop.getId(), shop.getPriceMultiplier(), shop.getUpgradePrice());
    }

    public static ShopEntity requestToEntity(ShopRequest request) {
        return new ShopEntity(null, request.getPriceMultiplier(), request.getUpgradePrice());
    }
}
