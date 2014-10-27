package bucketer

import storm.scala.dsl._
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Tuple, Values}

import collection.mutable.{Map, HashMap}
import util.Random
import scala.language.postfixOps

import spray.json._

class Bucketer() {
}

object Bucketer {
  def execute() = {
    println("Executing bucketer");
  }
}
