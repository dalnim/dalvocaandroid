package com.dalread.network;

import java.util.List;

public class EditResponse {
    private String object;
    private long created;
    private List<Choice> choices;
    private Usage usage;

    public String getObject() {
        return object;
    }

    public long getCreated() {
        return created;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public static class Choice {
        private String text;
        private int index;

        public String getText() {
            return text;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

        public int getPromptTokens() {
            return prompt_tokens;
        }

        public int getCompletionTokens() {
            return completion_tokens;
        }

        public int getTotalTokens() {
            return total_tokens;
        }
    }
}

