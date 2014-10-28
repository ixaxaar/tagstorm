package showtstats.bucketer

import storm.scala.dsl._
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Tuple, Values}

import collection.mutable
import scala.language.postfixOps

import spray.json._
import DefaultJsonProtocol._

import showtstats.apiinterface._


object Templates {
  val tupleTemplates = Map(
    "tuple1"    ->  List("target_id"),
    "tuple2"    ->  List("showt"),
    "tuple3"    ->  List("channel"),
    "tuple4"    ->  List("world_region"),
    "tuple5"    ->  List("country"),
    "tuple6"    ->  List("state"),
    "tuple7"    ->  List("city"),
    "tuple8"    ->  List("partner_id"),
    "tuple9"    ->  List("telco"),
    "tuple10"   ->  List("target_id", "showt"),
    "tuple11"   ->  List("target_id", "channel"),
    "tuple12"   ->  List("target_id", "world_region"),
    "tuple13"   ->  List("target_id", "country"),
    "tuple14"   ->  List("target_id", "country", "state"),
    "tuple15"   ->  List("target_id", "country", "state", "city"),
    "tuple16"   ->  List("showt", "channel"),
    "tuple17"   ->  List("showt", "world_region"),
    "tuple18"   ->  List("showt", "country"),
    "tuple19"   ->  List("showt", "country", "state"),
    "tuple20"   ->  List("showt", "country", "state", "city"),
    "tuple21"   ->  List("channel", "world_region"),
    "tuple22"   ->  List("channel", "country"),
    "tuple23"   ->  List("channel", "state"),
    "tuple24"   ->  List("channel", "country", "state", "city"),
    "tuple25"   ->  List("target_id", "showt", "world_region"),
    "tuple26"   ->  List("target_id", "showt", "country"),
    "tuple27"   ->  List("target_id", "showt", "country", "state"),
    "tuple28"   ->  List("target_id", "showt", "country", "state", "city")
  );
}


// mega stream splitter, works on showt streams
class ShowtDeserializer extends StormBolt(Templates.tupleTemplates) {

  // the template map for all generated tuple streams
  val tupleTemplates = Templates.tupleTemplates;

  def execute(t: Tuple) {
    t.matchSeq {
      case Seq(json:String) =>
        val showt = json.parseJson.convertTo[Map[String, String]];

        tupleTemplates.map {
          case (key, fields) =>
            val tup = fields.map(f => showt(f));

            // okay, we have the tuple constructed from the showt, emit it!
            using anchor t toStream key emit (tup);

          case _ =>
            println("tupleTemplates has some problem");
        }
      case _ =>
        println("JSON received is not a string!");
    }

    t.ack;
  }
}


// deserialize the follow event, format yet to be decided
// class FollowDeserializer extends StormBolt(List("target_id")) {
//   def execute(t: Tuple) {
//     t.matchSeq {
//       case Seq(json:String) =>
//         val follow = json.parseJson.convertTo[Map[String, String]];

//         using anchor t emit (follow("target_id"));
//     }

//     t.ack;
//   }
// }
