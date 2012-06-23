package ager;

public interface Loggr {
	public void log(String l);
	public void error(String l);
	public void error(Exception e);
}
