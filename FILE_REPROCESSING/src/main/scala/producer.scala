import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}
import org.codehaus.jettison.json.JSONObject

class producer(var FileDetailsJSON:JSONObject,queuename:String) {
  //Producer
//  var queuename:String="hss.after.reprocessing"

  //create connection to server
  val factory = new ConnectionFactory
  factory.setUsername("admin")
  factory.setPassword("StrongPassword")
  factory.setVirtualHost("/")
  factory.setHost("192.168.10.251")
  factory.setPort(15672)

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
  //channel.queueDeclare(queuename, durable, false, false, null)

  channel.basicPublish("", queuename, null, FileDetailsJSON.toString.getBytes)
  println(FileDetailsJSON+" delails is sent to queue\n")

}
