package fr.epita.assistants.item_producer.domain.service;

import fr.epita.assistants.item_producer.data.model.ItemModel;
import fr.epita.assistants.item_producer.data.repository.ItemRepository;
import fr.epita.assistants.item_producer.domain.entity.ItemEntity;
import fr.epita.assistants.common.aggregate.ItemAggregate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemEntity> getAllItems() {
        return itemRepository.listAll().stream()
                .map(model -> new ItemEntity(model.getId(), model.getType(), model.getQuantity()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addItem(ItemEntity entity) {
        itemRepository.persist(new ItemModel(null, entity.getType(), entity.getQuantity()));
    }

    @Transactional
    public void updateItemFromAggregate(ItemAggregate aggregate) {
        String itemType = String.valueOf(aggregate.getType().getItemInfo().getValue());
        Optional<ItemModel> existing = itemRepository.find("type", itemType).firstResultOptional();
        if (existing.isPresent()) {
            ItemModel model = existing.get();
            model.setQuantity(aggregate.getQuantity());
        } else {
            ItemModel newModel = new ItemModel(null, itemType, aggregate.getQuantity());
            itemRepository.persist(newModel);
        }
    }
}
