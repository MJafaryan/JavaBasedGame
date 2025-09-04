package my.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import models.buildings.Building;

public class BuildingInputHandler {
    private GameScreen gameScreen;

    public BuildingInputHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void handleGameInput(int screenX, int screenY, int button) {
        if (gameScreen.isClickOnUI(screenX, screenY)) return;

        Vector3 screenPos = new Vector3(screenX, screenY, 0);
        Vector3 worldPos = gameScreen.getCamera().unproject(screenPos.cpy());

        float roundedX = (int)(worldPos.x / 64) * 64;
        float roundedY = (int)(worldPos.y / 64) * 64;

        if (button == Input.Buttons.LEFT) {
            Building clickedBuilding = gameScreen.getBuildingAt(roundedX, roundedY);
            if (clickedBuilding != null) {
                gameScreen.setSelectedBuilding(clickedBuilding);
                gameScreen.setSelectedBuildingType(null);
                gameScreen.getBuildingInfoPanel().updateBuildingInfoPanel();
                gameScreen.addMessage("Selected: " + clickedBuilding.getType() + " (Level " + clickedBuilding.getLevel() + ")");
                return;
            } else {
                gameScreen.setSelectedBuilding(null);
                gameScreen.getBuildingInfoPanel().setBuildingInfoPanelVisible(false);
            }
        }

        if (gameScreen.getSelectedBuildingType() != null && button == Input.Buttons.LEFT) {
            gameScreen.placeBuilding(roundedX, roundedY);
        } else if (button == Input.Buttons.RIGHT) {
            gameScreen.setSelectedBuildingType(null);
            gameScreen.setSelectedBuilding(null);
            gameScreen.getBuildingInfoPanel().setBuildingInfoPanelVisible(false);
            gameScreen.addMessage("Selection cancelled");
        }

        if (!gameScreen.isTownHallPlaced() && gameScreen.getSelectedBuildingType() == null) {
            gameScreen.setSelectedBuildingType("townHall");
            gameScreen.placeBuilding(roundedX, roundedY);
        }
    }
}
