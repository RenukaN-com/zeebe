/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.1. You may not use this file
 * except in compliance with the Zeebe Community License 1.1.
 */
package io.camunda.zeebe.broker;

import io.camunda.zeebe.broker.jobstream.JobStreamService;
import io.camunda.zeebe.broker.system.management.BrokerAdminService;
import io.camunda.zeebe.broker.system.monitoring.BrokerHealthCheckService;
import io.camunda.zeebe.gateway.impl.stream.JobStreamClient;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/**
 * Helper class that allows Spring beans to access information from the Broker code that is not
 * managed by Spring
 */
@Component
public class SpringBrokerBridge {

  private Supplier<BrokerHealthCheckService> healthCheckServiceSupplier;
  private Supplier<BrokerAdminService> adminServiceSupplier;
  private Supplier<JobStreamService> jobStreamServiceSupplier;
  private Supplier<JobStreamClient> jobStreamClientSupplier;

  public void registerBrokerHealthCheckServiceSupplier(
      final Supplier<BrokerHealthCheckService> healthCheckServiceSupplier) {
    this.healthCheckServiceSupplier = healthCheckServiceSupplier;
  }

  public Optional<BrokerHealthCheckService> getBrokerHealthCheckService() {
    return Optional.ofNullable(healthCheckServiceSupplier).map(Supplier::get);
  }

  public void registerBrokerAdminServiceSupplier(
      final Supplier<BrokerAdminService> adminServiceSupplier) {
    this.adminServiceSupplier = adminServiceSupplier;
  }

  public Optional<BrokerAdminService> getAdminService() {
    return Optional.ofNullable(adminServiceSupplier).map(Supplier::get);
  }

  public void registerJobStreamClientSupplier(
      final Supplier<JobStreamClient> jobStreamClientSupplier) {
    this.jobStreamClientSupplier = jobStreamClientSupplier;
  }

  public Optional<JobStreamClient> getJobStreamClient() {
    return Optional.ofNullable(jobStreamClientSupplier).map(Supplier::get);
  }

  public void registerJobStreamServiceSupplier(
      final Supplier<JobStreamService> jobStreamServiceSupplier) {
    this.jobStreamServiceSupplier = jobStreamServiceSupplier;
  }

  public Optional<JobStreamService> getJobStreamService() {
    return Optional.ofNullable(jobStreamServiceSupplier).map(Supplier::get);
  }
}
