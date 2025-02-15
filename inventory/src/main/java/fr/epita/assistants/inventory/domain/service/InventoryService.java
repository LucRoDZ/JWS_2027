package fr.epita.assistants.inventory.domain.service;

import fr.epita.assistants.inventory.data.model.ItemModel;
import fr.epita.assistants.inventory.data.repository.InventoryRepository;
import fr.epita.assistants.inventory.domain.entity.InventoryEntity;
import fr.epita.assistants.common.aggregate.ItemAggregate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryEntity> getAllItems() {
        return inventoryRepository.listAll().stream()
                .map(model -> new InventoryEntity(model.getId(), model.getType(), model.getQuantity()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addItem(InventoryEntity entity) {
        ItemModel model = new ItemModel(null, entity.getType(), entity.getQuantity());
        inventoryRepository.persist(model);
    }

    @Transactional
    public void resetInventory() {
        inventoryRepository.deleteAll();
    }

    @Transactional
    public void updateInventoryWithAggregate(ItemAggregate aggregate) {
        String type = String.valueOf(aggregate.getType().getItemInfo().getValue());
        // Fixed query: use "type" to match the ItemModel field name
        Optional<ItemModel> existing = inventoryRepository.find("type", type).firstResultOptional();
        if (existing.isPresent()) {
            ItemModel model = existing.get();
            model.setQuantity(aggregate.getQuantity());
        } else {
            ItemModel newModel = new ItemModel(null, type, aggregate.getQuantity());
            inventoryRepository.persist(newModel);
        }
    }

}
