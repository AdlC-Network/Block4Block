package hasjamon.block4block.listener;

import hasjamon.block4block.Block4Block;
import hasjamon.block4block.utils.utils;
import net.minecraft.server.v1_16_R3.BlockPosition;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import java.util.List;


public class BlockBreak implements Listener {
    private long andesiteLatestBreak = 0;

    // This Class is for the block break event (This runs every time a player breaks a block)
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        FileConfiguration cfg = Block4Block.getInstance().getConfig();

        // Lecterns are exempt from B4B rules. Changing this would require refactoring of LecternBreak's onBreak.
        if (b.getType() == Material.LECTERN) return;
        if (p.getGameMode() == GameMode.CREATIVE) return;

        if (Block4Block.getInstance().cfg.getClaimData().contains(utils.getChunkID(b.getChunk()))) { //if claimed
            if (!utils.isClaimBlock(b)) {
                String[] members = utils.getMembers(b.getChunk());
                List<?> claimBlacklist = cfg.getList("blacklisted-claim-blocks");

                // If the player is a member of the claim and the block is claim-blacklisted: Don't apply B4B rules
                if (members != null && claimBlacklist != null) {
                    for (String member : members)
                        if (member.equalsIgnoreCase(p.getName()))
                            if (claimBlacklist.contains(b.getType().toString()))
                                return;

                    // If the chunk is claimed, you're not a member, and 'can-break-in-others-claims' isn't on
                    if (!cfg.getBoolean("can-break-in-others-claims")) {
                        // Cancel BlockBreakEvent, i.e., prevent block from breaking
                        e.setCancelled(true);
                        p.sendMessage(utils.chat("&cYou cannot break blocks in this claim"));
                        return;
                    }
                }
            }
        }

        if(b.getType() == Material.ANDESITE) {
            // Add splash if it's been at least 0.1 second since the last time andesite was broken (to avoid chain reaction)
            if(System.nanoTime() - andesiteLatestBreak > 1E8) {
                andesiteLatestBreak = System.nanoTime();
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (b.getRelative(x, y, z).getType() == Material.ANDESITE) {
                                BlockPosition pos = new BlockPosition(b.getX() + x, b.getY() + y, b.getZ() + z);
                                ((CraftPlayer) p).getHandle().playerInteractManager.breakBlock(pos);
                            }
                        }
                    }
                }
            }
            return;
        }

        List<?> blacklistedBlocks = cfg.getList("blacklisted-blocks");
        List<?> lootDisabled = cfg.getList("no-loot-on-break");

        if(blacklistedBlocks != null && lootDisabled != null) {
            // Does Block4Block apply, i.e., has the block type not been exempted from Block4Block through the blacklist
            boolean requiresBlock = !blacklistedBlocks.contains(b.getType().toString());
            // Are drops disabled for this block type
            boolean noloot = lootDisabled.contains(b.getType().toString());
            utils.b4bCheck(p, b, e, noloot, requiresBlock);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        if (Block4Block.getInstance().cfg.getClaimData().contains(utils.getChunkID(b.getChunk()))) {
            String[] members = utils.getMembers(b.getChunk());
            if (members != null) {
                for (String member : members)
                    if (member.equalsIgnoreCase(p.getName()))
                        if (b.getType() == Material.LAVA || b.getType() == Material.WATER)
                            return;

                p.sendMessage(utils.chat("&cYou cannot take Lava/Water inside this claim"));
                e.setCancelled(true);
            }
        }
    }
}
