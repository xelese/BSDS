//import io.swagger.client.*;
//import io.swagger.client.auth.*;
//import io.swagger.client.model.*;
//import io.swagger.client.api.TextbodyApi;
//
//
//import java.io.File;
//import java.util.*;
//
//public class TextbodyApiExample {
//    public static void main(String[] args) {
//
//        ApiClient testClient = new ApiClient();
//        testClient.setBasePath("http://localhost:8080/gortonator/TextProcessor/1.0.2/");
//        TextbodyApi apiInstance = new TextbodyApi(testClient);
//
//        TextLine body = new TextLine().message("graham norton show"); // TextLine | text string to analyze
//        String function = "function_example"; // String | the operation to perform on the text
//        try {
//            ApiResponse<ResultVal> result = apiInstance.analyzeNewLineWithHttpInfo(body, function);
//            System.out.println(result);
//        } catch (ApiException e) {
//            System.err.println("Exception when calling TextbodyApi#analyzeNewLine");
//            e.printStackTrace();
//        }
//    }
//}
