package com.example.app;

//사용자 계정 정보 모델 클래스
public class UserAccount
{
    private String idToken; //파이어베이스 uid (고유 토큰 정보)
    private String emailId; //이메일 아이디
    private String password; //비밀번호
    private String nickname; //닉네임
    private String grade; //등급
    private String imageUrl; //프로필 사진

    public UserAccount() { }

    public UserAccount(String nickname, String emailId, String password)
    {
        this.nickname = nickname;
        this.emailId = emailId;
        this.password = password;
    }

    public String getIdToken() { return idToken; }

    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getEmailId() { return emailId; }

    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
