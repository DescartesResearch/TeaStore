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

import tools.descartes.research.faasteastorelibrary.interfaces.cartitem.CartItem;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductEntity;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;
import tools.descartes.research.faasteastorelibrary.requests.order.ConfirmOrderRequest;
import tools.descartes.research.faasteastorelibrary.requests.product.GetProductByIdRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

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

    private final Logger logger = Logger.getLogger( "CartActionServlet" );

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

                addToCart( productID, request );

                redirect( "/cart", response, MESSAGECOOKIE, String.format( ADDPRODUCT, productID ) );

                break;
            }
            else if ( param.contains( "removeProduct" ) )
            {
                long productID = Long.parseLong( param.substring( "removeProduct_".length( ) ) );

                deleteFromCart( productID, request );

                redirect( "/cart", response, MESSAGECOOKIE, String.format( REMOVEPRODUCT, productID ) );

                break;
            }
            else if ( param.contains( "updateCartQuantities" ) )
            {
                updateCartItems( request, response );

                redirect( "/cart", response, MESSAGECOOKIE, CARTUPDATED );

                break;
            }
            else if ( param.contains( "proceedtoCheckout" ) )
            {
                this.logger.info( "isLoggedIn(): " + isLoggedIn( request ) );

                if ( isLoggedIn( request ) )
                {
                    updateCartItems( request, response );

                    this.logger.info( "navigate to /order" );

                    redirect( "/order", response );
                }
                else
                {
                    this.logger.info( "navigate to /login" );

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

    private void addToCart( final long productId, final HttpServletRequest request )
    {
        List< CartItem > cartItems = getCartItems( request );

        boolean isProductAlreadyInCart = false;

        for ( CartItem cartItem : cartItems )
        {
            if ( cartItem.getProduct( ).getId( ) == productId )
            {
                isProductAlreadyInCart = true;

                cartItem.setQuantity( cartItem.getQuantity( ) + 1 );

                break;
            }
        }

        if ( !isProductAlreadyInCart )
        {
            CartItem cartItem = createNewCartItem( productId );

            cartItems.add( cartItem );
        }

        request.getSession( ).setAttribute( "cartItems", cartItems );
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

    private void deleteFromCart( final long productId, final HttpServletRequest request )
    {
        List< CartItem > cartItems = getCartItems( request );

        for ( CartItem cartItem : cartItems )
        {
            if ( cartItem.getProduct( ).getId( ) == productId )
            {
                cartItems.remove( cartItem );

                break;
            }
        }

        request.getSession( ).setAttribute( "cartItems", cartItems );
    }

    private void updateCartItems( HttpServletRequest request, HttpServletResponse response )
    {
//        SessionBlob blob = getSessionBlob( request );

        List< CartItem > cartItems = getCartItems( request );

        for ( CartItem cartItem : cartItems )
        {
            if ( request.getParameter( "orderitem_" + cartItem.getProduct( ).getId( ) ) != null )
            {
                int quantity =
                        Integer.parseInt( request.getParameter( "orderitem_" + cartItem.getProduct( ).getId( ) ) );

                cartItem.setQuantity( quantity );
            }
        }

        request.getSession( ).setAttribute( "cartItems", cartItems );

//        saveSessionBlob( blob, response );
    }

    private void confirmOrder( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        String[] infos = extractOrderInformation( request );

        this.logger.info( "infos.length = " + infos.length );

        if ( infos.length == 0 )
        {
            redirect( "/order", response );
        }
        else
        {
//            SessionBlob blob = getSessionBlob( request );

            List< CartItem > cartItems = getCartItems( request );

            String addressName = infos[ 0 ] + " " + infos[ 1 ];
            String address1 = infos[ 2 ];
            String address2 = infos[ 3 ];
            String creditCardCompany = infos[ 4 ];
            String creditCardNumber = infos[ 5 ];
            String creditCardExpiryDate = infos[ 6 ];

            UserEntity user = getLoggedInUser( request );

            this.logger.info( "confirmOrder request" );

            new ConfirmOrderRequest(
                    addressName,
                    address1,
                    address2,
                    creditCardCompany,
                    creditCardNumber,
                    creditCardExpiryDate,
                    cartItems,
                    user ).performRequest( );

            request.getSession( ).removeAttribute( "cartItems" );

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
