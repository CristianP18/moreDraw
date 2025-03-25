package model;

import helper.DateHelper;
import service.ImageService;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

import java.util.List;
import java.util.UUID;

import static configuration.DynamoDBIndexes.GSI_IMAGE_CREATED_BY_CREATED;

@DynamoDbBean
public class Image {
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

    public Image(ImageRequestModel request, String clientId) {
        this.imageId = UUID.randomUUID().toString();
        this.imageName = request.getImageName();
        this.groupBy = request.getGroupBy();
        this.description = request.getDescription();
        this.imageUrl = request.getImageUrl() != null ? request.getImageUrl() : ImageService.image_GRAY;
        this.items = request.getItems() != null ? request.getItems() : null;
        this.type = request.getType() != null ? request.getType() : null;
        this.created = DateHelper.nowString();
        this.createdBy = clientId;
        this.modified = request.getModified();
    }

    public static void update(ImageRequestModel request, Image image) {
        if (request.getImageName() != null) {
            image.setImageName(request.getImageName());
        }
        if (request.getGroupBy() != null) {
            image.setGroupBy(request.getGroupBy());
        }
        if (request.getDescription() != null) {
            image.setDescription(request.getDescription());
        }
        if (request.getItems() != null) {
            image.setItems(request.getItems());
        }
        if (request.getType() != null) {
            image.setType(request.getType());
        }
        if (request.getModified() != null) {
            image.setModified(request.getModified());
        } else {
            image.setModified(DateHelper.nowString());
        }
    }

    public Image() {
    }

    @DynamoDbPartitionKey
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getDescription() {
        return description;
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

    @DynamoDbSecondarySortKey(indexNames = { GSI_IMAGE_CREATED_BY_CREATED })
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
    @DynamoDbSecondaryPartitionKey(indexNames = { GSI_IMAGE_CREATED_BY_CREATED })
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
