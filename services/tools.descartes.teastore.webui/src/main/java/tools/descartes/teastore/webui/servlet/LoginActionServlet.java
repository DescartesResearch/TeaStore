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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;
import tools.descartes.research.faasteastorelibrary.requests.authentication.BasicAuthRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.webui.authentication.AuthenticatorSingleton;

/**
 * Servlet for handling the login actions.
 *
 * @author Andre Bauer
 */
@WebServlet( "/loginAction" )
public class LoginActionServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    private static Logger LOG = LoggerFactory.getLogger( LoginActionServlet.class );

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginActionServlet( )
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
        redirect( "/", response );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handlePOSTRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException, LoadBalancerTimeoutException
    {
        boolean login = false;

        if ( request.getParameter( "username" ) != null && request.getParameter( "password" ) != null )
        {
//            SessionBlob blob = LoadBalancedStoreOperations.login( getSessionBlob( request ),
//                    request.getParameter( "username" ), request.getParameter( "password" ) );

            loginUser( request.getParameter( "username" ), request.getParameter( "password" ) );

            if ( isLoggedIn( ) )
            {
//                saveSessionBlob( blob, response );

                if ( request.getParameter( "referer" ) != null
                        && request.getParameter( "referer" ).contains( "tools.descartes.teastore.webui/cart" ) )
                {
                    redirect( "/cart", response, MESSAGECOOKIE, SUCESSLOGIN );
                }
                else
                {
                    redirect( "/", response, MESSAGECOOKIE, SUCESSLOGIN );
                }
            }
            else
            {
                redirect( "/login", response, ERRORMESSAGECOOKIE, WRONGCREDENTIALS );
            }
        }
        else if ( request.getParameter( "logout" ) != null )
        {
            logoutUser( );
//            SessionBlob blob = LoadBalancedStoreOperations.logout( getSessionBlob( request ) );
//            saveSessionBlob( blob, response );
//            destroySessionBlob( blob, response );
            redirect( "/", response, MESSAGECOOKIE, SUCESSLOGOUT );
        }
        else
        {
            handleGETRequest( request, response );
        }
    }

    private void loginUser( final String userName, final String password )
    {
        UserEntity user = new BasicAuthRequest( userName, password ).performRequest( ).getParsedResponseBody( );

        if ( user != null )
        {
            LOG.info( "loginUser() -> User.username = " + user.getUserName( ) );

            AuthenticatorSingleton.getInstance( ).setUser( user );
        }

    }

    private void logoutUser( )
    {
        AuthenticatorSingleton.getInstance( ).logOutUser( );
    }
}