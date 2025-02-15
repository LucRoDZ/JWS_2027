package fr.epita.assistants.shop.data.repository;

import fr.epita.assistants.shop.data.model.ShopModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShopRepository implements PanacheRepository<ShopModel> {
}
