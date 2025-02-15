package fr.epita.assistants.item_producer.data.repository;

import fr.epita.assistants.item_producer.data.model.ItemModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemRepository implements PanacheRepository<ItemModel> {
}
