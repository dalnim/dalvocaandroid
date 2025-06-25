package com.dalread.util;

import com.dalread.base.EnumLanguage;

public class ConversationBookUtil {
    public static int getBookBasicExpression(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.BASIC_EXPRESSION;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.BASIC_EXPRESSION;
            default:
                return Constant.BOOK.ENG.CONVERSATION.BASIC_EXPRESSION;
        }
    }
    public static int getBookTravelAirport(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_AIRPORT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_AIRPORT;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_AIRPORT;
        }
    }
    public static int getBookTravelHotel(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_HOTEL;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_HOTEL;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_HOTEL;
        }
    }
    public static int getBookTravelTransport(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TRANSPORT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TRANSPORT;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_TRANSPORT;
        }
    }
    public static int getBookTravelRestaurant(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_RESTAURANT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_RESTAURANT;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_RESTAURANT;
        }
    }
    public static int getBookTravelShopping(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_SHOPPING;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_SHOPPING;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_SHOPPING;
        }
    }
    public static int getBookTravelTour(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TOUR;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TOUR;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_TOUR;
        }
    }
    public static int getBookTravelTelephone(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TELEPHONE;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TELEPHONE;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_TELEPHONE;
        }
    }
    public static int getBookTravelEmergency(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_EMERGENCY;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_EMERGENCY;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_EMERGENCY;
        }
    }

}
