package com.dalread.helper;

import android.content.Context;

import com.dalread.base.EnumMessageAction;
import com.dalread.base.EnumMessageType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.ChatMessage;
import com.dalread.util.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {

    private Context context;
    private SharedPreferencesDB sharedPreferences;
    private CollectionReference messageRef;
    private List<ListenerRegistration> listenerRegistrations;
    private String editedMessageId;

    public FirestoreHelper(Context context, SharedPreferencesDB sharedPreferences, int chatRoomId) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        messageRef = FirebaseFirestore.getInstance().collection("channels/" + chatRoomId + "/messages");
        listenerRegistrations = new ArrayList<>();
    }

    public void listenToMessageList(EventListener<QuerySnapshot> onEventListener) {
        ListenerRegistration listenerRegistration = messageRef.orderBy("created", Query.Direction.DESCENDING)
                .limit(Constant.CHAT_MESSAGE_LIMIT)
                .addSnapshotListener(onEventListener);
        listenerRegistrations.add(listenerRegistration);
    }

    public void listenToMessageList(EventListener<QuerySnapshot> onEventListener, DocumentSnapshot lastVisibleDocumentSnapshot) {
        ListenerRegistration listenerRegistration = messageRef.orderBy("created", Query.Direction.DESCENDING)
                .limit(Constant.CHAT_MESSAGE_LIMIT)
                .startAfter(lastVisibleDocumentSnapshot)
                .addSnapshotListener(onEventListener);
        listenerRegistrations.add(listenerRegistration);
    }

    public void stopListenToMessageList() {
        for (ListenerRegistration listenerRegistration : listenerRegistrations) {
            listenerRegistration.remove();
        }
    }

    public String getEditedMessageId() {
        return editedMessageId;
    }

    public void setEditedMessageId(String editedMessageId) {
        this.editedMessageId = editedMessageId;
    }

    public void addTextMessage(String text, OnSuccessListener<? super DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("action", EnumMessageAction.ADD.getId());
        dataMap.put("content", text);
        dataMap.put("created", new Timestamp(new Date()));
        dataMap.put("senderId", sharedPreferences.getUid());
        dataMap.put("senderName", sharedPreferences.getUserName(context));
        dataMap.put("type", EnumMessageType.TEXT.getId());
        messageRef.add(dataMap)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void editTextMessage(String text, OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("action", EnumMessageAction.EDIT.getId());
        dataMap.put("content", text);
        dataMap.put("updated", new Timestamp(new Date()));
        messageRef.document(editedMessageId)
                .update(dataMap)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
        editedMessageId = "";
    }

    public void addVoiceMessage(String downloadURL, String duration, OnSuccessListener<? super DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("action", EnumMessageAction.ADD.getId());
        dataMap.put("created", new Timestamp(new Date()));
        dataMap.put("downloadURL", downloadURL);
        dataMap.put("duration", duration);
        dataMap.put("senderId", sharedPreferences.getUid());
        dataMap.put("senderName", sharedPreferences.getUserName(context));
        dataMap.put("type", EnumMessageType.VOICE.getId());
        messageRef.add(dataMap)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public ChatMessage createFakeVoiceMessage(String downloadURL, String duration) {
        ChatMessage message = new ChatMessage();
        message.setAction(EnumMessageAction.ADD.getId());
        message.setCreated(new Date());
        message.setDownloadURL(downloadURL);
        message.setDuration(duration);
        message.setSenderId(sharedPreferences.getUid());
        message.setSenderName(sharedPreferences.getUserName(context));
        message.setType(EnumMessageType.VOICE.getId());
        message.setMessageId(downloadURL);
        return message;
    }

    public void addPhotoMessage(String downloadURL, String thumbnailURL, OnSuccessListener<? super DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("action", EnumMessageAction.ADD.getId());
        dataMap.put("created", new Timestamp(new Date()));
        dataMap.put("downloadURL", downloadURL);
        dataMap.put("senderId", sharedPreferences.getUid());
        dataMap.put("senderName", sharedPreferences.getUserName(context));
        dataMap.put("type", EnumMessageType.PHOTO.getId());
        dataMap.put("thumbnailURL", thumbnailURL);
        messageRef.add(dataMap)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public ChatMessage createFakePhotoMessage(String downloadURL, String thumbnailURL) {
        ChatMessage message = new ChatMessage();
        message.setAction(EnumMessageAction.ADD.getId());
        message.setCreated(new Date());
        message.setDownloadURL(downloadURL);
        message.setSenderId(sharedPreferences.getUid());
        message.setSenderName(sharedPreferences.getUserName(context));
        message.setThumbnailURL(thumbnailURL);
        message.setType(EnumMessageType.PHOTO.getId());
        message.setMessageId(downloadURL);
        return message;
    }

    public void deleteMessage(String id, OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("action", EnumMessageAction.DELETE.getId());
        dataMap.put("content", "Deleted message");
        dataMap.put("downloadURL", "");
        dataMap.put("thumbnailURL", "");
        dataMap.put("updated", new Timestamp(new Date()));
        messageRef.document(id)
                .update(dataMap)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
