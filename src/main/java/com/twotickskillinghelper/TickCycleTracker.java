package com.twotickskillinghelper;

import lombok.Getter;

/**
 * Follows the 2-tick cycle of an attacker hitting the player and decides, each game tick, whether the player should
 * click their skilling target.
 * <p>
 * Hits normally land every 2 ticks. When PID swaps in PvP, one interval becomes 1 or 3 ticks; the cycle then
 * continues on the opposite tick parity. Clicks are never shown less than {@link #CYCLE_LENGTH} ticks apart, so after
 * a swap the player loses a tick (a 3 tick gap) and is back in cycle with the new PIDs. The lost tick is shown as
 * {@link TickState#SKIP}.
 */
class TickCycleTracker
{
	static final int CYCLE_LENGTH = 2;

	/**
	 * How many ticks the PID swap notice stays up after the swap is detected.
	 */
	private static final int PID_SWAP_NOTICE_TICKS = 3;

	private static final int NONE = Integer.MIN_VALUE;

	private int tick;
	private boolean hitThisTick;
	private int lastHitTick = NONE;
	private int lastClickTick = NONE;
	private int lastSkipTick = NONE;
	private int pidSwapTick = NONE;

	@Getter
	private TickState state = TickState.IDLE;

	/**
	 * Records that the player was hit during the current tick. Call before {@link #onTick}.
	 */
	void hitObserved()
	{
		hitThisTick = true;
	}

	/**
	 * Advances one game tick and recomputes {@link #getState()}.
	 *
	 * @param clickOffset  ticks after a hit on which to click
	 * @param idleTimeout  ticks without a hit before going idle
	 */
	void onTick(int clickOffset, int idleTimeout)
	{
		tick++;

		if (hitThisTick)
		{
			hitThisTick = false;
			if (lastHitTick != NONE && (tick - lastHitTick) % CYCLE_LENGTH != 0)
			{
				pidSwapTick = tick;
			}
			lastHitTick = tick;
		}

		if (lastHitTick == NONE || tick - lastHitTick > idleTimeout)
		{
			state = TickState.IDLE;
			lastHitTick = NONE;
			lastClickTick = NONE;
			lastSkipTick = NONE;
			pidSwapTick = NONE;
			return;
		}

		boolean onClickTick = tick - lastHitTick == clickOffset;
		boolean tooSoon = lastClickTick != NONE && tick - lastClickTick < CYCLE_LENGTH;
		// The regular click is due but there is no hit to click on, e.g. the hit is a tick late after a PID swap
		boolean clickMissed = !onClickTick && lastClickTick != NONE && tick - lastClickTick == CYCLE_LENGTH
			&& lastSkipTick < lastClickTick;

		if (onClickTick && !tooSoon)
		{
			state = TickState.CLICK;
			lastClickTick = tick;
		}
		else if (onClickTick || clickMissed)
		{
			state = TickState.SKIP;
			lastSkipTick = tick;
		}
		else
		{
			state = TickState.WAIT;
		}
	}

	/**
	 * @return whether a PID swap was detected within the last few ticks
	 */
	boolean isPidSwapRecent()
	{
		return pidSwapTick != NONE && tick - pidSwapTick < PID_SWAP_NOTICE_TICKS;
	}

	void reset()
	{
		tick = 0;
		hitThisTick = false;
		lastHitTick = NONE;
		lastClickTick = NONE;
		lastSkipTick = NONE;
		pidSwapTick = NONE;
		state = TickState.IDLE;
	}
}
