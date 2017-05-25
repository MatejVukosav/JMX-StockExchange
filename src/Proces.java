import javax.management.NotificationBroadcasterSupport;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.Random;
/* 
Klasa upravljanog resursa koji ujedno predstavlja i MBean komponentu.
Opcenito, moguca je situacija u kojoj su klasa upravljanog objekta -procesa
i klasa MBeana koja predstavlja upravljacku sliku upravljanog objekta 
dvije razlicite klase. 
*/

public class Proces extends NotificationBroadcasterSupport implements ProcesMBean, Runnable {

    private String identifikatorProcesa = null;
    private Thread runningThread = null;
    private Thread.State threadState = null;
    private long brojacIteracija = 0;
    private long trenutakPokretanjaProcesa = 0L;
    private long trajanjeIteracijeKumulativ = 0L; // milisec
    private long cpuTime; // nanosec
    private int cpuBoundInstructions = 100000000;
    private int noncpuBoundTime = 100;

    Date time = new Date();
    Random randomGen = new Random();
    ThreadMXBean tm = null;

    // javni konstruktor
    public Proces(String id) {
        this.identifikatorProcesa = id;
        tm = ManagementFactory.getThreadMXBean();
        tm.setThreadCpuTimeEnabled(true);
        trenutakPokretanjaProcesa = System.currentTimeMillis();
    }

    // rutina procesa
    public void run() {
        long trenutakPocetkaIteracija = 0L;
        this.runningThread = Thread.currentThread();
        System.out.println("Proces pokrenut ...\nctrl+C za kraj.");
        while (true) {
            trenutakPocetkaIteracija = System.currentTimeMillis();
            brojacIteracija++;
            for (int i = 0; i < randomGen.nextInt(cpuBoundInstructions); i++)
                randomGen.nextInt(10);
            try {
                Thread.sleep(randomGen.nextInt(noncpuBoundTime));
            } catch (Exception e) {
            }
            //ovo su nonCPU bound aktivnosti te se za njihovo vrijeme ne trosi CPU vrijeme

            //vrijeme zauzimanja CPUa
            cpuTime = tm.getCurrentThreadCpuTime();
            //System.out.println(cpuTime);
            //ukupno vrijeme izvodenja iteracije
            trajanjeIteracijeKumulativ = trajanjeIteracijeKumulativ + (System.currentTimeMillis() - trenutakPocetkaIteracija);
        }

    }

    // Implementacije metoda iz ProcesMBean sucelja.
    public int getCpuBoundInstructions() {
        return cpuBoundInstructions;
    }

    public void setCpuBoundInstructions(int cbi) {
        cpuBoundInstructions = cbi;
    }

    public int getNoncpuBoundTime() {
        return noncpuBoundTime;
    }

    public void setNoncpuBoundTime(int ncbt) {
        noncpuBoundTime = ncbt;
    }

    public double getCpuTime() {
        return cpuTime / 1000000.; // milisec
    }

    public double getTrajanjeIteracijeKumulativ() {
        return trajanjeIteracijeKumulativ;
    }

    public String getTrenutakPokretanjaProcesa() {
        time.setTime(trenutakPokretanjaProcesa);
        return time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds();
    }

    public double dajSrednjuVrijednostTrajanjaIteracija() {
        if (brojacIteracija != 0)
            return 1. * trajanjeIteracijeKumulativ / brojacIteracija;
        else
            return -1.;
    }

    public String dajZauzeceCPU() {
        //System.out.println(cpuTime + ", " + trajanjeIteracijeKumulativ);
        if (trajanjeIteracijeKumulativ != 0)
            return (100. * cpuTime) / (1000000. * trajanjeIteracijeKumulativ) + "%";
        else
            return "error";
    }

    public long getBrojacIteracija() {
        return this.brojacIteracija;
    }

    public String getIdentifikatorProcesa() {
        return this.identifikatorProcesa;
    }

    public String getRunningThreadData() {
        return this.runningThread.toString();
    }

    public Thread.State getThreadState() {
        return this.runningThread.getState();
    }

    public void reset() {
        AttributeChangeNotification acn = new AttributeChangeNotification(this, 0, System.currentTimeMillis(), "resetiran brojac iteracija",
                "brojacIteracija", "long", new Long(brojacIteracija), new Long(0));
        brojacIteracija = 0;
        trajanjeIteracijeKumulativ = 0L;
        sendNotification(acn);
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]{
                new MBeanNotificationInfo(new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                        AttributeChangeNotification.class.getName(),
                        "Ova obavijest se emitira kad se poziva reset()	methoda.")};
    }
}