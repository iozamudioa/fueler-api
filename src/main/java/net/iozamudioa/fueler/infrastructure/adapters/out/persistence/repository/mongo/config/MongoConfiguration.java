package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConfiguration {

    @Bean
    MongoTransactionManager transactionManager(
            MongoDatabaseFactory databaseFactory) {

        return new MongoTransactionManager(databaseFactory);
    }

    @Bean
    public SmartInitializingSingleton disableMongoClassField(
            MappingMongoConverter converter) {

        return () -> converter.setTypeMapper(
                new DefaultMongoTypeMapper(null));
    }

}
