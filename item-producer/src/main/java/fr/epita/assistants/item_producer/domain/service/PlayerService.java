package fr.epita.assistants.item_producer.domain.service;

import fr.epita.assistants.item_producer.data.model.PlayerModel;
import fr.epita.assistants.item_producer.data.repository.PlayerRepository;
import fr.epita.assistants.item_producer.domain.entity.PlayerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public void resetPlayerDatabase() {
        playerRepository.deleteAll();
    }

    @Transactional
    public void createPlayer() {
        PlayerModel player = new PlayerModel();
        player.setPosX(0);
        player.setPosY(0);
        player.setLastMove(null);
        player.setLastCollect(null);
        player.setMoveSpeedMultiplier(1.0f);
        player.setCollectRateMultiplier(1.0f);
        player.setStaminaMultiplier(1.0f);
        playerRepository.persist(player);
    }

    public PlayerEntity getPlayer() {
        Optional<PlayerModel> opt = playerRepository.findAll().stream().findFirst();
        if (opt.isPresent()) {
            PlayerModel model = opt.get();
            return new PlayerEntity(model.getId(), model.getPosX(), model.getPosY(), model.getLastMove(), model.getLastCollect(),
                    model.getMoveSpeedMultiplier(), model.getCollectRateMultiplier(), model.getStaminaMultiplier());
        }
        return null;
    }

    @Transactional
    public void updatePlayer(PlayerEntity playerEntity) {
        PlayerModel model = playerRepository.findById(playerEntity.getId());
        if (model != null) {
            model.setPosX(playerEntity.getPosX());
            model.setPosY(playerEntity.getPosY());
        }
    }
}
