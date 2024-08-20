package com.example.app;

public class Model {

    private String imageUri;
    private int imageNumber;

    public Model (){} //빈 생성자

    public Model(String imageUri, int imageNumber) {
        this.imageUri = imageUri;
        this.imageNumber = imageNumber;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getImageNumber() {
        return imageNumber;
    }

    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }

}
