package com.dalread.network;
// CompletionsRequest.java
public class CompletionsRequest {
    private String model;
    private String prompt;
    private int max_tokens;
    private double temperature;
    private double top_p;
    private int n = 1;
    private boolean stream;
    private Integer logprobs;
    private String stop;

    public CompletionsRequest(String model, String prompt, int max_tokens, double temperature) {
        this.model = model;
        this.prompt = prompt;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
        // Set other parameters as needed, or add additional constructors/ setters
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
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

    public double getTop_p() {
        return top_p;
    }

    public void setTop_p(double top_p) {
        this.top_p = top_p;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public Integer getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Integer logprobs) {
        this.logprobs = logprobs;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }
}

