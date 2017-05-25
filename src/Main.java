import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class Main {


    /**
     * Java Managament Extension, JMX arhitektura omogucava razvoj upravljivih Java aplikacija na standardiziran nacin.
     * Upravljive aplikacije su aplikacije cije je djelovanje moguce pratiti i kontrolirati.
     * Kontrola se odnosi najcesce na promjene stanja objekata (vrijednosti atributa) ili upravljanje njihovim zivotnim
     * ciklusom.
     * Najjednostavniji nacin da se aplikacija ucini upravljivom je definiranje standardnog MBean objekta.
     * Standardni MBean objekt predstavlja upravljiva svojstva aplikacije preko upravljackog sucelja koje MBean implementira.
     * Upravljacko sucelje specificira upravljive atribute i operacije aplikacije.
     * <p>
     * Naziv MBean objekta ima oblik: NazivDomene:svojstvo1=vrijednost1 [,svojstvo2=vrijednost2]...
     * <p>
     * <p>
     * Program je CPU bound ako bi se izvodio brze proporcionalno brzini CPUa. Program koji izvodi puno izracuna.
     * Npr izracunavanje vrijednosti broja pi
     * <p>
     * Program je I/O bound ako bi se izvodio brze proporcionalno I/O podsistemu. (disk) Npr. program koji puno
     * pretrazuje na disk jer ce zagusenje biti na citanju podataka s diska.
     *
     * @param args
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.initServer();
        main.initHtmlAdaptorServer();
        new Thread(main.getStockExchange("New York Stock Exchange")).start();
    }

    private MBeanServer mBeanServer;

    private void initServer() {
        mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    private void initHtmlAdaptorServer() {
        int port = 8080;
        HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
        htmlAdaptorServer.setPort(port);
        ObjectName htmlName = null;
        try {
            htmlName = new ObjectName("Adapter:name = html, port = " + port);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            System.out.println("Object name is malformed.");
        }
        try {
            mBeanServer.registerMBean(htmlAdaptorServer, htmlName);
            System.out.println("Object name: " + htmlName + " is registered.");
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
            System.out.println("Object is already registered.");
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
            System.out.println("Error while registration object.");
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
            System.out.println("Object is not compliant.");
        }
        htmlAdaptorServer.start();
    }

    private StockExchange getStockExchange(String name) {
        StockExchange stockExchange = new StockExchange(name);
        ObjectName stockExchangeName = null;
        try {
            stockExchangeName = new ObjectName("StockExchangeMBeans:type=StockExchange");
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        try {
            mBeanServer.registerMBean(stockExchange, stockExchangeName);
            System.out.println("StockExchange: " + stockExchangeName + " is registered.");
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
            System.out.println("Object is already registered.");
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
            System.out.println("Error while registration object.");
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
            System.out.println("Object is not compliant.");
        }
        return stockExchange;
    }
}
