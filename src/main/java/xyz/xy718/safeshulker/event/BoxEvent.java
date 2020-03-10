package xyz.xy718.safeshulker.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.block.tileentity.carrier.ShulkerBox;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.text.Text;

import xyz.xy718.safeshulker.XySafeShulkerBoxPlugin;

public class BoxEvent {

	public static final Logger LOGGER = XySafeShulkerBoxPlugin.LOGGER;
	
	public static Map<Integer, UUID> openedShulker=new HashMap<Integer, UUID>();
	
	@Listener(order = Order.EARLY,beforeModifications = true)
	public void onShulkerOpening(
			InteractInventoryEvent.Open event
			,@First Player player
			) {
		ShulkerBox sb=getShunkerOrNull(event);
		if(sb!=null) {
			openedShulker.put(
					sb.hashCode()
					,player.getUniqueId()
					);
		}
	}
	@Listener(order = Order.EARLY,beforeModifications = true)
	public void onShulkerClosing(
			InteractInventoryEvent.Close event
			,@First Player player
			) {
		if(openedShulker.containsValue(player.getUniqueId())) {
			for( Entry<Integer, UUID> a:openedShulker.entrySet()) {
				if(a.getValue().equals(player.getUniqueId())) {
					openedShulker.remove(a.getKey());
					return;
				}
			}
		}
	}
	
	@Listener(order = Order.EARLY,beforeModifications = true)
	public void onShulkerBreak(
			InteractBlockEvent event
			,@First Player player
			) {
		event.getTargetBlock().getLocation().ifPresent( location ->{
			location.getTileEntity().ifPresent( te ->{
				if(te.getType().equals(TileEntityTypes.SHULKER_BOX)) {
					if(openedShulker.containsKey(((ShulkerBox)te).hashCode())) {
						player.sendMessage(Text.of("其他人在使用"));
						event.setCancelled(true);
					}
				}
			} );
		});

	}
	
	ShulkerBox getShunkerOrNull(Event event){
		ShulkerBox retS=null;
		if(event.getContext().get(EventContextKeys.BLOCK_HIT).isPresent()) {
			if(event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().isPresent()) {
				if(event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().get().getTileEntity().isPresent()) {
					if(event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().get().getTileEntity().get().getType().equals(TileEntityTypes.SHULKER_BOX)) {
						retS = (ShulkerBox) event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().get().getTileEntity().get();
					}
				}
			}
		}
		return retS;
	}
}
