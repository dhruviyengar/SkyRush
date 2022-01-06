package me.secretagent.skyrush.game.tasks;

import me.secretagent.skyrush.game.SkyRushGame;
import me.secretagent.skyrush.game.state.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;

public class StartGameTask implements Runnable {

    private final SkyRushGame game;
    private int seconds;
    private boolean ended = false;

    public StartGameTask(SkyRushGame game, int seconds) {
        this.game = game;
        this.seconds = seconds;
    }

    @Override
    public void run() {
        if (ended) return;
        if (seconds <= 0) {
            game.setState(GameState.ACTIVE);
            ended = true;
            return;
        }
        for (Player player : game.getPlayers()) {
            player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.sharp(1, Note.Tone.C));
            player.sendMessage(ChatColor.GREEN + "Game starts in " + ChatColor.YELLOW + seconds + "" + ChatColor.GREEN + " seconds!");
        }
        seconds--;
    }

}
