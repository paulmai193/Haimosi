import java.util.Date;
import java.util.Set;

import logia.hibernate.util.HibernateUtil;

import org.hibernate.Session;

import com.haimosi.define.Constant;
import com.haimosi.hibernate.dao.ItemDAO;
import com.haimosi.hibernate.dao.TransactionDAO;
import com.haimosi.hibernate.dao.UserDAO;
import com.haimosi.hibernate.pojo.ItemPOJO;
import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.hibernate.pojo.UserPOJO;

public class TestHibernate {

	public static void main(String[] args) {
		HibernateUtil.setConfigPath("hibernate.cfg.xml");

		Session session = HibernateUtil.beginTransaction();
		UserDAO userDAO = new UserDAO();
		ItemDAO itemDAO = new ItemDAO();
		TransactionDAO transDAO = new TransactionDAO();

		UserPOJO user = userDAO.get(session, 1);
		ItemPOJO item = itemDAO.get(session, 1);
		TransactionPOJO trans = new TransactionPOJO(null, user, item, 1, 1, Constant.PAYMENT_CASH, new Date(), Constant.TRANS_WAIT, null);
		try {
			transDAO.saveOrUpdate(session, trans);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(user.addLike(item));
		try {
			userDAO.update(session, user);
			HibernateUtil.commitTransaction(session);
		}
		catch (Exception e) {
			e.printStackTrace();
			HibernateUtil.rollbackTransaction(session);
		}

		Set<ItemPOJO> iLikes = user.getLikes();
		System.out.println(iLikes.size());

		Set<UserPOJO> uLikes = item.getLikes();
		System.out.println(uLikes.size());

		// System.out.println(user.removeLike(item));
		// try {
		// userDAO.update(session, user);
		// HibernateUtil.commitTransaction(session);
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// HibernateUtil.rollbackTransaction(session);
		// }

		System.out.println(trans.getItem().getLikes().size());
		HibernateUtil.releaseFactory();
		System.exit(0);
	}
}
