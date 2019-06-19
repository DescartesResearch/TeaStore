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
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItem;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItemEntity;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductEntity;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;
import tools.descartes.research.faasteastorelibrary.requests.cartitem.CreateNewCartItemRequest;
import tools.descartes.research.faasteastorelibrary.requests.cartitem.DeleteCartItemRequest;
import tools.descartes.research.faasteastorelibrary.requests.cartitem.GetAllCartItemsOfUserByIdRequest;
import tools.descartes.research.faasteastorelibrary.requests.cartitem.GetCartItemByIdRequest;
import tools.descartes.research.faasteastorelibrary.requests.product.GetProductByIdRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.webui.authentication.AuthenticatorSingleton;
import tools.descartes.teastore.webui.cart.CartManagerSingleton;

/**
 * Servlet for handling all cart actions.
 *
 * @author Andre Bauer
 */
@WebServlet( "/cartAction" )
public class CartActionServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern( "MM/yyyy" );

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CartActionServlet( )
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
        for ( Object paramo : request.getParameterMap( ).keySet( ) )
        {
            String param = ( String ) paramo;

            if ( param.contains( "addToCart" ) )
            {
                long productID = Long.parseLong( request.getParameter( "productid" ) );

                addToCart( productID );

                redirect( "/cart", response, MESSAGECOOKIE, String.format( ADDPRODUCT, productID ) );

                break;
            }
            else if ( param.contains( "removeProduct" ) )
            {
                long productID = Long.parseLong( param.substring( "removeProduct_".length( ) ) );

                deleteFromCart( productID );

                redirect( "/cart", response, MESSAGECOOKIE, String.format( REMOVEPRODUCT, productID ) );

                break;
            }
            else if ( param.contains( "updateCartQuantities" ) )
            {
                List< CartItem > cartItems = CartManagerSingleton.getInstance( ).getCartItems( );

                updateCartItems( request, cartItems, response );

                redirect( "/cart", response, MESSAGECOOKIE, CARTUPDATED );

                break;
            }
            else if ( param.contains( "proceedtoCheckout" ) )
            {
                if ( isLoggedIn() )
                {
//                    List< OrderItem > orderItems = getSessionBlob( request ).getOrderItems( );

                    List< CartItem > cartItems = CartManagerSingleton.getInstance( ).getCartItems( );

                    updateCartItems( request, cartItems, response );

                    redirect( "/order", response );
                }
                else
                {
                    redirect( "/login", response );
                }
                break;
            }
            else if ( param.contains( "confirm" ) )
            {
                confirmOrder( request, response );
                break;
            }
        }
    }

    private void addToCart( final long productId )
    {
        CartItem cartItem = createNewCartItem( productId );

        CartManagerSingleton.getInstance( ).addCartItem( cartItem );
    }

    private CartItem createNewCartItem( final long productId )
    {
        ProductEntity product = getProductById( productId );

        CartItem cartItem = new CartItem( );
        cartItem.setProduct( product );
        cartItem.setQuantity( 1 );

        return cartItem;
    }

    private ProductEntity getProductById( final long productId )
    {
        return new GetProductByIdRequest( productId ).performRequest( ).getParsedResponseBody( );
    }

    private void deleteFromCart( final long productId )
    {
        CartManagerSingleton.getInstance( ).deleteCartItem( productId );
    }

    private void updateCartItems( HttpServletRequest request, List< CartItem > cartItems, HttpServletResponse
            response )
    {
        SessionBlob blob = getSessionBlob( request );

        for ( CartItem cartItem : cartItems )
        {
            if ( request.getParameter( "orderitem_" + cartItem.getProduct( ).getId( ) ) != null )
            {
                int quantity =
                        Integer.parseInt( request.getParameter( "orderitem_" + cartItem.getProduct( ).getId( ) ) );

                cartItem.setQuantity( quantity );

//                blob = LoadBalancedStoreOperations.updateQuantity( blob, orderItem.getProductId( ),
//                        Integer.parseInt( request.getParameter( "orderitem_" + orderItem.getProductId( ) ) ) );
            }
        }
        saveSessionBlob( blob, response );
    }

//    private void updateCartItems( )
//    {
//        //wahrscheinlich updateOrder aufrufen
//        List< CartItemEntity > cartItems = getAllCartItemsOfUserById( );
//
//        for ( CartItemEntity cartItem : cartItems )
//        {
//
//        }
//    }

    private List< CartItemEntity > getAllCartItemsOfUserById( )
    {
        UserEntity user = AuthenticatorSingleton.getInstance( ).getUser( );

        return new GetAllCartItemsOfUserByIdRequest( 0, 100, user.getId( ) ).performRequest( )
                .getParsedResponseBody( );
    }

    /**
     * Handles the confirm order action. Saves the order into the sessionBlob
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void confirmOrder( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {

        String[] infos = extractOrderInformation( request );
        if ( infos.length == 0 )
        {
            redirect( "/order", response );
        }
        else
        {

            SessionBlob blob = getSessionBlob( request );
            long price = 0;
            for ( OrderItem item : blob.getOrderItems( ) )
            {
                price += item.getQuantity( ) * item.getUnitPriceInCents( );
            }
//            blob = LoadBalancedStoreOperations.placeOrder( getSessionBlob( request ), infos[ 0 ] + " " + infos[ 1 ],
//                    infos[ 2 ],
//                    infos[ 3 ], infos[ 4 ],
//                    YearMonth.parse( infos[ 6 ], DTF ).atDay( 1 ).format( DateTimeFormatter.ISO_LOCAL_DATE ), price,
//                    infos[ 5 ] );
//            saveSessionBlob( blob, response );
            redirect( "/", response, MESSAGECOOKIE, ORDERCONFIRMED );
        }

    }

    /**
     * Extracts the user information from the input fields.
     *
     * @param request
     * @return String[] with user infos.
     */
    private String[] extractOrderInformation( HttpServletRequest request )
    {

        String[] parameters = new String[] { "firstname", "lastname", "address1", "address2", "cardtype", "cardnumber",
                "expirydate" };
        String[] infos = new String[ parameters.length ];
        for ( int i = 0; i < parameters.length; i++ )
        {
            if ( request.getParameter( parameters[ i ] ) == null )
            {
                return new String[ 0 ];
            }
            else
            {
                infos[ i ] = request.getParameter( parameters[ i ] );
            }
        }
        return infos;
    }
}
