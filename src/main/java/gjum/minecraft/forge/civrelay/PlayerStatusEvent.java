package gjum.minecraft.forge.civrelay;

import net.minecraft.util.math.Vec3i;

public class PlayerStatusEvent implements Event {

    private final String player;
    private final Action action;

    public PlayerStatusEvent(String player, Action action) {
        this.player = player;
        this.action = action;
    }

    @Override
    public Type getType() {
        return Type.PLAYER_STATUS;
    }

    @Override
    public String getAction() {
        return action.msg;
    }

    @Override
    public String getActionText() {
        return action == Action.LOGIN ? "logged in" : "logged out";
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public String getChatMessage() {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public String getSnitch() {
        return null;
    }

    @Override
    public String getWorld() {
        return null;
    }

    @Override
    public Vec3i getPos() {
        return null;
    }
}
