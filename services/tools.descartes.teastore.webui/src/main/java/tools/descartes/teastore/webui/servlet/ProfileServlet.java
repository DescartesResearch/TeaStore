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
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.OrderEntity;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;
import tools.descartes.research.faasteastorelibrary.requests.order.GetAllOrdersOfUserByIdRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.webui.authentication.AuthenticatorSingleton;
import tools.descartes.teastore.webui.servlet.formatter.DateTimeFormatter;
import tools.descartes.teastore.webui.servlet.formatter.PriceFormatter;

/**
 * Servlet implementation for the web view of "Profile".
 *
 * @author Andre Bauer
 */
@WebServlet( "/profile" )
public class ProfileServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger( "ProfileServlet" );

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfileServlet( )
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

        if ( !isLoggedIn( ) )
        {
            redirect( "/", response );
        }
        else
        {
            UserEntity user = getUser( );

            request.setAttribute( "storeIcon", getStoreIcon( ) );
            request.setAttribute( "CategoryList", getAllCategories( ) );
            request.setAttribute( "title", "TeaStore Home" );
            request.setAttribute( "User", getUser( ) );
            request.setAttribute( "Orders", getAllOrdersOfUserById( user.getId( ) ) );
            request.setAttribute( "login", isLoggedIn( ) );

            request.setAttribute( "dateTimeFormatter", new DateTimeFormatter( ) );
            request.setAttribute( "priceFormatter", new PriceFormatter( ) );

            request.getRequestDispatcher( "WEB-INF/pages/profile.jsp" ).forward( request, response );
        }
    }

    private UserEntity getUser( )
    {
        LOG.info( "getUser() -> " + AuthenticatorSingleton.getInstance( ).getUser( ).getId( ) );

        return AuthenticatorSingleton.getInstance( ).getUser( );
    }

    private List< OrderEntity > getAllOrdersOfUserById( final long userId )
    {
        List< OrderEntity > orders = new GetAllOrdersOfUserByIdRequest( 0, 100, userId ).performRequest( )
                .getParsedResponseBody( );

        LOG.info( "size() -> " + orders.size( ) );

        for ( OrderEntity order : orders )
        {
            LOG.info( order.toString( ) );
        }

        return orders;
    }
}