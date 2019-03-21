import java.beans.Transient

import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}
import org.apache.log4j.Logger
import org.codehaus.jettison.json.{JSONArray, JSONObject}


object index {
  def main(args: Array[String]): Unit = {

    @Transient val logger = Logger.getLogger(this.getClass.getName)
    //    logger.setAdditivity(false)
    //
    //    var rootLogger = Logger.getRootLogger
    //    rootLogger.setLevel(Level.ERROR)


    logger.info("File reprocessing Index file started running!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");




    //Implemented consumer
    var queuename: String = "hss.before.reprocessing"

    //create connextion to server
    val factory = new ConnectionFactory


    factory.setUsername("admin")
    factory.setPassword("StrongPassword")
    factory.setHost("192.168.10.251")
    factory.setPort(5672)
    factory.setVirtualHost("/")
//    factory.setConnectionTimeout(6000) // in milliseconds
    factory.setRequestedHeartbeat(60) // in seconds
    factory.setRequestedChannelMax(5)
    //  factory.setNetworkRecoveryInterval(500)
    //  factory.setAutomaticRecoveryEnabled(true)


    val conn: Connection = factory.newConnection
    val channel: Channel = conn.createChannel

    //configure message queues as durable
    var durable = true;

    channel.getConnection()
    //  channel.queueDeclare(queuename, durable, false, false, null)
    println(" 1 Waiting for messages. To exit press CTRL+C")

    logger.info("1 Waiting for messages from queue!!!!!!!!!!!!!!!!!!!")

    import com.rabbitmq.client.QueueingConsumer

    var jsonConsumer: JSONArray = null;
    var message: String = null;
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
      message = new String(delivery.getBody)
      System.out.println(" 1 Consumer : received '" + message + "'")
      logger.info("1 Consumer : received '" + message + "'")

      channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)
      jsonConsumer = new JSONArray(message)
      println(jsonConsumer)
      //println(jsonConsumer.get(0))


      //FILE REPROCESSING

      var frp: hss_file_reprocessing_implementation = new hss_file_reprocessing_implementation()
      var ja: JSONArray = new JSONArray()
      var jsonObj: JSONObject = new JSONObject()

      var totcurrentRowcount = 0;
      var totoldRowcount = 0;




      if (!message.isEmpty()) {
        for (i <- 0.to(jsonConsumer.length() - 1)) {
          var filename = jsonConsumer.get(i).toString
          var FNandRC = frp.CalculateRowCount(filename)
          var rowCount = (-1);
          FNandRC.map({ x => rowCount = x._2 })
          //println(consumer.jsonConsumer.get(i))
          for (l <- frp.getfromdb(filename)) {
            //            var jsonObj: JSONObject = new JSONObject()

            //            jsonObj.put("file_name", l._1)
            //            jsonObj.put("old_line_count", l._2)
            //            jsonObj.put("current_line_count", rowCount)
            //
            //            println(jsonObj)
            //
            //            ja.put(jsonObj)

            if (rowCount == (-1)) //file not found in the location
            {
              totcurrentRowcount = totcurrentRowcount + 0
            }
            else {
              totcurrentRowcount = totcurrentRowcount + rowCount
            }

            if (l._2 == (-1)) //file not found in the data base
            {
              totoldRowcount = totoldRowcount + 0
            }
            else {
              totoldRowcount = totoldRowcount + l._2

            }


          }


        }


        jsonObj.put("old_line_count", totoldRowcount)
        jsonObj.put("current_line_count", totcurrentRowcount)
        println(jsonObj)
        var prod: producer = new producer(jsonObj,"hss.after.reprocessing")

        //--------------------------------------------
        //Add Current Line Count to DB

        var qname:String="hss.proceed"

        val channel1: Channel= conn.createChannel

        channel1.getConnection()


        println(" 2 Waiting for messages. To exit press CTRL+C")

        import com.rabbitmq.client.QueueingConsumer

        //dispatch messages fairly rather than round-robin by waiting for ack before sending next message
        //be careful because queue can fill up if all workers are busy
        val prefetchCount = 1
        channel1.basicQos(prefetchCount)
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

        var messageJsonObjectProceed=new JSONObject(message1)
        var qmessage:String = null
        var remark:String = null




        for (j <- 0.to(messageJsonObjectProceed.length() - 1)) {
          qmessage=messageJsonObjectProceed.get("message").toString
          remark=messageJsonObjectProceed.get("remark").toString

          println(remark)
        }


          if (qmessage=="proceed")
          {
            for (i <- 0.to(jsonConsumer.length() - 1)) {
              var filename = jsonConsumer.get(i).toString
              var FNandRC = frp.CalculateRowCount(filename)
              var rowCount = (-1);
              FNandRC.map({ x => rowCount = x._2 })
              //println(consumer.jsonConsumer.get(i))
              for (l <- frp.getfromdb(filename)) {


                frp.setCurrentLineCountToDB(l._1, rowCount,remark)


                    }
              }



            channel1.close()

          }
        else if(qmessage=="cancel")
          {
            println("proceed canceled")
            channel1.close()
          }

//          }


        //-----------------------------------
      }
      else {
        println("Waiting for file names from the queue")
      }

    }

  }

}
