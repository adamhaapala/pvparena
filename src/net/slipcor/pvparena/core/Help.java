package net.slipcor.pvparena.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.slipcor.pvparena.PVPArena;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * <pre>
 * Help class
 * </pre>
 * 
 * provides methods to display configurable help texts
 * 
 * @author slipcor
 * 
 * @version v0.9.9
 */

public final class Help {
	private Help() {
		
	}

	private static String version = "v0.9.9.9";
	private final static String LINE = "-------------------------------------------";
	
	public static enum HELP {
		BLACKLIST("nulang.help.msg.blacklist", new String[]{
				"Manages block break/place blacklist entries",
				LINE,
				"shorthand command: !bl",
				LINE,
				"/pa [arenaname] blacklist clear",
				"/pa [arenaname] blacklist [type] clear",
				"    - clear the blacklist [of a special type]",
				"/pa [arenaname] blacklist [type] [sub] [blockID]",
				LINE,
				"Valid types: break | place | use",
				"Valid subs: add | remove | show"}),
		CHECK("nulang.help.msg.check", new String[]{
				"Validate an arena configuration",
				LINE,
				"shorthand command: !ch",
				LINE,
				"/pa [arenaname] check"}),
		CLASS("nulang.help.msg.class", new String[]{
				"Manage arena classes",
				LINE,
				"shorthand command: !cl",
				LINE,
				"/pa [arenaname] class load [classname]",
				"/pa [arenaname] class save [classname]",
				"/pa [arenaname] class remove [classname]"}),
		CREATE("nulang.help.msg.create", new String[]{
				"Create an arena",
				LINE,
				"shorthand command: !c",
				LINE,
				"/pa create [arenaname] {legacytype}",
				LINE,
				"Valid legacy types: teams, teamdm, dm, free, ctf, ctp, spleef"}),
		DEBUG("nulang.help.msg.debug", new String[]{
				"Debug PVP Arena",
				LINE,
				"shorthand command: !d",
				LINE,
				"/pa debug [values]",
				LINE,
				"Valid values are class numbers (separated by comma) and &enone&r"}),
		DISABLE("nulang.help.msg.disable", new String[]{
				"Disable an arena",
				LINE,
				"shorthand command: !dis",
				LINE,
				"/pa [arenaname] disable"}),
		EDIT("nulang.help.msg.edit", new String[]{
				"Toggle edit mode of an arena",
				LINE,
				"shorthand command: !e",
				LINE,
				"/pa [arenaname] edit"}),
		ENABLE("nulang.help.msg.enable", new String[]{
				"Enable an arena",
				LINE,
				"shorthand command: !en",
				LINE,
				"/pa [arenaname] enable"}),
		GAMEMODE("nulang.help.msg.gamemode", new String[]{
				"Set the general game mode of an arena. Teams or Free for all",
				LINE,
				"shorthand command: !gm",
				LINE,
				"/pa [arenaname] gamemode [free|teams]"}),
		GOAL("nulang.help.msg.goal", new String[]{
				"Manage arena goals",
				LINE,
				"shorthand command: !g",
				LINE,
				"/pa [arenaname] goal [goalname] {value}",
				LINE,
				"Entering an invalid goal will list available goals",
				"No value will toggle and display the result",
				"Valid values: yes, on, 1, true, no, off, 0, false"}),
		IMPORT("nulang.help.msg.import", new String[]{
				"Import v0.8 arenas",
				LINE,
				"shorthand command: !imp",
				LINE,
				"/pa import | import all arenas",
				"/pa import [arenaname] | import an arena"}),
		INSTALL("nulang.help.msg.install", new String[]{
				"Install modules",
				LINE,
				"shorthand command: !i",
				LINE,
				"/pa install (modules | goals)",
				"/pa install [modulename]",
				LINE,
				"modules / arenas will list available files"}),
		PROTECTION("nulang.help.msg.protection", new String[]{
				"Manage arena region protections",
				LINE,
				"shorthand command: !p",
				LINE,
				"/pa [arenaname] protection [regionname] [protection] {value}",
				LINE,
				"Entering an invalid protection will list available ones",
				"No value will toggle and display the result",
				"Valid values: yes, on, 1, true, no, off, 0, false"}),
		REGION("nulang.help.msg.region", new String[]{
				"Manage arena regions",
				LINE,
				"shorthand command: !rg",
				LINE,
				"/pa [arenaname] region",
				"    - start selecting a region",
				"/pa [arenaname] region [regionname] {regiontype}",
				"    - save region, default regiontype is a cuboid",
				"    - valid regions: &e/pa version regions",
				"/pa [arenaname] region [regionname] border",
				"    - display the region border for a short time",
				"/pa [arenaname] region remove [regionname]",
				"    - remove a region",
				LINE,
				"/pa [arenaname] region [regionname] [key] [value]",
				"    - Enhanced region editing, valid keys include:",
				"    - radius, height, position"}),
		REGIONFLAG("nulang.help.msg.regionflag", new String[]{
				"Manage arena region flags",
				LINE,
				"shorthand command: !rf",
				LINE,
				"/pa [arenaname] regionflag [regionname] [flagtype] {value}",
				LINE,
				"Entering an invalid flagtype will list available ones",
				"No value will toggle and display the result",
				"Valid values: yes, on, 1, true, no, off, 0, false"}),
		REGIONS("nulang.help.msg.regions", new String[]{
				"Debug arena regions",
				LINE,
				"shorthand command: !rs",
				LINE,
				"/pa [arenaname] regions {regionname}",
				LINE,
				"Show all defined regions / detailed information about a region"}),
		REGIONTYPE("nulang.help.msg.regiontype", new String[]{
				"Manage region types",
				LINE,
				"shorthand command: !rt",
				LINE,
				"/pa [arenaname] regiontype [regionname] [type]",
				LINE,
				"Valid region types "}),
		RELOAD("nulang.help.msg.reload", new String[]{
				"Reload an arena",
				LINE,
				"shorthand command: !rl",
				LINE,
				"/pa [arenaname] reload"}),
		REMOVE("nulang.help.msg.remove", new String[]{
				"Remove an arena",
				LINE,
				"shorthand command: !rem",
				LINE,
				"/pa [arenaname] remove"}),
		ROUND("nulang.help.msg.round", new String[]{
				"Manage arena rounds",
				LINE,
				"shorthand command: !rd",
				LINE,
				"/pa [arenaname] round",
				"    - list rounds",
				"/pa [arenaname] round [number]",
				"    - list round goals",
				"/pa [arenaname] round [number] [goal]",
				"    - toggle round goal"}),
		SET("nulang.help.msg.set", new String[]{
				"Set an arena config setting",
				LINE,
				"shorthand command: !s",
				LINE,
				"/pa [arenaname] set [node] [value]",
				LINE,
				"Values will be explained if necessary"}),
		SETOWNER("nulang.help.msg.setowner", new String[]{
				"Set an arena owner",
				LINE,
				"shorthand command: !so",
				LINE,
				"/pa [arenaname] setowner [playername]"}),
		SPAWN("nulang.help.msg.spawn", new String[]{
				"Manage arena spawns",
				LINE,
				"shorthand command: !sp",
				LINE,
				"/pa [arenaname] spawn [spawnname]",
				"/pa [arenaname] spawn [spawnname] remove",
				LINE,
				"Spawn names vary based on installed/active modules!"}),
		START("nulang.help.msg.start", new String[]{
				"Force start an arena",
				LINE,
				"shorthand command: !go",
				LINE,
				"/pa [arenaname] start"}),
		STOP("nulang.help.msg.stop", new String[]{
				"Force stop an arena",
				LINE,
				"shorthand command: !st",
				LINE,
				"/pa [arenaname] stop"}),
		TOGGLEMOD("nulang.help.msg.togglemod", new String[]{
				"Toggle an arena module",
				LINE,
				"shorthand command: !tm",
				LINE,
				"/pa [arenaname] togglemod [name]"}),
		TEAMS("nulang.help.msg.teams", new String[]{
				"Manage arena teams",
				LINE,
				"shorthand command: !ts",
				LINE,
				"/pa [arenaname] teams - list teams",
				"/pa [arenaname] teams add [name] [value]",
				"/pa [arenaname] teams set [name] [value]",
				"/pa [arenaname] teams remove [name]"}),
		TELEPORT("nulang.help.msg.teleport", new String[]{
				"Teleport to an arena spawn",
				LINE,
				"/pa [arenaname] teleport [spawnname]",
				"shorthand command: !t"}),
		UNINSTALL("nulang.help.msg.uninstall", new String[]{
				"Uninstall a PVP Arena module",
				LINE,
				"shorthand command: !ui",
				LINE,
				"/pa uninstall [modulename]"}),
		UPDATE("nulang.help.msg.update", new String[]{
				"Update a PVP Arena module",
				LINE,
				"shorthand command: !u",
				LINE,
				"/pa update [modulename]"}),
		WHITELIST("nulang.help.msg.whitelist", new String[]{
				"Manages block break/place whitelist entries",
				LINE,
				"shorthand command: !wl",
				LINE,
				"/pa [arenaname] whitelist clear",
				"/pa [arenaname] whitelist [type] clear",
				"    - clear the whitelist [of a special type]",
				"/pa [arenaname] whitelist [type] [sub] [blockID]",
				LINE,
				"Valid types: break | place | use",
				"Valid subs: add | remove | show"}),
				
