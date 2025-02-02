package example

import org.hibernate.Hibernate

import grails.gorm.transactions.Rollback
import grails.test.hibernate.HibernateSpec
import spock.lang.Ignore

/**
 * Tests Proxy with hibernate-groovy-proxy
 */

class ProxySpec extends HibernateSpec {

    @Rollback
    @Ignore("java.lang.IllegalStateException: Either class [example.Customer] is not a domain class or GORM has not been initialized correctly or has already been shutdown. Ensure GORM is loaded and configured correctly before calling any methods on a GORM entity.")
    void "Test Proxy"() {
        when:
        new Customer(1, "Bob").save(failOnError: true, flush: true)
        hibernateDatastore.currentSession.clear()

        def proxy
        Customer.withNewSession {
            proxy = Customer.load(1)
        }

        then:
        //without ByteBuddyGroovyInterceptor this would normally cause the proxy to init
        proxy
        proxy.metaClass
        proxy.getMetaClass()
        !Hibernate.isInitialized(proxy)
        //id calls
        proxy.id == 1
        proxy.getId() == 1
        proxy["id"] == 1
        !Hibernate.isInitialized(proxy)
        // gorms trait implements in the class so no way to tell
        // proxy.toString() == "Customer : 1 (proxy)"
        // !Hibernate.isInitialized(proxy)
    }

}
