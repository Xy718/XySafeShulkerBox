package xyz.xy718.safeshulker.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.TileEntity;
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

import org.spongepowered.api.world.Location;
import xyz.xy718.safeshulker.XySafeShulkerBoxPlugin;

/**
 * 事件类
 * 诶呀，迟来的注释
 * @author: Xy718
 * @create: 2020/9/6
 * @description:
 */
public class BoxEvent {

	public static final Logger LOGGER = XySafeShulkerBoxPlugin.LOGGER;
	
	public static Map<Integer, UUID> openedShulker=new HashMap<Integer, UUID>();
	
	@Listener(order = Order.EARLY,beforeModifications = true)
	public void onShulkerOpening(
			InteractInventoryEvent.Open event
			,@First Player player
			) {
		Integer sbhashCode=getShunkerOrNull(event);
		if(sbhashCode!=null) {
			openedShulker.put(
					sbhashCode
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
		if(event.getTargetBlock().getLocation().isPresent()){
			Location location = event.getTargetBlock().getLocation().get();
			if(location.getTileEntity().isPresent()){
				TileEntity te= (TileEntity) location.getTileEntity().get();
				LOGGER.info(String.valueOf(te));
				//判断是否是原生sb
				if(te.getType().equals(TileEntityTypes.SHULKER_BOX)) {
					//是原生的sb，那么保存了吗？
					if(openedShulker.containsKey(te.hashCode())) {
						player.sendMessage(Text.of("其他人在使用"));
						event.setCancelled(true);
					}
				}else if(te.getClass().getName().contains("cpw.mods.ironchest")){
					//哦吼，是更多箱子的sb哦
					if(te.getClass().getSimpleName().contains("ShulkerBox")){
						if(openedShulker.containsKey(te.hashCode())) {
							player.sendMessage(Text.of("其他人在使用"));
							event.setCancelled(true);
						}
					}
				}
			}
		}

	}

	Integer getShunkerOrNull(Event event){
		Integer retHashCode=null;
		//死亡姨夫
		if(event.getContext().get(EventContextKeys.BLOCK_HIT).isPresent()) {
			if(event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().isPresent()) {
				if(event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().get().getTileEntity().isPresent()) {
					TileEntity te=event.getContext().get(EventContextKeys.BLOCK_HIT).get()
							.getLocation().get()
							.getTileEntity().get();
					if(te.getType().equals(TileEntityTypes.SHULKER_BOX)) {
						retHashCode = event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().get().getTileEntity().get().hashCode();
					}else if(te.getClass().getName().contains("cpw.mods.ironchest")){
						//哦吼，是更多箱子的sb哦
						if(te.getClass().getSimpleName().contains("ShulkerBox")){
							retHashCode = event.getContext().get(EventContextKeys.BLOCK_HIT).get().getLocation().get().getTileEntity().get().hashCode();
						}
					}
				}
			}
		}
		return retHashCode;
	}
}
