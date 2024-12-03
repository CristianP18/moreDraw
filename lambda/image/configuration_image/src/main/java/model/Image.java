package model;


import helper.DateHelper;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;


import java.util.List;

import static configuration.DynamoDBIndexes.GSI_IMAGE_CREATED_BY_CREATED;

@DynamoDbBean
public class Image {

    private String imageId; // Identificador único do índice de busca
    private String imageUrl; // Referência a Image correspondente
    private String title; // Título da imagem
    private String description; // Descrição detalhada
    private String type; // Tipo de componente gráfico
    private List<String> tags; // Lista de tags para facilitar a busca
    private String language; // Idioma dos dados indexados
    private String modified;
    private String created;
    private String createdBy;

    // Construtor padrão
    public Image() {}

    // Construtor de cópia
    public Image(ImageRequestModel data, String userId) {
        if (data != null) {
            this.imageId = data.getImageId();
            this.imageUrl = data.getImageUrl();
            this.title = data.getTitle();
            this.description = data.getDescription();
            this.type = data.getType();
            this.tags = data.getTags();
            this.language = data.getLanguage();
            this.modified = data.getModified();
            this.created = DateHelper.nowString();
            this.createdBy = userId;
        }
    }

    public static void update(ImageRequestModel request, Image image) {
        if (request != null && image != null) {
            if (request.getImageId() != null) {
                image.setImageId(request.getImageId());
            }
            if (request.getImageUrl() != null) {
                image.setImageUrl(request.getImageUrl());
            }
            if (request.getTitle() != null) {
                image.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                image.setDescription(request.getDescription());
            }
            if (request.getType() != null) {
                image.setType(request.getType());
            }
            if (request.getTags() != null) {
                image.setTags(request.getTags());
            }
            if (request.getLanguage() != null) {
                image.setLanguage(request.getLanguage());
            }
            if (request.getModified() != null) {
                image.setModified(request.getModified());
            } else {
                image.setModified(DateHelper.nowString()); // Atualiza com a data atual se não for fornecida.
            }
        }
    }

    // Getters e Setters

    @DynamoDbPartitionKey
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
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
}