package showtstats.apiinterface

import storm.scala.dsl._
import collection.mutable.{Map, HashMap}

import org.zeromq.ZMQ


// the spouts
class ShowtInterface extends StormSpout(List("showt")) {

  // var puller:ZMQ.Socket = _;
  // val context = ZMQ.context(1);
  // val puller = context.socket(ZMQ.PULL);

  // puller.connect("tcp://127.0.0.1:9999");

  // interface here with zeromq for showts
  def nextTuple {
    emit("""{"showt": "yes"}""");
    // emit(new String(puller.recv(0)));
  }
}


// class FollowInterface extends StormSpout(outputFields = List("follow"), isDistributed=false) {
//   // intarface here with zeromq for follows
//   // val context = ZMQ.context(1);
//   // val puller = context.socket(ZMQ.PULL);
//   // puller.connect("tcp://127.0.0.1:9998");

//   def nextTuple {
//     emit(new String(puller.recv(0)));
//   }
// }
