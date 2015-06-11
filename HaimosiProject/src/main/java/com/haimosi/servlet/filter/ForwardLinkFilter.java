package com.haimosi.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import logia.utility.string.EncryptionUtils;

import com.haimosi.define.Config;

/**
 * The Class ForwardLinkFilter.
 * 
 * @author Paul Mai
 */
public class ForwardLinkFilter implements Filter {

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
		String action = req.getParameter("a");
		if (action.equalsIgnoreCase("activation")) {
			String code = req.getParameter("s");
			code = EncryptionUtils.decode(code, Config.encrypt_password);
			String iduser = req.getParameter("i");

			System.out.println("/api/v1.0/apps/account/activate?verifycode=" + code + "&user=" + iduser);
			RequestDispatcher dispatcher = req.getRequestDispatcher("/api/v1.0/apps/account/activate?verifycode=" + code + "&user=" + iduser);
			dispatcher.forward(request, response);

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

}
