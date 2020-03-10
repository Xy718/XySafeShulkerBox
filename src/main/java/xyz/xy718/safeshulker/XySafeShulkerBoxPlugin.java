package xyz.xy718.safeshulker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import lombok.Getter;
import xyz.xy718.safeshulker.event.BoxEvent;

@Plugin(
id = XySafeShulkerBoxPlugin.PLUGIN_ID
, name = XySafeShulkerBoxPlugin.NAME
, version = XySafeShulkerBoxPlugin.VERSION
, description = XySafeShulkerBoxPlugin.DESCRIPTION)
public class XySafeShulkerBoxPlugin {
	@Getter public static final String PLUGIN_ID = "xysafeshulkerbox";
	@Getter public static final String NAME = "@name@";
	@Getter public static final String VERSION = "@version@";
	@Getter public static final String DESCRIPTION = "@description@";
	
	private static XySafeShulkerBoxPlugin instance;
	
	public static final Logger LOGGER = LoggerFactory.getLogger("XySafeShulkerBox");
	
    public XySafeShulkerBoxPlugin() {
    	if (instance != null)
			throw new IllegalStateException();
		instance = this;
	}

    @Listener
    public void onGameStarting(GameInitializationEvent event) {
    	LOGGER.info("SafeShulkerBox开始注册事件~");
    	Sponge.getEventManager().registerListeners(this,new BoxEvent());
	}
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    	LOGGER.info("服务器启动成功，SafeShulkerBox也开始工作了~");
    }
    
	public static XySafeShulkerBoxPlugin get() {
		if (instance == null)
			throw new IllegalStateException("Instance not available");
		return instance;
	}
}
