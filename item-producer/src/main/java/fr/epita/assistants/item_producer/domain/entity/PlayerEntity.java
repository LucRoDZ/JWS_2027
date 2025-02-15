package fr.epita.assistants.item_producer.domain.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private int posX;
    @Setter
    private int posY;

    private LocalDateTime lastMove;
    private LocalDateTime lastCollect;

    private float moveSpeedMultiplier;
    private Float collectRateMultiplier;
    private Float staminaMultiplier;
}
