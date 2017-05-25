/**
 * Created by Vuki on 25.5.2017..
 */
public interface StockExchangeMBean {

    //getteri
    String getStockExchangeIdentificatior();

    String getRunningThreadData();

    Thread.State getThreadState();

    long getCpuTime();

    long getWorkingTime();

    long getStartingTime();

    int getCurrentFreeStockAmount();

    int getMyCurrentStocksAmount();

    int getCurrentMoney();

    int getCurrentStockValue();

    //getteri i setteri
    int getCpuBoundInstructions();

    void setCpuBoundInstructions(int cpuBoundInstructions);

    int getNonCpuBoundTime();

    void setNonCpuBoundTime(int nonCpuBoundTime);

    void setInitialMoney(int money);

    int getInitialMoney();

    //operacije
    void reset();

    void sellStock(int value);

    void buyStock(int value);

    String getCpuBusyTime();


}
