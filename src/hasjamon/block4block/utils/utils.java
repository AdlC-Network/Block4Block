package hasjamon.block4block.utils;

import hasjamon.block4block.Block4Block;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Consumer;

public class utils {
    private static final Block4Block plugin = Block4Block.getInstance();
    private static final Map<Material, Material> specialTypes = new HashMap<>();
    public static final Map<Block, Pair<Long, String>> b4bGracePeriods = new LinkedHashMap<>();
    public static final Map<String, Set<Player>> intruders = new HashMap<>();
    public static final Map<IronGolem, String> ironGolems = new HashMap<>();
    public static final Map<Player, Set<String>> playerClaimsIntruded = new HashMap<>();
    public static final Map<Player, Long> lastIntrusionMsgReceived = new HashMap<>();
    public static int minSecBetweenAlerts;
    private static boolean masterBookChangeMsgSent = false;

    static {
        specialTypes.put(Material.REDSTONE_WIRE, Material.REDSTONE);
        specialTypes.put(Material.WALL_TORCH, Material.TORCH);
        specialTypes.put(Material.REDSTONE_WALL_TORCH, Material.REDSTONE_TORCH);
        specialTypes.put(Material.TALL_SEAGRASS, Material.SEAGRASS);
        specialTypes.put(Material.CHORUS_PLANT, Material.CHORUS_FRUIT);
        specialTypes.put(Material.KELP_PLANT, Material.KELP);
        specialTypes.put(Material.BAMBOO_SAPLING, Material.BAMBOO);
        specialTypes.put(Material.WARPED_WALL_SIGN, Material.WARPED_SIGN);
        specialTypes.put(Material.ACACIA_WALL_SIGN, Material.ACACIA_SIGN);
        specialTypes.put(Material.BIRCH_WALL_SIGN, Material.BIRCH_SIGN);
        specialTypes.put(Material.CRIMSON_WALL_SIGN, Material.CRIMSON_SIGN);
        specialTypes.put(Material.JUNGLE_WALL_SIGN, Material.JUNGLE_SIGN);
        specialTypes.put(Material.DARK_OAK_WALL_SIGN, Material.DARK_OAK_SIGN);
        specialTypes.put(Material.OAK_WALL_SIGN, Material.OAK_SIGN);
        specialTypes.put(Material.SPRUCE_WALL_SIGN, Material.SPRUCE_SIGN);
        specialTypes.put(Material.SKELETON_WALL_SKULL, Material.SKELETON_SKULL);
        specialTypes.put(Material.WITHER_SKELETON_WALL_SKULL, Material.WITHER_SKELETON_SKULL);
        specialTypes.put(Material.ZOMBIE_WALL_HEAD, Material.ZOMBIE_HEAD);
        specialTypes.put(Material.PLAYER_WALL_HEAD, Material.PLAYER_HEAD);
        specialTypes.put(Material.CREEPER_WALL_HEAD, Material.CREEPER_HEAD);
        specialTypes.put(Material.DRAGON_WALL_HEAD, Material.DRAGON_HEAD);
        specialTypes.put(Material.BRAIN_CORAL_WALL_FAN, Material.BRAIN_CORAL_FAN);
        specialTypes.put(Material.BUBBLE_CORAL_WALL_FAN, Material.BUBBLE_CORAL_FAN);
        specialTypes.put(Material.DEAD_BRAIN_CORAL_WALL_FAN, Material.DEAD_BRAIN_CORAL_FAN);
        specialTypes.put(Material.DEAD_BUBBLE_CORAL_WALL_FAN, Material.DEAD_BUBBLE_CORAL_FAN);
        specialTypes.put(Material.DEAD_FIRE_CORAL_WALL_FAN, Material.DEAD_FIRE_CORAL_FAN);
        specialTypes.put(Material.DEAD_HORN_CORAL_WALL_FAN, Material.DEAD_HORN_CORAL_FAN);
        specialTypes.put(Material.DEAD_TUBE_CORAL_WALL_FAN, Material.DEAD_TUBE_CORAL_FAN);
        specialTypes.put(Material.FIRE_CORAL_WALL_FAN, Material.FIRE_CORAL_FAN);
        specialTypes.put(Material.HORN_CORAL_WALL_FAN, Material.HORN_CORAL_FAN);
        specialTypes.put(Material.TUBE_CORAL_WALL_FAN, Material.TUBE_CORAL_FAN);
        specialTypes.put(Material.BLACK_WALL_BANNER, Material.BLACK_BANNER);
        specialTypes.put(Material.BLUE_WALL_BANNER, Material.BLUE_BANNER);
        specialTypes.put(Material.BROWN_WALL_BANNER, Material.BROWN_BANNER);
        specialTypes.put(Material.CYAN_WALL_BANNER, Material.CYAN_BANNER);
        specialTypes.put(Material.GRAY_WALL_BANNER, Material.GRAY_BANNER);
        specialTypes.put(Material.GREEN_WALL_BANNER, Material.GREEN_BANNER);
        specialTypes.put(Material.LIGHT_BLUE_WALL_BANNER, Material.LIGHT_BLUE_BANNER);
        specialTypes.put(Material.LIGHT_GRAY_WALL_BANNER, Material.LIGHT_GRAY_BANNER);
        specialTypes.put(Material.LIME_WALL_BANNER, Material.LIME_BANNER);
        specialTypes.put(Material.MAGENTA_WALL_BANNER, Material.MAGENTA_BANNER);
        specialTypes.put(Material.ORANGE_WALL_BANNER, Material.ORANGE_BANNER);
        specialTypes.put(Material.PINK_WALL_BANNER, Material.PINK_BANNER);
        specialTypes.put(Material.PURPLE_WALL_BANNER, Material.PURPLE_BANNER);
        specialTypes.put(Material.RED_WALL_BANNER, Material.RED_BANNER);
        specialTypes.put(Material.WHITE_WALL_BANNER, Material.WHITE_BANNER);
        specialTypes.put(Material.YELLOW_WALL_BANNER, Material.YELLOW_BANNER);
        specialTypes.put(Material.TRAPPED_CHEST, Material.CHEST);
        specialTypes.put(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES);
        specialTypes.put(Material.INFESTED_STONE, Material.STONE);
        specialTypes.put(Material.INFESTED_COBBLESTONE, Material.COBBLESTONE);
        specialTypes.put(Material.INFESTED_STONE_BRICKS, Material.STONE_BRICKS);
        specialTypes.put(Material.INFESTED_CRACKED_STONE_BRICKS, Material.CRACKED_STONE_BRICKS);
        specialTypes.put(Material.INFESTED_MOSSY_STONE_BRICKS, Material.MOSSY_STONE_BRICKS);
        specialTypes.put(Material.INFESTED_CHISELED_STONE_BRICKS, Material.CHISELED_STONE_BRICKS);
    }

    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getChunkID(int blockX, int blockZ, World.Environment environment) {
        return environment.name() + "|" + (blockX >> 4) + "," + (blockZ >> 4);
    }

