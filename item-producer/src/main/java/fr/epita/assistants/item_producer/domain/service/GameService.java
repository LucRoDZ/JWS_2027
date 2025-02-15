package fr.epita.assistants.item_producer.domain.service;

import fr.epita.assistants.common.aggregate.ItemAggregate;
import fr.epita.assistants.item_producer.data.model.GameModel;
import fr.epita.assistants.item_producer.data.repository.GameRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    @Transactional
    public void resetGameDatabase() {
        gameRepository.deleteAll();
    }

    @Transactional
    public void createGame(String map) {
        GameModel game = new GameModel(null, map);
        gameRepository.persist(game);
    }

    public String getGameMap() {
        return gameRepository.findAll().stream()
                .findFirst()
                .map(GameModel::getMap)
                .orElse(""); // Return an empty string if no map is found
    }

    public List<List<String>> decodeRLE(String rle) {
        List<List<String>> decodedRows = new ArrayList<>();

        // Split the map into rows (assuming `;` is the delimiter)
        String[] rows = rle.split(";");

        for (String row : rows) {
            List<String> decodedRow = new ArrayList<>();
            int i = 0;

            while (i < row.length()) {
                // Get the repeat count (number)
                char countChar = row.charAt(i);
                if (!Character.isDigit(countChar)) {
                    throw new IllegalArgumentException("Invalid RLE encoding: expected digit at index " + i);
                }
                int count = Character.getNumericValue(countChar);
                i++;

                // Get the tile character
                if (i >= row.length()) {
                    throw new IllegalArgumentException("Invalid RLE encoding: missing tile character at index " + i);
                }
                char tileChar = row.charAt(i);
                i++;

                // Convert tile character to readable type
                String tileType = mapTile(tileChar);

                // Add the tile to the row, repeated `count` times
                for (int j = 0; j < count; j++) {
                    decodedRow.add(tileType);
                }
            }
            decodedRows.add(decodedRow);
        }

        return decodedRows;
    }

    private String mapTile(char tile) {
        return switch (tile) {
            case 'R' -> "ROCK";
            case 'W' -> "WOOD";
            case 'G' -> "GROUND";
            case 'O' -> "WATER";
            case 'M' -> "MONEY";
            default -> throw new IllegalArgumentException("Unknown tile type: " + tile);
        };
    }

    public String getTileAtPosition(int x, int y) {
        List<List<String>> decodedMap = decodeRLE(getGameMap());

        if (y < 0 || y >= decodedMap.size() || x < 0 || x >= decodedMap.get(y).size()) {
            return "INVALID";
        }

        return decodedMap.get(y).get(x);
    }

    public boolean isWalkable(String tileType) {
        return switch (tileType) {
            case "GROUND", "WOOD", "ROCK" -> true;
            case "WATER", "MONEY" -> false;
            default -> false;
        };
    }

}
