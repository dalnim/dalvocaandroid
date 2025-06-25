package com.dalread.network;

import java.util.List;

//이건 아무도 안쓰는거 같다.
public class ChatGPTResponse {
    private List<ChatGPTChoice> choices;
    private Usage usage;

    public List<ChatGPTChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<ChatGPTChoice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int system_tokens;
        private int assistant_tokens;
        private int total_tokens;

        public int getPrompt_tokens() {
            return prompt_tokens;
        }

        public void setPrompt_tokens(int prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
        }

        public int getCompletion_tokens() {
            return completion_tokens;
        }

        public void setCompletion_tokens(int completion_tokens) {
            this.completion_tokens = completion_tokens;
        }

        public int getSystem_tokens() {
            return system_tokens;
        }

        public void setSystem_tokens(int system_tokens) {
            this.system_tokens = system_tokens;
        }

        public int getAssistant_tokens() {
            return assistant_tokens;
        }

        public void setAssistant_tokens(int assistant_tokens) {
            this.assistant_tokens = assistant_tokens;
        }

        public int getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
        }
    }

    public static class ChatGPTChoice {
        private String prompt;
        private String text;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}