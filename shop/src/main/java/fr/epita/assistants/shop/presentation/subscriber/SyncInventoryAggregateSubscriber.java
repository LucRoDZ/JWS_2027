package fr.epita.assistants.shop.presentation.subscriber;

import fr.epita.assistants.common.aggregate.SyncInventoryAggregate;
import fr.epita.assistants.shop.domain.service.ShopService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class SyncInventoryAggregateSubscriber {

    private static final Logger LOGGER = Logger.getLogger(SyncInventoryAggregateSubscriber.class);
    private final ShopService shopService;

    @Incoming("sync-inventory-aggregate")
    @Blocking
    public void onSyncInventoryAggregate(SyncInventoryAggregate aggregate) {
        LOGGER.info("Received SyncInventoryAggregate: " + aggregate);
        shopService.updateItemsFromAggregate(aggregate);
    }
}
