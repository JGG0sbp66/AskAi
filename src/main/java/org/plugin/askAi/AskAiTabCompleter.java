package org.plugin.askAi;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AskAiTabCompleter implements TabCompleter {

    private static final List<String> UPDATERS = Arrays.asList("bossbar", "actionbar");
    private static final List<String> MODES = Arrays.asList("self", "all");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1: // 第一个参数：updater 或 mode
                completions.addAll(UPDATERS);
                completions.addAll(MODES);
                break;
            case 2: // 第二个参数：mode 或 prompt
                String firstArg = args[0].toLowerCase();
                if (UPDATERS.contains(firstArg)) {
                    // 如果第一个参数是 updater，补全 mode
                    completions.addAll(MODES);
                } else if (MODES.contains(firstArg)) {
                    // 如果第一个参数是 mode，补全 prompt（无固定补全）
                }
                break;
            case 3: // 第三个参数：prompt
                // 无固定补全
                break;
        }

        // 过滤匹配当前输入的部分建议
        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}