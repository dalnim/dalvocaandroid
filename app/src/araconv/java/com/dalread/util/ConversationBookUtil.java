package com.dalread.util;

import com.dalread.base.EnumLanguage;
import com.dalread.model.WordListType;

public class ConversationBookUtil implements ChatGptWebUtil.ConversationBookUtilInterface{
    @Override
    public boolean isConversationBook(WordListType type, int bookId) {
        if (type == WordListType.USER_VOCA_BOOK_LOCAL) {
            return true;
        }
        if (bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.BASIC_EXPRESSION_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.BASIC_EXPRESSION_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.BASIC_EXPRESSION_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.BASIC_EXPRESSION_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.BASIC_EXPRESSION_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.BASIC_EXPRESSION_6 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_COUNTER_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_COUNTER_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_COUNTER_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_COUNTER_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_ON_AIRPLANE_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_ON_AIRPLANE_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_ON_AIRPLANE_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_ON_AIRPLANE_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_ON_AIRPLANE_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_IMMIGRATION_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_IMMIGRATION_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT_IMMIGRATION_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_ASK_DIRECTION_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_ASK_DIRECTION_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_ASK_DIRECTION_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_ASK_DIRECTION_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_ASK_DIRECTION_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_SUBWAY_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_SUBWAY_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_SUBWAY_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_SUBWAY_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_SUBWAY_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_SUBWAY_6 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_SUBWAY_7 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_RENT_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_RENT_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT_RENT_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_ORDER_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_ORDER_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_ORDER_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_ORDER_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_ORDER_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_ORDER_6 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_ORDER_7 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_PAYING_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_PAYING_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_PAYING_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_PAYING_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_PAYING_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT_PAYING_6 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL_CHECK_IN_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL_CHECK_IN_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL_CHECK_IN_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL_CHECK_IN_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL_CHECK_OUT_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL_CHECK_OUT_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL_CHECK_OUT_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING_REFUND_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING_REFUND_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING_REFUND_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TOUR_PHOTO_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TOUR_PHOTO_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TOUR_PHOTO_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TOUR_PHOTO_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TOUR_PHOTO_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TOUR_PHOTO_6 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_3 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_4 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_5 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_6 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_7 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_8 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_9 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE_10 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_EMERGENCY_1 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_EMERGENCY_2 ||
                bookId == Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_EMERGENCY_3 ) {
            return true;
        }
        return false;
    }

    public static int getBookBasicExpression(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.BASIC_EXPRESSION;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.BASIC_EXPRESSION;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.BASIC_EXPRESSION;
            default:
                return Constant.BOOK.ENG.CONVERSATION.BASIC_EXPRESSION;
        }
    }
    public static int getBookBasicExpressionGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.BASIC_EXPRESSION; //중국어는 서버에 GROUP 데이타를 아직 안만들었다.
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.BASIC_EXPRESSION; //일본어도 서버에 GROUP 데이타를 아직 안만들었다.
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.BASIC_EXPRESSION; //영어처럼 업데이트 필요
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.BASIC_EXPRESSION;
        }
    }
    public static int getBookTravelAirport(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_AIRPORT;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_AIRPORT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_AIRPORT;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_AIRPORT;
        }
    }
    public static int getBookTravelAirportGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_AIRPORT;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_AIRPORT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_AIRPORT;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_AIRPORT;
        }
    }
    public static int getBookTravelHotel(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_HOTEL;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_HOTEL;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_HOTEL;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_HOTEL;
        }
    }
    public static int getBookTravelHotelGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_HOTEL;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_HOTEL;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_HOTEL;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_HOTEL;
        }
    }
    public static int getBookTravelTransport(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TRANSPORT;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_TRANSPORT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TRANSPORT;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_TRANSPORT;
        }
    }
    public static int getBookTravelTransportGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TRANSPORT;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_TRANSPORT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TRANSPORT;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TRANSPORT;
        }
    }
    public static int getBookTravelRestaurant(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_RESTAURANT;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_RESTAURANT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_RESTAURANT;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_RESTAURANT;
        }
    }
    public static int getBookTravelRestaurantGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_RESTAURANT;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_RESTAURANT;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_RESTAURANT;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_RESTAURANT;
        }
    }
    public static int getBookTravelShopping(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_SHOPPING;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_SHOPPING;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_SHOPPING;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_SHOPPING;
        }
    }
    public static int getBookTravelShoppingGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_SHOPPING;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_SHOPPING;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_SHOPPING;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_SHOPPING;
        }
    }
    public static int getBookTravelTour(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TOUR;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_TOUR;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TOUR;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_TOUR;
        }
    }
    public static int getBookTravelTourGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TOUR;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_TOUR;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TOUR;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TOUR;
        }
    }
    public static int getBookTravelTelephone(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TELEPHONE;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_TELEPHONE;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TELEPHONE;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_TELEPHONE;
        }
    }
    public static int getBookTravelTelephoneGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_TELEPHONE;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_TELEPHONE;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_TELEPHONE;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_TELEPHONE;
        }
    }
    public static int getBookTravelEmergency(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_EMERGENCY;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_EMERGENCY;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_EMERGENCY;
            default:
                return Constant.BOOK.ENG.CONVERSATION.TRAVEL_EMERGENCY;
        }
    }
    public static int getBookTravelEmergencyGroup(EnumLanguage enumLanguage) {
        switch (enumLanguage) {
            case CHINESE_SIMPLIFIED:
                return Constant.BOOK.CH_S.CONVERSATION.TRAVEL_EMERGENCY;
            case JAPANESE:
                return Constant.BOOK.JP.CONVERSATION.TRAVEL_EMERGENCY;
            case KOREAN:
                return Constant.BOOK.KO.CONVERSATION.TRAVEL_EMERGENCY;
            default:
                return Constant.BOOK.ENG.CONVERSATION_GROUP.TRAVEL_EMERGENCY;
        }
    }
}
