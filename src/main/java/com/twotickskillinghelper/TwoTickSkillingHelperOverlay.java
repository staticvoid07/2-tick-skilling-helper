package com.twotickskillinghelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * On-screen box telling the player whether to click this tick.
 */
class TwoTickSkillingHelperOverlay extends Overlay
{
	private static final int WIDTH = 110;
	private static final int HEIGHT = 44;
	private static final int NOTICE_HEIGHT = 16;
	private static final Color IDLE_COLOR = new Color(30, 30, 30, 150);
	private static final Font LABEL_FONT = FontManager.getRunescapeBoldFont().deriveFont(28f);
	private static final Font NOTICE_FONT = FontManager.getRunescapeSmallFont();

	private final TwoTickSkillingHelperPlugin plugin;
	private final TwoTickSkillingHelperConfig config;

	@Inject
	TwoTickSkillingHelperOverlay(TwoTickSkillingHelperPlugin plugin, TwoTickSkillingHelperConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_CENTER);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		TickState state = plugin.getState();
		if (state == TickState.IDLE && !config.showWhenIdle())
		{
			return null;
		}

		boolean pidSwap = plugin.isPidSwapRecent();
		int height = pidSwap ? HEIGHT + NOTICE_HEIGHT : HEIGHT;

		graphics.setColor(backgroundColor(state));
		graphics.fillRect(0, 0, WIDTH, height);
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, WIDTH - 1, height - 1);

		graphics.setFont(LABEL_FONT);
		drawCentered(graphics, state.getLabel(), HEIGHT, 0, state == TickState.IDLE ? Color.GRAY : Color.WHITE);

		if (pidSwap)
		{
			graphics.setFont(NOTICE_FONT);
			drawCentered(graphics, "PID swap", NOTICE_HEIGHT, HEIGHT - 4, Color.WHITE);
		}

		return new Dimension(WIDTH, height);
	}

	private Color backgroundColor(TickState state)
	{
		switch (state)
		{
			case CLICK:
				return config.clickColor();
			case WAIT:
				return config.waitColor();
			case SKIP:
				return config.skipColor();
			default:
				return IDLE_COLOR;
		}
	}

	private static void drawCentered(Graphics2D graphics, String text, int boxHeight, int top, Color color)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		int x = (WIDTH - metrics.stringWidth(text)) / 2;
		int y = top + (boxHeight - metrics.getHeight()) / 2 + metrics.getAscent();

		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 1, y + 1);
		graphics.setColor(color);
		graphics.drawString(text, x, y);
	}
}
