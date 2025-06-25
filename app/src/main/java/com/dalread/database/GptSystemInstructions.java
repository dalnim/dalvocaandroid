package com.dalread.database;

public class GptSystemInstructions {
    public static final String FIRST_CHAT_ROOM_USE_COMMON_1 = "\"You're an English teacher. I'm learning English\"";
    public static final String FIRST_CHAT_ROOM_USE_COMMON_2 = "\"Forget the previous conversation in the ChatGPT\"";
    public static final String FIRST_CHAT_ROOM_USE_FREE_TALKING_1 = "\"Let's have a free talking\"";
    public static final String FIRST_CHAT_ROOM_USE_FREE_TALKING_2 = "\"\"";
    public static final String FIRST_CHAT_ROOM_USE_DIALOGUE_1 = "\"I'll give you dialogue don't answer me with full whole dialogue back\"";
    public static final String FIRST_CHAT_ROOM_USE_DIALOGUE_2 = "\"A is person A. You're A. Say A sentence once and wait for my response.\"";
    public static final String FIRST_CHAT_ROOM_USE_VOCA_1 = "\"I want to practice a sentence or vocab\"";
    public static final String FIRST_CHAT_ROOM_USE_VOCA_2 = "\"Explain it in detail\"";

    public static final String START_NEW_CONVERSATION_1 = "\"Answer in English in the [ ]\"";
    public static final String START_NEW_CONVERSATION_2 = "\"Show Korean translation in << >> after English Answer.\" ";
    public static final String START_NEW_CONVERSATION_3 = "\"I want to practice with you based on these conversations.\" ";
    public static final String START_NEW_CONVERSATION_4 = "A1: Greet the customer and ask what they would like to order.\n" +
            "B1: Respond with the order, including a specific food item and its quantity.\n" +
            "A2: Ask the customer what they would like to drink.\n" +
            "B2: Respond with the drink order, including the type of drink and any additional specifications.\n" +
            "A4: Ask if there is anything else the customer would like to order.\n" +
            "B4: Respond appropriately based on the customer's answer.\n" +
            "A5: Ask if the customer's order is for here or to go.\n" +
            "B5: Respond appropriately based on the customer's answer.\n" +
            "A6: Provide the total cost of the order.\n" +
            "B6: Pay for the order and provide the appropriate amount.\n" +
            "A7: Thank the customer and wish them a pleasant meal.\n" +
            "B7: Respond appropriately to the thank you.";

    public static final String VOCA_KNOW_PROMPT_1 = "\"뜻을 설명해줘. 한국어로 해줘 \" ";
    public static final String VOCA_KNOW_PROMPT_2 = "\"같은 뜻의 다른 영어표현도 알려줘. 한국어로 해줘 \" ";

    public static final String INTRO = "";
    public static final String INTRO_1 = "";
    public static final String INTRO_2 = "";
    public static final String CORRECTION = "";
    public static final String ANSWER_STUDY_LANG = "\"Answer me in English\"";
    public static final String ANSWER_MOTHER_TONGUE = "\"Answer me in Korean\"";
    public static final String UNKNOWN = "";
    public static final String KNOWN = "";
}
