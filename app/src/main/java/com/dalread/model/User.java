package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("UID")
    private int uid;
    @SerializedName("NAME")
    private String name;
    @SerializedName("SEX")
    private int sex;
    @SerializedName("AGE")
    private int age;
    @SerializedName("NATION")
    private String nation;
    @SerializedName("CITY")
    private String city;
    @SerializedName("FOLLOWING_COUNT")
    private int followingCount;
    @SerializedName("FOLLOWER_COUNT")
    private int followerCount;
    @SerializedName("MAX_HOMEWORK")
    private int maxHomework;
    @SerializedName("ALLOW_CHAT")
    private int allowChat;
    @SerializedName("SHARE_MY_RECORDING")
    private int shareMyRecording;
    @SerializedName("SCORE")
    private int score;
    @SerializedName("BIO")
    private String bio;
    @SerializedName("JOIN_DATE_TS")
    private long joinDateTS;
    @SerializedName("IS_BLOCK_USER")
    private int isBlockUser;
    @SerializedName("IS_FAVORITE_USER")
    private int isFavoriteUser;
    @SerializedName("IS_FOLLOWING_USER")
    private int isFollowingUser;
    @SerializedName("OPPONENT_NAME_BY_ME")
    private String oponentNameByMe;
    @SerializedName("OPPONENT_DESC_BY_ME")
    private String oponentDescByMe;
    @SerializedName("IS_USER_ONLINE")
    private int isUserOnline;
    @SerializedName("HAS_INTRODUCTION_FILE")
    private int hasIntroductionFile;
    @SerializedName("APP_NAME")
    private String appName;
    @SerializedName("CLIENT_TYPE")
    private String clientType;
    @SerializedName("langNative")
    private String langNative;
    @SerializedName("ALLOW_PUSH")
    private int allowPush;
    @SerializedName("USER_ROLE")
    private int userRole;
    @SerializedName("STUDY_ROLE")
    private int studyRole;
    @SerializedName("STUDY_ROLE_MAIN")
    private int studyRoleMain;
    @SerializedName("LAST_ACCESS_DATE_TS")
    private long lastAccessDateTS;
    private int index;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getMaxHomework() {
        return maxHomework;
    }

    public void setMaxHomework(int maxHomework) {
        this.maxHomework = maxHomework;
    }

    public boolean isAllowChat() {
        return allowChat == 1;
    }

    public void setAllowChat(int allowChat) {
        this.allowChat = allowChat;
    }

    public boolean isShareMyRecording() {
        return shareMyRecording == 1;
    }

    public void setShareMyRecording(int shareMyRecording) {
        this.shareMyRecording = shareMyRecording;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public long getJoinDateTS() {
        return joinDateTS;
    }

    public void setJoinDateTS(long joinDateTS) {
        this.joinDateTS = joinDateTS;
    }

    public boolean isBlockUser() {
        return isBlockUser == 1;
    }

    public void setIsBlockUser(int isBlockUser) {
        this.isBlockUser = isBlockUser;
    }

    public boolean isFavoriteUser() {
        return isFavoriteUser == 1;
    }

    public void setIsFavoriteUser(int isFavoriteUser) {
        this.isFavoriteUser = isFavoriteUser;
    }

    public boolean isFollowingUser() {
        return isFollowingUser == 1;
    }

    public void setIsFollowingUser(int isFollowingUser) {
        this.isFollowingUser = isFollowingUser;
    }

    public String getOponentNameByMe() {
        return oponentNameByMe;
    }

    public void setOponentNameByMe(String oponentNameByMe) {
        this.oponentNameByMe = oponentNameByMe;
    }

    public String getOponentDescByMe() {
        return oponentDescByMe;
    }

    public void setOponentDescByMe(String oponentDescByMe) {
        this.oponentDescByMe = oponentDescByMe;
    }

    public boolean isUserOnline() {
        return isUserOnline == 1;
    }

    public void setIsUserOnline(int isUserOnline) {
        this.isUserOnline = isUserOnline;
    }

    public int getHasIntroductionFile() {
        return hasIntroductionFile;
    }

    public void setHasIntroductionFile(int hasIntroductionFile) {
        this.hasIntroductionFile = hasIntroductionFile;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getLangNative() {
        return langNative;
    }

    public void setLangNative(String langNative) {
        this.langNative = langNative;
    }

    public int getAllowPush() {
        return allowPush;
    }

    public void setAllowPush(int allowPush) {
        this.allowPush = allowPush;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public int getStudyRole() {
        return studyRole;
    }

    public void setStudyRole(int studyRole) {
        this.studyRole = studyRole;
    }

    public boolean isStudyRoleMain() {
        return studyRoleMain == 1;
    }

    public int getStudyRoleMain() {
        return studyRoleMain;
    }

    public void setStudyRoleMain(int studyRoleMain) {
        this.studyRoleMain = studyRoleMain;
    }

    public long getLastAccessDateTS() {
        return lastAccessDateTS;
    }

    public void setLastAccessDateTS(long lastAccessDateTS) {
        this.lastAccessDateTS = lastAccessDateTS;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
