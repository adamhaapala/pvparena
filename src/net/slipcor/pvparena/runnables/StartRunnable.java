package net.slipcor.pvparena.runnables;

import net.slipcor.pvparena.PVPArena;
import net.slipcor.pvparena.arena.Arena;
import net.slipcor.pvparena.core.Debug;
import net.slipcor.pvparena.core.Language.MSG;

/**
 * <pre>Arena Runnable class "Start"</pre>
 * 
 * An arena timer to start the arena
 * 
 * @author slipcor
 * 
 * @version v0.9.8
 */

public class StartRunnable extends ArenaRunnable {
	private final static Debug DEBUG = new Debug(43);

	/**
	 * create a timed arena start runnable
	 * 
	 * @param arena
	 *            the arena we are running in
	 */
	public StartRunnable(final Arena arena, final int seconds) {
		super(MSG.ARENA_STARTING_IN.getNode(), seconds, null, arena, false);
		DEBUG.i("StartRunnable constructor");
		arena.startRunner = this;
	}

	@Override
	protected void commit() {
		arena.startRunner = null;
		DEBUG.i("StartRunnable commiting");
		arena.start();
	}
	
	@Override
	protected void warn() {
		PVPArena.instance.getLogger().warning("StartRunnable not scheduled yet!");
	}
}
