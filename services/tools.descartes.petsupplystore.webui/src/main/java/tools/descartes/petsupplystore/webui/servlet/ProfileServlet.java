/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.petsupplystore.webui.servlet.elhelper.ELHelperUtils;

/**
 * Servlet implementation for the web view of "Profile"
 * 
 * @author Andre Bauer
 */
@WebServlet("/profile")
public class ProfileServlet extends AbstractUIServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProfileServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		checkforCookie(request,response);
		if (!LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request))) {
			redirect("/", response);
		} else {

			request.setAttribute("storeIcon", 
					LoadBalancedImageOperations.getWebImage("icon", ImageSize.Preset.ICON.getSize()));
			request.setAttribute("CategoryList", LoadBalancedStoreOperations.getCategories());
			request.setAttribute("title", "Pet Supply Store Home");
			request.setAttribute("User", LoadBalancedStoreOperations.getUser(getSessionBlob(request).getUID()));
			request.setAttribute("Orders",
					LoadBalancedStoreOperations.getOrdersForUser(getSessionBlob(request).getUID()));
			request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));
			request.setAttribute("helper", ELHelperUtils.UTILS);

			request.getRequestDispatcher("WEB-INF/pages/profile.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
