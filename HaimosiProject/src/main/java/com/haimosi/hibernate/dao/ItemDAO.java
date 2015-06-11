package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import com.haimosi.hibernate.pojo.ItemPOJO;

/**
 * The Class ItemDAO.
 * 
 * @author Paul Mai
 */
public class ItemDAO extends AbstractDAO<ItemPOJO, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.itemPool.returnObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<ItemPOJO> getPOJOClass() {
		return ItemPOJO.class;
	}

}
