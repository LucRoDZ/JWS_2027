package fr.epita.assistants.inventory.presentation.subscriber;

import fr.epita.assistants.common.aggregate.ItemAggregate;
import fr.epita.assistants.inventory.domain.service.InventoryService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class CollectItemAggregateSubscriber {

    private static final Logger LOGGER = Logger.getLogger(CollectItemAggregateSubscriber.class);
    private final InventoryService inventoryService;

    @Incoming("collect-item-aggregate")
    @Blocking
    public void onCollectItemAggregate(ItemAggregate aggregate) {
        LOGGER.info("Received ItemAggregate: " + aggregate);
        inventoryService.updateInventoryWithAggregate(aggregate);
    }
}
