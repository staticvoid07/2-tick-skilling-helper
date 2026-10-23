package com.twotickskillinghelper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TwoTickSkillingHelperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TwoTickSkillingHelperPlugin.class);
		RuneLite.main(args);
	}
}
