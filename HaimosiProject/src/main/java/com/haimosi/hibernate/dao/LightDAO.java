package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import com.haimosi.hibernate.pojo.LightPOJO;

/**
 * The Class LightDAO.
 * 
 * @author Paul Mai
 */
public class LightDAO extends AbstractDAO<LightPOJO, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.lightPool.returnObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<LightPOJO> getPOJOClass() {
		return LightPOJO.class;
	}

}
