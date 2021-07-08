package hasjamon.block4block.listener;

import hasjamon.block4block.Block4Block;
import hasjamon.block4block.utils.utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Set;

public class PlayerJoin implements Listener {
    private final Block4Block plugin;

    public PlayerJoin(Block4Block plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        List<String> welcomeMessages = plugin.getConfig().getStringList("welcome-messages");

        if(!p.hasPlayedBefore())
            for(String msg : welcomeMessages)
                p.sendMessage(utils.chat(msg));
        else
            utils.populatePlayerClaimsIntruded(p);

        utils.updateClaimCount();

        String chunkID = utils.getChunkID(p.getLocation());
        if(utils.isIntruder(p, chunkID))
            utils.onIntruderEnterClaim(p, chunkID);


        String pName = p.getName();
        FileConfiguration offlineClaimNotifications = plugin.cfg.getOfflineClaimNotifications();
        ConfigurationSection chunksLost = offlineClaimNotifications.getConfigurationSection(pName + ".chunks");
        ConfigurationSection masterBooksRemovedFrom = offlineClaimNotifications.getConfigurationSection(pName + ".masterbooks");

        if(chunksLost != null){
            Set<String> chunkIDs = chunksLost.getKeys(false);
            int i = 0;

            for(String cID : chunkIDs){
                if(++i >= 10 && chunkIDs.size() > 10){
                    p.sendMessage(ChatColor.RED + "... and " + (chunkIDs.size() - 9) + " other claims");
                    break;
                }

                String xyz = chunksLost.getString(cID);
                p.sendMessage(ChatColor.RED + "You have lost a claim! Location: " + xyz);
            }
        }

        if(masterBooksRemovedFrom != null){
            for(String mbID : masterBooksRemovedFrom.getKeys(false))
                if(masterBooksRemovedFrom.getBoolean(mbID))
                    p.sendMessage(ChatColor.RED + "Your name has been removed from Master Book #" + mbID + " and all related claims!");
        }

        offlineClaimNotifications.set(pName, null);
        plugin.cfg.saveOfflineClaimNotifications();

        utils.originalPlayerTextures.put(p, utils.getTextures(p));
    }
}
