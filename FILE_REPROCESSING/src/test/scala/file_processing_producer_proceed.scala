import com.rabbitmq.client.{Channel, Connection, ConnectionFactory};

object file_processing_producer_proceed  {

  def main(args: Array[String]): Unit = {


    var queuename: String = "hss.proceed"
//        var queuename: String = "hss.before.reprocessing"

    //create connextion to server
    val factory = new ConnectionFactory


//    factory.setUsername("admin")
//    factory.setPassword("123")
//    factory.setHost("localhost")
    factory.setUsername("admin")
    factory.setPassword("StrongPassword")
    factory.setHost("192.168.10.251")
    factory.setPort(5672)
    factory.setVirtualHost("/")
    factory.setConnectionTimeout(6000) // in milliseconds

    factory.setRequestedHeartbeat(60) // in seconds

    factory.setConnectionTimeout(6000)
    factory.setRequestedChannelMax(5)
    //  factory.setNetworkRecoveryInterval(500)
    //  factory.setAutomaticRecoveryEnabled(true)


    val conn: Connection = factory.newConnection


    val channel: Channel = conn.createChannel

    //configure message queues as durable
    var durable = true;
channel.getConnection()
//    channel.queueDeclare(queuename, durable, false, false, null)
//    val message = "[\"MSC050120170625000000176477GCDR\", \"MSC050120170625000513176478GCDR\", \"MSC050120170625001111176479GCDR\", \"MSC050120170625001111176479GCDR\"]"
    val message="{\"remark\":\"hi gowsi\",\"message\":\"proceed\"}"
//    val message="cancel"

    channel.basicPublish("", queuename, null, message.getBytes)
    println(" [x] Sent '" + message + "'")
  }




}
