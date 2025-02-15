package fr.epita.assistants.inventory.domain.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEntity {
    private Long id;
    private String type;
    private float quantity;
}
