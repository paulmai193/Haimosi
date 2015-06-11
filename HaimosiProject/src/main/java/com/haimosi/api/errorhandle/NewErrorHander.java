package com.haimosi.api.errorhandle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.haimosi.define.StatusCode;
import com.haimosi.param.ParamDefine;

/**
 * Servlet implementation class NewErrorHander.
 * 
 * @author Paul Mai
 */
@WebServlet("/errorhander")
public class NewErrorHander extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new new error hander.
	 *
	 * @see HttpServlet#HttpServlet()
	 */
	public NewErrorHander() {
		super();
	}

	/**
	 * On error process.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the string
	 */
	private String onErrorProcess(HttpServletRequest request) {
		String errorcode = request.getParameter("errorcode");
		String message = request.getParameter("errormessage");

		JsonObject error = new JsonObject();

		// Analyze the servlet exception
		if (errorcode != null) {
			error.add(ParamDefine.RESULT, StatusCode.printStatus(Integer.parseInt(errorcode), message));
		}
		else {
			Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
			if (statusCode != null) {
				error.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus());
			}
			else {
				Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
				if (throwable == null) {
					if (message != null) {
						error.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus(message));
					}
					else {
						error.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus());
					}
				}
				else {
					error.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus(throwable.toString()));
				}
			}
		}
		System.err.println(error.toString());
		return error.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String status = this.onErrorProcess(request);
		response.getWriter().print(status);
	}

	/**
	 * Do get.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String status = this.onErrorProcess(request);
		response.getWriter().print(status);
	}

	/**
	 * Do post.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String status = this.onErrorProcess(request);
		response.getWriter().print(status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String status = this.onErrorProcess(request);
		response.getWriter().print(status);
	}
}
