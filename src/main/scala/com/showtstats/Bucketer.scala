package showtstats.bucketer

import storm.scala.dsl._
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Tuple, Values}

import collection.mutable
import scala.language.postfixOps
import java.util.Date

import spray.json._
import DefaultJsonProtocol._

import com.datastax.driver.core.{PreparedStatement}

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

  // all this gymnastics to separate each tuple template member into a different stream
  // these here define stream names to which the above templates go to
  val streamTemplates = Map(
    "tuple1"    ->  List("bucket", "tag", "count"),
    "tuple2"    ->  List("bucket", "tag", "count"),
    "tuple3"    ->  List("bucket", "tag", "count"),
    "tuple4"    ->  List("bucket", "tag", "count"),
    "tuple5"    ->  List("bucket", "tag", "count"),
    "tuple6"    ->  List("bucket", "tag", "count"),
    "tuple7"    ->  List("bucket", "tag", "count"),
    "tuple8"    ->  List("bucket", "tag", "count"),
    "tuple9"    ->  List("bucket", "tag", "count"),
    "tuple10"   ->  List("bucket", "tag", "count"),
    "tuple11"   ->  List("bucket", "tag", "count"),
    "tuple12"   ->  List("bucket", "tag", "count"),
    "tuple13"   ->  List("bucket", "tag", "count"),
    "tuple14"   ->  List("bucket", "tag", "count"),
    "tuple15"   ->  List("bucket", "tag", "count"),
    "tuple16"   ->  List("bucket", "tag", "count"),
    "tuple17"   ->  List("bucket", "tag", "count"),
    "tuple18"   ->  List("bucket", "tag", "count"),
    "tuple19"   ->  List("bucket", "tag", "count"),
    "tuple20"   ->  List("bucket", "tag", "count"),
    "tuple21"   ->  List("bucket", "tag", "count"),
    "tuple22"   ->  List("bucket", "tag", "count"),
    "tuple23"   ->  List("bucket", "tag", "count"),
    "tuple24"   ->  List("bucket", "tag", "count"),
    "tuple25"   ->  List("bucket", "tag", "count"),
    "tuple26"   ->  List("bucket", "tag", "count"),
    "tuple27"   ->  List("bucket", "tag", "count"),
    "tuple28"   ->  List("bucket", "tag", "count")
  );
}


// mega stream splitter, works on showt streams
class ShowtTagger extends StormBolt(streamToFields=Templates.streamTemplates) {

  // the template map for all generated tuple streams
  val tupleTemplates = Templates.tupleTemplates;
  var client:CassandraClient = _;
  var incQuery:String = _;
  var getQuery:String = _;
  var incQPrep:PreparedStatement = _;
  var getQPrep:PreparedStatement = _;

  setup {
    client = new CassandraClient();
    client.connect("datacenter1", "daum", 1, "localhost");

    incQuery = "UPDATE milestone_counters SET ctr=ctr+1 WHERE tag=? AND time=?";
    getQuery = "SELECT ctr from milestone_counters WHERE tag=? AND time=?";
    incQPrep = client.prepare(incQuery);
    getQPrep = client.prepare(getQuery);
  }

  def execute(t: Tuple) {
    t.matchSeq {
      case Seq(json:String) =>
        val showt = json.parseJson.convertTo[Map[String, String]];
        val timestamp:Long = (math.floor((showt("timestamp").toFloat)/86400)*86400*1000).toLong;

        tupleTemplates.map {
          case (key, fields) =>
            val tup = fields.map(f => showt.getOrElse(f, null));

            if (!tup.contains(null) && !tup.contains("")) {
              val tag = tup.mkString(":");

              client.execute(incQPrep, tag, new Date(timestamp));
              client.execute(incQPrep, tag, new Date(0));
              val binCount = client.execute(getQPrep, tag, new Date(timestamp));
              val count = client.execute(getQPrep, tag, new Date(0));

              using anchor t toStream key emit (0, tag, count.one.getLong(0));
              using anchor t toStream key emit (timestamp, tag, binCount.one.getLong(0));
            }

          case _ =>
            println("tupleTemplates has some problem");
        }

      case _ =>
        println("JSON received is not a string!");
    }

    t.ack;
  }
}
