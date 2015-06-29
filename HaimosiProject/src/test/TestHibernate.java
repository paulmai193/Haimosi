import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.hibernate.Session;

import com.haimosi.hibernate.dao.ListTransDAO;
import com.haimosi.pool.DAOPool;

public class TestHibernate {

	public static void main(String[] args) {
		HibernateUtil.setConfigPath("hibernate.cfg.xml");
		Session session = HibernateUtil.getSession();
		try (ListTransDAO dao = AbstractDAO.borrowFromPool(DAOPool.listTransPool)) {
			HibernateUtil.indexing();
			// List<ListTransView> list = dao.searchByKeyword(session, "1");
			// for (ListTransView listTransView : list) {
			// System.out.println(listTransView.getIdTransaction());
			// }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		HibernateUtil.releaseFactory();
		System.exit(0);
	}
}
