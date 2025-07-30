package com.dialogai.service;

import com.dialogai.config.DeepSeekConfig;
import com.dialogai.dto.ChatRequestDto;
import com.dialogai.entity.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;

/**
 * DeepSeek API服务
 * 
 * @author DialogAI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekService {

    private final DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper;

    /**
     * 发送消息到DeepSeek API
     */
    public String sendMessage(String userMessage, List<Message> conversationHistory) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(userMessage, conversationHistory);
            String requestJson = objectMapper.writeValueAsString(requestBody);

            // 创建HTTP请求
            HttpPost httpPost = new HttpPost(deepSeekConfig.getBaseUrl() + "/v1/chat/completions");
            httpPost.setHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON));

            // 发送请求
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(httpPost)) {

                if (response.getCode() == 200) {
                    String responseBody = readInputStream(response.getEntity().getContent());
                    return parseResponse(responseBody);
                } else {
                    log.error("DeepSeek API请求失败，状态码: {}", response.getCode());
                    throw new RuntimeException("DeepSeek API请求失败");
                }
            }
        } catch (Exception e) {
            log.error("调用DeepSeek API时发生错误", e);
            throw new RuntimeException("AI服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 流式对接AI接口，逐步推送内容到回调
     */
    public void streamChat(ChatRequestDto request, Consumer<String> onData) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(deepSeekConfig.getBaseUrl() + "/v1/chat/completions");
            httpPost.setHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "text/event-stream");
            httpPost.setHeader("Cache-Control", "no-cache");

            Map<String, Object> body = new HashMap<>();
            body.put("model", deepSeekConfig.getModel());
            body.put("messages", buildMessagesFromRequest(request));
            body.put("stream", true);
            body.put("temperature", 0.7);

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(body), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            log.info("[DeepSeek] 开始流式请求");
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                log.info("[DeepSeek] 收到响应，状态码: {}", response.getCode());
                
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    
                    String line;
                    int chunkCount = 0;
                    while ((line = reader.readLine()) != null) {
                        chunkCount++;
                        log.debug("[DeepSeek] 读取行 {}: {}", chunkCount, line);
                        
                        if (line.startsWith("data:")) {
                            String jsonData = line.substring(5).trim();
                            log.debug("[DeepSeek] 提取数据: {}", jsonData);
                            
                            if ("[DONE]".equals(jsonData)) {
                                log.info("[DeepSeek] 流式响应完成");
                                break;
                            }
                            
                            if (!jsonData.isEmpty()) {
                                try {
                                    JsonNode node = objectMapper.readTree(jsonData);
                                    JsonNode deltaNode = node.at("/choices/0/delta/content");
                                    if (deltaNode != null && !deltaNode.isNull() && !deltaNode.isMissingNode()) {
                                        String content = deltaNode.asText();
                                        if (!content.isEmpty()) {
                                            log.debug("[DeepSeek] 回调内容: {}", content);
                                            onData.accept(content);
                                        }
                                    }
                                } catch (Exception e) {
                                    log.warn("[DeepSeek] 解析JSON失败: {}, 原始数据: {}", e.getMessage(), jsonData);
                                }
                            }
                        }
                    }
                    log.info("[DeepSeek] 流式读取完成，总行数: {}", chunkCount);
                }
            }
        } catch (Exception e) {
            log.error("[DeepSeek] 流式接口调用失败", e);
            throw new RuntimeException("AI流式接口调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 读取输入流（Java 8兼容）
     */
    private String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.append(new String(buffer, 0, length, "UTF-8"));
        }
        return result.toString();
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String userMessage, List<Message> conversationHistory) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 添加历史消息
        if (conversationHistory != null) {
            for (Message message : conversationHistory) {
                Map<String, String> msg = new HashMap<>();
                msg.put("role", message.getRole().name().toLowerCase());
                msg.put("content", message.getContent());
                messages.add(msg);
            }
        }

        // 添加当前用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", deepSeekConfig.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);

        return requestBody;
    }

    /**
     * 解析API响应
     */
    private String parseResponse(String responseBody) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode choices = rootNode.get("choices");
        
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode firstChoice = choices.get(0);
            JsonNode message = firstChoice.get("message");
            if (message != null) {
                return message.get("content").asText();
            }
        }
        
        throw new RuntimeException("无法解析DeepSeek API响应");
    }

    private List<Map<String, String>> buildMessagesFromRequest(ChatRequestDto request) {
        List<Map<String, String>> messages = new ArrayList<>();
        // 这里可以加入历史消息的逻辑
        // for (Message message : conversationHistory) { ... }

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", request.getMessage());
        messages.add(userMsg);
        return messages;
    }
} 