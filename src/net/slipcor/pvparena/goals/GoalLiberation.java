package net.slipcor.pvparena.goals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import net.slipcor.pvparena.PVPArena;
import net.slipcor.pvparena.arena.ArenaClass;
import net.slipcor.pvparena.arena.ArenaPlayer;
import net.slipcor.pvparena.arena.ArenaPlayer.Status;
import net.slipcor.pvparena.arena.ArenaTeam;
import net.slipcor.pvparena.classes.PABlockLocation;
import net.slipcor.pvparena.classes.PACheck;
import net.slipcor.pvparena.commands.PAA_Region;
import net.slipcor.pvparena.core.Config.CFG;
import net.slipcor.pvparena.core.Debug;
import net.slipcor.pvparena.core.Language;
import net.slipcor.pvparena.core.Language.MSG;
import net.slipcor.pvparena.loadables.ArenaGoal;
import net.slipcor.pvparena.loadables.ArenaModuleManager;
import net.slipcor.pvparena.managers.InventoryManager;
import net.slipcor.pvparena.managers.SpawnManager;
import net.slipcor.pvparena.managers.TeamManager;
import net.slipcor.pvparena.runnables.EndRunnable;
import net.slipcor.pvparena.runnables.InventoryRefillRunnable;
import net.slipcor.pvparena.runnables.RespawnRunnable;

/**
 * <pre>
 * Arena Goal class "Liberation"
 * </pre>
 * 
 * Players have lives. When every life is lost, the player is teleported
 * to the killer's team's jail. Once every player of a team is jailed, the
 * team is out.
 * 
 * @author slipcor
 */

public class GoalLiberation extends ArenaGoal  {
	public GoalLiberation() {
		super("Liberation");
		debug = new Debug(102);
	}

	private EndRunnable endRunner = null;
	private String flagName = "";

	@Override
	public String version() {
		return "v1.0.1.59";
	}

	private static final int PRIORITY = 10;

	public PACheck checkCommand(final PACheck res, final String string) {
		if (res.getPriority() > PRIORITY) {
			return res;
		}

		for (ArenaTeam team : arena.getTeams()) {
			final String sTeam = team.getName();
			if (string.contains(sTeam + "button")) {
				res.setPriority(this, PRIORITY);
			}
		}

		return res;
	}

	@Override
	public PACheck checkEnd(final PACheck res) {
		debug.i("checkEnd - " + arena.getName());
		if (res.getPriority() > PRIORITY) {
			debug.i(res.getPriority() + ">" + PRIORITY);
			return res;
		}

		if (!arena.isFreeForAll()) {
			debug.i("TEAMS!");
			final int count = TeamManager.countActiveTeams(arena);
			debug.i("count: " + count);

			if (count <= 1) {
				res.setPriority(this, PRIORITY); // yep. only one team left. go!
			}
			return res;
		}

		PVPArena.instance.getLogger().warning("Liberation goal running in FFA mode: " + arena.getName());

		return res;
	}

	@Override
	public String checkForMissingSpawns(final Set<String> list) {
		if (!arena.isFreeForAll()) {
			String team = checkForMissingTeamSpawn(list);
			if (team != null) {
				return team;
			}
			
			return checkForMissingTeamCustom(list,"jail");
		}
		PVPArena.instance.getLogger().warning("Liberation goal running in FFA mode: " + arena.getName());
		return null;
	}

