package fr.epita.assistants.inventory.presentation.subscriber;

import fr.epita.assistants.common.aggregate.SyncInventoryAggregate;
import fr.epita.assistants.common.command.SyncInventoryCommand;
import fr.epita.assistants.inventory.domain.service.InventoryService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class SyncInventorySubscriber {

    private static final Logger LOGGER = Logger.getLogger(SyncInventorySubscriber.class);
    private final InventoryService inventoryService;

    @Channel("sync-inventory-aggregate")
    Emitter<SyncInventoryAggregate> syncAggregateEmitter;

    @Incoming("sync-inventory-command")
    @Blocking
    public void onSyncInventoryCommand(SyncInventoryCommand command) {
        LOGGER.info("Received SyncInventoryCommand: " + command);
        List<fr.epita.assistants.inventory.domain.entity.InventoryEntity> items = inventoryService.getAllItems();
        List<fr.epita.assistants.common.aggregate.ItemAggregate> aggregates = items.stream().map(entity -> {
            char typeChar = entity.getType().charAt(0);
            fr.epita.assistants.common.aggregate.ItemAggregate.ResourceType resourceType =
                    fr.epita.assistants.common.aggregate.ItemAggregate.ResourceType.getResource(typeChar);
            return new fr.epita.assistants.common.aggregate.ItemAggregate(resourceType, entity.getQuantity());
        }).toList();
        SyncInventoryAggregate aggregate = new SyncInventoryAggregate(aggregates);
        syncAggregateEmitter.send(aggregate);
    }
}
