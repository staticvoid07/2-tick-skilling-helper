# 2 Tick Skilling Helper

A RuneLite plugin that helps with 2-tick skilling methods.

Many 2-tick methods (such as 2-tick fishing) rely on an NPC, or another player on a PvP world, attacking you every
2 ticks. This plugin follows the rhythm of those attacks and tells you on which tick to click your skilling target.

## Features

- **On-screen indicator** showing what to do each tick:
  - **CLICK**: click the target (e.g. the fishing spot) this tick
  - **WAIT**: off-cycle tick, do nothing
  - **SKIP**: don't click; this is the tick you lose when the cycle shifts after a PID swap
  - **IDLE**: nothing is hitting you
- **Target highlight**: outlines the NPC you are skilling on (e.g. the fishing spot) on click ticks.
- **PID swap handling**: on PvP worlds PID reshuffles often, which moves the attacker's hits by one tick. The plugin
  detects the shift, shows a "PID swap" notice, and makes you lose a tick (3 ticks between clicks) so you are back in
  cycle with the new PIDs.

## How it works

The timing comes from the hitsplats that land on you, including blocked 0s. Poison, venom, heals and other splats
that run on their own timers are ignored. Hits normally land every 2 ticks. A gap of 1 or 3 ticks means the cycle has
shifted, and the plugin follows the new rhythm. Clicks are never shown less than 2 ticks apart.

## Configuration

| Option            | Description                                                                         | Default |
|-------------------|-------------------------------------------------------------------------------------|---------|
| Click tick offset | Ticks after the hit lands on which to click: 0 = same tick, 1 = the tick after     | 0       |
| Idle timeout      | Ticks without being hit before the indicator goes idle                              | 4       |
| Show when idle    | Keep the indicator on screen when nothing is hitting you                            | On      |
| Highlight target  | Outline the skilling target on click ticks                                          | On      |
| Click color       | Color for CLICK                                                                     | Green   |
| Wait color        | Color for WAIT                                                                      | Grey    |
| Skip color        | Color for SKIP                                                                      | Orange  |

If clicking on CLICK doesn't give you 2-tick rolls, try setting **Click tick offset** to 1.

## Limitations

- Only one attacker is supported. Two attackers hitting on alternating ticks look like a PID swap every tick.

## Building

```
./gradlew build
./gradlew run   # launches RuneLite with the plugin loaded
```

## License

BSD 2-Clause, see [LICENSE](LICENSE).