    public static String getChunkID(Location loc) {
        return getChunkID(loc.getBlockX(), loc.getBlockZ(), loc.getWorld().getEnvironment());
    }

    public static String[] getMembers(Location loc) {
        return getMembers(getChunkID(loc));
    }

    public static String[] getMembers(String chunkID) {
        String members = plugin.cfg.getClaimData().getString(chunkID + ".members");

        if (members != null)
            return members.split("\\n");
        else
            return null;
    }

    // Check if a block is a lectern with a claim book
    public static boolean isClaimBlock(Block b) {
        FileConfiguration claimData = plugin.cfg.getClaimData();
        String cID = getChunkID(b.getLocation());
        double lecternX = claimData.getDouble(cID + ".location.X", Double.MAX_VALUE);
        double lecternY = claimData.getDouble(cID + ".location.Y", Double.MAX_VALUE);
        double lecternZ = claimData.getDouble(cID + ".location.Z", Double.MAX_VALUE);

        if(lecternX == Double.MAX_VALUE || lecternY == Double.MAX_VALUE || lecternZ == Double.MAX_VALUE)
            return false;

        return lecternX == b.getLocation().getX() && lecternY == b.getLocation().getY() && lecternZ == b.getLocation().getZ();
    }

    public static boolean claimChunk(Block block, BookMeta meta, Consumer<String> sendMessage) {
        if (meta != null) {
            List<String> members = findMembersInBook(meta);

            // If it's a valid claim book
            if(members.size() > 0) {
                // If the lectern is next to bedrock: Cancel
                if(isNextToBedrock(block)){
                    sendMessage.accept(utils.chat("&cYou cannot place a claim next to bedrock"));
                    return false;
                }

                setChunkClaim(block, members, sendMessage, null);
                updateClaimCount();

            }else{
                sendMessage.accept(utils.chat("&cHINT: Add \"claim\" at the top of the first page, followed by a list members, to claim this chunk!"));
            }
        }

        return true;
    }

