import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.hibernate.Session;

import com.haimosi.define.Config;
import com.haimosi.hibernate.dao.TransactionDAO;
import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.pool.DAOPool;
import com.haimosi.util.Payment;
import com.stripe.Stripe;
import com.stripe.model.Charge;

public class TestPayment {

	public static void main(String[] args) {
		Config.encrypt_password = "haimosiv1.0";
		Stripe.apiKey = "sk_test_lUPVYJ6U3a7vIh3c04s5XNke";
		HibernateUtil.setConfigPath("hibernate.cfg.xml");
		Session session = HibernateUtil.beginTransaction();
		try (TransactionDAO dao = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			TransactionPOJO trans = dao.get(session, 24);
			Payment payment = new Payment(trans.getUser().getCreditAccount().getCardNumber(), trans.getUser().getCreditAccount().getExpireDate(),
			        trans.getUser().getCreditAccount().getCvvNumber(), (int) (trans.getAmount() * 100), "usd", "Pay for Haimosi's goods");
			Charge charge = payment.doPayment();
			System.out.println(charge.getStatus());
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		HibernateUtil.closeSession(session);
		HibernateUtil.releaseFactory();
		System.exit(0);
	}
}
