

public interface ProcesMBean {
	// getteri
	public String getIdentifikatorProcesa();
	public String getRunningThreadData();
	public Thread.State getThreadState();
	public long getBrojacIteracija();
	public double getCpuTime();
	public String getTrenutakPokretanjaProcesa();
	public double getTrajanjeIteracijeKumulativ();
	// getteri i setteri
	public int getCpuBoundInstructions();
	public void setCpuBoundInstructions(int cbi);
	public int getNoncpuBoundTime() ;
	public void setNoncpuBoundTime(int ncbt);
	// operacije
	public void reset();
	public double dajSrednjuVrijednostTrajanjaIteracija();
	public String dajZauzeceCPU();
}
