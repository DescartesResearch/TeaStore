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
package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.teastore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.ImageSizePreset;

/**
 * Servlet implementation for the web view of "Login".
 * 
 * @author Andre Bauer
 */
@WebServlet("/login")
public class LoginServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, LoadBalancerTimeoutException {
		checkforCookie(request, response);
		request.setAttribute("CategoryList",
				LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "categories", Category.class, -1, -1));
		request.setAttribute("storeIcon",
				LoadBalancedImageOperations.getWebImage("icon", ImageSizePreset.ICON.getSize()));
		request.setAttribute("title", "TeaStore Login");
		request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));

		request.setAttribute("referer", request.getHeader("Referer"));

		request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
	}

}
