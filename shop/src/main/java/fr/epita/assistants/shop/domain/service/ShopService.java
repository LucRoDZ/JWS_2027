package fr.epita.assistants.shop.domain.service;

import fr.epita.assistants.shop.data.model.ShopModel;
import fr.epita.assistants.shop.data.model.ItemModel;
import fr.epita.assistants.shop.data.repository.ShopRepository;
import fr.epita.assistants.shop.data.repository.ItemRepository;
import fr.epita.assistants.shop.domain.entity.ShopEntity;
import fr.epita.assistants.common.aggregate.SyncInventoryAggregate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;

    private float currentShopPrice = 17.5f;
    private static final float JWS_UPGRADE_MULTIPLIER = 1.15f;
    private static final int JWS_MAX_SHOP_QUANTITY = 8;

    @Transactional
    public void updateShopWithAggregate(fr.epita.assistants.common.aggregate.UpgradeShopAggregate aggregate) {
        Long shopId = aggregate.getShopId().longValue();
        ShopModel model = shopRepository.findById(shopId);
        if (model != null) {
            float upgradeMultiplier = 1.15f;
            model.setPriceMultiplier(model.getPriceMultiplier() * upgradeMultiplier);
            model.setUpgradePrice(aggregate.getNewMoney());
        } else {
            throw new IllegalArgumentException("Shop ID not found: " + shopId);
        }
    }

    public List<ShopEntity> getAllShops() {
        return shopRepository.listAll().stream()
                .map(model -> new ShopEntity(
                        model.getId(),
                        model.getPriceMultiplier(),
                        model.getUpgradePrice()
                ))
                .toList();
    }

    public float getCurrentShopPrice() {
        return currentShopPrice;
    }

    public boolean canCreateShop() {
        return shopRepository.count() < JWS_MAX_SHOP_QUANTITY;
    }

    @Transactional
    public void addShop(ShopEntity shop) {
        if (!canCreateShop()) {
            throw new IllegalStateException("Max shop limit reached.");
        }
        ShopModel model = new ShopModel(null, shop.getPriceMultiplier(), shop.getUpgradePrice());
        shopRepository.persist(model);
        updateShopPrice();
    }

    private void updateShopPrice() {
        currentShopPrice *= JWS_UPGRADE_MULTIPLIER;
    }

    @Transactional
    public void resetShopsAndCreateInitialShop(float shopPrice, float upgradePriceCost) {
        shopRepository.deleteAll();
        currentShopPrice = shopPrice;
        ShopModel model = new ShopModel(null, 1.0f, upgradePriceCost);
        shopRepository.persist(model);
    }

    public Optional<ShopEntity> getShopById(int id) {
        return shopRepository.findByIdOptional((long)id)
                .map(model -> new ShopEntity(
                        model.getId(),
                        model.getPriceMultiplier(),
                        model.getUpgradePrice()
                ));
    }

    @Transactional
    public void updateItemsFromAggregate(SyncInventoryAggregate aggregate) {
        aggregate.getItems().forEach(itemAggregate -> {
            String type = String.valueOf(itemAggregate.getType().getItemInfo().getValue());
            Optional<ItemModel> existing = itemRepository.find("type", type).firstResultOptional();
            if (existing.isPresent()) {
                ItemModel model = existing.get();
                model.setQuantity(itemAggregate.getQuantity());
            } else {
                ItemModel newModel = new ItemModel(null, type, itemAggregate.getQuantity());
                itemRepository.persist(newModel);
            }
        });
    }
}
