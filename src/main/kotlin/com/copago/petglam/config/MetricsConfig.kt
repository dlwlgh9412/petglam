package com.copago.petglam.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {
    /**
     * 메트릭 레지스트리 커스터마이징
     */
    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags(
                "application", "petglam-api",
                "environment", "\${spring.profiles.active:default}"
            )
        }
    }

    /**
     * JVM 가비지 컬렉션 메트릭
     */
    @Bean
    fun jvmGcMetrics(): JvmGcMetrics {
        return JvmGcMetrics()
    }

    /**
     * JVM 메모리 메트릭
     */
    @Bean
    fun jvmMemoryMetrics(): JvmMemoryMetrics {
        return JvmMemoryMetrics()
    }

    /**
     * 프로세서 메트릭
     */
    @Bean
    fun processorMetrics(): ProcessorMetrics {
        return ProcessorMetrics()
    }
}