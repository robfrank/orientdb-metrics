package com.orientechnologies.metrics;

import com.codahale.metrics.Counter;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;

import static com.codahale.metrics.MetricRegistry.name;
import static com.orientechnologies.metrics.OMetricsPluginJava.registry;

/**
 * Created by frank on 03/01/2017.
 */

public class OMetricsDocumentHook extends ODocumentHookAbstract {

  private final Counter reads;
  private final Counter writes;
  private final Counter replicatedWrites;
  private final Counter updates;
  private final String  nodeName;


  public OMetricsDocumentHook(String nodeName) {
    this.nodeName = nodeName;
    System.out.println("doc hook created");
    reads = registry.counter(name(nodeName, "documents", "read"));
    writes = registry.counter(name(nodeName, "documents", "write"));
    updates = registry.counter(name(nodeName, "documents", "update"));
    replicatedWrites = registry.counter(name(nodeName, "documents", "replicated", "write"));
  }

  @Override
  public void onRecordAfterUpdate(ODocument iDocument) {
    updates.inc();
  }

  @Override
  public void onRecordAfterCreate(ODocument iDocument) {
    writes.inc();
  }

  @Override
  public void onRecordAfterRead(ODocument iDocument) {
    reads.inc();
  }

  @Override
  public void onRecordCreateReplicated(ODocument iDocument) {
    replicatedWrites.inc();
  }

  public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
    return DISTRIBUTED_EXECUTION_MODE.BOTH;
  }
}
