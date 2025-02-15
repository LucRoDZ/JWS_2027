package fr.epita.assistants.item_producer.converter;

import fr.epita.assistants.common.api.response.ItemResponse;
import fr.epita.assistants.item_producer.data.model.ItemModel;
import fr.epita.assistants.item_producer.domain.entity.ItemEntity;

public class ItemConverter {

    public static ItemResponse entityToResponse(ItemEntity entity) {
        return new ItemResponse(
                entity.getId() != null ? entity.getId().intValue() : null,
                entity.getType(),
                entity.getQuantity()
        );
    }

    public static ItemEntity modelToEntity(ItemModel model) {
        return new ItemEntity(model.getId(), model.getType(), model.getQuantity());
    }
}
