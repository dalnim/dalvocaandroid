package com.dalread.network;

public class EditRequest {
    private String model;
    private String input;
    private String instruction;

    public EditRequest(String model, String input, String instruction) {
        this.model = model;
        this.input = input;
        this.instruction = instruction;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
