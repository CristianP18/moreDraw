package configuration;

public class Response<T>{
    private T content;
    private Message message;

    private Response(T content, Message message) {
        this.content = content;
        this.message = message;
    }

    public Response(Message message) {
        this.message = message;
    }

    public static Response<?> success(Object content, Message message){
        return new Response<>(content, message);
    }

    public static Response<?> success(Message message){
        return new Response<>(message);
    }

    public static Response<?> error(Message message){
        return new Response<>(message);
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


    public class ResponsePage<T> {
        private T shipmentResponseModels;
        private String lastEvaluatedKey;
        private String requestId;
        private String message;
        private ResponseType responseType;
        private ResponseStatusCode responseStatusCode;

        public ResponsePage(T shipmentResponseModels, String lastEvaluatedKey, String requestId, String message, ResponseType responseType, ResponseStatusCode responseStatusCode) {
            System.out.println("Construtor resposta final");
            this.shipmentResponseModels = shipmentResponseModels;
            this.lastEvaluatedKey = lastEvaluatedKey;
            this.requestId = requestId;
            this.message = message;
            this.responseType = responseType;
            this.responseStatusCode = responseStatusCode;
        }

        // Getters and setters for each field
        public T getShipmentResponseModels() {
            return shipmentResponseModels;
        }

        public void setShipmentResponseModels(T shipmentResponseModels) {
            this.shipmentResponseModels = shipmentResponseModels;
        }

        public String getLastEvaluatedKey() {
            return lastEvaluatedKey;
        }

        public void setLastEvaluatedKey(String lastEvaluatedKey) {
            this.lastEvaluatedKey = lastEvaluatedKey;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ResponseType getResponseType() {
            return responseType;
        }

        public void setResponseType(ResponseType responseType) {
            this.responseType = responseType;
        }

        public ResponseStatusCode getResponseStatusCode() {
            return responseStatusCode;
        }

        public void setResponseStatusCode(ResponseStatusCode responseStatusCode) {
            this.responseStatusCode = responseStatusCode;
        }

    }

}
