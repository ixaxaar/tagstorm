package stats.achievements

import storm.scala.dsl._
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Tuple, Values}

import collection.mutable
import scala.language.postfixOps


class Achievements extends StormBolt(streamToFields=Map("achievement" -> List("tag", "count"))) {

  val achievements = Array(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L);

  def execute(t:Tuple) {
    t matchSeq {
      case Seq(bucket:Long, tag:String, count:Long) =>
        if (achievements.contains(count) && bucket != 0L) {
          using anchor t toStream "feed" emit(tag, count);
        }

      case _ =>
        ()
    }

    t.ack;
  }
}
