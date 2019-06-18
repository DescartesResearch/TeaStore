/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;

/**
 * Servlet implementation for the web view of "Order".
 *
 * @author Andre Bauer
 */
@WebServlet( "/order" )
public class OrderServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderServlet( )
    {
        super( );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleGETRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException, LoadBalancerTimeoutException
    {
        checkforCookie( request, response );

        if ( getSessionBlob( request ).getOrderItems( ).size( ) == 0 )
        {
            redirect( "/", response );
        }
        else
        {
            doPost( request, response );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handlePOSTRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException, LoadBalancerTimeoutException
    {
        request.setAttribute( "CategoryList", getAllCategories( ) );
        request.setAttribute( "storeIcon", getStoreIcon( ) );
        request.setAttribute( "title", "TeaStore Order" );
        request.setAttribute( "login", isLoggedIn( ) );

        request.getRequestDispatcher( "WEB-INF/pages/order.jsp" ).forward( request, response );
    }
}