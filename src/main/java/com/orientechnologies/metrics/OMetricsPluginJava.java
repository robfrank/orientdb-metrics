package com.orientechnologies.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
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

import java.util.concurrent.TimeUnit;

/**
 * Created by frank on 03/01/2017.
 */
public class OMetricsPluginJava extends OServerPluginAbstract implements ODatabaseLifecycleListener {

  public static final MetricRegistry registry = new MetricRegistry();
  private final Slf4jReporter        reporter;
  private final Counter              counter;
  private final OMetricsDocumentHook documentHook;

  public OMetricsPluginJava() {

    reporter = Slf4jReporter.forRegistry(registry)
        .outputTo(LoggerFactory.getLogger("com.example.metrics"))
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    reporter.start(5, TimeUnit.SECONDS);

    counter = registry.counter(MetricRegistry.name("connections"));

    documentHook = new OMetricsDocumentHook();
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
    counter.inc();
  }

  @Override
  public void onClientDisconnection(OClientConnection iConnection) {
    counter.dec();
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
    iDatabase.registerHook(documentHook);

  }

  public void onOpen(ODatabaseInternal iDatabase) {
    iDatabase.registerHook(documentHook);

  }

  public void onClose(ODatabaseInternal iDatabase) {

    iDatabase.unregisterHook(documentHook);
  }

  public void onDrop(ODatabaseInternal iDatabase) {

  }

  public void onCreateClass(ODatabaseInternal iDatabase, OClass iClass) {

  }

  public void onDropClass(ODatabaseInternal iDatabase, OClass iClass) {

  }

  public void onLocalNodeConfigurationRequest(ODocument iConfiguration) {

  }
}
