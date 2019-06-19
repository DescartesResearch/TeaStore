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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItem;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductEntity;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;
import tools.descartes.research.faasteastorelibrary.requests.recommender.GetRecommendedProductsRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.webui.authentication.AuthenticatorSingleton;
import tools.descartes.teastore.webui.cart.CartManagerSingleton;
import tools.descartes.teastore.webui.servlet.network.ProductImageHelper;

/**
 * Servlet implementation for the web view of "Cart".
 *
 * @author Andre Bauer
 */
@WebServlet( "/cart" )
public class CartServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CartServlet( )
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

        request.setAttribute( "storeIcon", getStoreIcon( ) );
        request.setAttribute( "title", "TeaStore Cart" );
        request.setAttribute( "CategoryList", getAllCategories( ) );
        request.setAttribute( "CartItems", getCartItems( ) );
        request.setAttribute( "login", isLoggedIn( ) );

        request.setAttribute( "RecommendedProducts", getRecommendedProducts( ) );
        request.setAttribute( "productImageHelper", new ProductImageHelper( ) );

        request.getRequestDispatcher( "WEB-INF/pages/cart.jsp" ).forward( request, response );
    }

    private List< CartItem > getCartItems( )
    {
        return CartManagerSingleton.getInstance( ).getCartItems( );
    }

    private List< ProductEntity > getRecommendedProducts( )
    {
        UserEntity user = AuthenticatorSingleton.getInstance( ).getUser( );

        long userId = 0;

        if ( user != null )
        {
            userId = user.getId( );
        }

        List< CartItem > cartItems = CartManagerSingleton.getInstance( ).getCartItems( );

        return new GetRecommendedProductsRequest( userId, 3, cartItems )
                .performRequest( ).getParsedResponseBody( );
    }
}