import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;

/**
 * Created on 25.5.2017..
 */
public class StockExchange extends NotificationBroadcasterSupport implements StockExchangeMBean, Runnable {

    private String identificator;
    private Thread runningThread;

    //how long stock exchange is working that day
    private long workingTime;
    //when did it start working
    private long startingStockExchangeTime;
    //time of one iteration of negotiation
    private long negotiatingTime;
    private long cpuTime;
    private int cpuBoundInstructions = 100000000;
    private int nonCpuBoundTime = 100;

    private Random random;
    private ThreadMXBean threadMXBean;

    /**
     * Stock Exchange custom data
     **/

    //initial num of stocks available
    private int initialStockAmount = 10000;
    private int currentFreeStockAmount = 10000;
    //initial value of stocks on Stock Exchange
    private int initialStockValue = 500;

    //current value of stocks on Stock Exchange
    private int currentStockValue = 50;

    //initial money available for buying stock that I have
    private int initialMoney = 5000000;
    //current money that I have
    private int currentMoney = initialMoney;
    //current amount of stocks that I have
    private int currentStocks = 0;


    public StockExchange(String name) {
        this.identificator = name;
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.threadMXBean.setThreadCpuTimeEnabled(true);
        this.startingStockExchangeTime = System.currentTimeMillis();
        this.random = new Random();
    }

    @Override
    public void run() {
        this.negotiatingTime = 0;
        this.runningThread = Thread.currentThread();
        System.out.println("Stock Exchange: " + this.identificator + " is running,");
        System.out.println("Press ctrl+C for exit.");

        while (true) {
            negotiatingTime = System.currentTimeMillis();
            try {
                //wait 1 second before changing stock price
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //CPU job here is negotiatingTime about new changing of stock price
            int negotiationgStockPrizeChange = 0;
            for (int i = 0; i < random.nextInt(cpuBoundInstructions); i++) {
                //imaginary stock selling and buying
                int number = random.nextInt(5);
                negotiationgStockPrizeChange += random.nextBoolean() ? number : -number;
            }
            //after they have determine new stock price change value, update value
            if (currentStockValue + negotiationgStockPrizeChange > 0) {
                currentStockValue += negotiationgStockPrizeChange;
            } else {
                //don't allow negative or zero stock value because everyone will buy them
                currentStockValue = 1;
            }
            cpuTime = threadMXBean.getCurrentThreadCpuTime();
            workingTime += (System.currentTimeMillis() - negotiatingTime);
        }


    }

    @Override
    public String getStockExchangeIdentificatior() {
        return this.identificator;
    }

    @Override
    public String getRunningThreadData() {
        return this.runningThread.toString();
    }

    @Override
    public Thread.State getThreadState() {
        return this.runningThread.getState();
    }

    @Override
    public long getCpuTime() {
        return cpuTime / 100000;
    }

    @Override
    public long getWorkingTime() {
        return this.workingTime;
    }

    @Override
    public long getStartingTime() {
        return this.startingStockExchangeTime;
    }

    @Override
    public int getCpuBoundInstructions() {
        return this.cpuBoundInstructions;
    }

    @Override
    public void setCpuBoundInstructions(int cpuBoundInstructions) {
        this.cpuBoundInstructions = cpuBoundInstructions;
    }

    @Override
    public int getNonCpuBoundTime() {
        return this.nonCpuBoundTime;
    }

    @Override
    public void setNonCpuBoundTime(int nonCpuBoundTime) {
        this.nonCpuBoundTime = nonCpuBoundTime;
    }

    @Override
    public void reset() {
        AttributeChangeNotification attributeChangeNotification = new AttributeChangeNotification(this, 0, System.currentTimeMillis(), "Working time reseted",
                "workingTime", "long", workingTime, 0L);
        workingTime = 0;
        sendNotification(attributeChangeNotification);
    }

    @Override
    public void sellStock(int value) {
        if (currentStocks - value >= 0) {
            //reduce my stock amount
            currentStocks -= currentStocks - value;
            //increase stock exchange stock amount
            currentFreeStockAmount += value;
            //reduce my money
            currentMoney -= value * currentStockValue;
        } else {
            System.out.println("There is no free stock available");
            System.out.println("Needed: " + value);
            System.out.println("Available: " + currentFreeStockAmount);
        }
    }

    @Override
    public void buyStock(int value) {
        //if I can buy stocks
        if (currentStockValue * value <= currentMoney) {
            //reduce my sum of money
            currentMoney -= currentStockValue * value;
            //increase my sum of stocks
            currentStocks += value;
            //reduce free stocks
            currentFreeStockAmount -= value;
        } else {
            System.out.println("Don't have enough money to buy " + value + " stocks.");
            System.out.println("Needed " + currentStockValue * value);
            System.out.println("Available " + currentMoney);
        }
    }

    @Override
    public String getCpuBusyTime() {
        if (workingTime > 0) {
            return (100. * cpuTime) / (1000000. * workingTime) + "%";
        } else {
            return "CPU is not working right.";
        }
    }

    @Override
    public int getCurrentFreeStockAmount() {
        return this.currentFreeStockAmount;
    }

    @Override
    public int getMyCurrentStocksAmount() {
        return this.currentStocks;
    }

    @Override
    public int getCurrentMoney() {
        return this.currentMoney;
    }

    @Override
    public int getCurrentStockValue() {
        return this.currentStockValue;
    }

    @Override
    public void setInitialMoney(int money) {
        this.initialMoney = money;
    }

    @Override
    public int getInitialMoney() {
        return this.initialMoney;
    }


    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] strings = {
                AttributeChangeNotification.ATTRIBUTE_CHANGE
        };
        MBeanNotificationInfo mBeanNotificationInfo = new MBeanNotificationInfo(strings, AttributeChangeNotification.class.getName(), "Reset call is emitted.");
        return new MBeanNotificationInfo[]{
                mBeanNotificationInfo
        };
    }
}
