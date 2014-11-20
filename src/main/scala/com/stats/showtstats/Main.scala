package stats.main

import storm.scala.dsl._
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.StormSubmitter
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Tuple, Values}

import collection.mutable.{Map, HashMap}
import scala.language.postfixOps

import stats.apiinterface._
import stats.bucketer._
import stats.milestones._
import stats.achievements._


object Showtstats {
  def main(args:Array[String]) {
    println("Ahoy mateys");

    val builder = new TopologyBuilder;

    builder.setSpout("stuffs", new ShowtInterface, 1);

    builder.setBolt("stufftags", new ShowtTagger, 10)
      .shuffleGrouping("stuffs");

    builder.setBolt("milestones1", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple1");
    builder.setBolt("milestones2", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple2");
    builder.setBolt("milestones3", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple3");
    builder.setBolt("milestones4", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple4");
    builder.setBolt("milestones5", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple5");
    builder.setBolt("milestones6", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple6");
    builder.setBolt("milestones7", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple7");
    builder.setBolt("milestones8", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple8");
    builder.setBolt("milestones9", new Milestones, 2)
      .shuffleGrouping("stufftags", "tuple9");

    builder.setBolt("achievement1", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple1");
    builder.setBolt("achievement2", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple2");
    builder.setBolt("achievement3", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple3");
    builder.setBolt("achievement4", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple4");
    builder.setBolt("achievement5", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple5");
    builder.setBolt("achievement6", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple6");
    builder.setBolt("achievement7", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple7");
    builder.setBolt("achievement8", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple8");
    builder.setBolt("achievement9", new Achievements, 2)
      .shuffleGrouping("stufftags", "tuple9");

    val conf = new Config;

    // for local testing
    // conf.setDebug(true);
    // conf.setMaxTaskParallelism(100);

    // local cluster
    // val cluster = new LocalCluster;
    // cluster.submitTopology("stats", conf, builder.createTopology);

    // production
    var cluster = new StormSubmitter;
    // cluster.submitTopology("stats", conf, builder.createTopology);

    // local cluster
    // while (true) {
    //   Thread sleep 10000;
    // }
    // cluster.shutdown;
  }
}

