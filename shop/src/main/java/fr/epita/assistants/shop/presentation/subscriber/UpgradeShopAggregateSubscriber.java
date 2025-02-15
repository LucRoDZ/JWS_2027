package fr.epita.assistants.shop.presentation.subscriber;

import fr.epita.assistants.common.aggregate.UpgradeShopAggregate;
import fr.epita.assistants.shop.domain.service.ShopService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class UpgradeShopAggregateSubscriber {

    private static final Logger LOGGER = Logger.getLogger(UpgradeShopAggregateSubscriber.class);
    private final ShopService shopService;

    @Incoming("upgrade-shop-price-aggregate")
    @Blocking
    public void onUpgradeShopAggregate(UpgradeShopAggregate aggregate) {
        LOGGER.info("Received UpgradeShopAggregate: " + aggregate);
        shopService.updateShopWithAggregate(aggregate);
    }
}