		CHAT("nulang.help.msg.chat", new String[]{
				"Set arena team chat mode",
				LINE,
				"shorthand command: -c",
				LINE,
				"/pa [arenaname] chat {value}",
				LINE,
				"Switch between team and global chat",
				"No value will toggle and display the result",
				"Valid values: yes, on, 1, true, no, off, 0, false"}),
						
		SHUTUP("nulang.help.msg.shutup", new String[]{
				"Ignore arena announcements",
				LINE,
				"shorthand command: -su",
				LINE,
				"/pa [arenaname] shutup {value}",
				LINE,
				"Switch ignore mode",
				"No value will toggle and display the result",
				"Valid values: yes, on, 1, true, no, off, 0, false"}),
		JOIN("nulang.help.msg.join", new String[]{
				"Join an arena",
				LINE,
				"shorthand command: -j",
				LINE,
				"/pa [arenaname] join {teamname}"}),
		LEAVE("nulang.help.msg.leave", new String[]{
				"Leave the arena",
				LINE,
				"shorthand command: -l",
				LINE,
				"/pa leave"}),
		SPECTATE("nulang.help.msg.spectate", new String[]{
				"Spectate an arena",
				LINE,
				"shorthand command: -s",
				LINE,
				"/pa [arenaname] spectate"}),
				

