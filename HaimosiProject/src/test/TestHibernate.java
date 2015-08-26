import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.hibernate.Session;

import com.haimosi.hibernate.dao.UserDAO;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.pool.DAOPool;

public class TestHibernate {

	public static void main(String[] args) {
		HibernateUtil.setConfigPath("hibernate.cfg.xml");
		Session session = HibernateUtil.beginTransaction();
		try (UserDAO dao = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			for (UserPOJO item : dao.getList(session)) {
				System.out.println(item.getFirstName());
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
