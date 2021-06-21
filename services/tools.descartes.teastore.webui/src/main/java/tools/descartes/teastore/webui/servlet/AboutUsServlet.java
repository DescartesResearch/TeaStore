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
import java.util.Arrays;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.teastore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.teastore.entities.ImageSizePreset;

/**
 * Servlet implementation for the web view of "About us".
 * 
 * @author Andre Bauer
 */
@WebServlet("/about")
public class AboutUsServlet extends AbstractUIServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public AboutUsServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, LoadBalancerTimeoutException {
    checkforCookie(request, response);
    HashMap<String, String> portraits = LoadBalancedImageOperations
        .getWebImages(Arrays.asList("andreBauer", "johannesGrohmann", "joakimKistowski",
            "simonEismann", "norbertSchmitt", "samuelKounev"), ImageSizePreset.PORTRAIT.getSize());
    request.setAttribute("portraitAndre", portraits.get("andreBauer"));
    request.setAttribute("portraitJohannes", portraits.get("johannesGrohmann"));
    request.setAttribute("portraitJoakim", portraits.get("joakimKistowski"));
    request.setAttribute("portraitSimon", portraits.get("simonEismann"));
    request.setAttribute("portraitNorbert", portraits.get("norbertSchmitt"));
    request.setAttribute("portraitKounev", portraits.get("samuelKounev"));
    request.setAttribute("descartesLogo",
        LoadBalancedImageOperations.getWebImage("descartesLogo", ImageSizePreset.LOGO.getSize()));
    request.setAttribute("storeIcon",
        LoadBalancedImageOperations.getWebImage("icon", ImageSizePreset.ICON.getSize()));
    request.setAttribute("title", "TeaStore About Us");
    request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));

    request.getRequestDispatcher("WEB-INF/pages/about.jsp").forward(request, response);
  }

}
