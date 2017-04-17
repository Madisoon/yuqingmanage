package com.alienlab.db;

public interface ISyncDb {
	/**
	 * 从数据库初始化对象
	 */
	public void initfdb();
	/**
	 * 将对象插入数据库
	 * @return
	 */
	public boolean insert2db();
	/**
	 * 依据对象更新数据库中值
	 * @return
	 */
	public boolean update2db();
	/**
	 * 从数据库中删除对象
	 * @return
	 */
	public boolean deletefdb();
}