    public static void claimChunkBulk(Set<Block> blocks, BookMeta meta, String masterBookID) {
        if (meta != null) {
            List<String> members = findMembersInBook(meta);

            // If it's a valid claim book
            if(members.size() > 0) {
                for (Block block : blocks) {
                    // If the lectern is next to bedrock: Cancel
                    if (isNextToBedrock(block))
                        continue;

                    setChunkClaim(block, members, masterBookID);
                }
                updateClaimCount();
            }
        }
    }

    private static void setChunkClaim(Block block, List<String> members, String masterBookID){
        setChunkClaim(block, members, null, masterBookID);
    }

    private static void setChunkClaim(Block block, List<String> members, @Nullable Consumer<String> sendMessage, String masterBookID) {
        FileConfiguration claimData = plugin.cfg.getClaimData();
        Location blockLoc = block.getLocation();
        String chunkID = utils.getChunkID(blockLoc);
        String membersString = String.join("\n", members);

        claimData.set(chunkID + ".location.X", blockLoc.getX());
        claimData.set(chunkID + ".location.Y", blockLoc.getY());
        claimData.set(chunkID + ".location.Z", blockLoc.getZ());
        claimData.set(chunkID + ".members", membersString);
        plugin.cfg.saveClaimData();

        onChunkClaim(chunkID, members, sendMessage, masterBookID);
    }

    private static void onChunkClaim(String chunkID, List<String> members, @Nullable Consumer<String> sendMessage, String masterBookID){
        if(sendMessage == null)
            sendMessage = (msg) -> {};
        OfflinePlayer[] knownPlayers = Bukkit.getOfflinePlayers();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        // Inform the player of the claim and its members
        sendMessage.accept(utils.chat("&eThis chunk has now been claimed!"));
        sendMessage.accept(utils.chat("&aMembers who can access this chunk:"));
        for (String member : members) {
            Optional<OfflinePlayer> offlinePlayer = Arrays.stream(knownPlayers).filter(kp -> kp.getName() != null && kp.getName().equalsIgnoreCase(member)).findFirst();

            if (offlinePlayer.isPresent()) {
                sendMessage.accept(ChatColor.GRAY + " - " + member);

                boolean isOffline = true;

                for(Player player : onlinePlayers) {
                    if (player.getName().equalsIgnoreCase(member)) {
                        isOffline = false;
                        break;
                    }
                }

                if(isOffline){
                    String name = offlinePlayer.get().getName();
                    FileConfiguration offlineClaimNotifications = plugin.cfg.getOfflineClaimNotifications();

                    if(masterBookID != null)
                        offlineClaimNotifications.set(name + ".masterbooks." + masterBookID, false);
                    else
                        offlineClaimNotifications.set(name + ".chunks." + chunkID, null);
                    plugin.cfg.saveOfflineClaimNotifications();
                }
            } else {
                sendMessage.accept(ChatColor.GRAY + " - " + member + ChatColor.RED + " (unknown player)");
            }
        }

        for (Player player : onlinePlayers)
            if (chunkID.equals(getChunkID(player.getLocation())))
                if (isIntruder(player, chunkID))
                    onIntruderEnterClaim(player, chunkID);
    }

