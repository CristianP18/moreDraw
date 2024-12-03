package repository;

import bundles.Bundle;
import com.google.firebase.messaging.FirebaseMessagingException;
import configuration.DynamoDBIndexes;
import configuration.DynamoDBTables;
import configuration.DynamoDBUser;
import exceptions.NotFound;
import model.PostUsersLoginRequestModel;
import model.User;
import model.UsersVerifyRequestModel;

import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sns.model.*;

import java.io.IOException;
import java.util.*;

import static configuration.GenericRequestHandler.DynamoService;
import static repository.Dynamo.DB_PREFIX;

public final class UserRepository {
    private static UserRepository INSTANCE;

    public static UserRepository getInstance() {
        if (INSTANCE == null)
            INSTANCE = new UserRepository();
        return INSTANCE;
    }

    private static final DynamoDbTable<User> table =
            DynamoService.table(
                    DB_PREFIX + DynamoDBTables.USER_TABLE,
                    TableSchema.fromBean(User.class));

    public static final String SNS_TOPIC = "arn:aws:sns:us-east-1:170387992571:topic-sns-otp-moreDraw";

    public synchronized User loadUserById(String userId) {
        Key key = Key.builder()
                .partitionValue(userId)
                .build();

        User user = table.getItem(key);
        return user;
    }

    /**
     * Captura usuário determinado pelo Identificador.
     */
    public synchronized User getUserById(String userId) {
        Key key = Key.builder()
                .partitionValue(userId)
                .build();

        QueryConditional condition = QueryConditional.keyEqualTo(key);

        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(condition)
                .scanIndexForward(false)
                .consistentRead(true)
                .build();

        Iterator<Page<User>> queryResult = table.query(query).iterator();

        ArrayList<User> users = new ArrayList<>(queryResult.next().items());

        if (users.isEmpty())
            return null;

        return users.get(0);
    }

    /**
     * Captura usuário determinado pelo Identificador do veículo.
     */
    public User getUserByVehicleId(String vehicleId) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":requestVehicleId", AttributeValue.builder().s(vehicleId).build());
        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#vehicleId", DynamoDBUser.VEHICLE_ID);

        Expression expression = Expression.builder()
                .expressionValues(attributeValues)
                .expressionNames(attributeNames)
                .expression("#vehicleId = :requestVehicleId")
                .build();

        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();

        Iterator<User> users = table.scan(scan).items().iterator();

        if (!users.hasNext())
            return null;

        return users.next();
    }

    /**
     * Cria usuário.
     */
    public void postUser(User user, String locale) throws IOException, FirebaseMessagingException {
        table.putItem(user);
//        sendSMS(user, locale);
    }

    /**
     * Cria lote de usuários.
     */
    public void postBatchUser(ArrayList<User> users) throws IOException, FirebaseMessagingException {
        Queue<WriteBatch> batch = new LinkedList<>();
        for (User user : users) {
            WriteBatch writeBatch = WriteBatch.builder(User.class)
                    .mappedTableResource(table)
                    .addPutItem(r -> r.item(user))
                    .build();
            batch.add(writeBatch);

            if (batch.size() % 25 == 0) {
                BatchWriteItemEnhancedRequest bwier = BatchWriteItemEnhancedRequest.builder().writeBatches(batch).build();
                DynamoService.batchWriteItem(bwier);
                System.out.println("LOTE DE USUÁRIOS CRIADO");
                batch.clear();
            }

            sendSMS(user, "pt_BR");
        }

        if (batch.size() != 0) {
            BatchWriteItemEnhancedRequest bwier = BatchWriteItemEnhancedRequest.builder().writeBatches(batch).build();
            DynamoService.batchWriteItem(bwier);
            System.out.println("LOTE RESTANTE DE USUÁRIOS CRIADO");
        }
    }

    /**
     * Cria usuário temporário.
     */
    public void postTempUser(User user) {
        table.putItem(user);
    }

    /**
     * Cria/atualiza usuário já existente.
     */
    public void postUserAlreadyExists(User user, String locale) throws IOException, FirebaseMessagingException {
        table.updateItem(user);
        sendSMS(user, locale);
    }

    /**
     * Atualiza novo número de celular.
     */
    public void postNewMobilePhone(User user, String locale) {
        table.updateItem(user);
        System.out.println("salvou na tabela");
        resendSMS(user, locale);
        System.out.println("reenviou sms");
    }

    /**
     * Captura usuário no login.
     */
    public User getUserLogin(PostUsersLoginRequestModel request, String locale) throws NotFound {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":requestMobilePhone", AttributeValue.builder().s(request.getMobilePhone()).build());
        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#mobilePhone", DynamoDBUser.MOBILE_PHONE);

        Expression expression = Expression.builder()
                .expressionValues(attributeValues)
                .expressionNames(attributeNames)
                .expression("#mobilePhone = :requestMobilePhone")
                .build();

        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();

        Iterator<User> users = table.scan(scan).items().iterator();

        if (!users.hasNext())
            throw new NotFound(Bundle.getInstance().getString("InvalidLoginUser", locale));

        return users.next();
    }

    /**
     * Captura usuário determinado pelo CPF.
     */
    public User getUserFederalTax(String federalTax) {
        DynamoDbIndex<User> federalTaxGlobalSecondIndex =
                table.index(DynamoDBIndexes.GSI_USER_FEDERAL_TAX);

        Key key = Key.builder()
                .partitionValue(federalTax)
                .build();

        QueryConditional condition = QueryConditional.keyEqualTo(key);

        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(condition)
                .scanIndexForward(false)
                .build();

        Iterator<Page<User>> queryResult = federalTaxGlobalSecondIndex.query(query).iterator();

        ArrayList<User> users = new ArrayList<>(queryResult.next().items());

        if (users.isEmpty())
            return null;

        return users.get(0);
    }

