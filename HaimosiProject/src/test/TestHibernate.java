import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.hibernate.Session;

import com.haimosi.hibernate.dao.ItemDAO;
import com.haimosi.hibernate.pojo.ItemPOJO;
import com.haimosi.pool.DAOPool;

public class TestHibernate {

	public static void main(String[] args) {
		HibernateUtil.setConfigPath("hibernate.cfg.xml");
		Session session = HibernateUtil.beginTransaction();
		try (ItemDAO dao = AbstractDAO.borrowFromPool(DAOPool.itemPool)) {
			for (ItemPOJO item : dao.getList(session)) {
				System.out.println(item.getName());
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
