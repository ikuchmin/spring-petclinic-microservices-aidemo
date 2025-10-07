/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.api;

import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServerConfigDataLocationResolver;
import org.springframework.cloud.config.client.ConfigServerInstanceProvider;
import org.springframework.util.ClassUtils;

import java.util.Collections;

/**
 * Bootstrap configuration for static discovery with config server.
 * Registers ConfigServerInstanceProvider.Function during bootstrap phase.
 */
public class SimpleDiscoveryConfigServerBootstrapConfiguration implements BootstrapRegistryInitializer {

    private final static String SIMPLE_DISCOVERY_PROPERTIES_PREFIX = "spring.cloud.discovery.client.simple";

    @Override
    public void initialize(BootstrapRegistry registry) {
        if (hasConfigServerInstanceProvider()) {
            return;
        }

        registry.registerIfAbsent(SimpleDiscoveryProperties.class, context -> {
            if (!isDiscoveryEnabled(context)) {
                return null;
            }
            var propertyResolver = getPropertyResolver(context);

            return propertyResolver.resolveConfigurationProperties(
                SIMPLE_DISCOVERY_PROPERTIES_PREFIX, SimpleDiscoveryProperties.class,
                SimpleDiscoveryProperties::new);
        });

        registry.registerIfAbsent(SimpleDiscoveryClient.class, context -> {
            if (!isDiscoveryEnabled(context)) {
                return null;
            }
            ConfigServerConfigDataLocationResolver.PropertyResolver propertyResolver = getPropertyResolver(context);
            SimpleDiscoveryProperties properties = propertyResolver.resolveConfigurationProperties(
                SIMPLE_DISCOVERY_PROPERTIES_PREFIX, SimpleDiscoveryProperties.class,
                SimpleDiscoveryProperties::new);
            return new SimpleDiscoveryClient(properties);
        });

        registry.registerIfAbsent(ConfigServerInstanceProvider.Function.class, context -> {
            if (!isDiscoveryEnabled(context)) {
                return (id) -> Collections.emptyList();
            }
            SimpleDiscoveryClient discoveryClient = context.get(SimpleDiscoveryClient.class);
            return discoveryClient::getInstances;
        });
    }

    private static boolean hasConfigServerInstanceProvider() {
        return !ClassUtils.isPresent("org.springframework.cloud.config.client.ConfigServerInstanceProvider", null);
    }

    private static ConfigServerConfigDataLocationResolver.PropertyResolver getPropertyResolver(
        BootstrapContext context) {
        return context.getOrElseSupply(ConfigServerConfigDataLocationResolver.PropertyResolver.class,
            () -> new ConfigServerConfigDataLocationResolver.PropertyResolver(context.get(Binder.class),
                context.getOrElse(BindHandler.class, null)));
    }

    private static boolean isDiscoveryEnabled(BootstrapContext bootstrapContext) {
        ConfigServerConfigDataLocationResolver.PropertyResolver propertyResolver = getPropertyResolver(
            bootstrapContext);
        return propertyResolver.get(ConfigClientProperties.CONFIG_DISCOVERY_ENABLED, Boolean.class, false)
            && propertyResolver.get("spring.cloud.discovery.enabled", Boolean.class, true);
    }
}
