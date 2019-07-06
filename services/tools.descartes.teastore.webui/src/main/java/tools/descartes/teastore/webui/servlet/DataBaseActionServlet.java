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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.research.faasteastorelibrary.functions.database.image.generator.ProductImageGenerator;
import tools.descartes.research.faasteastorelibrary.functions.image.util.Base64Encoder;
import tools.descartes.research.faasteastorelibrary.functions.image.util.PrependMediaTypeToBase64String;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductImageEntity;
import tools.descartes.research.faasteastorelibrary.requests.database.*;
import tools.descartes.research.faasteastorelibrary.requests.image.CreateNewProductImageRequest;
import tools.descartes.research.faasteastorelibrary.requests.product.GetAllProductIdsRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Servlet implementation for handling the data base action.
 *
 * @author Andre
 */
@WebServlet( "/dataBaseAction" )
public class DataBaseActionServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;
    private static final String[] PARAMETERS = new String[] { "categories", "products", "users", "orders" };
    private static final Logger LOG = LoggerFactory.getLogger( DataBaseActionServlet.class );

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataBaseActionServlet( )
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
        if ( request.getParameter( "confirm" ) != null )
        {
            String[] infos = extractOrderInformation( request );

            if ( infos.length == 0 )
            {
                redirect( "/database", response );
            }
            else
            {
                destroySessionBlob( getSessionBlob( request ), response );

                int numberOfCategories = Integer.parseInt( infos[ 0 ] );
                int productsPerCategory = Integer.parseInt( infos[ 1 ] );
                int numberOfUsers = Integer.parseInt( infos[ 2 ] );
                int maxOrdersPerUser = Integer.parseInt( infos[ 3 ] );

                new DropTablesRequest( ).performRequest( );

                new GenerateCategoriesRequest( numberOfCategories ).performRequest( );
                new GenerateProductsRequest( productsPerCategory ).performRequest( );
                new GenerateUsersRequest( numberOfUsers ).performRequest( );
                new GenerateOrdersRequest( maxOrdersPerUser ).performRequest( );
                new GenerateOrderItemsRequest( 10 ).performRequest( );
//                new GenerateProductImagesRequest( 10 ).performRequest( );
                generateProductImages( 10 );

                redirect( "/status", response );
            }
        }
        else
        {
            redirect( "/", response );
        }
    }

    /**
     * Extracts the information from the input fields.
     *
     * @param request
     * @return String[] with the info for the database generation
     */
    private String[] extractOrderInformation( HttpServletRequest request )
    {
        String[] infos = new String[ PARAMETERS.length ];

        for ( int i = 0; i < PARAMETERS.length; i++ )
        {
            if ( request.getParameter( PARAMETERS[ i ] ) == null )
            {
                return new String[ 0 ];
            }
            else
            {
                infos[ i ] = request.getParameter( PARAMETERS[ i ] );
            }
        }

        return infos;
    }

    private void generateProductImages( final int numberOfShapes )
    {
        List< Long > listOfProductIds = getAllProductIds( );

        for ( Long productId : listOfProductIds )
        {
            BufferedImage productImage = generateNewProductImage( productId, numberOfShapes );

            String base64String = convertBufferedImageToBase64String( productImage );

            base64String = prependMediaType( base64String );

            postProductImage( new ProductImageEntity( productId, base64String ) );
        }
    }

    private List< Long > getAllProductIds( )
    {
        return new GetAllProductIdsRequest( 0, 0 ).performRequest( ).getParsedResponseBody( );
    }

    private BufferedImage generateNewProductImage( final long productId, final int numberOfShapes )
    {
        return new ProductImageGenerator( ).generateNewProductImage( productId, numberOfShapes );
    }

    private String convertBufferedImageToBase64String( final BufferedImage productImage )
    {
        return Base64Encoder.encode( productImage, "png" );
    }

    private String prependMediaType( final String base64String )
    {
        return PrependMediaTypeToBase64String.prependMediaType( base64String );
    }

    private void postProductImage( final ProductImageEntity productImageEntity )
    {
        new CreateNewProductImageRequest( productImageEntity ).performRequest( );
    }
}