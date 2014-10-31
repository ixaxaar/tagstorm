package showtstats.cassandra

import com.datastax.driver.core._
import com.datastax.driver.core.exceptions._

// todo: use shapeless to get rid of type erasure warnings
// import shapeless.Typeable._

class CassandraClient() {
  var cluster:Cluster = _;
  var metadata:Metadata = _;
  var session:Session = _;


  def connect(datacenter:String, keyspace:String, concurrency:Int, nodes:String*) = {
    // set pooling settings
    // var pools:PoolingOptions = new PoolingOptions;
    // pools.setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance.LOCAL, concurrency);
    // max connections per host: 1 for loacal DC nodes, 0 for remote DC nodes
    // pools.setCoreConnectionsPerHost(HostDistance.LOCAL, 1);
    // pools.setMaxConnectionsPerHost(HostDistance.LOCAL, 1);
    // pools.setCoreConnectionsPerHost(HostDistance.REMOTE, 0);
    // pools.setMaxConnectionsPerHost(HostDistance.REMOTE, 0);

    // add a try-catch here for more elegant error handling
    cluster = Cluster.builder()
      .addContactPoints("localhost")
      // .withPoolingOptions(pools)
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
  def execute(ps:PreparedStatement, params:Any*) = {
    // try-catch here as well for IllegalArgumentException
    session.execute(ps.bind(params.map(_.asInstanceOf[AnyRef]) : _*));
  }


  def batchExecute[R](statements:List[R],
                  params:List[List[String]],
                  isCounter:Boolean=false,
                  consistency:ConsistencyLevel=ConsistencyLevel.LOCAL_ONE) = {

    var batchType:BatchStatement.Type = if (isCounter) BatchStatement.Type.COUNTER else BatchStatement.Type.UNLOGGED;
    var bs:BatchStatement = new BatchStatement(batchType);
    var exec:Boolean = true;

    // add all the batch statements
    statements match {
      case s:List[String] =>
        var ctr:Int = 0;
        for( ctr <- 0 until s.size) {
          bs.add(new SimpleStatement(s(ctr), params(ctr)));
        }

      case s:List[SimpleStatement] =>
        s.map { statement =>
          bs.add(statement);
        }

      case _ =>
        exec = false;
    }

    if (exec) session.execute(bs);
  }
}
