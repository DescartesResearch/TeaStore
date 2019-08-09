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
import tools.descartes.research.faasteastorelibrary.functions.database.category.generator.MultipleCategoryGenerator;
import tools.descartes.research.faasteastorelibrary.functions.database.image.generator.MultipleProductImageGenerator;
import tools.descartes.research.faasteastorelibrary.functions.database.order.generator.MultipleOrderGenerator;
import tools.descartes.research.faasteastorelibrary.functions.database.orderitem.generator.MultipleOrderItemGenerator;
import tools.descartes.research.faasteastorelibrary.functions.database.product.generator.MultipleProductGenerator;
import tools.descartes.research.faasteastorelibrary.functions.database.user.generator.MultipleUserGenerator;
import tools.descartes.research.faasteastorelibrary.requests.database.*;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
                int maxItemsPerOrder = 5;
                int numberOfShapes = 6;

                dropTables( );

                generateCategories( numberOfCategories );
                generateProducts( productsPerCategory );
                generateUsers( numberOfUsers );
                generateOrders( maxOrdersPerUser );
                generateOrderItems( maxItemsPerOrder );
                generateProductImages( numberOfShapes );

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

    private void dropTables( )
    {
        new DropTablesRequest( ).performRequest( );
    }

    private void generateCategories( final int numberOfCategories )
    {
        new MultipleCategoryGenerator( numberOfCategories ).generate( );
    }

    private void generateProducts( final int productsPerCategory )
    {
        new MultipleProductGenerator( productsPerCategory ).generate( );
    }

    private void generateUsers( final int numberOfUsers )
    {
        new MultipleUserGenerator( numberOfUsers ).generate( );
    }

    private void generateOrders( final int maxOrdersPerUser )
    {
        new MultipleOrderGenerator( maxOrdersPerUser ).generate( );
    }

    private void generateOrderItems( final int maxItemsPerOrder )
    {
        new MultipleOrderItemGenerator( maxItemsPerOrder ).generate( );
    }

    private void generateProductImages( final int numberOfShapes )
    {
        new MultipleProductImageGenerator( numberOfShapes ).generate( );
    }
}