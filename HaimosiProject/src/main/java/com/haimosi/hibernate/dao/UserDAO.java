package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import org.hibernate.Query;
import org.hibernate.Session;

import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.pool.DAOPool;

/**
 * The Class UserDAO.
 * 
 * @author Paul Mai
 */
public class UserDAO extends AbstractDAO<UserPOJO, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.userPool.returnObject(this);
	}

	/**
	 * Login.
	 *
	 * @param session the session
	 * @param email the email
	 * @param password the password
	 * @return the user pojo
	 */
	public UserPOJO selectByEmail(Session session, String email) {
		String queryString = "from UserPOJO where email = :email";
		Query query = session.createQuery(queryString);
		query.setString("email", email);
		query.uniqueResult();
		return (UserPOJO) query.uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<UserPOJO> getPOJOClass() {
		return UserPOJO.class;
	}
}
