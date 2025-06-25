package com.dalread.helper;

import java.util.ArrayList;
import java.util.List;

public class GptDialogueHelper {
    private List<Conversation> dialogue;

    public GptDialogueHelper() {
        dialogue = new ArrayList<>();
    }

    public void addMessage(Conversation message) {
        dialogue.add(message);
    }

    public List<Conversation> getDialogue() {
        return dialogue;
    }

    public enum Person {
        A, B
    }

    public static class Conversation {
        private static int nextId = 1;
        private int id;
        private Person person;
        private String text;

        public Conversation(Person person, String text) {
            this.id = nextId;
            this.person = person;
            this.text = text;
            nextId++;
        }

        public int getId() {
            return id;
        }

        public Person getPerson() {
            return person;
        }

        public String getText() {
            return text;
        }

        public String getTextWithPerson() {
            return person + " : " + text;
        }

        public String nextPersonWithbrackets() {
            String sufix =  " : [ ] ";
            if (person == Person.A) {
                return Person.B.toString() + sufix;
            }
            return Person.A.toString() + sufix;
        }
    }

}
