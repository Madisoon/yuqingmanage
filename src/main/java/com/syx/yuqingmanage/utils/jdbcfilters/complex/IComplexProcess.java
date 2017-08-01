package com.syx.yuqingmanage.utils.jdbcfilters.complex;

public interface IComplexProcess {
	public void begin(boolean reverse, int relation);

	public void end(boolean reverse);

	public void nextRelation(boolean reverse, int relation);

	public boolean process(boolean reverse, int relation, String value);

	public void processStart();

	public void processEnd();

	public boolean goout();
}
