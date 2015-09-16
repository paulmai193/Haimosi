import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.hibernate.Session;

import com.haimosi.hibernate.dao.TransactionDAO;
import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.pool.DAOPool;

public class TestHibernate {

	public static void main(String[] args) {
		HibernateUtil.setConfigPath("hibernate.cfg.xml");
		Session session = HibernateUtil.beginTransaction();
		try (TransactionDAO transDao = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			TransactionPOJO trans = transDao.getByIdCharge(session, "ch_16jXq3BEvZ6wV7rNms7pQC9O");
			if (trans != null) {
				System.out.println(trans.getIdTransaction());
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		HibernateUtil.closeSession(session);
		HibernateUtil.releaseFactory();
		System.exit(0);
	}
}
