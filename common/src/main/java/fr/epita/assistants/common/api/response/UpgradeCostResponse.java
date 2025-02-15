package fr.epita.assistants.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpgradeCostResponse {
    private float upgradeCollectCost;
    private float upgradeMoveCost;
    private float upgradeStaminaCost;
}
