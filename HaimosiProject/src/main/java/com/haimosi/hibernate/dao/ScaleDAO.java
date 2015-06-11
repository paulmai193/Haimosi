package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import com.haimosi.hibernate.pojo.ScalePOJO;

/**
 * The Class ScaleDAO.
 * 
 * @author Paul Mai
 */
public class ScaleDAO extends AbstractDAO<ScalePOJO, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.scalePool.returnObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<ScalePOJO> getPOJOClass() {
		return ScalePOJO.class;
	}

}
