package showtstats.apiinterface

import storm.scala.dsl._
import collection.mutable.{Map, HashMap}

import org.zeromq.ZMQ


object conn {
  val context = ZMQ.context(1);
  val puller = context.socket(ZMQ.PULL);

  puller.bind("tcp://127.0.0.1:9999");
}

// the spouts
class ShowtInterface extends StormSpout(List("showt")) {
  def nextTuple {
    val s = conn.puller.recv(0);
    println(s)
    emit(new String(s));
  }
}
