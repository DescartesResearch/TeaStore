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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.research.faasteastorelibrary.interfaces.image.size.ImageSize;
import tools.descartes.research.faasteastorelibrary.interfaces.image.size.ImageSizePreset;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductEntity;
import tools.descartes.research.faasteastorelibrary.requests.image.GetProductImageByProductIdRequest;
import tools.descartes.research.faasteastorelibrary.requests.product.GetProductByIdRequest;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.rest.LoadBalancedRecommenderOperations;
import tools.descartes.teastore.webui.servlet.elhelper.ELHelperUtils;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.webui.servlet.formatter.PriceFormatter;

/**
 * Servlet implementation for the web view of "Product".
 *
 * @author Andre Bauer
 */
@WebServlet( "/product" )
public class ProductServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductServlet( )
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

        if ( request.getParameter( "id" ) != null )
        {
            Long productId = Long.valueOf( request.getParameter( "id" ) );

            request.setAttribute( "CategoryList", getAllCategories( ) );
            request.setAttribute( "title", "TeaStore Product" );

            SessionBlob blob = getSessionBlob( request );

            request.setAttribute( "login", isLoggedIn( ) );

//            Product p = LoadBalancedCRUDOperations.getEntity( Service.PERSISTENCE, "products",
//                    Product.class, id );
            ProductEntity product = getProductById( productId );

            request.setAttribute( "product", product );

            List< OrderItem > items = new LinkedList<>( );
            OrderItem oi = new OrderItem( );
            oi.setProductId( productId );
            oi.setQuantity( 1 );
            items.add( oi );
            items.addAll( getSessionBlob( request ).getOrderItems( ) );

            List< Long > productIds = LoadBalancedRecommenderOperations.getRecommendations( items,
                    getSessionBlob( request ).getUID( ) );

            List< Product > ads = new LinkedList< Product >( );
            for ( Long pId : productIds )
            {
//                ads.add( LoadBalancedCRUDOperations.getEntity( Service.PERSISTENCE, "products", Product.class,
//                        pId ) );
            }

            if ( ads.size( ) > 3 )
            {
                ads.subList( 3, ads.size( ) ).clear( );
            }

            request.setAttribute( "Advertisment", ads );

//            request.setAttribute( "productImages", LoadBalancedImageOperations.getProductImages( ads,
//                    ImageSizePreset.RECOMMENDATION.getSize( ) ) );

            request.setAttribute( "productImage", getProductImageByProductId( productId ) );

            request.setAttribute( "storeIcon", getStoreIcon( ) );
            request.setAttribute( "helper", ELHelperUtils.UTILS );
            request.setAttribute( "priceFormatter", new PriceFormatter( ) );

            request.getRequestDispatcher( "WEB-INF/pages/product.jsp" ).forward( request, response );
        }
        else
        {
            redirect( "/", response );
        }
    }

    private ProductEntity getProductById( final long productId )
    {
        return new GetProductByIdRequest( productId ).performRequest( ).getParsedResponseBody( );
    }

    private String getProductImageByProductId( final long productId )
    {
        ImageSize imageSize = ImageSizePreset.FULL.getImageSize( );

        return new GetProductImageByProductIdRequest( productId, imageSize.getWidth( ), imageSize.getHeight( ) )
                .performRequest( ).getParsedResponseBody( );
    }
}