//    /**
//     * Captura usuário determinado pelo Nome.
//     */
//    public ArrayList<User> getUserName(String name) {
//        ArrayList<User> usersArray = new ArrayList<>();
//        // Tratamento da string de entrada
//        String trimmedInput = name.trim(); // Remove espaços iniciais e finais
//        String inputName = trimmedInput.replaceAll("(%20)+", "%20").replaceAll("^%20+|%20+$", "").replace("%20", " ");
//
//        // Divisão da string em primeiro nome e sobrenome
//        String[] split = inputName.split(" ");
//        String firstName = split.length > 0 ? split[0] : "";
//
//        // Construção do sobrenome
//        String lastName = "";
//        for (int i = 1; i < split.length; i++) {
//            lastName += (i > 1 ? " " : "") + split[i];
//        }
//
//        Map<String, AttributeValue> attributeValues = new HashMap<>();
//        Map<String, String> attributeNames = new HashMap<>();
//
//        String expressionString = "";
//        if(!firstName.equals("")){
//            expressionString = "contains(#firstName, :requestFirstName)";
//            attributeValues.put(":requestFirstName", AttributeValue.builder().s(firstName).build());
//            attributeNames.put("#firstName", DynamoDBUser.FIRST_NAME);
//        }
//         if(!lastName.equals("")){
//            expressionString = "contains(#firstName, :requestFirstName) and contains(#lastName, :requestLastName) or contains(#firstName, :requestFirstName) or contains(#lastName, :requestLastName)";
//            attributeValues.put(":requestLastName", AttributeValue.builder().s(lastName).build());
//            attributeNames.put("#lastName", DynamoDBUser.LAST_NAME);
//            attributeValues.put(":requestFirstName", AttributeValue.builder().s(firstName).build());
//            attributeNames.put("#firstName", DynamoDBUser.FIRST_NAME);
//        }
//
//        Expression expression = Expression.builder()
//                .expressionValues(attributeValues)
//                .expressionNames(attributeNames)
//                .expression(expressionString)
//                .build();
//
//        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
//                .filterExpression(expression)
//                .consistentRead(false)
//                .build();
//
//        Iterator<User> users = table.scan(scan).items().iterator();
//
//        while(users.hasNext())
//            usersArray.add(users.next());
//
//        return usersArray;
//    }


    /**
     * Captura usuário determinado pelo Nome.
     */

    public ArrayList<User> getUserName(String firstName, String lastName) {
        ArrayList<User> usersArray = new ArrayList<>();

        System.out.println("Nome tratado: " + firstName + ", Sobrenome aki: " + lastName);

        // Construção da expressão para o DynamoDB
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        Map<String, String> attributeNames = new HashMap<>();
        String expressionString = "";

        if (!firstName.isEmpty()) {
            attributeValues.put(":requestFirstName", AttributeValue.builder().s(firstName).build());
            attributeNames.put("#firstName", DynamoDBUser.FIRST_NAME);
            expressionString = "contains(#firstName, :requestFirstName)";
        }

        // Execução da varredura no DynamoDB, se houver uma expressão válida
        if (!expressionString.isEmpty()) {
            Expression expression = Expression.builder()
                    .expressionValues(attributeValues)
                    .expressionNames(attributeNames)
                    .expression(expressionString)
                    .build();

            ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                    .filterExpression(expression)
                    .consistentRead(false)
                    .build();


            Iterator<User> users = table.scan(scan).items().iterator();
            while (users.hasNext()) {
                usersArray.add(users.next());
            }
        }

        return usersArray;
    }


    /**
     * Captura usuário determinado pelo Nome.
     */
    public ArrayList<User> getUserLastName(String name) {
        ArrayList<User> usersArray = new ArrayList<>();
        System.out.println("Pesquisa por lastName se não encontrar nada pelo primeiro nome");
        // Separa strings com espaço
        String[] split = name.split("%20");

        String firstName = "";
        if (split.length > 0 && split[0] != null) {
            firstName = split[0];
        }

//        String lastName = "";
//        for (int i = 1; i < split.length; i++) {
//            if (i == split.length - 1) {
//                lastName = lastName.concat(split[i]);
//            } else {
//                lastName = lastName.concat(split[i]).concat(" ");
//            }
//        }

        Map<String, AttributeValue> attributeValues = new HashMap<>();
        Map<String, String> attributeNames = new HashMap<>();
        String expressionString = "";

        if (!firstName.isEmpty()) {
            attributeValues.put(":requestLastName", AttributeValue.builder().s(firstName).build());
            attributeNames.put("#lastName", DynamoDBUser.FIRST_NAME);
            expressionString = "contains(#lastName, :requestLastName)";
        }

        Expression expression = Expression.builder()
                .expressionValues(attributeValues)
                .expressionNames(attributeNames)
                .expression(expressionString)
                .build();

        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .consistentRead(false)
                .build();

        Iterator<User> users = table.scan(scan).items().iterator();
        while (users.hasNext()) {
            usersArray.add(users.next());
        }

        return usersArray;
    }


    /**
     * Captura usuário determinado pelo Telefone celular.
     */
    public User getUserMobilePhone(String mobilePhone) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":requestMobilePhone", AttributeValue.builder().s(mobilePhone).build());
        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#mobilePhone", DynamoDBUser.MOBILE_PHONE);

        Expression expression = Expression.builder()
                .expressionValues(attributeValues)
                .expressionNames(attributeNames)
                .expression("#mobilePhone = :requestMobilePhone")
                .build();

        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();

        Iterator<User> users = table.scan(scan).items().iterator();

        if (!users.hasNext())
            return null;

        User user = users.next();
        if (user == null)
            return null;
        return user;
    }

    /**
     * Captura usuario na verificaçao do codigo OTP.
     */
    public User getUserVerify(UsersVerifyRequestModel request, String locale) throws NotFound {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":requestMobilePhone", AttributeValue.builder().s(request.getMobilePhone()).build());
        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#mobilePhone", DynamoDBUser.MOBILE_PHONE);

        Expression expression = Expression.builder()
                .expressionValues(attributeValues)
                .expressionNames(attributeNames)
                .expression("#mobilePhone = :requestMobilePhone")
                .build();

        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .consistentRead(true)
                .build();

        Iterator<User> users = table.scan(scan).items().iterator();

        if (!users.hasNext())
            throw new NotFound(Bundle.getInstance().getString("UserNotFound", locale));

        return users.next();
    }

    /**
     * Atualiza usuario.
     */
    public void putUser(User user) {
        table.updateItem(user);
    }

    /**
     * Atualiza usuario na verificaçao OTP.
     */
    public void putUserVerify(User user, String locale) {
        table.updateItem(user);
        resendSMS(user, locale);
    }

    /**
     * Reenvia SMS para usuario.
     */
    public void resendSMS(User user, String locale) {
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", MessageAttributeValue
                .builder()
                .stringValue("SPARK")
                .dataType("String")
                .build());
        smsAttributes.put("AWS.SNS.SMS.SMSType", MessageAttributeValue
                .builder()
                .stringValue("Transactional")
                .dataType("String")
                .build());

        String message = Bundle.getInstance().getString("SMSVerificationCode", locale) + " " + user.getOneTimePassword();

        SNS.getInstance().publish(PublishRequest
                .builder()
                .message(message)
                .phoneNumber(user.getMobilePhone())
                .messageAttributes(smsAttributes)
                .build());
    }

    /**
     * Envia SMS para usuario.
     */
    public void sendSMS(User user, String locale) throws IOException, FirebaseMessagingException {
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", MessageAttributeValue
                .builder()
                .stringValue("SPARK")
                .dataType("String")
                .build());
        smsAttributes.put("AWS.SNS.SMS.SMSType", MessageAttributeValue
                .builder()
                .stringValue("Transactional")
                .dataType("String")
                .build());

        List<String> invalid = Arrays.asList("Vitor", "Eduardo");

        String message;
        if (invalid.contains(user.getFirstName())) {
            message = Bundle.getInstance().getString("SMSWelcome", locale) + "!\n\n" +
                    Bundle.getInstance().getString("SMSVerificationCode", locale) + " " + user.getOneTimePassword();
        } else {
            message = Bundle.getInstance().getString("SMSWelcome", locale) +
                    ", " + user.getFirstName() + "!\n\n" +
                    Bundle.getInstance().getString("SMSVerificationCode", locale) + " " + user.getOneTimePassword();
        }

        SNS.getInstance().publish(PublishRequest
                .builder()
                .message(message)
                .phoneNumber(user.getMobilePhone())
                .messageAttributes(smsAttributes)
                .build());

        String title = "Código de verificação";
        String body = "O código OTP é: " + user.getOneTimePassword();
