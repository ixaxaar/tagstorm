package main

import storm.scala.dsl._
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Tuple, Values}

import collection.mutable.{Map, HashMap}
import scala.language.postfixOps

import showtstats.apiinterface._
import showtstats.bucketer._


object Showtstats {
  def main(args:Array[String]) {
    println("Ahoy mateys");

    val builder = new TopologyBuilder;

    builder.setSpout("showts", new ShowtInterface, 1);

    builder.setBolt("showtsplitter", new ShowtTagger, 10)
      .shuffleGrouping("showts");

    val conf = new Config;
    conf.setDebug(true);
    conf.setMaxTaskParallelism(100);

    val cluster = new LocalCluster;
    cluster.submitTopology("showtstats", conf, builder.createTopology);
    while (true) {
      Thread sleep 10000;
    }
    cluster.shutdown;
  }
}

