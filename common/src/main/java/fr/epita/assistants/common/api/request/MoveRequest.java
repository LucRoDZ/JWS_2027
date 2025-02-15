package fr.epita.assistants.common.api.request;

import fr.epita.assistants.common.utils.Direction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoveRequest {
    private Direction direction;
}
