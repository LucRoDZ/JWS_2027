package fr.epita.assistants.shop.data.repository;

import fr.epita.assistants.shop.data.model.ItemModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemRepository implements PanacheRepository<ItemModel> {
}
