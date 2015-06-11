package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import com.haimosi.hibernate.pojo.RolePOJO;

/**
 * The Class RoleDAO.
 * 
 * @author Paul Mai
 */
public class RoleDAO extends AbstractDAO<RolePOJO, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.rolePool.returnObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<RolePOJO> getPOJOClass() {
		return RolePOJO.class;
	}

}
