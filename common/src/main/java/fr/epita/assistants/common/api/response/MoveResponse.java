package fr.epita.assistants.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MoveResponse {
    private int posX;
    private int posY;
}
