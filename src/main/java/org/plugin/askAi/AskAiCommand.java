package org.plugin.askAi;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.plugin.askAi.StreamMessage.StreamMessageUpdater;
import org.plugin.askAi.StreamMessage.StreamMode;
import org.plugin.askAi.StreamMessage.StreamingMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AskAiCommand implements CommandExecutor {

    private final AskAiAPI askAiAPI;
    private final StreamingMessage streamingMessage;

    public AskAiCommand(AskAiAPI askAiAPI, StreamingMessage streamingMessage) {
        this.askAiAPI = askAiAPI;
        this.streamingMessage = streamingMessage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        String model = AskAiMain.main.getConfig().getString("AskAiAPI.model");

        // 获取玩家信息
        Player player = (Player) commandSender;
        String playerName = player.getDisplayName();
        UUID playerUUID = player.getUniqueId();

        // 设置默认值
        String Updater = "actionbar";
        String Mode = "self";
        StringBuilder sbprompt = new StringBuilder();

        // 解析参数
        List<String> UpdaterOptions = Arrays.asList("bossbar", "actionbar");
        List<String> ModeOptions = Arrays.asList("self", "all");

        for (String param : strings) {
            if (UpdaterOptions.contains(param)) {
                Updater = param;
            } else if (ModeOptions.contains(param)) {
                Mode = param;
            } else {
                sbprompt.append(" ").append(param);
            }
        }

        if (sbprompt.isEmpty()) { // 若提示词长度为0，则说明用户未输入提示词
            sayError(player);
        } else {
            String prompt = sbprompt.toString();

            // 获取更新器
            StreamMessageUpdater updater = chooseUpdater(Updater, Mode, player);
            checkUpdate(updater, player);

            // 发送消息
            asynHTTPrequest(player, model, prompt, playerName, playerUUID, updater);
        }

        return false;
    }

    // 选择StreamMessageUpdater
    public StreamMessageUpdater chooseUpdater(String Updater, String Mode, Player player) {
        StreamMessageUpdater updater = null;

        if (Updater.equalsIgnoreCase("bossbar")) {
            updater = streamingMessage.createBossBarUpdater(
                    Mode.equalsIgnoreCase("self") ? StreamMode.SELF : StreamMode.ALL
            );
        } else if (Updater.equalsIgnoreCase("actionbar")) {
            updater = streamingMessage.createActionBarUpdater(
                    Mode.equalsIgnoreCase("self") ? StreamMode.SELF : StreamMode.ALL
            );
        } else {
            sayError(player);
        }

        return updater;
    }

    // 检查updater
    public void checkUpdate(StreamMessageUpdater updater,Player player) {
        if(updater == null){
            player.sendMessage("参数填写错误，请检查命令");
            throw new NullPointerException("Updater为空，可能是是玩家参数填写错误");
        }
    }

    // 异步处理 HTTP 请求
    public void asynHTTPrequest(Player player, String model, String prompt, String playerName, UUID playerUUID, StreamMessageUpdater updater) {
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(AskAiMain.class), () -> {
            try {
                AskAiAPI.ResponseResult result = askAiAPI.generateCompletionStream(model, prompt, playerName, playerUUID);

                // 在主线程中发送流式消息
                Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(AskAiMain.class), () -> {
                    streamingMessage.StreamMessage(player, result.getResponse(), 1L, updater);
                });
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage("请求失败，请稍后重试");
            }
        });
    }

    public void sayError(Player player) {

        player.sendMessage("指令用法: " +
                "\n 完整命令: /askai <StreamMessageUpdater> <StreamMode> <prompt>" +
                "\n <StreamMessageUpdater> 参数为 BossBar 或者 ActionBar " +
                "\n <StreamMode> 参数为 ALL 或者 SELF " +
                "\n <prompt> 参数为提示词，即你想询问ai的语句，例如：你是一只猫娘" +
                "\n ps.支持参数智能缺省");

    }
}
