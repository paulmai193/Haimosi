package com.haimosi.servlet.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.haimosi.define.Constant;
import com.haimosi.define.StatusCode;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.param.ParamDefine;

/**
 * The Class AdminFilter.
 * 
 * @author Paul Mai
 */
public class AdminFilter implements Filter {

	/** The logger. */
	private final Logger              LOGGER = Logger.getLogger(this.getClass());

	/** The Constant listAllowReq. List of allow http request without validate */
	private static final List<String> listAllowReq;

	static {
		listAllowReq = new ArrayList<String>();

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
		String reqPath = req.getRequestURI();
		if (this.checkPath(reqPath) == false) {
			UserPOJO user = (UserPOJO) req.getAttribute(ParamDefine.USER);
			if (user != null && user.getRole().getIdRole().equals(Constant.USER_ROLE_ADMIN)) {
				chain.doFilter(request, response);
			}
			else {
				RequestDispatcher dispatcher = req.getRequestDispatcher("/errorhander?errorcode=" + StatusCode.BAD_REQUEST.getCode());
				dispatcher.forward(request, response);
				this.LOGGER.info("User was locked");
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
		for (String uri : AdminFilter.listAllowReq) {
			if (path.contains(uri)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
