package fr.epita.assistants.item_producer.presentation.subscriber;

import fr.epita.assistants.common.aggregate.ItemAggregate;
import fr.epita.assistants.item_producer.domain.service.ItemService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class ItemAggregateSubscriber {

    private static final Logger LOGGER = Logger.getLogger(ItemAggregateSubscriber.class);
    private final ItemService itemService;

    @Incoming("collect-item-aggregate")
    @Blocking
    public void onCollectItemAggregate(ItemAggregate aggregate) {
        LOGGER.info("Received ItemAggregate: " + aggregate);
        itemService.updateItemFromAggregate(aggregate);
    }
}
