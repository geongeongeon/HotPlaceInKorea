package com.example.app;

public class Post {

    private String posttitle; //게시글 제목
    private String postcontents; //게시글 내용
    private String postuser; //게시글 작성자
    private String posttime; //게시글 작성시간
    private String postimage; //게시글 이미지
    private int postCount; //게시글 번호
    private int postCommentCount; //게시글 댓글번호
    private float postStar; //게시글 누적 별점
    private String postPlace; //게시글 장소
    private int postPlaceCount; //게시글 장소 카운트
    private float postStarAvg; //게시글 별점 평균
    private int postStarCount; //게시글 별점 번호
    private int postReport; //게시글 신고

    public String getPostPlacePictureUrl() {
        return postPlacePictureUrl;
    }

    public void setPostPlacePictureUrl(String postPlacePicture) {
        this.postPlacePictureUrl = postPlacePicture;
    }

    private String postPlacePictureUrl; //게시글 대표 사진


    public int getPostPlaceCount() {
        return postPlaceCount;
    }

    public void setPostPlaceCount(int postPlaceCount) {
        this.postPlaceCount = postPlaceCount;
    }

    public Post() {}

    public Post(String postuser, String posttitle, String postcontents) {
        this.postuser = postuser;
        this.posttitle = posttitle;
        this.postcontents = postcontents;
    }


    public String getPostPlace() {
        return postPlace;
    }

    public void setPostPlace(String postPlace) {
        this.postPlace = postPlace;
    }

    public float getPostStarAvg() {
        return postStarAvg;
    }

    public void setPostStarAvg(float postStarAvg) {
        this.postStarAvg = postStarAvg;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public void setPosttitle(String posttitle) {
        this.posttitle = posttitle;
    }

    public String getPostcontents() {
        return postcontents;
    }

    public void setPostcontents(String postcontents) {
        this.postcontents = postcontents;
    }

    public String getPostuser() {
        return postuser;
    }

    public void setPostuser(String postuser) {
        this.postuser = postuser;
    }

    public String getPosttime() { return posttime; }

    public void setPosttime(String posttime) { this.posttime = posttime; }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public int getPostCommentCount() { return postCommentCount; }

    public void setPostCommentCount(int postCommentCount) { this.postCommentCount = postCommentCount; }

    public int getPostReport() {
        return postReport;
    }

    public void setPostReport(int postReport) {
        this.postReport = postReport;
    }

    public float getPostStar() {
        return postStar;
    }

    public void setPostStar(float postStar) {
        this.postStar = postStar;
    }

    public int getPostStarCount() {
        return postStarCount;
    }

    public void setPostStarCount(int postStarCount) {
        this.postStarCount = postStarCount;
    }


}
