package com.orientechnologies.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Created by frank on 03/01/2017.
 */
public class OMetricsDocumentHook extends ODocumentHookAbstract {

  private final Counter document;

  public OMetricsDocumentHook() {
    document = OMetricsPluginJava.registry.counter(MetricRegistry.name(this.getClass(), "document"));
  }

  @Override
  public void onRecordAfterCreate(ODocument iDocument) {

  }

  @Override
  public void onRecordAfterRead(ODocument iDocument) {
    document.inc();
  }

  public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
    return DISTRIBUTED_EXECUTION_MODE.BOTH;
  }
}
