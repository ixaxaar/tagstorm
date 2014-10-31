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
import showtstats.milestones._


object Showtstats {
  def main(args:Array[String]) {
    println("Ahoy mateys");

    val builder = new TopologyBuilder;

    builder.setSpout("showts", new ShowtInterface, 1);

    builder.setBolt("showttags", new ShowtTagger, 10)
      .shuffleGrouping("showts");
    builder.setBolt("milestones1", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple1");
    builder.setBolt("milestones2", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple2");
    builder.setBolt("milestones3", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple3");
    builder.setBolt("milestones4", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple4");
    builder.setBolt("milestones5", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple5");
    builder.setBolt("milestones6", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple6");
    builder.setBolt("milestones7", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple7");
    builder.setBolt("milestones8", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple8");
    builder.setBolt("milestones9", new Milestones, 2)
      .shuffleGrouping("showttags", "tuple9");

    val conf = new Config;
    // conf.setDebug(true);
    conf.setMaxTaskParallelism(100);

    val cluster = new LocalCluster;
    cluster.submitTopology("showtstats", conf, builder.createTopology);
    while (true) {
      Thread sleep 10000;
    }
    cluster.shutdown;
  }
}

