package fr.epita.assistants.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemsResponse {
    private List<ItemResponse> itemsResponse;
}
