import Configuration.RabbitMQConfiguration;
import Controller.ConsumerController;

public class Receiver {

    public static void main(String[] args) throws Exception {
        ConsumerController consumerController = new ConsumerController();
        consumerController.run();
    }

}
