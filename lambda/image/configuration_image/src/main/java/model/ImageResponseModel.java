package model;

import java.util.List;

public class ImageResponseModel {
    private String imageId;
    private String imageName;
    private String groupBy;
    private String description;
    private String imageUrl ;
    private String items;
    private String type;
    private String created;
    private String createdBy;
    private String modified;
    // Constructors, getters, and setters
    public ImageResponseModel(Image image) {
        this.imageId = image.getImageId();
        this.imageName = image.getImageName() != null ? image.getImageName() : "";
        this.groupBy = image.getGroupBy() != null ? image.getGroupBy() : "";
        this.description = image.getDescription() != null ? image.getDescription() : "";
        this.imageUrl = image.getImageUrl() != null ? image.getImageUrl() : "";
        this.items = image.getItems() != null ? image.getItems() : null;
        this.type = image.getType() != null ? image.getType() : "";
        this.created = image.getCreated() != null ? image.getCreated() : "";
        this.createdBy = image.getCreatedBy() != null ? image.getCreatedBy() : "";
        this.modified = image.getModified() != null ? image.getModified() : "";
    }

    public ImageResponseModel() {
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
