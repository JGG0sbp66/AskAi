package org.plugin.askAi.StreamMessage;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class StreamingMessage {

    private final JavaPlugin plugin;

    public StreamingMessage(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 通用流式输出方法
     *
     * @param player   目标玩家
     * @param message  消息内容
     * @param interval 发送间隔（单位：tick）
     * @param updater  消息更新器（例如 BossBar 或 ActionBar）
     */
    public void StreamMessage(Player player, String message, long interval, StreamMessageUpdater updater) {
        final boolean[] isPaused = {false};
        // 按照换行符切割，去除空值
        List<String> lines = Arrays.stream(message.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .toList();
//        getLogger().info("lines:{}", lines);

        new BukkitRunnable() {
            int lineIndex = 0;
            int charIndex = 0;
            final StringBuilder currentLineChar = new StringBuilder();

            @Override
            public void run() {
                // 换行时暂停执行
                if (isPaused[0]) {
                    return;
                }

                if (lineIndex >= lines.size()) {
                    // 控制台输出ai语句
                    getLogger().info("from ai:{}", message);

                    // 消息发送完成，延迟 3 秒后清理资源
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            updater.cleanup();
                        }
                    }.runTaskLater(plugin, 60L);

                    // 清理资源
                    this.cancel();
                    return;
                }

                String currentLine = lines.get(lineIndex);

                currentLineChar.append(currentLine.charAt(charIndex));
                updater.update(player, currentLineChar.toString());
                charIndex++;

                // 当前行已经显示完毕，切换下一行
                if (charIndex >= currentLine.length()) {
                    isPaused[0] = true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            isPaused[0] = false;
                            lineIndex++;
                            // 重置索引和字符串
                            charIndex = 0;
                            currentLineChar.setLength(0);
                        }
                    }.runTaskLater(plugin, 25L);
                }
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    /**
     * 创建 BossBar 更新器（支持 self/all 模式）
     */
    public StreamMessageUpdater createBossBarUpdater(StreamMode mode) {
        return new StreamMessageUpdater() {
            private BossBar bossBar;

            @Override
            public void update(Player player, String message) {
                if (bossBar == null) {
                    bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);

                    if (mode == StreamMode.SELF) {
                        bossBar.addPlayer(player);
                    } else {
                        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
                    }
                }
                bossBar.setTitle(message);
            }

            @Override
            public void cleanup() {
                if (bossBar != null) {
                    bossBar.removeAll();
                }
            }
        };
    }

    /**
     * 创建 ActionBar 更新器（支持 self/all 模式）
     */
    public StreamMessageUpdater createActionBarUpdater(StreamMode mode) {
        return new StreamMessageUpdater() {
            @Override
            public void update(Player player, String message) {
                if (mode == StreamMode.SELF) {
                    player.sendActionBar(message);
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendActionBar(message));
                }
            }

            @Override
            public void cleanup() {
                // ActionBar 无需清理
            }
        };
    }
}

