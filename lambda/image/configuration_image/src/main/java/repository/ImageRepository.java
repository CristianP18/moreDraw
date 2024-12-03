package repository;


import configuration.DynamoDBIndexes;
import configuration.DynamoDBTables;
import model.Image;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Iterator;

import static configuration.GenericRequestHandler.DynamoService;
import static repository.Dynamo.DB_PREFIX;

public final class ImageRepository {
    private static ImageRepository INSTANCE;

    public static ImageRepository getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ImageRepository();
        return INSTANCE;
    }

    private static final DynamoDbTable<Image> table =
            DynamoService.table(
                    DB_PREFIX + DynamoDBTables.IMAGE_TABLE,
                    TableSchema.fromBean(Image.class));

    /**
     * Captura localidade de armazenagem determinado pelo Identificador do criador.
     */
    public Iterator<Page<Image>> getImage(String createdBy) {
        DynamoDbIndex<Image> createdByCreatedGlobalSecondIndex =
                table.index(DynamoDBIndexes.GSI_IMAGE_CREATED_BY_CREATED);

        Key key = Key.builder()
                .partitionValue(createdBy)
                .build();

        QueryConditional condition = QueryConditional.keyEqualTo(key);

        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(condition)
                .limit(50)
                .scanIndexForward(false)
                .build();

        Iterator<Page<Image>> queryResult = createdByCreatedGlobalSecondIndex.query(query).iterator();

        if(!queryResult.hasNext())
            return null;

        return queryResult;
    }

    /**
     * Cadastra image.
     */
    public void postImage(Image image) {
        table.putItem(image);
    }

    /**
     * Captura Image.
     */
    public Image getImageById(String imageId) {
        Key key = Key.builder()
                .partitionValue(imageId)
                .build();

        return table.getItem(key);
    }

    /**
     * Atualiza Image.
     */
    public void saveImage(Image image) {
        table.updateItem(image);
    }
}
