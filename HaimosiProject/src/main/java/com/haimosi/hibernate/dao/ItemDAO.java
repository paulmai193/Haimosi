package com.haimosi.hibernate.dao;

import java.util.List;

import logia.hibernate.dao.AbstractDAO;

import org.hibernate.Query;
import org.hibernate.Session;

import com.haimosi.hibernate.pojo.ItemPOJO;
import com.haimosi.pool.DAOPool;

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

	/**
	 * Gets the list primaries.
	 *
	 * @param session the session
	 * @return the list primaries
	 */
	@SuppressWarnings("unchecked")
	public List<ItemPOJO> getListPrimaries(Session session) {
		String queryString = "from ItemPOJO where isPrimary = :primary";
		List<ItemPOJO> entities = null;
		try {
			Query query = session.createQuery(queryString);
			query.setBoolean("primary", true);
			entities = query.list();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return entities;
	}

}
