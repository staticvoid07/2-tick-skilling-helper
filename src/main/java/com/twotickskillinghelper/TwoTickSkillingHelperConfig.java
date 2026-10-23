package com.twotickskillinghelper;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("twotickskillinghelper")
public interface TwoTickSkillingHelperConfig extends Config
{
	@ConfigItem(
		keyName = "clickTickOffset",
		name = "Click tick offset",
		description = "Ticks after the attacker's hit lands on which to click.<br>0 = the same tick as the hit, 1 = the tick after",
		position = 0
	)
	@Range(max = 1)
	default int clickTickOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "idleTimeout",
		name = "Idle timeout",
		description = "Ticks without being hit before the indicator goes idle",
		position = 1
	)
	@Range(min = 2, max = 10)
	default int idleTimeout()
	{
		return 4;
	}

	@ConfigItem(
		keyName = "showWhenIdle",
		name = "Show when idle",
		description = "Keep the indicator on screen when no attacker is hitting you",
		position = 2
	)
	default boolean showWhenIdle()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightTarget",
		name = "Highlight target",
		description = "Outline the NPC you are skilling on (e.g. the fishing spot) on click ticks",
		position = 3
	)
	default boolean highlightTarget()
	{
		return true;
	}

	@ConfigItem(
		keyName = "clickColor",
		name = "Click color",
		description = "Color shown on ticks where you should click",
		position = 4
	)
	default Color clickColor()
	{
		return new Color(0, 200, 83);
	}

	@ConfigItem(
		keyName = "waitColor",
		name = "Wait color",
		description = "Color shown on off-cycle ticks",
		position = 5
	)
	default Color waitColor()
	{
		return new Color(60, 60, 60);
	}

	@ConfigItem(
		keyName = "skipColor",
		name = "Skip color",
		description = "Color shown when a click must be skipped to lose a tick after a PID swap",
		position = 6
	)
	default Color skipColor()
	{
		return new Color(255, 145, 0);
	}
}
