/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.community.model.openai.entity.chat;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Aliases;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * OpenAi API 格式的会话补全响应。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
public class OpenAiChatCompletionResponse {
    private static final ChatMessage EMPTY_RESPONSE = new AiMessage(StringUtils.EMPTY);

    private List<OpenAiChatCompletionChoice> choices;

    /**
     * 获取响应中的消息。
     *
     * @return 表示模型回复的 {@link ChatMessage}。
     */
    public ChatMessage message() {
        return extractMessage(OpenAiChatMessage::content, OpenAiChatMessage::toolCalls);
    }

    /**
     * 获取响应中的模型推理。
     *
     * @return 表示模型回复的 {@link ChatMessage}。
     */
    public ChatMessage reasoningContent() {
        return extractMessage(OpenAiChatMessage::reasoningContent, m -> null); // toolCalls 传 null
    }

    /**
     * 通用方法，提取消息内容并构建 AiMessage
     * @param contentExtractor 内容提取函数（如 OpenAiChatMessage::content）
     * @param toolCallsExtractor toolCalls 提取函数
     * @return 构建的 AiMessage，如果无数据则返回 EMPTY_RESPONSE
     */
    private ChatMessage extractMessage(
            Function<OpenAiChatMessage, Object> contentExtractor,
            Function<OpenAiChatMessage, List<ToolCall>> toolCallsExtractor) {
        if (CollectionUtils.isEmpty(choices)) {
            return EMPTY_RESPONSE;
        }
        OpenAiChatMessage openAiChatMessage = choices.get(0).message;
        if (openAiChatMessage == null) {
            return EMPTY_RESPONSE;
        }

        // 提取 content（支持 String 或其他类型）
        String content = Optional.ofNullable(contentExtractor.apply(openAiChatMessage))
                .filter(obj -> obj instanceof String)
                .map(obj -> (String) obj)
                .orElse(StringUtils.EMPTY);

        // 提取 toolCalls（允许传入 null 或实际提取逻辑）
        List<ToolCall> toolCalls = Optional.ofNullable(toolCallsExtractor.apply(openAiChatMessage))
                .orElse(Collections.emptyList());

        return new AiMessage(content, toolCalls);
    }

    /**
     * 模型响应消息。
     */
    public static class OpenAiChatCompletionChoice {
        @Aliases(@Alias("delta"))
        private OpenAiChatMessage message;
    }
}
