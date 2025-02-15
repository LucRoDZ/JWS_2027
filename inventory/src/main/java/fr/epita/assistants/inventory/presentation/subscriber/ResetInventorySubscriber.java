package fr.epita.assistants.inventory.presentation.subscriber;

import fr.epita.assistants.common.command.ResetInventoryCommand;
import fr.epita.assistants.inventory.domain.service.InventoryService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class ResetInventorySubscriber {

    private static final Logger LOGGER = Logger.getLogger(ResetInventorySubscriber.class);
    private final InventoryService inventoryService;

    @Incoming("reset-inventory-command")
    @Blocking
    public void onResetInventoryCommand(ResetInventoryCommand command) {
        LOGGER.info("Received ResetInventoryCommand: " + command);
        inventoryService.resetInventory();
    }
}
