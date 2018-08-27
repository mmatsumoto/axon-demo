package br.com.zup.axon.application.bank.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import java.util.*
import javax.sql.DataSource


@Configuration
class QuartzConfiguration(@Value("\${quartz.properties.config:quartz.properties}")
                          val quartzConfig: String) {

    @Bean
    fun schedulerFactoryBean(dataSource: DataSource): SchedulerFactoryBean = SchedulerFactoryBean().apply {
        setOverwriteExistingJobs(true);
        setDataSource(dataSource);
        setQuartzProperties(quartzProperties());
    }

    @Bean
    fun quartzProperties(): Properties = PropertiesFactoryBean().apply {
        setLocation(ClassPathResource(quartzConfig));
        afterPropertiesSet();
    }.`object`

}
