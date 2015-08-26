package com.haimosi.hibernate.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import logia.hibernate.dao.AbstractDAO;

import org.hibernate.Query;
import org.hibernate.Session;

import com.haimosi.define.Constant;
import com.haimosi.hibernate.pojo.ListTransView;
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

	/**
	 * Search by keyword.
	 *
	 * @param session the session
	 * @param keywords the keywords
	 * @param begin the begin
	 * @param end the end
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<ListTransView> searchByKeyword(Session session, String keywords, Date begin, Date end) {
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD);
		String beginString;
		String endString;
		if (begin == null || end == null) {
			beginString = "2015-01-01";
			endString = sdf.format(new Date());
		}
		else {
			beginString = sdf.format(begin);
			endString = sdf.format(end);
		}

		// if (keywords.trim().isEmpty()) {
		List<ListTransView> finalList = null;
		String queryString = "from ListTransView where name like :item and date(time) between :begin and :end order by time desc";
		try {
			Query query = session.createQuery(queryString);
			query.setString("item", "%" + keywords + "%");
			query.setString("begin", beginString);
			query.setString("end", endString);
			System.out.println(query.toString());
			finalList = query.list();
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
		}

		return finalList;
		// }
		// else {
		// FullTextSession fullTextSession = HibernateUtil.getFullTextSession();
		// List<ListTransView> finalList = null;
		// try {
		// QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(ListTransView.class).get();
		// org.apache.lucene.search.Query lucenceQuery;
		// FullTextQuery fullTextQuery;
		//
		// if (begin != null && end != null) {
		// queryBuilder.range().onField("time").ignoreFieldBridge().from(DateTools.dateToString(begin, Resolution.SECOND))
		// .to(DateTools.dateToString(end, Resolution.SECOND));
		// }
		// lucenceQuery = queryBuilder.phrase().onField("name").sentence(keywords).createQuery();
		// System.out.println(lucenceQuery);
		// fullTextQuery = fullTextSession.createFullTextQuery(lucenceQuery, ListTransView.class);
		// fullTextQuery.setCriteriaQuery(session.createCriteria(ListTransView.class).addOrder(Order.desc("time")));
		// return fullTextQuery.list();
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// System.err.println(e.getMessage());
		// }
		// return finalList;
		// }
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
}
