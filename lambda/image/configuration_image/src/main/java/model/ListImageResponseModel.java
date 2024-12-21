package model;

import java.util.ArrayList;

public class ListImageResponseModel {
    private ArrayList<ImageResponseModel> images;
    private String next;

    public ListImageResponseModel(){}

    public ArrayList<ImageResponseModel> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageResponseModel> images) {
        this.images = images;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public ListImageResponseModel(ArrayList<ImageResponseModel> images, String next) {
        this.images = images;
        this.next = next;
    }
}
