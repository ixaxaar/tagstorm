package showtstats.cassandra

import com.datastax.driver.core._
import com.datastax.driver.core.exceptions._


class CassandraClient(datacenter:String, keyspace:String, concurrency:Int, nodes:String*) {
  var cluster:Cluster = _;
  var metadata:Metadata = _;
  var session:Session = _;

  def connect(node: String) = {
    // set pooling settings
    var pools:PoolingOptions = new PoolingOptions;
    pools.setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance.LOCAL, concurrency);
    // max connections per host: 1 for loacal DC nodes, 0 for remote DC nodes
    pools.setCoreConnectionsPerHost(HostDistance.LOCAL, 1);
    pools.setMaxConnectionsPerHost(HostDistance.LOCAL, 1);
    pools.setCoreConnectionsPerHost(HostDistance.REMOTE, 0);
    pools.setMaxConnectionsPerHost(HostDistance.REMOTE, 0);

    // add a try-catch here for more elegant error handling
    cluster = Cluster.builder()
      .addContactPoints(String.valueOf(nodes))
      .withPoolingOptions(pools)
      .withSocketOptions(new SocketOptions().setTcpNoDelay(true))
      .withLoadBalancingPolicy(new policies.DCAwareRoundRobinPolicy(datacenter))
      .build;

    metadata = cluster.getMetadata;

    session = cluster.connect(keyspace);
    println("Connected to " + nodes);
  }

  // force to use prepared statements?
  def prepare(statement:String):PreparedStatement = {
    val ps:PreparedStatement = session.prepare(statement);
    ps;
  }

  // sync execute, block so that each bolt is serialized
  // we do __NOT__ want to handle thread unsafe shit here right now
  // rather increase the parallelization of bolts if needed
  def execute(ps:PreparedStatement, params:String*) = {
    // try-catch here as well for IllegalArgumentException
    session.execute(ps.bind(params));
  }
}
