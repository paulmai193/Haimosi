/*
 * 
 */
package com.haimosi.servlet.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.haimosi.define.Constant;
import com.haimosi.define.StatusCode;
import com.haimosi.hibernate.dao.UserDAO;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.param.ParamDefine;
import com.haimosi.pool.DAOPool;

/**
 * The Class AuthenticateFilter.
 * 
 * @author Paul Mai
 */
public class AuthenAppsFilter implements Filter {

	/** The logger. */
	private final Logger               LOGGER     = Logger.getLogger(this.getClass());

	/** The _client map. */
	public static Map<Integer, String> _clientMap = new HashMap<Integer, String>();

	/** The Constant listAllowReq. List of allow http request without validate */
	private static final List<String>  listAllowReq;

	static {
		listAllowReq = new ArrayList<String>();

		AuthenAppsFilter.listAllowReq.add("api/v1.0/apps/account/register");
		AuthenAppsFilter.listAllowReq.add("api/v1.0/apps/account/login");
		AuthenAppsFilter.listAllowReq.add("api/v1.0/apps/account/forgetpassword");
		AuthenAppsFilter.listAllowReq.add("api/v1.0/apps/account/resetpassword");
		AuthenAppsFilter.listAllowReq.add("api/v1.0/apps/account/activate");
		AuthenAppsFilter.listAllowReq.add("api/v1.0/apps/item/list");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		Session session = HibernateUtil.beginTransaction();
		req.setAttribute(ParamDefine.HIBERNATE_SESSION, session);

		String reqPath = req.getRequestURI();
		if (this.checkPath(reqPath) == false) {
			Integer idUser = (Integer) req.getSession().getAttribute(ParamDefine.USER);

			if (idUser != null) {
				String curToken = (String) req.getSession().getAttribute(ParamDefine.TOKEN);
				String trueToken = AuthenAppsFilter._clientMap.get(idUser);
				if (curToken.equals(trueToken)) {
					// Session session = (Session) req.getAttribute(ParamDefine.HIBERNATE_SESSION);
					try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
						UserPOJO user = userDAO.get(session, idUser);
						if (user != null) {
							Boolean firstTimeUse = (Boolean) req.getSession().getAttribute(ParamDefine.FIRST_TIME_USE);

							if (firstTimeUse != null && firstTimeUse.equals(Boolean.TRUE) || user.getStatus() == Constant.USER_STATUS_ACTIVATE) {
								req.setAttribute(ParamDefine.USER, user);

								chain.doFilter(req, response);
							}
							else {
								RequestDispatcher dispatcher = req.getRequestDispatcher("/errorhander?errorcode=" + StatusCode.LOCKED.getCode());
								dispatcher.forward(request, response);
								this.LOGGER.info("User was locked");
							}
						}
						else {
							RequestDispatcher dispatcher = req.getRequestDispatcher("/errorhander?errorcode=" + StatusCode.WRONG_ACCOUNT.getCode());
							dispatcher.forward(request, response);
							this.LOGGER.info("Wrong account");
						}
					}
					catch (Exception e) {
						this.LOGGER.error(e.getMessage(), e);
						RequestDispatcher dispatcher = req.getRequestDispatcher("/errorhander?errorcode=" + StatusCode.INTERNAL_ERROR.getCode()
								+ "&errormessage=" + e.getMessage());
						dispatcher.forward(request, response);
					}
				}
				else {
					req.getSession().invalidate();

					this.LOGGER.info("Session timeout, must login again");
					RequestDispatcher dispatcher = req.getRequestDispatcher("/errorhander?errorcode=" + StatusCode.MUST_LOGIN.getCode());
					dispatcher.forward(request, response);
				}
			}
			else {
				this.LOGGER.info("Not recognize session, maybe not login or session was timeout");
				RequestDispatcher dispatcher = req.getRequestDispatcher("/errorhander?errorcode=" + StatusCode.TIME_OUT.getCode());
				dispatcher.forward(request, response);
			}
		}
		else {
			chain.doFilter(request, response);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	/**
	 * Check path.
	 *
	 * @param path the path
	 * @return true, if successful
	 */
	private boolean checkPath(String path) {
		boolean result = false;
		for (String uri : AuthenAppsFilter.listAllowReq) {
			if (path.contains(uri)) {
				result = true;
				break;
			}
		}
		return result;
	}

}
