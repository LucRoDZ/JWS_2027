package fr.epita.assistants.item_producer.presentation.rest;

import fr.epita.assistants.common.api.request.StartRequest;
import fr.epita.assistants.common.api.response.StartResponse;
import fr.epita.assistants.common.command.ResetInventoryCommand;
import fr.epita.assistants.common.utils.ErrorInfo;
import fr.epita.assistants.item_producer.domain.service.GameService;
import fr.epita.assistants.item_producer.domain.service.PlayerService;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/start")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StartResource {

    private static final Logger LOGGER = Logger.getLogger(StartResource.class);

    private final GameService gameService;
    private final PlayerService playerService;

    @Channel("reset-inventory-command")
    Emitter<ResetInventoryCommand> inventoryEmitter;

    @POST
    public Response startGame(StartRequest startRequest) {
        try {
            String mapPath = startRequest.getMapPath();

            if (mapPath == null || mapPath.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorInfo("Invalid path provided."))
                        .build();
            }

            String rleMap = processMapFile(mapPath);

            gameService.resetGameDatabase();
            playerService.resetPlayerDatabase();
            gameService.createGame(rleMap);
            playerService.createPlayer();
            inventoryEmitter.send(new ResetInventoryCommand());

            LOGGER.info("The game started successfully.");

            List<List<String>> decodedMap = decodeRLE(rleMap);
            return Response.ok(new StartResponse(decodedMap)).build();
        } catch (Exception e) {
            LOGGER.error("Error starting game", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Invalid path provided."))
                    .build();
        }
    }

    private String processMapFile(String mapPathStr) throws IOException {
        java.nio.file.Path mapPath = java.nio.file.Path.of(mapPathStr);
        if (!Files.exists(mapPath)) {
            throw new IOException();
        }
        return Files.readAllLines(mapPath).stream().collect(Collectors.joining(";"));
    }

    private List<List<String>> decodeRLE(String rle) {
        String[] rows = rle.split(";");
        List<List<String>> decodedRows = new ArrayList<>();
        for (String row : rows) {
            List<String> decodedRow = new ArrayList<>();
            int i = 0;
            while (i < row.length()) {
                char countChar = row.charAt(i);
                if (!Character.isDigit(countChar)) {
                    throw new IllegalArgumentException();
                }
                int count = Character.getNumericValue(countChar);
                i++;
                if (i >= row.length()) {
                    throw new IllegalArgumentException();
                }
                char tileChar = row.charAt(i);
                i++;
                String tileType = mapTile(tileChar);
                for (int j = 0; j < count; j++) {
                    decodedRow.add(tileType);
                }
            }
            decodedRows.add(decodedRow);
        }
        return decodedRows;
    }

    private String mapTile(char tile) {
        switch (tile) {
            case 'R': return "ROCK";
            case 'W': return "WOOD";
            case 'G': return "GROUND";
            case 'O': return "WATER";
            case 'M': return "MONEY";
            default:
                throw new IllegalArgumentException();
        }
    }
}