	/**
	 * hook into an interacting player
	 * 
	 * @param res
	 * 
	 * @param player
	 *            the interacting player
	 * @param clickedBlock
	 *            the block being clicked
	 * @return
	 */
	@Override
	public PACheck checkInteract(final PACheck res, final Player player, final Block block) {
		if (block == null || res.getPriority() > PRIORITY) {
			return res;
		}
		debug.i("checking interact", player);

		if (block.getType() != Material.STONE_BUTTON) {
			debug.i("block, but not button", player);
			return res;
		}
		debug.i("button click!", player);

		Vector vLoc;
		Vector vFlag = null;
		final ArenaPlayer aPlayer = ArenaPlayer.parsePlayer(player.getName());

		final ArenaTeam pTeam = aPlayer.getArenaTeam();
		if (pTeam == null) {
			return res;
		}
		final Set<ArenaTeam> setTeam = new HashSet<ArenaTeam>();

		for (ArenaTeam team : arena.getTeams()) {
			setTeam.add(team);
		}
		
		for (ArenaTeam team : setTeam) {
			final String aTeam = team.getName();

			if (aTeam.equals(pTeam.getName())) {
				debug.i("equals!OUT! ", player);
				continue;
			}
			if (team.getTeamMembers().size() < 1) {
				debug.i("size!OUT! ", player);
				continue; // dont check for inactive teams
			}
			debug.i("checking for flag of team " + aTeam, player);
			vLoc = block.getLocation().toVector();
			debug.i("block: " + vLoc.toString(), player);
			if (SpawnManager.getBlocks(arena, aTeam + "button").size() > 0) {
				vFlag = SpawnManager
						.getBlockNearest(
								SpawnManager.getBlocks(arena, aTeam
										+ "button"),
								new PABlockLocation(player.getLocation()))
						.toLocation().toVector();
			}
			if ((vFlag != null) && (vLoc.distance(vFlag) < 2)) {
				debug.i("button found!", player);
				debug.i("vFlag: " + vFlag.toString(), player);


				arena.broadcast(Language
						.parse(MSG.GOAL_LIBERATION_LIBERATED,
								pTeam.getColoredName()
										+ ChatColor.YELLOW));
				
				for (ArenaPlayer jailedPlayer : pTeam.getTeamMembers()) {
					if (jailedPlayer.getStatus() == Status.DEAD) {
						SpawnManager.respawn(arena, jailedPlayer);
					}
				}

				return res;
			}
		}

		return res;
	}

	@Override
	public PACheck checkJoin(final CommandSender sender, final PACheck res, final String[] args) {
		if (res.getPriority() >= PRIORITY) {
			return res;
		}

		final int maxPlayers = arena.getArenaConfig().getInt(CFG.READY_MAXPLAYERS);
		final int maxTeamPlayers = arena.getArenaConfig().getInt(
				CFG.READY_MAXTEAMPLAYERS);

		if (maxPlayers > 0 && arena.getFighters().size() >= maxPlayers) {
			res.setError(this, Language.parse(MSG.ERROR_JOIN_ARENA_FULL));
			return res;
		}

		if (args == null || args.length < 1) {
			return res;
		}

		if (!arena.isFreeForAll()) {
			final ArenaTeam team = arena.getTeam(args[0]);

			if (team != null && maxTeamPlayers > 0
						&& team.getTeamMembers().size() >= maxTeamPlayers) {
				res.setError(this, Language.parse(MSG.ERROR_JOIN_TEAM_FULL));
				return res;
			}
		}

		res.setPriority(this, PRIORITY);
		return res;
	}

	@Override
	public PACheck checkSetBlock(final PACheck res, final Player player, final Block block) {

		if (res.getPriority() > PRIORITY
				|| !PAA_Region.activeSelections.containsKey(player.getName())) {
			return res;
		}
		res.setPriority(this, PRIORITY); // success :)

		return res;
	}

	@Override
	public PACheck checkPlayerDeath(final PACheck res, final Player player) {
		if (res.getPriority() <= PRIORITY) {
			res.setPriority(this, PRIORITY);
		}
		return res;
	}

	@Override
	public void commitCommand(final CommandSender sender, final String[] args) {
		if (args[0].contains("button")) {
			for (ArenaTeam team : arena.getTeams()) {
				final String sTeam = team.getName();
				if (args[0].contains(sTeam + "button")) {
					flagName = args[0];
					PAA_Region.activeSelections.put(sender.getName(), arena);

					arena.msg(sender,
							Language.parse(MSG.GOAL_LIBERATION_TOSET, flagName));
				}
			}
		}
	}
	
	@Override
	public void commitEnd(final boolean force) {
		if (endRunner != null) {
			return;
		}

		for (ArenaTeam team : arena.getTeams()) {
			for (ArenaPlayer ap : team.getTeamMembers()) {
				if (!ap.getStatus().equals(Status.FIGHT)) {
					continue;
				}
				if (arena.isFreeForAll()) {

					ArenaModuleManager.announce(arena,
							Language.parse(MSG.PLAYER_HAS_WON, ap.getName()),
							"WINNER");

					arena.broadcast(Language.parse(MSG.PLAYER_HAS_WON,
							ap.getName()));
				} else {

					ArenaModuleManager.announce(
							arena,
							Language.parse(MSG.TEAM_HAS_WON,
									team.getColoredName()), "WINNER");

					arena.broadcast(Language.parse(MSG.TEAM_HAS_WON,
							team.getColoredName()));
					break;
				}
			}

			if (ArenaModuleManager.commitEnd(arena, team)) {
				return;
			}
		}

		endRunner = new EndRunnable(arena, arena.getArenaConfig().getInt(
				CFG.TIME_ENDCOUNTDOWN));
	}

