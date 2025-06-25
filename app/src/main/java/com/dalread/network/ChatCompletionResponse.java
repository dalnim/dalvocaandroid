package com.dalread.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatCompletionResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("object")
    private String object;

    @SerializedName("created")
    private long created;

    @SerializedName("model")
    private String model;

    @SerializedName("usage")
    private Usage usage;

    @SerializedName("choices")
    private List<Choice> choices;

    public String getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public long getCreated() {
        return created;
    }

    public String getModel() {
        return model;
    }

    public Usage getUsage() {
        return usage;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Usage {
        @SerializedName("prompt_tokens")
        private int promptTokens;

        @SerializedName("completion_tokens")
        private int completionTokens;

        @SerializedName("total_tokens")
        private int totalTokens;

        public int getPromptTokens() {
            return promptTokens;
        }

        public int getCompletionTokens() {
            return completionTokens;
        }

        public int getTotalTokens() {
            return totalTokens;
        }
    }

    public static class Choice {
        @SerializedName("message")
        private Message message;

        @SerializedName("finish_reason")
        private String finishReason;

        @SerializedName("index")
        private int index;

        public Message getMessage() {
            return message;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Message {
        @SerializedName("role")
        private String role;

        @SerializedName("content")
        private String content;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChatCompletionResponse {\n");
        sb.append("  id: ").append(id).append("\n");
        sb.append("  object: ").append(object).append("\n");
        sb.append("  created: ").append(created).append("\n");
        sb.append("  model: ").append(model).append("\n");
        sb.append("  usage: {\n");
        sb.append("    promptTokens: ").append(usage.promptTokens).append("\n");
        sb.append("    completionTokens: ").append(usage.completionTokens).append("\n");
        sb.append("    totalTokens: ").append(usage.totalTokens).append("\n");
        sb.append("  }\n");
        sb.append("  choices: [\n");
        for (int i = 0; i < choices.size(); i++) {
            sb.append("    {\n");
            sb.append("      index: ").append(choices.get(i).index).append("\n");
            sb.append("      finishReason: ").append(choices.get(i).finishReason).append("\n");
            sb.append("      message: {\n");
            sb.append("        role: ").append(choices.get(i).message.role).append("\n");
            sb.append("        content: ").append(choices.get(i).message.content).append("\n");
            sb.append("      }\n");
            sb.append("    }");
            if (i < choices.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }
}
