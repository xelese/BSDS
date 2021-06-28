import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Receiver {
    private final static Integer threads = 256;

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("3.225.35.105");
        final Connection connection = factory.newConnection();
        ProfileCredentialsProvider credentials = new ProfileCredentialsProvider("bsds");
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).withCredentials(credentials)
                .build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        for (int i = 0; i < threads; i++) {
            new Thread(new ChannelConsumer(connection, mapper)).start();
        }
    }
}