	@Override
	public void commitPlayerDeath(final Player player, final boolean doesRespawn,
			final String error, final PlayerDeathEvent event) {
		
		if (!getLifeMap().containsKey(player.getName())) {
			return;
		}
		int pos = getLifeMap().get(player.getName());
		debug.i("lives before death: " + pos, player);
		if (pos <= 1) {
			getLifeMap().put(player.getName(),1);
			
			ArenaPlayer aPlayer = ArenaPlayer.parsePlayer(player.getName());
			
			aPlayer.setStatus(Status.DEAD);
			
			ArenaTeam team = aPlayer.getArenaTeam();
			
			boolean someoneAlive = false;
			
			for (ArenaPlayer temp : team.getTeamMembers()) {
				if (temp.getStatus() == Status.FIGHT) {
					someoneAlive = true;
					break;
				}
			}
			
			if (!someoneAlive) {
				PACheck.handleEnd(arena, false);
			} else {

				final ArenaTeam respawnTeam = ArenaPlayer.parsePlayer(player.getName())
						.getArenaTeam();
				if (arena.getArenaConfig().getBoolean(CFG.USES_DEATHMESSAGES)) {
					arena.broadcast(Language.parse(
							MSG.FIGHT_KILLED_BY,
							respawnTeam.colorizePlayer(player) + ChatColor.YELLOW,
							arena.parseDeathCause(player, event.getEntity()
									.getLastDamageCause().getCause(),
									player.getKiller()), String.valueOf(pos)));
				}
				
				if (arena.isCustomClassAlive()
						|| arena.getArenaConfig().getBoolean(
								CFG.PLAYER_DROPSINVENTORY)) {
					InventoryManager.drop(player);
					event.getDrops().clear();
				}
				new InventoryRefillRunnable(arena, aPlayer.get(), event.getDrops());
				
				String teamName = null;
				
				Bukkit.getScheduler().runTaskLater(PVPArena.instance, new RespawnRunnable(arena, aPlayer, teamName+"jail"), 1L);
				
				arena.unKillPlayer(aPlayer.get(), aPlayer.get().getLastDamageCause()==null?null:aPlayer.get().getLastDamageCause().getCause(), aPlayer.get().getKiller());
				
			}
			
		} else {
			pos--;
			getLifeMap().put(player.getName(), pos);

			final ArenaTeam respawnTeam = ArenaPlayer.parsePlayer(player.getName())
					.getArenaTeam();
			if (arena.getArenaConfig().getBoolean(CFG.USES_DEATHMESSAGES)) {
				arena.broadcast(Language.parse(
						MSG.FIGHT_KILLED_BY_REMAINING,
						respawnTeam.colorizePlayer(player) + ChatColor.YELLOW,
						arena.parseDeathCause(player, event.getEntity()
								.getLastDamageCause().getCause(),
								player.getKiller()), String.valueOf(pos)));
			}

			if (arena.isCustomClassAlive()
					|| arena.getArenaConfig().getBoolean(
							CFG.PLAYER_DROPSINVENTORY)) {
				InventoryManager.drop(player);
				event.getDrops().clear();
			}

			PACheck.handleRespawn(arena,
					ArenaPlayer.parsePlayer(player.getName()), event.getDrops());

		}
	}

	@Override
	public boolean commitSetFlag(final Player player, final Block block) {
		if (block == null
				|| block.getType() != Material.STONE_BUTTON) {
			return false;
		}

		if (!PVPArena.hasAdminPerms(player)
				&& !(PVPArena.hasCreatePerms(player, arena))) {
			return false;
		}

		debug.i("trying to set a button", player);

		// command : /pa redbutton1
		// location: redbutton1:

		SpawnManager.setBlock(arena, new PABlockLocation(block.getLocation()),
				flagName);

		arena.msg(player, Language.parse(MSG.GOAL_LIBERATION_SET, flagName));

		PAA_Region.activeSelections.remove(player.getName());
		flagName = "";

		return true;
	}

