package com.dalread.network;

import com.dalread.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ChatGPTRequest {

    private List<ChatGPTRequest.Message> messages;
    private String model;
    private int max_tokens;
    private double temperature;
    private ChatGPTRequest.MessagesInRole messagesInRole;
    public ChatGPTRequest(List<ChatGPTRequest.Message> messages, String model, int max_tokens, double temperature) {
        this.messages = messages;
        this.model = model;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatGPTRequest.Message> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatGPTRequest.Message> messages) {
        this.messages = messages;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setStream(boolean stream, ChatGPTRequest.Message.Role role) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(new ChatGPTRequest.Message(role, stream ? "\n" : ""));
    }

    public ChatGPTRequest.MessagesInRole getMessagesInRole() {
        return ChatGPTRequest.MessagesInRole.getContentOfRole(messages);
    }
    public static class MessagesInRole {
        private String user;
        private String system;
        private String assistant;

        public MessagesInRole(String user, String system, String assistant) {
            this.user = user;
            this.system = system;
            this.assistant = assistant;
        }

        public String getSystem() {
            return system;
        }
        public String getUser() {
            return user;
        }
        public String getAssistant() {
            return assistant;
        }
        public static ChatGPTRequest.MessagesInRole getContentOfRole(List<ChatGPTRequest.Message> messages) {
            StringJoiner sjUser = new StringJoiner("\n");
            StringJoiner sjSystem = new StringJoiner("\n");
            StringJoiner sjAssistant = new StringJoiner("\n");
            for (ChatGPTRequest.Message message : messages) {
                String content = message.getContent();
                if (message.getRole() == ChatGPTRequest.Message.Role.user) {
                    sjUser.add(content);
                } else if (message.getRole() == ChatGPTRequest.Message.Role.system) {
                    sjSystem.add(content);
                } else if (message.getRole() == ChatGPTRequest.Message.Role.assistant) {
                    sjAssistant.add(content);
                }
            }
            return new ChatGPTRequest.MessagesInRole(StringUtils.trimIncludeWhitespace(sjUser.toString()),
                    StringUtils.trimIncludeWhitespace(sjSystem.toString()),
                    StringUtils.trimIncludeWhitespace(sjAssistant.toString()));
        }

    }
    public static class Message {
        public enum Role {
            system,
            user,
            assistant
        }

        private ChatGPTRequest.Message.Role role;
        private String content;

        public Message(ChatGPTRequest.Message.Role role, String content) {
            this.role = role;
            this.content = content;
        }

        public ChatGPTRequest.Message.Role getRole() {
            return role;
        }

        public void setRole(ChatGPTRequest.Message.Role role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChatGPTRequest{");
        builder.append("model=").append(model).append(", ");
        builder.append("max_tokens=").append(max_tokens).append(", ");
        builder.append("messages=[");
        for (ChatGPTRequest.Message message : messages) {
            builder.append("{role=").append(message.getRole().toString()).append(", ");
            builder.append("content=").append(message.getContent()).append("}");
        }
        builder.append("]}");
        return builder.toString();
    }
}