//        String fcm = user.getFcm();
    }

    /**
     * Envia SMS para usuario para validar telefone em troca de senha.
     */
    public void sendPasswordVerificationSMS(User user, String locale) {
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", MessageAttributeValue
                .builder()
                .stringValue("MoreDraw")
                .dataType("String")
                .build());
        smsAttributes.put("AWS.SNS.SMS.SMSType", MessageAttributeValue
                .builder()
                .stringValue("Transactional")
                .dataType("String")
                .build());
        String message = Bundle.getInstance().getString("SMSVerificationCode", locale) + " " + user.getOneTimePassword();

        SNS.getInstance().publish(PublishRequest
                .builder()
                .message(message)
                .phoneNumber(user.getMobilePhone())
                .messageAttributes(smsAttributes)
                .build());
    }

    /**
     * Inscreve um telefone celular no SNS.
     */
    public void subscribe(String mobilePhone) {
        SNS.getInstance().subscribe(SubscribeRequest
                .builder()
                .topicArn(SNS_TOPIC)
                .protocol("SMS")
                .endpoint(mobilePhone)
                .build());
    }

    /**
     * Desinscreve um telefone celular no SNS.
     */
    public void unsubscribe(String mobilePhone) {
        ListSubscriptionsByTopicResponse result = SNS.getInstance().listSubscriptionsByTopic(
                ListSubscriptionsByTopicRequest
                        .builder()
                        .topicArn(SNS_TOPIC)
                        .build());

        for (Subscription sub : result.subscriptions()) {
            if (sub.endpoint().equals("+" + mobilePhone))
                SNS.getInstance().unsubscribe(UnsubscribeRequest
                        .builder()
                        .subscriptionArn(sub.subscriptionArn())
                        .build());
        }
    }

    public void deleteUser(User user) {
        table.deleteItem(user);
    }

}
