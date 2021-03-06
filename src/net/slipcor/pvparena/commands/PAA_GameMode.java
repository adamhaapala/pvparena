package net.slipcor.pvparena.commands;

import net.slipcor.pvparena.arena.Arena;
import net.slipcor.pvparena.core.Help;
import net.slipcor.pvparena.core.Language;
import net.slipcor.pvparena.core.Config.CFG;
import net.slipcor.pvparena.core.Help.HELP;
import net.slipcor.pvparena.core.Language.MSG;

import org.bukkit.command.CommandSender;

/**
 * <pre>PVP Arena GAMEMODE Command class</pre>
 * 
 * A command to set the arena game mode
 * 
 * @author slipcor
 * 
 * @version v0.10.0
 */

public class PAA_GameMode extends AbstractArenaCommand {

	public PAA_GameMode() {
		super(new String[] {});
	}

	@Override
	public void commit(final Arena arena, final CommandSender sender, final String[] args) {
		if (!this.hasPerms(sender, arena)) {
			return;
		}
		
		if (!argCountValid(sender, arena, args, new Integer[]{1})) {
			return;
		}
		
		//                                   args[0]
		// usage: /pa {arenaname} gamemode [gamemode]
		
		// game modes: free , team
		
		if (args[0].toLowerCase().startsWith("free")) {
			arena.setFree(true);
			arena.msg(sender, Language.parse(MSG.GAMEMODE_FREE, arena.getName()));
			arena.getArenaConfig().set(CFG.GENERAL_TYPE, "free");
		} else {
			arena.setFree(false);
			arena.msg(sender, Language.parse(MSG.GAMEMODE_TEAM, arena.getName()));
			arena.getArenaConfig().set(CFG.GENERAL_TYPE, "none");
		}

		arena.getArenaConfig().save();
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public void displayHelp(final CommandSender sender) {
		Arena.pmsg(sender, Help.parse(HELP.GAMEMODE));
	}
}
