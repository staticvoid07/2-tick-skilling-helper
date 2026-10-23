package com.twotickskillinghelper;

import static com.twotickskillinghelper.TickState.CLICK;
import static com.twotickskillinghelper.TickState.IDLE;
import static com.twotickskillinghelper.TickState.SKIP;
import static com.twotickskillinghelper.TickState.WAIT;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class TickCycleTrackerTest
{
	private static final int IDLE_TIMEOUT = 4;

	private TickCycleTracker tracker;

	@Before
	public void setUp()
	{
		tracker = new TickCycleTracker();
	}

	/**
	 * Runs one tick per character: 'x' = hit that tick, '.' = no hit.
	 */
	private TickState[] run(String hits, int clickOffset)
	{
		TickState[] states = new TickState[hits.length()];
		for (int i = 0; i < hits.length(); i++)
		{
			if (hits.charAt(i) == 'x')
			{
				tracker.hitObserved();
			}
			tracker.onTick(clickOffset, IDLE_TIMEOUT);
			states[i] = tracker.getState();
		}
		return states;
	}

	@Test
	public void steadyCycleClicksOnHitTicks()
	{
		assertArrayEquals(
			new TickState[]{IDLE, CLICK, WAIT, CLICK, WAIT, CLICK, WAIT},
			run(".x.x.x.", 0));
		assertFalse(tracker.isPidSwapRecent());
	}

	@Test
	public void steadyCycleWithOffsetClicksTickAfterHit()
	{
		assertArrayEquals(
			new TickState[]{WAIT, CLICK, WAIT, CLICK, WAIT, CLICK},
			run("x.x.x.", 1));
	}

	@Test
	public void earlyPidSwapSkipsOneClick()
	{
		// Hits at 0, 2, then 3 (1 tick early), then every 2 from there
		assertArrayEquals(
			new TickState[]{CLICK, WAIT, CLICK, SKIP, WAIT, CLICK, WAIT, CLICK},
			run("x.xx.x.x", 0));
	}

	@Test
	public void latePidSwapLosesATick()
	{
		// Hits at 0, 2, then 5 (1 tick late), then every 2 from there
		assertArrayEquals(
			new TickState[]{CLICK, WAIT, CLICK, WAIT, SKIP, CLICK, WAIT, CLICK},
			run("x.x..x.x", 0));
	}

	@Test
	public void latePidSwapWithOffsetLosesATick()
	{
		assertArrayEquals(
			new TickState[]{WAIT, CLICK, WAIT, CLICK, WAIT, SKIP, CLICK, WAIT},
			run("x.x..x.x", 1));
	}

	@Test
	public void earlyPidSwapWithOffsetLosesATick()
	{
		assertArrayEquals(
			new TickState[]{WAIT, CLICK, WAIT, SKIP, CLICK, WAIT, CLICK, WAIT},
			run("x.xx.x.x", 1));
	}

	@Test
	public void pidSwapNoticeShownAfterSwap()
	{
		run("x.xx", 0);
		assertTrue(tracker.isPidSwapRecent());
		run("...", 0);
		assertFalse(tracker.isPidSwapRecent());
	}

	@Test
	public void goesIdleAfterTimeout()
	{
		assertArrayEquals(
			new TickState[]{CLICK, WAIT, SKIP, WAIT, WAIT, IDLE},
			run("x.....", 0));
	}

	@Test
	public void resumesAfterIdleWithoutSkipping()
	{
		run("x......", 0);
		assertArrayEquals(new TickState[]{CLICK, WAIT, CLICK}, run("x.x", 0));
		assertFalse(tracker.isPidSwapRecent());
	}
}
