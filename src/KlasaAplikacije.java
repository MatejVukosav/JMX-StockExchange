import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import java.lang.management.ManagementFactory;


public class KlasaAplikacije {

    public static void main(String[] args) {
        MBeanServer mBeanServer = null;
        try {
            // Inicijaliziranje reference na platform MBean Server
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        } catch (Exception e) {
            System.out.println(e);
        }


        // Kreiranje, registiranje i pokretanje HTML adaptera
        HtmlAdaptorServer html = new HtmlAdaptorServer();
        ObjectName html_name = null;
        try {
            html_name = new ObjectName("Adapteri:name=html,port=8082");
            mBeanServer.registerMBean(html, html_name);
            System.out.println("\tOBJECT NAME           = " + html_name + " registriran.");
        } catch (Exception e) {
            System.out.println("\t!!! Nije moguce kreirati HTML adapter !!!");
            e.printStackTrace();
            return;
        }
        html.start();


        // Kreiranje procesa.
        Proces proces = new Proces("DEmo Proces");
        ObjectName proces_name = null;
        // Kreiranje primjerka MBean komponente
        try {
            // Registriranje Proces MBean komponente na MBean serveru
            proces_name = new ObjectName("DemoMBeansi:type=Proces");
            mBeanServer.registerMBean(proces, proces_name);
            System.out.println("\tOBJECT NAME           = " + proces_name + " registriran.");
        } catch (MalformedObjectNameException e) {
            System.out.println("String koje je naveden kao parametar nije u zahtjevanom obliku." + e);

        } catch (InstanceAlreadyExistsException e) {
            System.out.println("MBean je ve� prijavljen na MBean serveru." + e);

        } catch (NullPointerException e) {
            System.out.println(e);

        } catch (MBeanRegistrationException e) {
            System.out.println("MBeanRegistrationException, MBean nece biti registriran." + e);

        } catch (NotCompliantMBeanException e) {
            System.out.println("Objekt proslijedjen kao parametar je null ili naziv objekta nije specificiran, " + e);
        }
        // Pokretanje procesa.
        new Thread(proces).start();

    }
}
