package me.secretagent.skyrush.game;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import me.secretagent.skyrush.SkyRush;
import me.secretagent.skyrush.game.state.GameState;
import me.secretagent.skyrush.game.tasks.StartGameTask;
import me.secretagent.skyrush.util.FileUtil;
import me.secretagent.skyrush.util.ItemBuilder;
import me.secretagent.skyrush.util.RandomUtil;
import me.secretagent.skyrush.util.YamlUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.util.*;

public class SkyRushGame implements Listener {
    
    private final Player[] players;
    private GameState state = GameState.CREATED;
    private final SkyRush plugin = SkyRush.getPlugin();
    private final long startTime = System.currentTimeMillis();
    private SlimeWorld world;
    private final String matchName;
    private final String mapName;
    private final List<Location> spawns = new ArrayList<>();
    private final List<Block> glassBlocks = new ArrayList<>();
    
    private final int maxPlayers;
    private int alivePlayers;

    private long ticks = 0;

    private final JPerPlayerScoreboard scoreboard;

    private final List<SkyRushGame> GAMES = new ArrayList<>();

    public SkyRushGame(Player[] players, String mapName) {
        this.maxPlayers = FileUtil.getMapConfig(mapName).getStringList("spawns").size();
        this.alivePlayers = players.length;
        if (alivePlayers > maxPlayers) {
            throw new IllegalArgumentException("Number of players is greater than max players!");
        }
        this.mapName = mapName;
        this.players = players;
        this.scoreboard = new JPerPlayerScoreboard(
                (player) -> "&e&lSKYRUSH ALPHA",
                (player) -> Arrays.asList(
                        "&aMap: &e" + mapName,
                        "&aPlayers: &e" + getAlivePlayers(),
                        "&aGame Length: &e" + (ticks / 20) + "&asec"
                )
        );
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        this.matchName = mapName + "-" + startTime;
        SlimePropertyMap properties = new SlimePropertyMap();
        properties.setString(SlimeProperties.DIFFICULTY, "normal");
        properties.setInt(SlimeProperties.SPAWN_X, 0);
        properties.setInt(SlimeProperties.SPAWN_Y, 60);
        properties.setInt(SlimeProperties.SPAWN_Z, 0);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            SlimeWorld world = null;
            try {
                world = plugin.getSlimeAPI().loadWorld(plugin.getSlimeLoader(), mapName, true, properties).clone(matchName);
            } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException e) {
                e.printStackTrace();
            }
            if (world != null) {
                plugin.getSlimeAPI().generateWorld(world);
            } else {
                plugin.getLogger().severe("UNABLE TO CREATE WORLD!");
            }
            this.world = world;
        });
        GAMES.add(this);
        Bukkit.getScheduler().runTaskLater(plugin, () -> setState(GameState.PREPARING), 10);
    }

    public void setState(GameState state) {
        if (state == this.state) return;
        switch (state) {
            case CREATED:

                break;
            case PREPARING:
                List<String> list = FileUtil.getMapConfig(mapName).getStringList("spawns");
                list.forEach( s -> {
                    spawns.add(YamlUtil.getLocationAdvanced(getBukkitWorld(), s));
                });
                setState(GameState.STARTING);
                break;
            case STARTING:
                List<Location> locations = new ArrayList<>();
                for (Location location : spawns) {
                    Location location1 = location.clone().add(0, 3, 0);
                    locations.add(location1);
                    glassBlocks.add(location1.clone().subtract(0, 1, 0).getBlock());
                    glassBlocks.add(location1.clone().add(0, 2, 0).getBlock());
                    glassBlocks.add(location1.clone().add(1, 0, 0).getBlock());
                    glassBlocks.add(location1.clone().add(-1, 0, 0).getBlock());
                    glassBlocks.add(location1.clone().add(0, 0, 1).getBlock());
                    glassBlocks.add(location1.clone().add(0, 0, -1).getBlock());
                    for (Block block : glassBlocks) {
                        block.setType(Material.GLASS);
                    }
                }
                int i = 0;
                for (Player player : players) {
                    getScoreboard().addPlayer(player);
                    player.getInventory().clear();
                    player.teleport(locations.get(i));
                    player.setGameMode(GameMode.ADVENTURE);
                    i++;
                }
                for (Chunk chunk : getBukkitWorld().getLoadedChunks()) {
                    for (BlockState blockState : chunk.getTileEntities()) {
                        if (blockState instanceof Chest) {
                            Chest chest = (Chest) blockState;
                           if (chest.getY() == 51) {
                               List<Integer> list1 = RandomUtil.getRandomNumbers(6, 26);
                               chest.getBlockInventory().setItem(list1.get(0), new ItemStack(Material.IRON_HELMET));
                               chest.getBlockInventory().setItem(list1.get(1), new ItemStack(Material.IRON_CHESTPLATE));
                               chest.getBlockInventory().setItem(list1.get(2), new ItemStack(Material.IRON_LEGGINGS));
                               chest.getBlockInventory().setItem(list1.get(3), new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_FALL, 1).build());
                               chest.getBlockInventory().setItem(list1.get(4), new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.DIG_SPEED, 5).build());
                               chest.getBlockInventory().setItem(list1.get(5), new ItemStack(Material.STONE, 64));
                           } else if (chest.getY() == 50) {
                               Potion potion = new Potion(PotionType.REGEN);
                               potion.setSplash(true);
                               potion.setLevel(2);
                               List<Integer> list1 = RandomUtil.getRandomNumbers(4, 26);
                               chest.getBlockInventory().setItem(list1.get(0), new ItemStack(Material.SNOW_BALL, 64));
                               chest.getBlockInventory().setItem(list1.get(1), new ItemStack(Material.GOLDEN_APPLE));
                               chest.getBlockInventory().setItem(list1.get(2), new ItemStack(Material.GOLDEN_APPLE));
                               chest.getBlockInventory().setItem(list1.get(3), potion.toItemStack(1));
                           } else if (chest.getZ() == 52) {
                               //give extreme items
                           }
                        }
                    }
                }
                StartGameTask task = new StartGameTask(this, 5);
                Bukkit.getScheduler().runTaskTimer(plugin, task, 0, 20);
                startUpdateTask();
                break;
            case ACTIVE:
                for (Player player : getPlayers()) {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                    player.sendMessage(ChatColor.GREEN + "Game is active!");
                    player.setGameMode(GameMode.SURVIVAL);
                }
                for (Block block : glassBlocks) {
                    block.setType(Material.AIR);
                }
                break;
            case ENDING:
                for (Player player : getPlayers()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "GAME OVER");
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                HandlerList.unregisterAll(this);
                for (Player player : getPlayers()) {
                    getScoreboard().removePlayer(player);
                    for (Map.Entry<Integer, ? extends ItemStack> entry : player.getInventory().all(Material.LOG).entrySet()) {
                        player.getInventory().setItem(entry.getKey(), null);
                    }
                }
                GAMES.remove(this);
                Bukkit.unloadWorld(getBukkitWorld(), false);
        }
        this.state = state;
    }

    public World getBukkitWorld() {
        return Bukkit.getWorld(matchName);
    }

    public Player[] getPlayers() {
        return players;
    }

    public JPerPlayerScoreboard getScoreboard() {
        return scoreboard;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Location location = event.getEntity().getLocation();
        if (!location.getWorld().getName().equals(getBukkitWorld().getName())) return;
        location.setY(60);
        event.getEntity().spigot().respawn();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            event.getEntity().setGameMode(GameMode.SPECTATOR);
            event.getEntity().teleport(location);
        }, 1);
        alivePlayers--;
        if (alivePlayers <= 1) {
            event.setDeathMessage(ChatColor.GREEN + "");
            setState(GameState.ENDING);
        } else {
            event.setDeathMessage(ChatColor.GREEN + event.getEntity().getName() + " died!");
        }
    }

    private int getAlivePlayers() {
        return alivePlayers;
    }

    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            getScoreboard().updateScoreboard();
            ticks++;
        }, 0, 1);
    }

}
