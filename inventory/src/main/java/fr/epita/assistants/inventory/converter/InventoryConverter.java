package fr.epita.assistants.inventory.converter;

import fr.epita.assistants.common.api.request.InventoryRequest;
import fr.epita.assistants.common.api.response.InventoryResponse;
import fr.epita.assistants.inventory.domain.entity.InventoryEntity;

public class InventoryConverter {

    public static InventoryEntity requestToEntity(InventoryRequest request) {
        return new InventoryEntity(null, request.getItemType(), request.getQuantity());
    }

    public static InventoryResponse entityToResponse(InventoryEntity entity) {
        // Fixed typo: use getType() instead of get.Type()
        return new InventoryResponse(entity.getId(), entity.getType(), entity.getQuantity());
    }
}
