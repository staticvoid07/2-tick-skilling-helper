package com.twotickskillinghelper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

/**
 * Outlines the skilling target (e.g. the fishing spot) on click ticks.
 */
class TwoTickSkillingHelperTargetOverlay extends Overlay
{
	private static final int OUTLINE_WIDTH = 3;
	private static final int OUTLINE_FEATHER = 4;

	private final TwoTickSkillingHelperPlugin plugin;
	private final TwoTickSkillingHelperConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	TwoTickSkillingHelperTargetOverlay(TwoTickSkillingHelperPlugin plugin, TwoTickSkillingHelperConfig config,
		ModelOutlineRenderer modelOutlineRenderer)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		NPC target = plugin.getTarget();
		if (!config.highlightTarget() || target == null || plugin.getState() != TickState.CLICK)
		{
			return null;
		}

		modelOutlineRenderer.drawOutline(target, OUTLINE_WIDTH, config.clickColor(), OUTLINE_FEATHER);
		return null;
	}
}