    private static void onChunkUnclaim(String chunkID, String[] members, Location lecternLoc, String masterBookID){
        OfflinePlayer[] knownPlayers = Bukkit.getOfflinePlayers();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        if(members != null) {
            for (String member : members) {
                Optional<OfflinePlayer> offlinePlayer = Arrays.stream(knownPlayers).filter(kp -> kp.getName() != null && kp.getName().equalsIgnoreCase(member)).findFirst();

                if (offlinePlayer.isPresent()) {
                    boolean isOffline = true;
                    String xyz = lecternLoc.getBlockX() +", "+ lecternLoc.getBlockY() +", "+ lecternLoc.getBlockZ();

                    // Notify online members that they have lost the claim
                    for (Player player : onlinePlayers) {
                        if (player.getName().equalsIgnoreCase(member)) {
                            isOffline = false;
                            if(masterBookID != null) {
                                if(!masterBookChangeMsgSent) {
                                    String msg = "Your name has been removed from Master Book #" + masterBookID + " and all related claims!";
                                    player.sendMessage(ChatColor.RED + msg);
                                    masterBookChangeMsgSent = true;
                                }
                            }else {
                                player.sendMessage(ChatColor.RED + "You have lost a claim! Location: " + xyz);
                            }
                            break;
                        }
                    }

                    if (isOffline) {
                        String name = offlinePlayer.get().getName();
                        FileConfiguration offlineClaimNotifications = plugin.cfg.getOfflineClaimNotifications();

                        if(masterBookID != null) {
                            offlineClaimNotifications.set(name + ".masterbooks." + masterBookID, true);
                        }else {
                            offlineClaimNotifications.set(name + ".chunks." + chunkID, xyz);
                        }
                        plugin.cfg.saveOfflineClaimNotifications();
                    }
                }
            }
        }

        if(intruders.containsKey(chunkID))
            for(Player intruder : intruders.get(chunkID))
                onIntruderLeaveClaim(intruder, chunkID);
    }

    private static List<String> findMembersInBook(BookMeta meta) {
        List<String> pages = meta.getPages();
        List<String> members = new ArrayList<>();

        for (String page : pages) {
            // If it isn't a claim page, stop looking for members
            if (!isClaimPage(page))
                break;

            String[] lines = page.split("\\n");

            for (int i = 1; i < lines.length; i++) {
                String member = lines[i].trim();

                // If the member name is valid
                if(!member.contains(" ") && !member.isEmpty() && !members.contains(member))
                    members.add(member);
            }
        }

        return members;
    }

