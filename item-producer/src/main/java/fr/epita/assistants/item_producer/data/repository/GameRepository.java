package fr.epita.assistants.item_producer.data.repository;

import fr.epita.assistants.item_producer.data.model.GameModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameRepository implements PanacheRepository<GameModel> {
}
