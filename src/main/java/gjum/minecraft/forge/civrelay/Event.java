package gjum.minecraft.forge.civrelay;

public interface Event {
    enum Type {
        SNITCH("Snitch"),
        CHAT("Chat"),
        PLAYER_STATUS("Login/out");

        public final String description;

        Type(String description) {
            this.description = description;
        }
    }

    enum Action {
        ENTER("Enter"), LOGIN("Login"), LOGOUT("Logout"), UNKNOWN("UNKNOWN");

        public final String msg;

        Action(String msg) {
            this.msg = msg;
        }
    }

    Type getType();

    String getAction();

    String getActionText();

    String getChatMessage();

    String getGroup();

    String getPlayer();

    String getSnitch();

    String getWorld();

    int getX();

    int getY();

    int getZ();
}
