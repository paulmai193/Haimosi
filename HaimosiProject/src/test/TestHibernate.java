import logia.hibernate.util.HibernateUtil;

public class TestHibernate {

	public static void main(String[] args) {
		HibernateUtil.setConfigPath("hibernate.cfg.xml");
		try {
			HibernateUtil.indexing();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		HibernateUtil.releaseFactory();
		System.exit(0);
	}
}
