package fr.epita.assistants.item_producer.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "player")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer posX;
    private Integer posY;
    private LocalDateTime lastMove;
    private LocalDateTime lastCollect;
    private Float moveSpeedMultiplier;
    private Float collectRateMultiplier;
    private Float staminaMultiplier;

    public Integer getPosX() {
        return posX;
    }

    public void setPosX(Integer posX) {
        this.posX = posX;
    }

    public Integer getPosY() {
        return posY;
    }

    public void setPosY(Integer posY) {
        this.posX = posY;
    }
}
