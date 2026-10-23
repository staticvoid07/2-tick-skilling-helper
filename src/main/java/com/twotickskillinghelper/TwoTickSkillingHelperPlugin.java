package com.twotickskillinghelper;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "2 Tick Skilling Helper",
	description = "Helps with 2-tick skilling methods",
	tags = {"tick", "2t", "skilling", "tick manipulation", "fishing"}
)
public class TwoTickSkillingHelperPlugin extends Plugin
{
	/**
	 * Hitsplats caused by an attack (including blocked 0s). Poison, venom, heals etc. tick on their own timers and
	 * would break the cycle.
	 */
	private static final Set<Integer> ATTACK_HITSPLATS = ImmutableSet.of(
		HitsplatID.BLOCK_ME, HitsplatID.BLOCK_OTHER,
		HitsplatID.DAMAGE_ME, HitsplatID.DAMAGE_OTHER,
		HitsplatID.DAMAGE_ME_CYAN, HitsplatID.DAMAGE_OTHER_CYAN,
		HitsplatID.DAMAGE_ME_ORANGE, HitsplatID.DAMAGE_OTHER_ORANGE,
		HitsplatID.DAMAGE_ME_YELLOW, HitsplatID.DAMAGE_OTHER_YELLOW,
		HitsplatID.DAMAGE_ME_WHITE, HitsplatID.DAMAGE_OTHER_WHITE,
		HitsplatID.DAMAGE_MAX_ME, HitsplatID.DAMAGE_MAX_ME_CYAN, HitsplatID.DAMAGE_MAX_ME_ORANGE,
		HitsplatID.DAMAGE_MAX_ME_YELLOW, HitsplatID.DAMAGE_MAX_ME_WHITE,
		HitsplatID.DAMAGE_ME_POISE, HitsplatID.DAMAGE_OTHER_POISE, HitsplatID.DAMAGE_MAX_ME_POISE
	);

	@Inject private Client client;
	@Inject private TwoTickSkillingHelperConfig config;
	@Inject private OverlayManager overlayManager;
	@Inject private TwoTickSkillingHelperOverlay overlay;
	@Inject private TwoTickSkillingHelperTargetOverlay targetOverlay;

	private final TickCycleTracker tracker = new TickCycleTracker();

	/**
	 * The NPC the player is skilling on, e.g. the fishing spot.
	 */
	@Getter
	private NPC target;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(targetOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(targetOverlay);
		tracker.reset();
		target = null;
	}

	TickState getState()
	{
		return tracker.getState();
	}

	boolean isPidSwapRecent()
	{
		return tracker.isPidSwapRecent();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() == client.getLocalPlayer()
			&& ATTACK_HITSPLATS.contains(event.getHitsplat().getHitsplatType()))
		{
			tracker.hitObserved();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		tracker.onTick(config.clickTickOffset(), config.idleTimeout());
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		Actor localPlayer = client.getLocalPlayer();
		if (event.getSource() != localPlayer || !(event.getTarget() instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) event.getTarget();
		// Don't treat the attacker as the skilling target if the player retaliates
		if (npc.getInteracting() != localPlayer)
		{
			target = npc;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc() == target)
		{
			target = null;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			tracker.reset();
			target = null;
		}
	}

	@Provides
	TwoTickSkillingHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TwoTickSkillingHelperConfig.class);
	}
}
