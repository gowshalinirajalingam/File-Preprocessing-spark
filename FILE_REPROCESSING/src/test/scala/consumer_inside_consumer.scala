
import com.rabbitmq.client.{Channel, Connection}


object consumer_inside_consumer extends App {
  import com.rabbitmq.client.ConnectionFactory

    var queuename:String="hss.after.reprocessing"
//  var queuename:String="hss.proceed"



  //create connextion to server
  val factory = new ConnectionFactory


  factory.setUsername("admin")
  factory.setPassword("StrongPassword")
  factory.setHost("192.168.10.251")
  factory.setPort(5672)
  factory.setVirtualHost("/")
  factory.setConnectionTimeout(6000) // in milliseconds
  factory.setRequestedHeartbeat(60) // in seconds
  factory.setRequestedChannelMax(5)
  //  factory.setNetworkRecoveryInterval(500)
  //  factory.setAutomaticRecoveryEnabled(true)


  val conn: Connection = factory.newConnection
  val channel: Channel = conn.createChannel

  //configure message queues as durable
  var durable = true;

  channel.getConnection()
  // channel.queueDeclare(queuename, durable, false, false, null)
  println(" [*] Waiting for messages. To exit press CTRL+C")

  import com.rabbitmq.client.QueueingConsumer

  //dispatch messages fairly rather than round-robin by waiting for ack before sending next message
  //be careful because queue can fill up if all workers are busy
  val prefetchCount = 1
  channel.basicQos(prefetchCount)
  //ensure that an explicit ack is sent from worker before removing from the queue
  val autoAck = false

  val consumer = new QueueingConsumer(channel)
  channel.basicConsume(queuename, autoAck, consumer)

  while ( {
    true
  }) {
    val delivery = consumer.nextDelivery
    val message = new String(delivery.getBody)
    System.out.println(" [x] Consumer : received '" + message + "'")

    channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)


    //----------
      var qname:String="hss.proceed"

    val channel1: Channel= conn.createChannel

    channel1.getConnection()


    println(" 2 Waiting for messages. To exit press CTRL+C")

    import com.rabbitmq.client.QueueingConsumer

    //dispatch messages fairly rather than round-robin by waiting for ack before sending next message
    //be careful because queue can fill up if all workers are busy
    val prefetchCount = 1
    channel.basicQos(prefetchCount)
    //ensure that an explicit ack is sent from worker before removing from the queue
    val autoAck = false

    val consumer1 = new QueueingConsumer(channel1)
    channel1.basicConsume(qname, autoAck, consumer1)

//    while ( {
//      true
//    }) {
      val delivery1 = consumer1.nextDelivery
      val message1 = new String(delivery1.getBody)
      System.out.println(" 2 Consumer : received '" + message1 + "'")

      channel1.basicAck(delivery1.getEnvelope.getDeliveryTag, false)

      channel1.close()

//    }

      //---------------




  }







}
