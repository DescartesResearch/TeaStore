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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItem;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductEntity;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductImageEntity;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;
import tools.descartes.research.faasteastorelibrary.requests.image.GetProductImagesByProductIdsRequest;
import tools.descartes.research.faasteastorelibrary.requests.recommender.GetRecommendedProductsRequest;
import tools.descartes.research.faasteastorelibrary.requests.user.GetAllUsersRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.webui.authentication.AuthenticatorSingleton;
import tools.descartes.teastore.webui.cart.CartManagerSingleton;

/**
 * Servlet implementation for the web view of "Cart".
 *
 * @author Andre Bauer
 */
@WebServlet( "/cart" )
public class CartServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    private final Logger logger = Logger.getLogger( "CartServlet" );

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

        List< ProductEntity > recommendedProducts = getRecommendedProducts( );

        request.setAttribute( "RecommendedProducts", recommendedProducts );
        request.setAttribute( "productImagesAsMap", getProductImagesAsMap( recommendedProducts ) );

        request.getRequestDispatcher( "WEB-INF/pages/cart.jsp" ).forward( request, response );
    }

    private List< CartItem > getCartItems( )
    {
        return CartManagerSingleton.getInstance( ).getCartItems( );
    }

    private List< ProductEntity > getRecommendedProducts( )
    {
        UserEntity loggedInUser = AuthenticatorSingleton.getInstance( ).getUser( );

        long userId;

        if ( loggedInUser == null )
        {
//            this.logger.info( "loggedInUser is null" );

            UserEntity firstUser = getFirstUserFromDatabase( );
            userId = firstUser.getId( );
        }
        else
        {
//            this.logger.info( "loggedInUser exists" );

            userId = loggedInUser.getId( );
        }

        List< CartItem > cartItems = CartManagerSingleton.getInstance( ).getCartItems( );

        return new GetRecommendedProductsRequest( userId, 3, cartItems )
                .performRequest( ).getParsedResponseBody( );
    }

    private Map< Long, String > getProductImagesAsMap( final List< ProductEntity > products )
    {
        List< Long > productIds = new LinkedList<>( );

        for ( ProductEntity product : products )
        {
            productIds.add( product.getId( ) );
        }

        List< ProductImageEntity > productImageEntities =
                new GetProductImagesByProductIdsRequest( productIds ).performRequest( ).getParsedResponseBody( );

        Map< Long, String > productImagesAsMap = new HashMap<>( );

        for ( ProductImageEntity productImageEntity : productImageEntities )
        {
            productImagesAsMap.put(
                    productImageEntity.getProductId( ),
                    productImageEntity.getProductImageAsBase64String( ) );
        }

        return productImagesAsMap;
    }


    private UserEntity getFirstUserFromDatabase( )
    {
        List< UserEntity > users = new GetAllUsersRequest( 0, 1 ).performRequest( ).getParsedResponseBody( );

        return users.get( 0 );
    }
}