    private static boolean isNextToBedrock(Block block) {
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++)
                    if (block.getRelative(x, y, z).getType() == Material.BEDROCK)
                        return true;
        return false;
    }

    public static boolean isClaimPage(String page) {
        return page.length() >= 5 && page.substring(0, 5).equalsIgnoreCase("claim");
    }

    public static void unclaimChunk(Block block, boolean causedByPlayer, Consumer<String> sendMessage) {
        FileConfiguration claimData = plugin.cfg.getClaimData();
        Location blockLoc = block.getLocation();
        String chunkID = utils.getChunkID(blockLoc);
        String[] members = getMembers(chunkID);

        claimData.set(chunkID, null);
        plugin.cfg.saveClaimData();

        if (causedByPlayer)
            sendMessage.accept(ChatColor.RED + "You have removed this claim!");

        onChunkUnclaim(chunkID, members, blockLoc, null);
        updateClaimCount();
    }

    public static void unclaimChunkBulk(Set<Block> blocks, String masterBookID) {
        FileConfiguration claimData = plugin.cfg.getClaimData();

        for(Block b : blocks) {
            Location bLoc = b.getLocation();
            String chunkID = utils.getChunkID(bLoc);
            String[] members = getMembers(chunkID);

            claimData.set(chunkID, null);

            onChunkUnclaim(chunkID, members, bLoc, masterBookID);
        }
        plugin.cfg.saveClaimData();

        masterBookChangeMsgSent = false;
        updateClaimCount();
    }

    // Update tablist with current number of claims for each player
    public static void updateClaimCount() {
        HashMap<String, Integer> membersNumClaims = countMemberClaims();

        for(Player p : Bukkit.getOnlinePlayers()) {
            Integer pClaims = membersNumClaims.get(p.getName().toLowerCase());

            if(pClaims == null)
                p.setPlayerListName(p.getName() + utils.chat(" - &c0"));
            else
                p.setPlayerListName(p.getName() + utils.chat(" - &c" + pClaims));
        }
    }

    // Returns a HashMap of player name (lowercase) and number of claims
    public static HashMap<String, Integer> countMemberClaims() {
        FileConfiguration claimData = plugin.cfg.getClaimData();
        HashMap<String, Integer> count = new HashMap<>();

        for(String key : claimData.getKeys(false)){
            ConfigurationSection chunk = claimData.getConfigurationSection(key);

            if(chunk != null){
                String currentMembers = chunk.getString("members");
                if(currentMembers != null)
                    for (String cm : currentMembers.toLowerCase().split("\\n"))
                        count.merge(cm, 1, Integer::sum);
            }
        }

        return count;
    }

    public static void b4bCheck(Player p, Block b, BlockBreakEvent e, Boolean noloot, boolean requiresBlock) {
        if(requiresBlock) {
            Material requiredType = b.getType();

            if (specialTypes.containsKey(requiredType))
                requiredType = specialTypes.get(requiredType);

            if (p.getInventory().getItemInOffHand().getType() == requiredType) {
                p.getInventory().getItemInOffHand().setAmount(p.getInventory().getItemInOffHand().getAmount() - 1);
            } else {
                boolean itemInInventory = false;
                for (int i = 0; i < 9; i++) {
                    ItemStack item = p.getInventory().getItem(i);
                    if (item != null) {
                        if (item.getType() == requiredType) {
                            item.setAmount(item.getAmount() - 1);
                            itemInInventory = true;
                            break;
                        }
                    }
                }

                if (!itemInInventory){
                    e.setCancelled(true);
                    p.sendMessage(utils.chat("&aYou need &c" + requiredType + " &ain your quick-slots to break this!"));
                    return;
                }
            }
        }

        if(noloot)
            e.setDropItems(false);
    }

    public static Material getRandomSpawnEgg(Map<Character, Integer> letterBonuses){
        ConfigurationSection weightConfig = plugin.getConfig().getConfigurationSection("spawn-egg-weights");
        Random rand = new Random();
        int totalWeight = calcTotalWeight(letterBonuses);
        int i = rand.nextInt(totalWeight);

        if(weightConfig != null) {
            for (String eggName : weightConfig.getKeys(false)) {
                Character firstLetter = eggName.toLowerCase().charAt(0);
                Integer bonus = letterBonuses.get(firstLetter);
                int weight = weightConfig.getInt(eggName);

                if (bonus != null)
                    weight *= (1 + bonus);
                i -= weight;

                if (i <= 0)
                    return Material.valueOf(eggName);
            }
        }

        // We should never get this far
        return Material.TROPICAL_FISH_SPAWN_EGG;
    }

    private static int calcTotalWeight(Map<Character, Integer> letterBonuses){
        ConfigurationSection weightConfig = plugin.getConfig().getConfigurationSection("spawn-egg-weights");
        int totalWeight = 0;

        if(weightConfig != null) {
            for (String eggName : weightConfig.getKeys(false)) {
                Character firstLetter = eggName.toLowerCase().charAt(0);
                Integer bonus = letterBonuses.get(firstLetter);
                int weight = weightConfig.getInt(eggName);

                if (bonus != null)
                    weight *= (1 + bonus);
                totalWeight += weight;
            }
        }

        return totalWeight;
    }

    public static void onIntruderEnterClaim(Player intruder, String chunkID) {
        if(intruder.getGameMode() != GameMode.SURVIVAL)
            return;

        if(!intruders.containsKey(chunkID))
            intruders.put(chunkID, new HashSet<>());

        intruders.get(chunkID).add(intruder);

        // Make all iron golems in chunk hostile to the intruder
        if(plugin.getConfig().getBoolean("golems-guard-claims"))
            for(IronGolem golem : ironGolems.keySet())
                if(chunkID.equals(getChunkID(golem.getLocation())))
                    golem.damage(0, intruder);

        String[] members = getMembers(chunkID);

        if(members != null) {
            for (String m : members) {
                Player p = Bukkit.getPlayerExact(m);

                if (p != null) {
                    FileConfiguration claimData = plugin.cfg.getClaimData();
                    long now = System.nanoTime();
                    double x = claimData.getDouble(chunkID + ".location.X");
                    double y = claimData.getDouble(chunkID + ".location.Y");
                    double z = claimData.getDouble(chunkID + ".location.Z");

                    if (!playerClaimsIntruded.containsKey(p))
                        playerClaimsIntruded.put(p, new HashSet<>());
                    playerClaimsIntruded.get(p).add(chunkID);

                    if(now - lastIntrusionMsgReceived.getOrDefault(p, 0L) >= minSecBetweenAlerts * 1e9){
                        p.sendMessage(ChatColor.RED + "An intruder has entered your claim at "+x+", "+y+", "+z);
                        lastIntrusionMsgReceived.put(p, now);
                    }
                }
            }
        }
    }

    public static void onIntruderLeaveClaim(Player intruder, String chunkID) {
        if(intruders.containsKey(chunkID))
            intruders.get(chunkID).remove(intruder);
    }

    public static boolean isIntruder(Player p, String chunkID){
        String[] members = utils.getMembers(chunkID);

        // If the chunk isn't claimed; else if p is a member
        if (members == null)
            return false;
        else
            for (String member : members)
                if (member.equalsIgnoreCase(p.getName()))
                    return false;

        return true;
    }

    public static void updateGolemHostility(){
        for(Map.Entry<IronGolem, String> entry : ironGolems.entrySet()){
            IronGolem golem = entry.getKey();
            String currentChunkID = getChunkID(golem.getLocation());
            String prevChunkID = entry.getValue();

            if(!currentChunkID.equals(prevChunkID)){
                entry.setValue(currentChunkID);

                // Make it hostile to all intruders in chunk
                if(utils.intruders.containsKey(currentChunkID))
                    for(Player intruder : utils.intruders.get(currentChunkID))
                        golem.damage(0, intruder);
            }
        }
    }

    public static void populatePlayerClaimsIntruded(Player p){
        // Go through all intruded claims
        for(String chunkID : intruders.keySet()){
            String[] members = getMembers(chunkID);

            if(members != null) {
                for (String m : members) {
                    // If p is a member
                    if (m.equalsIgnoreCase(p.getName())) {
                        if (!playerClaimsIntruded.containsKey(p))
                            playerClaimsIntruded.put(p, new HashSet<>());

                        // Add the chunk as one of p's intruded claims
                        playerClaimsIntruded.get(p).add(chunkID);
                        break;
                    }
                }
            }
        }
    }
}
