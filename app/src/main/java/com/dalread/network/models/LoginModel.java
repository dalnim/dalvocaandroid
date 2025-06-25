package com.dalread.network.models;

import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

/**
 * Created by JetVHS on 2/26/2017.
 */
public class LoginModel {
    @SerializedName("UID")
    private int uid;
    @SerializedName("EMAIL")
    private String email;
    @SerializedName("POINT_READING")
    private int pointReading;
    @SerializedName("NAME")
    private String name;
    @SerializedName("POINT_AMKI")
    private int pointVoca;
    @SerializedName("USER_ROLE")
    private int userRole;
    @SerializedName("USER_TYPE")
    private int userType;
    @SerializedName("SEX")
    private int sex;
    @SerializedName("AGE")
    private int age;
    @SerializedName("MAX_HOMEWORK")
    private int maxHomework;
    @SerializedName("MAX_QUIZ")
    private int maxQuiz;
    @SerializedName("MAX_HANJA_QUIZ")
    private int maxHanjaQuiz;
    @SerializedName("ALLOW_CHAT")
    private int allowChat;
    @SerializedName("SHARE_MY_RECORDING")
    private int shareMyRecording;

    public LoginModel() {
        this(0, Constant.BASE_BLANK, 0, Constant.BASE_BLANK);
    }

    public LoginModel(int uid, String email, int pointReading, String name) {
        this.uid = uid;
        this.email = email;
        this.pointReading = pointReading;
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPointReading() {
        return pointReading;
    }

    public void setPointReading(int pointReading) {
        this.pointReading = pointReading;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LoginModel{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", pointReading='" + pointReading + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public int getPointVoca() {
        return pointVoca;
    }

    public void setPointVoca(int pointVoca) {
        this.pointVoca = pointVoca;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMaxHomework() {
        return maxHomework;
    }

    public void setMaxHomework(int maxHomework) {
        this.maxHomework = maxHomework;
    }

    public int getMaxQuiz() {
        return maxQuiz;
    }

    public void setMaxQuiz(int maxQuiz) {
        this.maxQuiz = maxQuiz;
    }

    public int getMaxHanjaQuiz() {
        return maxHanjaQuiz;
    }

    public void setMaxHanjaQuiz(int maxHanjaQuiz) {
        this.maxHanjaQuiz = maxHanjaQuiz;
    }

    public int getAllowChat() {
        return allowChat;
    }

    public void setAllowChat(int allowChat) {
        this.allowChat = allowChat;
    }

    public int getShareMyRecording() {
        return shareMyRecording;
    }

    public void setShareMyRecording(int shareMyRecording) {
        this.shareMyRecording = shareMyRecording;
    }
}