	@Override
	public void displayInfo(final CommandSender sender) {
		sender.sendMessage("lives: "
				+ arena.getArenaConfig().getInt(CFG.GOAL_LLIVES_LIVES));
	}

	@Override
	public PACheck getLives(final PACheck res, final ArenaPlayer aPlayer) {
		if (res.getPriority() <= PRIORITY+1000) {
			res.setError(
					this,
					String.valueOf(getLifeMap().containsKey(aPlayer.getName()) ? getLifeMap().get(aPlayer
									.getName()) : 0));
		}
		return res;
	}

	@Override
	public boolean hasSpawn(final String string) {
		if (arena.isFreeForAll()) {
			PVPArena.instance.getLogger().warning("Liberation goal running in FFA mode! /pa " + arena.getName() + " !gm team");
			return false;
		}
		for (String teamName : arena.getTeamNames()) {
			if (string.toLowerCase().startsWith(
					teamName.toLowerCase() + "spawn")) {
				return true;
			}
			if (arena.getArenaConfig().getBoolean(CFG.GENERAL_CLASSSPAWN)) {
				for (ArenaClass aClass : arena.getClasses()) {
					if (string.toLowerCase().startsWith(teamName.toLowerCase() + 
							aClass.getName() + "spawn")) {
						return true;
					}
				}
			}
			if (string.toLowerCase().startsWith(
					teamName.toLowerCase() + "jail")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initate(final Player player) {
		getLifeMap().put(player.getName(),
				arena.getArenaConfig().getInt(CFG.GOAL_LLIVES_LIVES));
	}

	@Override
	public boolean isInternal() {
		return true;
	}

	@Override
	public void parseLeave(final Player player) {
		if (player == null) {
			PVPArena.instance.getLogger().warning(
					this.getName() + ": player NULL");
			return;
		}
		if (getLifeMap().containsKey(player.getName())) {
			getLifeMap().remove(player.getName());
		}
	}

	@Override
	public void parseStart() {
		for (ArenaTeam team : arena.getTeams()) {
			for (ArenaPlayer ap : team.getTeamMembers()) {
				this.getLifeMap().put(ap.getName(),
						arena.getArenaConfig().getInt(CFG.GOAL_LLIVES_LIVES));
			}
		}
	}

	@Override
	public void reset(final boolean force) {
		endRunner = null;
		getLifeMap().clear();
	}

	@Override
	public void setDefaults(final YamlConfiguration config) {
		if (arena.isFreeForAll()) {
			return;
		}

		if (config.get("teams.free") != null) {
			config.set("teams", null);
		}
		if (config.get("teams") == null) {
			debug.i("no teams defined, adding custom red and blue!");
			config.addDefault("teams.red", ChatColor.RED.name());
			config.addDefault("teams.blue", ChatColor.BLUE.name());
		}
	}

	@Override
	public void setPlayerLives(final int value) {
		final Set<String> plrs = new HashSet<String>();

		for (String name : getLifeMap().keySet()) {
			plrs.add(name);
		}

		for (String s : plrs) {
			getLifeMap().put(s, value);
		}
	}

	@Override
	public void setPlayerLives(final ArenaPlayer aPlayer, final int value) {
		getLifeMap().put(aPlayer.getName(), value);
	}

	@Override
	public Map<String, Double> timedEnd(final Map<String, Double> scores) {
		double score;

		for (ArenaPlayer ap : arena.getFighters()) {
			score = (getLifeMap().containsKey(ap.getName()) ? getLifeMap().get(ap.getName())
					: 0);
			if (arena.isFreeForAll()) {

				if (scores.containsKey(ap.getName())) {
					scores.put(ap.getName(), scores.get(ap.getName()) + score);
				} else {
					scores.put(ap.getName(), score);
				}
			} else {
				if (ap.getArenaTeam() == null) {
					continue;
				}
				if (scores.containsKey(ap.getArenaTeam().getName())) {
					scores.put(ap.getArenaTeam().getName(),
							scores.get(ap.getName()) + score);
				} else {
					scores.put(ap.getArenaTeam().getName(), score);
				}
			}
		}

		return scores;
	}

	@Override
	public void unload(final Player player) {
		getLifeMap().remove(player.getName());
	}
}
