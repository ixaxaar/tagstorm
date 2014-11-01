package stats.apiinterface

import storm.scala.dsl._
import collection.mutable.{Map, HashMap}

import org.zeromq.ZMQ


// the spouts
class ShowtInterface extends StormSpout(List("stuff")) {

  var puller:ZMQ.Socket = _;

  setup {
    val context = ZMQ.context(1);
    // val puller = context.socket(ZMQ.PULL);
    puller = context.socket(ZMQ.SUB);
    puller.subscribe("".getBytes());

    puller.bind("tcp://127.0.0.1:9999");
  }

  def nextTuple {
    emit(new String(puller.recv(0)));
  }
}
