package com.twotickskillinghelper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum TickState
{
	/**
	 * No attacker hits seen recently; the cycle is unknown.
	 */
	IDLE("IDLE"),
	/**
	 * Click the skilling target this tick.
	 */
	CLICK("CLICK"),
	/**
	 * Off-cycle tick; do nothing.
	 */
	WAIT("WAIT"),
	/**
	 * Would be a click tick, but the cycle just shifted (PID swap), so this click must be skipped to lose a tick.
	 */
	SKIP("SKIP");

	private final String label;
}
