package me.secretagent.skyrush.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.secretagent.skyrush.SkyRush;
import me.secretagent.skyrush.game.SkyRushGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandAlias("sr|skyrush")
public class SkyRushCommand extends BaseCommand {

    private final SkyRush plugin = SkyRush.getPlugin();

    @Subcommand("start")
    public void onStart(Player player, String args[]) {
        List<Player> players = new ArrayList<>();
        players.add(player);
        for (String string : args) {
            if (Bukkit.getPlayer(string) != null) {
                players.add(Bukkit.getPlayer(string));
            }
        }
        Player[] players1 = new Player[players.size()];
        for (int i = 0; i < players.size(); i++) {
            players1[i] = players.get(i);
        }
        SkyRushGame game = new SkyRushGame(players1, "SkyRush");
    }


}
