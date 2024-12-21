package model;

import java.util.List;

public class ImageResponseModel {
    private String imageId;
    private String imageName;
    private String groupBy;
    private String description;
    private String imageUrl ;
    private String items;
    private String created;
    private String createdBy;
    private String modified;
    // Constructors, getters, and setters
    public ImageResponseModel(Image yard) {
        this.imageId = yard.getImageId();
        this.imageName = yard.getImageName() != null ? yard.getImageName() : "";
        this.groupBy = yard.getGroupBy() != null ? yard.getGroupBy() : "";
        this.description = yard.getDescription() != null ? yard.getDescription() : "";
        this.imageUrl = yard.getImageUrl() != null ? yard.getImageUrl() : "";
        this.items = yard.getItems() != null ? yard.getItems() : null;
        this.created = yard.getCreated() != null ? yard.getCreated() : "";
        this.createdBy = yard.getCreatedBy() != null ? yard.getCreatedBy() : "";
        this.modified = yard.getModified() != null ? yard.getModified() : "";
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
