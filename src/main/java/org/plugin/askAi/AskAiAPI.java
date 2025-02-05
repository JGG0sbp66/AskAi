package org.plugin.askAi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AskAiAPI {

    private static final String BASE_URL = AskAiMain.main.getConfig().getString("AskAiAPI.BASE_URL");
    private static final Gson gson = new Gson();
    private final ContextStorage contextStorage;

    public AskAiAPI(ContextStorage contextStorage) {
        this.contextStorage = contextStorage;
    }

    /**
     * 生成流式响应
     *
     * @param model    模型名称
     * @param prompt   提示词
     * @param playerId 玩家 UUID
     * @return 包含响应内容和上下文的复合对象
     * @throws IOException 如果请求失败
     */
    public ResponseResult generateCompletionStream(String model, String prompt, String playerName, UUID playerId) throws IOException {
        // 加载玩家上下文
        JsonArray context = contextStorage.loadContext(playerName, playerId);

        // BASE_URL异常处理
        URL url = null;
        if (BASE_URL != null && !BASE_URL.trim().isEmpty()) {
            try {
                url = new URL(BASE_URL);
            } catch (MalformedURLException e) {
                // 如果 BASE_URL 格式不正确，记录错误并抛出异常
                System.err.println("BASE_URL 格式有误: " + BASE_URL);
                throw new IllegalArgumentException("BASE_URL 格式有误: " + BASE_URL, e);
            }
        } else {
            // 如果 BASE_URL 为空或无效，记录错误并抛出异常
            System.err.println("BASE_URL 为空或无效.");
            throw new IllegalStateException("BASE_URL 为空或无效.");
        }

        // 创建http请求
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("prompt", prompt);
        requestBody.addProperty("stream", true);
        if (context != null) {
            requestBody.add("context", context);
        }

        // 发送请求体
        try (OutputStream os = connection.getOutputStream()){
            byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder responseBuilder = new StringBuilder();
        JsonArray lastContext = context;

        // 检查 HTTP 状态码
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP 请求失败，状态码: " + responseCode + "\n responseBody:" + requestBody);
        }

        // 读取相应数据
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                JsonObject chunk = gson.fromJson(line, JsonObject.class);
                if (chunk.has("response") && !chunk.get("response").isJsonNull()) {
                    responseBuilder.append(chunk.get("response").getAsString());
                }
                if (chunk.has("context") && !chunk.get("context").isJsonNull()) {
                    lastContext = chunk.get("context").getAsJsonArray();
                }
            }
        }

        // 保存更新后的context
        contextStorage.saveContext(playerName, playerId, lastContext);

        return new ResponseResult(responseBuilder.toString(), lastContext);
    }

    /**
     * 响应结果类
     */
    public static class ResponseResult {
        private final String response;
        private final JsonArray context;

        public ResponseResult(String response, JsonArray context) {
            this.response = response;
            this.context = context;
        }

        public String getResponse() {
            return response;
        }

        public JsonArray getContext() {
            return context;
        }
    }
}
