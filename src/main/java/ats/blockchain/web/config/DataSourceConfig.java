package ats.blockchain.web.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 
 * @author shuhao.song
 * java 注解 数据源配置类
 *
 */
@Configuration
//指定扫描的mapper接口,dao层的接口，如果是com.ats.web,就会把com.ats.web下面所有的接口扫描成mybatis的映射类。
@MapperScan(basePackages = "ats.blockchain.web.dao", sqlSessionFactoryRef = "DBDataSqlSessionFactory")
public class DataSourceConfig {
	@Bean(name = "DBDataSource")	
	@ConfigurationProperties(prefix="spring.datasource") //告诉自动加载配置的属性
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "DBDataSqlSessionFactory")
	public SqlSessionFactory  sqlSessionFactory(@Qualifier("DBDataSource") DataSource dataSource)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
		return bean.getObject();
	}

	@Bean(name = "DBDataTransactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("DBDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "DBDataSqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(
			@Qualifier("DBDataSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
