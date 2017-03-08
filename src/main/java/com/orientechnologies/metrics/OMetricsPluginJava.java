package com.orientechnologies.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.orientechnologies.common.parser.OSystemVariableResolver;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OClientConnection;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;
import com.orientechnologies.orient.server.plugin.OServerPluginAbstract;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by frank on 03/01/2017.
 */
public class OMetricsPluginJava extends OServerPluginAbstract implements ODatabaseLifecycleListener {

  public static final MetricRegistry registry = new MetricRegistry();
  private final Slf4jReporter        slf4jReporter;
  private final Counter              connections;
  private final String               nodeName;
  private       OMetricsDocumentHook documentHook;

  public OMetricsPluginJava() {

    nodeName = OSystemVariableResolver.resolveVariable("ORIENTDB_NODE_NAME");

    slf4jReporter = Slf4jReporter.forRegistry(registry)
        .outputTo(LoggerFactory.getLogger(getClass().getPackage().getName()))
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    slf4jReporter.start(1, TimeUnit.MINUTES);

    final Graphite graphite = new Graphite(new InetSocketAddress("graphite", 2003));
    final GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(registry)
        .prefixedWith("orientdb")
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build(graphite);
    graphiteReporter.start(1, TimeUnit.MINUTES);

    connections = registry.counter(MetricRegistry.name(nodeName, "connections", "active"));

//    documentHook = new OMetricsDocumentHook();
  }

  @Override
  public void startup() {
    Orient.instance().addDbLifecycleListener(this);

  }

  @Override
  public void config(OServer oServer, OServerParameterConfiguration[] iParams) {
    super.config(oServer, iParams);

  }

  @Override
  public void onClientConnection(OClientConnection iConnection) {
    connections.inc();

  }

  @Override
  public void onClientDisconnection(OClientConnection iConnection) {
    connections.dec();
  }

  @Override
  public void onAfterClientRequest(OClientConnection iConnection, byte iRequestType) {

  }

  public String getName() {
    return "metric plugin java";
  }

  public PRIORITY getPriority() {
    return PRIORITY.REGULAR;
  }

  public void onCreate(ODatabaseInternal iDatabase) {
    if (documentHook == null)
      documentHook = new OMetricsDocumentHook(nodeName);
    iDatabase.registerHook(documentHook);

  }

  public void onOpen(ODatabaseInternal iDatabase) {
    if (documentHook == null)
      documentHook = new OMetricsDocumentHook(nodeName);
    iDatabase.registerHook(documentHook);

  }

  public void onClose(ODatabaseInternal iDatabase) {

    iDatabase.unregisterHook(documentHook);
  }

  public void onDrop(ODatabaseInternal iDatabase) {
    iDatabase.unregisterHook(documentHook);

  }

  public void onCreateClass(ODatabaseInternal iDatabase, OClass iClass) {

  }

  public void onDropClass(ODatabaseInternal iDatabase, OClass iClass) {

  }

  public void onLocalNodeConfigurationRequest(ODocument iConfiguration) {

  }
}
