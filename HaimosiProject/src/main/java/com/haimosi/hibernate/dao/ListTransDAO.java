package com.haimosi.hibernate.dao;

import java.util.List;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.haimosi.exception.BadParamException;
import com.haimosi.hibernate.pojo.ListTransView;
import com.haimosi.param.IntegerParam;
import com.haimosi.pool.DAOPool;

/**
 * The Class ListTransDAO.
 * 
 * @author Paul Mai
 */
public class ListTransDAO extends AbstractDAO<ListTransView, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.listTransPool.returnObject(this);
		;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<ListTransView> getPOJOClass() {
		return ListTransView.class;
	}

	@SuppressWarnings("unchecked")
	public List<ListTransView> searchByKeyword(Session session, String keywords) {
		if (keywords.trim().isEmpty()) {
			return this.getList(session);
		}
		else {
			FullTextSession fullTextSession = HibernateUtil.getFullTextSession();
			List<ListTransView> finalList = null;
			try {
				QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(ListTransView.class).get();
				org.apache.lucene.search.Query lucenceQuery;
				Query fullTextQuery;

				IntegerParam integerKeyword;
				try {
					integerKeyword = new IntegerParam(keywords);
					lucenceQuery = queryBuilder.phrase().onField("idtransaction").andField("time").sentence(integerKeyword.getOriginalParam())
					        .createQuery();
					System.out.println(lucenceQuery);
				}
				catch (BadParamException e) {
					lucenceQuery = queryBuilder.phrase().onField("name").sentence(keywords).createQuery();
					System.out.println(lucenceQuery);
				}
				fullTextQuery = fullTextSession.createFullTextQuery(lucenceQuery, ListTransView.class);
				return fullTextQuery.list();
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
			return finalList;
		}
	}

}
