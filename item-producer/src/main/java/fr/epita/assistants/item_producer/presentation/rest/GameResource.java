package fr.epita.assistants.item_producer.presentation.rest;

import fr.epita.assistants.common.api.request.MoveRequest;
import fr.epita.assistants.common.api.response.ItemResponse;
import fr.epita.assistants.common.api.response.ItemsResponse;
import fr.epita.assistants.common.api.response.MoveResponse;
import fr.epita.assistants.common.api.response.PlayerResponse;
import fr.epita.assistants.common.api.response.StartResponse;
import fr.epita.assistants.common.api.response.UpgradeCostResponse;
import fr.epita.assistants.common.command.CollectItemCommand;
import fr.epita.assistants.common.command.UpgradeCollectRateCommand;
import fr.epita.assistants.common.command.UpgradeMovementSpeedCommand;
import fr.epita.assistants.common.command.UpgradeStaminaCommand;
import fr.epita.assistants.common.utils.ErrorInfo;
import fr.epita.assistants.item_producer.converter.ItemConverter;
import fr.epita.assistants.item_producer.domain.entity.ItemEntity;
import fr.epita.assistants.item_producer.domain.entity.PlayerEntity;
import fr.epita.assistants.item_producer.domain.service.GameService;
import fr.epita.assistants.item_producer.domain.service.ItemService;
import fr.epita.assistants.item_producer.domain.service.PlayerService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameResource {

    private static final Logger LOGGER = Logger.getLogger(GameResource.class);

    private final ItemService itemService;
    private final PlayerService playerService;
    private final GameService gameService;

    @Channel("collect-item-command")
    Emitter<CollectItemCommand> collectEmitter;

    @Channel("upgrade-collect-rate-command")
    Emitter<UpgradeCollectRateCommand> upgradeCollectEmitter;

    @Channel("upgrade-movement-speed-command")
    Emitter<UpgradeMovementSpeedCommand> upgradeMoveEmitter;

    @Channel("upgrade-stamina-command")
    Emitter<UpgradeStaminaCommand> upgradeStaminaEmitter;

    @ConfigProperty(name = "JWS_UPGRADE_COLLECT_COST", defaultValue = "50.0")
    Float upgradeCollectCost;

    @ConfigProperty(name = "JWS_UPGRADE_MOVE_COST", defaultValue = "27.5")
    Float upgradeMoveCost;

    @ConfigProperty(name = "JWS_UPGRADE_STAMINA_COST", defaultValue = "32.25")
    Float upgradeStaminaCost;

    @ConfigProperty(name = "JWS_TICK_DURATION", defaultValue = "100")
    Long tickDuration;

    @ConfigProperty(name = "JWS_DELAY_MOVEMENT", defaultValue = "2")
    Long delayMovement;

    @GET
    public Response getInventory() {
        List<ItemEntity> items = itemService.getAllItems();
        List<ItemResponse> responses = items.stream()
                .map(ItemConverter::entityToResponse)
                .collect(Collectors.toList());
        ItemsResponse itemsResponse = new ItemsResponse(responses);
        return Response.ok(itemsResponse).build();
    }

    @POST
    @Path("/collect")
    public Response collectResource() {
        CollectItemCommand command = new CollectItemCommand(
                fr.epita.assistants.common.aggregate.ItemAggregate.ResourceType.ROCK,
                1.0f
        );
        collectEmitter.send(command);
        List<List<String>> dummyMap = List.of(
                List.of("GROUND", "WOOD"),
                List.of("ROCK", "MONEY")
        );
        StartResponse response = new StartResponse(dummyMap);
        return Response.ok(response).build();
    }

    @POST
    @Path("/move")
    public Response movePlayer(MoveRequest moveRequest) {
        PlayerEntity player = playerService.getPlayer();

        if (player == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Invalid direction or the game is not running."))
                    .build();
        }

        long requiredDelay = (long) ((tickDuration * delayMovement) / player.getMoveSpeedMultiplier());
        LocalDateTime now = LocalDateTime.now();

        if (player.getLastMove() != null) {
            long elapsed = Duration.between(player.getLastMove(), now).toMillis();
            if (elapsed < requiredDelay) {
                return Response.status(429)
                        .entity(new ErrorInfo("Player has recently moved and must wait before moving again"))
                        .build();
            }
        }

        int newX = player.getPosX();
        int newY = player.getPosY();
        String direction = moveRequest.getDirection().name();

        switch (direction) {
            case "UP":
                newY -= 1;
                break;
            case "DOWN":
                newY += 1;
                break;
            case "LEFT":
                newX -= 1;
                break;
            case "RIGHT":
                newX += 1;
                break;
            default:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorInfo("Invalid direction."))
                        .build();
        }

        String tileType = gameService.getTileAtPosition(newX, newY);
        if (!gameService.isWalkable(tileType)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Cannot move there: " + tileType + " is not walkable."))
                    .build();
        }

        player.setPosX(newX);
        player.setPosY(newY);
        player.setLastMove(now);
        playerService.updatePlayer(player);

        return Response.ok(new MoveResponse(newX, newY)).build();
    }


    @GET
    @Path("/player")
    public Response getPlayerInfo() {
        PlayerEntity player = playerService.getPlayer();
        if (player == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Game is not running"))
                    .build();
        }
        PlayerResponse response = new PlayerResponse(player.getId(), player.getPosX(), player.getPosY());
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/upgrade/collect")
    public Response upgradeCollect() {
        UpgradeCollectRateCommand command = new UpgradeCollectRateCommand(upgradeCollectCost);
        upgradeCollectEmitter.send(command);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/upgrade/move")
    public Response upgradeMove() {
        UpgradeMovementSpeedCommand command = new UpgradeMovementSpeedCommand(upgradeMoveCost);
        upgradeMoveEmitter.send(command);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/upgrade/stamina")
    public Response upgradeStamina() {
        UpgradeStaminaCommand command = new UpgradeStaminaCommand(upgradeStaminaCost);
        upgradeStaminaEmitter.send(command);
        return Response.noContent().build();
    }

    @GET
    @Path("/upgrades")
    public Response getUpgradeCosts() {
        UpgradeCostResponse response = new UpgradeCostResponse(upgradeCollectCost, upgradeMoveCost, upgradeStaminaCost);
        return Response.ok(response).build();
    }
}
