package gjum.minecraft.forge.civrelay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.lwjgl.input.Keyboard.KEY_NONE;

@Mod(
        modid = CivRelayMod.MOD_ID,
        name = CivRelayMod.MOD_NAME,
        version = CivRelayMod.VERSION,
        guiFactory = "gjum.minecraft.forge.civrelay.GuiFactory",
        clientSideOnly = true)
public class CivRelayMod {
    public static final String MOD_ID = "civrelay";
    public static final String MOD_NAME = "CivRelay";
    public static final String VERSION = "@VERSION@";
    public static final String BUILD_TIME = "@BUILD_TIME@";

    @Mod.Instance(MOD_ID)
    public static CivRelayMod instance;

    public static Logger logger;
    private static final Minecraft mc = Minecraft.getMinecraft();

    private long inhibitStacktraceUntil = 0;
    private Collection<String> previousPlayerList = Collections.EMPTY_LIST;

    private final KeyBinding toggleEnabledKey = new KeyBinding(MOD_ID + ".key.toggleEnabled", KEY_NONE, MOD_NAME);
    private final KeyBinding openMenuKey = new KeyBinding(MOD_ID + ".key.openGui", KEY_NONE, MOD_NAME);

    private long nextPlayerListScan = 0;
    private DiscordWebhookThread discordWebhookThread;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        final File configFile = new File(event.getSuggestedConfigurationFile()
                .getAbsolutePath().replaceAll("\\.[^.]+$", ".json"));
        Config.instance.load(configFile);
        Config.instance.save(configFile);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info(String.format("%s version %s built at %s", MOD_NAME, VERSION, BUILD_TIME));

        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(toggleEnabledKey);
        ClientRegistry.registerKeyBinding(openMenuKey);

        discordWebhookThread = new DiscordWebhookThread();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        try {
            if (event.phase != TickEvent.Phase.START) return;
            if (!Config.instance.modEnabled) return;
            if (nextPlayerListScan > System.currentTimeMillis()) return;
            nextPlayerListScan = System.currentTimeMillis() + 1000;
            if (mc.world == null) return;
            final NetHandlerPlayClient connection = mc.getConnection();
            if (connection == null) return;

            ArrayList<String> playerList = new ArrayList<>();
            Collection<NetworkPlayerInfo> players = connection.getPlayerInfoMap();
            for (Object p : players) {
                if ((p instanceof NetworkPlayerInfo)) {
                    NetworkPlayerInfo info = (NetworkPlayerInfo) p;
                    playerList.add(TextFormatting.getTextWithoutFormattingCodes(info.getGameProfile().getName()));
                }
            }
            ArrayList<String> temp = new ArrayList<>(playerList);
            playerList.removeAll(previousPlayerList);
            previousPlayerList.removeAll(temp);

            for (String player : previousPlayerList) {
                emitEvent(new PlayerStatusEvent(player, Event.Action.LOGOUT));
            }
            for (String player : playerList) {
                emitEvent(new PlayerStatusEvent(player, Event.Action.LOGIN));
            }

            previousPlayerList = temp;
        } catch (Exception e) {
            if (inhibitStacktraceUntil < System.currentTimeMillis()) {
                inhibitStacktraceUntil = System.currentTimeMillis() + 5000;
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        try {
            if (!Config.instance.modEnabled) return;

            final SnitchEvent snitchEvent = SnitchEvent.fromChat(event.getMessage());
            if (snitchEvent != null) {
                emitEvent(snitchEvent);
                return;
            }

            final ChatEvent chatEvent = ChatEvent.fromChat(event.getMessage());
            if (chatEvent != null) {
                emitEvent(chatEvent);
                return;
            }
        } catch (Exception e) {
            if (inhibitStacktraceUntil < System.currentTimeMillis()) {
                inhibitStacktraceUntil = System.currentTimeMillis() + 5000;
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        try {
            if (toggleEnabledKey.isPressed()) {
                Config.instance.modEnabled = !Config.instance.modEnabled;
            }
            if (openMenuKey.isPressed()) {
                mc.displayGuiScreen(new SettingsGui(null));
            }
        } catch (Exception e) {
            if (inhibitStacktraceUntil < System.currentTimeMillis()) {
                inhibitStacktraceUntil = System.currentTimeMillis() + 5000;
                e.printStackTrace();
            }
        }
    }

    private void emitEvent(Event event) {
        for (Filter filter : Config.instance.filters) {
            if (filter.test(event, getCurrentGameAddress())) {
                discordWebhookThread.pushJsonTo(filter.formatEvent(event), filter.getWebhookAddress());
            }
        }
    }

    public static String getCurrentGameAddress() {
        try {
            if (mc.player.connection.getNetworkManager().isLocalChannel()) {
                return "<singleplayer>";
            }
            return mc.getCurrentServerData().serverIP;
        } catch (Exception e) {
            return "?";
        }
    }
}
