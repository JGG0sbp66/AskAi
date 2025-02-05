package org.plugin.askAi.StreamMessage;

import org.bukkit.entity.Player;

// 定义流式消息更新接口
public interface StreamMessageUpdater {
    /**
     * 更新消息显示
     *
     * @param player  目标玩家
     * @param message 当前消息内容
     */
    void update(Player player, String message);

    /**
     * 清理资源（例如关闭 BossBar）
     */
    void cleanup();
}
