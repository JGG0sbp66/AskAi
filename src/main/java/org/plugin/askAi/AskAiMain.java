package org.plugin.askAi;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.plugin.askAi.StreamMessage.StreamingMessage;

import java.util.Objects;

public final class AskAiMain extends JavaPlugin {

    // 生成全局变量main，用于调用配置文件参数
    static AskAiMain main;

    @Override
    public void onEnable() {

        // 生成配置文件
        saveDefaultConfig();
        main = this;

        // 注册命令和补全
        ContextStorage contextStorage = new ContextStorage();
        AskAiAPI askAiAPI = new AskAiAPI(contextStorage);
        StreamingMessage streamingMessage = new StreamingMessage(this);

        Objects.requireNonNull(this.getCommand("askai")).setExecutor(new AskAiCommand(askAiAPI, streamingMessage));
        Objects.requireNonNull(getCommand("askai")).setTabCompleter(new AskAiTabCompleter());

        // Plugin startup logic
        say("   §9___         §b__    §6___    §e_");
        say("  §9/ _ |  §3___  §b/ /__ §6/ _ |  §e(_)    §2AskAi §bv1.0");
        say(" §9/ __ | §3(_-< §b/  '_/§6/ __ | §e/ /     §8Produced by JGG0sbp66");
        say("§9/_/ |_/§3/___/§b/_/\\_\\§6/_/ |_|§e/_/      §9AskAi插件: §a[已加载]");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        say("   §9___         §b__    §6___    §e_");
        say("  §9/ _ |  §3___  §b/ /__ §6/ _ |  §e(_)    §2AskAi §bv1.0");
        say(" §9/ __ | §3(_-< §b/  '_/§6/ __ | §e/ /     §8Produced by JGG0sbp66");
        say("§9/_/ |_/§3/___/§b/_/\\_\\§6/_/ |_|§e/_/      §9AskAi插件: §c[已卸载]");
    }

    public void say(String s){
        CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage(s);
    }
}
