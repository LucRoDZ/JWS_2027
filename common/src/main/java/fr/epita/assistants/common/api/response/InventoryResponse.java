package fr.epita.assistants.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private String itemType;
    private float quantity;
}
