/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.sample.aggregate.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author: fengyu.zhang
 */
@Configuration
@MapperScan(basePackages = "com.ly.train.flower.common.sample.aggregate.dao", sqlSessionFactoryRef = "dbSessionFactory")
public class DbConfiguration {
    @Bean("dataSource")
    public DruidDataSource getDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/flower?characterEncoding=UTF-8&serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("flower123");
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(30);
        dataSource.setValidationQuery("select version()");
        dataSource.init();
        return dataSource;
    }

    @Bean("dbSessionFactory")
    public SqlSessionFactory mySessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/aggregate/*.xml"));
        return bean.getObject();
    }
}
