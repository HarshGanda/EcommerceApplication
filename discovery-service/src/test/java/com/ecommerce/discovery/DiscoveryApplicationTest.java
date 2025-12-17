package com.ecommerce.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscoveryApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testContextLoads() {
        // Test: Application context loads successfully, Eureka Server enabled
        assertNotNull(applicationContext);
        assertTrue(applicationContext.containsBean("eurekaServerBootstrap"));
    }

    @Test
    void testEurekaServerBeans() {
        // Test: Essential Eureka Server beans are present
        assertNotNull(applicationContext);
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0);

        boolean hasEurekaServerContext = false;
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("eureka")) {
                hasEurekaServerContext = true;
                break;
            }
        }
        assertTrue(hasEurekaServerContext, "Eureka Server context beans should be present");
    }
}

