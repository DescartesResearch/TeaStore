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
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItem;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;
import tools.descartes.research.faasteastorelibrary.requests.order.ConfirmOrderRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
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
                this.logger.info( "isLoggedIn(): " + isLoggedIn( ) );

                if ( isLoggedIn( ) )
                {
//                    List< OrderItem > orderItems = getSessionBlob( request ).getOrderItems( );

                    List< CartItem > cartItems = CartManagerSingleton.getInstance( ).getCartItems( );

                    this.logger.info( "cartItems.size(): " + cartItems.size( ) );

                    updateCartItems( request, cartItems, response );

                    this.logger.info( "move to /order" );

                    redirect( "/order", response );
                }
                else
                {
                    this.logger.info( "move to /login" );

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
        CartManagerSingleton.getInstance( ).addCartItem( productId );
    }

    private void deleteFromCart( final long productId )
    {
        CartManagerSingleton.getInstance( ).deleteCartItem( productId );
    }

    private void updateCartItems( HttpServletRequest request, List< CartItem > cartItems, HttpServletResponse
            response )
    {
//        SessionBlob blob = getSessionBlob( request );

        for ( CartItem cartItem : cartItems )
        {
            if ( request.getParameter( "orderitem_" + cartItem.getProduct( ).getId( ) ) != null )
            {
                int quantity =
                        Integer.parseInt( request.getParameter( "orderitem_" + cartItem.getProduct( ).getId( ) ) );

                cartItem.setQuantity( quantity );
            }
        }

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

            List< CartItem > cartItems = CartManagerSingleton.getInstance( ).getCartItems( );

//            long price = 0;
//
//            for ( CartItem cartItem : cartItems )
//            {
//                price += cartItem.getQuantity( ) * cartItem.getProduct( ).getListPriceInCents();
//            }

            String addressName = infos[ 0 ] + " " + infos[ 1 ];
            String address1 = infos[ 2 ];
            String address2 = infos[ 3 ];
            String creditCardCompany = infos[ 4 ];
            String creditCardNumber = infos[ 5 ];
            String creditCardExpiryDate = infos[ 6 ];

            UserEntity user = AuthenticatorSingleton.getInstance( ).getUser( );

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
