package com.mot.upd.pcba.docgen;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.mot.upd.pcba.utils.SwaggerApplicationProperties;
import com.wordnik.swagger.jaxrs.config.BeanConfig;

@SuppressWarnings("serialFetch")
public class SwaggerServlet extends HttpServlet { 
	private static Logger log = Logger.getLogger(SwaggerServlet.class);
	
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        SwaggerApplicationProperties appProperties = SwaggerApplicationProperties.getInstance();
         // final PropertyResourceBundle bundle = InitProperty
    	//		.getProperty("pcbaSwagger.properties");
        String serviceVersion = appProperties.getAppProperty("swaggerServiceVersion");
        String basePath = appProperties.getAppProperty("swaggerBasePath");
        String resourcePackage = appProperties.getAppProperty("swaggerResourcePackage");
//        		swaggerServiceVersion=5.0.0
//        		swaggerBasePath=/Bootloader
//        		swaggerResourcePackage=com.mot.upd.bootloader
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(serviceVersion);
        beanConfig.setBasePath(basePath);
        beanConfig.setResourcePackage(resourcePackage);
        beanConfig.setScan(true);
      //  log.info("SwaggerBootstrap -- SWAGGER BOOTSTRAP SAYS HELLO!!! ");
      //  log.info("Swagger Version - "+serviceVersion);
     //   log.info("Swagger BasePath - "+basePath);
     //   log.info("Swagger Package - "+resourcePackage);
    }
}
