package model;

import helper.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class ImageResponseModel {
    private String imageId; // Identificador único do índice de busca
    private String imageUrl; // Referência a Image correspondente
    private String title; // Título da imagem
    private String description; // Descrição detalhada
    private String type; // Tipo de componente gráfico
    private List<String> tags; // Lista de tags para facilitar a busca
    private String language; // Idioma dos dados indexados
    private String modified; // Data de modificação
    private String created; // Data de criação
    private String createdBy; // Criador do recurso

    // Construtor que inicializa o modelo com dados do objeto Image
    public ImageResponseModel(Image image) {
        if (image != null) {
            this.imageId = image.getImageId() != null ? image.getImageId() : "";
            this.imageUrl = image.getImageUrl() != null ? image.getImageUrl() : "";
            this.title = image.getTitle() != null ? image.getTitle() : "";
            this.description = image.getDescription() != null ? image.getDescription() : "";
            this.type = image.getType() != null ? image.getType() : "";
            this.tags = image.getTags() != null ? image.getTags() : new ArrayList<>();
            this.language = image.getLanguage() != null ? image.getLanguage() : "";
            this.modified = image.getModified() != null ? image.getModified() : DateHelper.nowString();
            this.created = image.getCreated() != null ? image.getCreated() : DateHelper.nowString();
            this.createdBy = image.getCreatedBy() != null ? image.getCreatedBy() : "";
        } else {
            // Inicializa com valores padrão se o objeto Image for nulo
            this.imageId = "";
            this.imageUrl = "";
            this.title = "";
            this.description = "";
            this.type = "";
            this.tags = new ArrayList<>();
            this.language = "";
            this.modified = DateHelper.nowString();
            this.created = DateHelper.nowString();
            this.createdBy = "";
        }
    }

    // Getters e Setters
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
}
