import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
  
public class JBossCaller {
  
    public static void main(String[] args) throws Exception {
 
        invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
 
    }
 
    public static void invoke(String host, String port, String user, String password, String beanDomain, String beanService, String func) {
        // preparing connection
        String urlString = "service:jmx:remote+http://" + host + ":" + port;
        JMXServiceURL serviceURL;
        try {
            serviceURL = new JMXServiceURL(urlString);
        } catch (MalformedURLException e) {
            System.out.printf("error creating URL from string: %s\n", urlString);
            System.out.println(e);
            return;
        }
        HashMap env = new HashMap();
        String[] creds = {user, password};
        env.put(JMXConnector.CREDENTIALS, creds);
 
        System.out.printf("Connecting to JBoss at %s...\n", host);
        JMXConnector jmxConnector;
        try {
            jmxConnector = JMXConnectorFactory.connect(serviceURL, env);
        } catch (SecurityException e) {
            System.out.println("ERROR! Security problem while connecting!");
            System.out.println(e);
            return;
        } catch (IOException e) {
            System.out.println("ERROR! IO error while connecting!");
            System.out.println(e);
            return;
        }
        MBeanServerConnection connection;
        try {
            connection = jmxConnector.getMBeanServerConnection();
        } catch (IOException e) {
            System.out.println("ERROR! IO error while connecting!");
            System.out.println(e);
            return;
        } catch (Exception e) {
            System.out.println("ERROR! Unknown MBeans error while connecting!");
            System.out.println(e);
            return;
        }

        System.out.printf("Invoking %s:%s.%s\n", beanDomain, beanService, func);
        String serviceNameString = beanDomain + ":name=" + beanService;
        ObjectName serviceConfigName;
        try {
            serviceConfigName = new ObjectName(serviceNameString);
        } catch (MalformedObjectNameException e) {
            System.out.printf("Error creating object name from string: %s\n", serviceNameString);
            System.out.println(e);
            return;
        }
 
        try {
            Object res = connection.invoke(serviceConfigName, func, null, null);
            System.out.println(res);
        } catch (IOException e) {
            System.out.println("ERROR! IO error while calling function!");
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("ERROR! Unknown MBeans error while calling function!");
            System.out.println(e);
        }
 
        try {
            jmxConnector.close();
        } catch (Exception e) {
            System.out.println("ERROR! IO error while closing connection!");
            System.out.println(e);
        }
    }
 
}
