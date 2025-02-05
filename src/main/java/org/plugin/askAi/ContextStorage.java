package org.plugin.askAi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

// 将上下文对话文件存储在本地，根据不同的UUID来创建不同的对话
public class ContextStorage {
    //需要序列化为json字符串
    private static final Gson gson = new Gson();
    private static final String CONTEXT_DIR = AskAiMain.main.getConfig().getString("ContextPath");

    /**
     * 初始化上下文存储目录
     */
    public ContextStorage() {
        if (CONTEXT_DIR != null) {
            File dir = new File(CONTEXT_DIR);
            if (!dir.exists()) {
                try {
                    if (!dir.mkdirs()) {
                        // 如果 mkdirs() 返回 false，表示目录创建失败
                        throw new IOException("创建目录失败: " + CONTEXT_DIR);
                    }
                } catch (IOException e) {
                    // 记录异常信息，可以使用日志库（如 SLF4J 或 Log4j）记录日志
                    System.err.println("已记录异常信息: " + e.getMessage());
                    // 可以选择抛出异常，或者记录日志后继续运行
                    throw new IllegalStateException("该目录不可用: " + CONTEXT_DIR, e);
                }
            }
        } else {
            // 如果 CONTEXT_DIR 为 null，抛出异常或记录错误
            throw new IllegalStateException("目录不存在，请检查配置文件");
        }
    }

    /**
     * 保存玩家的上下文到文件
     *
     * @param playerName 玩家名
     * @param playerId 玩家 UUID
     * @param context  上下文
     */
    public void saveContext(String playerName, UUID playerId, JsonArray context) throws IOException {
        File file = new File(CONTEXT_DIR, playerName + "_" + playerId + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中加载玩家的上下文
     *
     * @param playerId 玩家 UUID
     * @return 上下文（如果文件不存在，返回 null）
     */
    public JsonArray loadContext(String playerName, UUID playerId) {
        File file = new File(CONTEXT_DIR, playerName + "_" + playerId + ".json");
        if (!file.exists()) {
            return null; // 文件不存在，返回 null
        }

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, JsonArray.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
