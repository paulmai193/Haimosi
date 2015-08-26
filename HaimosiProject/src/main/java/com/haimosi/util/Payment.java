package com.haimosi.util;

import java.util.HashMap;
import java.util.Map;

import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

/**
 * The Class Payment.
 * 
 * @author Paul Mai
 */
public class Payment {

	/** The card params. */
	private final Map<String, Object> cardParams   = new HashMap<String, Object>();

	/** The charge params. */
	private final Map<String, Object> chargeParams = new HashMap<String, Object>();

	/**
	 * Instantiates a new payment.
	 *
	 * @param cardNumber the card number
	 * @param exp_month the exp_month
	 * @param exp_year the exp_year
	 * @param ccvNumber the ccv number
	 * @param amount the amount
	 * @param currency the currency
	 * @param decription the decription
	 */
	public Payment(String cardNumber, int exp_month, int exp_year, String ccvNumber, Float amount, String currency, String decription) {
		/* Card info */
		this.cardParams.put("number", cardNumber);
		this.cardParams.put("exp_month", exp_month);
		this.cardParams.put("exp_year", exp_year);
		this.cardParams.put("cvc", ccvNumber);

		/* Payment info */
		this.chargeParams.put("amount", amount);
		this.chargeParams.put("currency", currency);
		this.chargeParams.put("card", this.cardParams);
		this.chargeParams.put("description", decription);
	}

	/**
	 * Instantiates a new payment.
	 *
	 * @param cardNumber the card number
	 * @param expireDate the expire date
	 * @param ccvNumber the ccv number
	 * @param amount the amount
	 * @param currency the currency
	 * @param decription the decription
	 */
	public Payment(String cardNumber, String expireDate, String ccvNumber, Integer amount, String currency, String decription) {
		/* Card info */
		int exp_month;
		int exp_year;
		try {
			String[] arr = expireDate.split("/");
			exp_month = Integer.parseInt(arr[0]);
			exp_year = Integer.parseInt(arr[1]);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Invalid expire date");
		}

		this.cardParams.put("number", cardNumber);
		this.cardParams.put("exp_month", exp_month);
		this.cardParams.put("exp_year", exp_year);
		this.cardParams.put("cvc", ccvNumber);

		/* Payment info */
		this.chargeParams.put("amount", amount);
		this.chargeParams.put("currency", currency);
		this.chargeParams.put("card", this.cardParams);
		this.chargeParams.put("description", decription);
	}

	/**
	 * Do payment.
	 *
	 * @return the charge
	 * @throws AuthenticationException the authentication exception
	 * @throws InvalidRequestException the invalid request exception
	 * @throws APIConnectionException the API connection exception
	 * @throws CardException the card exception
	 * @throws APIException the API exception
	 */
	public Charge doPayment() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		Charge charge = Charge.create(this.chargeParams);
		return charge;
	}
}