		ARENACLASS("nulang.help.msg.arenaclass", new String[]{
				"Switch your arena class",
				LINE,
				"shorthand command: -ac",
				LINE,
				"/pa arenaclass [classname]"}),
		ARENALIST("nulang.help.msg.arenalist", new String[]{
				"List available arenas",
				LINE,
				"shorthand command: -ls",
				LINE,
				"/pa list"}),
		HELP("nulang.help.msg.help", new String[]{
				"Receive help",
				LINE,
				"shorthand command: -h",
				LINE,
				"/pa help [topic/command]"}),
		INFO("nulang.help.msg.info", new String[]{
				"Detailed information about an arena",
				LINE,
				"shorthand command: -i",
				LINE,
				"/pa [arenaname] info"}),
		LIST("nulang.help.msg.list", new String[]{
				"List arena players",
				LINE,
				"shorthand command: -ls",
				LINE,
				"/pa {arenaname} list"}),
		
		READY("nulang.help.msg.ready", new String[]{
				"Ready up / show ready players",
				LINE,
				"shorthand command: -r",
				LINE,
				"/pa {arenaname} ready {show}"}),
		STATS("nulang.help.msg.stats", new String[]{
				"Show arena/player statistics",
				LINE,
				"shorthand command: -s",
				LINE,
				"/pa {arenaname} stats [stattype] {amount}",
				LINE,
				"An invalid stattype will list all available ones"}),
		VERSION("nulang.help.msg.version", new String[]{
				"Show detailed version information",
				LINE,
				"shorthand command: -v",
				LINE,
				"/pa version"});
		

		private String node;
		private List<String> value;

		private HELP(final String node, final List<String> value) {
			this.node = node;
			this.value = value;
		}
		
		private HELP(final String node, final String[] sArray) {
			this.node = node;
			final List<String> list = new ArrayList<String>();
			for (String s : sArray) {
				list.add(s);
			}
			this.value = list;
		}

		public String getNode() {
			return node;
		}

		public void setValue(final List<String> strList) {
			value = strList;
		}

		public List<String> get() {
			return value;
		}
	}

	/**
	 * create a language manager instance
	 */
	public static void init(final String langString) {
		PVPArena.instance.getDataFolder().mkdir();
		final File configFile = new File(PVPArena.instance.getDataFolder().getPath()
				+ "/help_" + langString + ".yml");
		if (!(configFile.exists())) {
			try {
				configFile.createNewFile();
			} catch (Exception e) {
				Bukkit.getLogger().severe(
						"[PVP Arena] Error when creating help language file.");
			}
        }
		boolean override = false;

		final YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);
			final String ver = config.getString("version", "0");
			
			if (!ver.equals(version)) {
				override = true;
				final File file = new File(configFile.getParent(), "/help_"+langString+"_backup.yml");
				if (!file.exists()) {
					file.createNewFile();
				}
				config.save(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (HELP m : HELP.values()) {
			if (override) {
				config.set(m.getNode(), m.get());
			} else {
				config.addDefault(m.getNode(), m.get());
			}
		}
		
		config.set("version", version);

		config.options().copyDefaults(true);
		try {
			config.save(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * read a node from the config and return its value
	 * 
	 * @param s
	 *            the node name
	 * @return the node string
	 */
	public static String[] parse(final HELP help) {
		return StringParser.colorize(help.get());
	}
}
