package fr.epita.assistants.common.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryRequest {
    private String itemType;
    private float quantity;
}
