package net.slipcor.pvparena.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import net.slipcor.pvparena.arena.Arena;
import net.slipcor.pvparena.arena.ArenaPlayer;
import net.slipcor.pvparena.arena.ArenaTeam;
import net.slipcor.pvparena.core.Help;
import net.slipcor.pvparena.core.Language;
import net.slipcor.pvparena.core.StringParser;
import net.slipcor.pvparena.core.Help.HELP;
import net.slipcor.pvparena.core.Language.MSG;

import org.bukkit.command.CommandSender;

/**
 * <pre>PVP Arena LIST Command class</pre>
 * 
 * A command to display the players of an arena
 * 
 * @author slipcor
 * 
 * @version v0.10.0
 */

public class PAI_List extends AbstractArenaCommand {

	public PAI_List() {
		super(new String[] {"pvparena.user"});
	}
	
	private static Map<ArenaPlayer.Status, Character> colorMap = new HashMap<ArenaPlayer.Status, Character>();

	static {

		colorMap.put(ArenaPlayer.Status.NULL, 'm'); // error? strike through
		colorMap.put(ArenaPlayer.Status.WARM, '6'); // warm = gold
		colorMap.put(ArenaPlayer.Status.LOUNGE, 'b'); // readying up = aqua
		colorMap.put(ArenaPlayer.Status.READY, 'a'); // ready = green
		colorMap.put(ArenaPlayer.Status.FIGHT, 'f'); // fighting = white
		colorMap.put(ArenaPlayer.Status.WATCH, 'e'); // watching = yellow
		colorMap.put(ArenaPlayer.Status.DEAD, '7'); // dead = silver
		colorMap.put(ArenaPlayer.Status.LOST, 'c'); // lost = red
	}
	
	@Override
	public void commit(final Arena arena, final CommandSender sender, final String[] args) {
		if (!this.hasPerms(sender, arena)) {
			return;
		}
		
		if (!argCountValid(sender, arena, args, new Integer[]{0,1})) {
			return;
		}
		
		if (args.length < 1) {
		
			
			for (ArenaTeam teams : arena.getTeams()) {
				final Set<String> names = new HashSet<String>();
				
				for (ArenaPlayer player : teams.getTeamMembers()) {
					names.add("&" + colorMap.get(player.getStatus()) + player.getName() + "&r");
				}
				
				if (arena.isFreeForAll()) {
					arena.msg(sender, Language.parse(MSG.LIST_PLAYERS, StringParser.joinSet(names, ", ")));
				} else {
					final int count = teams.getTeamMembers().size();
					final String sCount = " &r(" + count + ")";
					arena.msg(sender, Language.parse(MSG.LIST_TEAM, teams.getColoredName() + sCount, StringParser.joinSet(names, ", ")));
				}
			}
			return;
		}
		
		final Map<ArenaPlayer.Status,Set<String>> stats = new HashMap<ArenaPlayer.Status, Set<String>>();
		
		for (ArenaPlayer player : arena.getEveryone()) {
			final Set<String> players = stats.containsKey(player.getStatus()) ? stats.get(player.getStatus()) : new HashSet<String>();
			
			players.add(player.getName());
			stats.put(player.getStatus(), players);
		}
		
		for (ArenaPlayer.Status stat : stats.keySet()) {
			arena.msg(sender, Language.parse(MSG.getByNode("LIST_" + stat.name()), "&" + colorMap.get(stat) + StringParser.joinSet(stats.get(stat), ", ")));
		}
		
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public void displayHelp(final CommandSender sender) {
		Arena.pmsg(sender, Help.parse(HELP.LIST));
	}
}
