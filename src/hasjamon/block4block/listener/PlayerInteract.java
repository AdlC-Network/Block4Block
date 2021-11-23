package hasjamon.block4block.listener;

import hasjamon.block4block.utils.utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e){
        Block b = e.getClickedBlock();

        if(isShovel(e.getMaterial()) && b != null){
            if(b.getType() == Material.DIRT_PATH) {
                utils.removeExpiredBlockChangeGracePeriods();

                // If the block is still covered by the grace period, change it back to grass_block
                if (utils.blockChangeGracePeriods.containsKey(b.getLocation())) {
                    b.setType(Material.GRASS_BLOCK);
                    e.setCancelled(true);
                }
            }else if(b.getType() == Material.GRASS_BLOCK){
                utils.blockChangeGracePeriods.put(b.getLocation(), System.nanoTime());
            }
        }
    }

    private boolean isShovel(Material mat){
        switch (mat){
            case NETHERITE_SHOVEL, DIAMOND_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, STONE_SHOVEL, WOODEN_SHOVEL -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
