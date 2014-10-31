package showtstats.milestones

import storm.scala.dsl._
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Tuple, Values}

import collection.mutable
import scala.language.postfixOps


class Milestones extends StormBolt(streamToFields=Map("milestone" -> List("tag", "count"))) {

  val milestones = Array(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L);

  def execute(t:Tuple) {
    println(t)
    t matchSeq {
      case Seq(bucket:Long, tag:String, count:Long) =>
        if (milestones.contains(count) && bucket == 0L) {
          using anchor t toStream "feed" emit(tag, count);
        }

      case _ =>
        ()
    }

    t.ack;
  }
}
