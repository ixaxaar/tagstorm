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
import showtstats.cassandra._


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

  val tagTemplates = Map("tags" -> List("tuple1", "tuple2",
    "tuple3", "tuple4", "tuple5", "tuple6", "tuple7", "tuple8",
    "tuple9", "tuple10", "tuple11", "tuple12", "tuple13", "tuple14",
    "tuple15", "tuple16", "tuple17", "tuple18", "tuple19", "tuple20",
    "tuple21", "tuple22", "tuple23", "tuple24", "tuple25", "tuple26",
    "tuple27", "tuple28"))
}


// mega stream splitter, works on showt streams
class ShowtTagger extends StormBolt(streamToFields=Templates.tagTemplates) {

  // the template map for all generated tuple streams
  val tupleTemplates = Templates.tupleTemplates;

  def execute(t: Tuple) {
    t.matchSeq {
      case Seq(json:String) =>
        val showt = json.parseJson.convertTo[Map[String, String]];
        var tags = mutable.ListBuffer.empty[String];

        tupleTemplates.map {
          case (key, fields) =>
            val tup = fields.map(f => showt.getOrElse(f, null));
            if (!tup.contains(null) && !tup.contains("")) {
              tup :+ showt("timestamp");
              tags += tup.mkString(":");
            }

          case _ =>
            println("tupleTemplates has some problem");
        }

        // emit the tags
        using anchor t toStream "tags" emit (tags.toList);

      case _ =>
        println("JSON received is not a string!");
    }

    t.ack;
  }
}
