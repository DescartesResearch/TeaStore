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

import tools.descartes.research.faasteastorelibrary.interfaces.image.ExistingImage;
import tools.descartes.research.faasteastorelibrary.interfaces.image.size.ImageSize;
import tools.descartes.research.faasteastorelibrary.interfaces.image.size.ImageSizePreset;
import tools.descartes.research.faasteastorelibrary.requests.image.GetWebImageRequest;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation for the web view of "Error page".
 *
 * @author Andre Bauer
 */
@WebServlet( "/error" )
public class ErrorServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ErrorServlet( )
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
        Integer statusCode = ( Integer ) request.getAttribute( "javax.servlet.error.status_code" );

        if ( statusCode == null )
        {
            redirect( "/", response );
        }
        else
        {
            request.setAttribute( "CategoryList", getAllCategories( ) );
            request.setAttribute( "storeIcon", getStoreIcon( ) );
            request.setAttribute( "errorImage", getErrorImage( ) );
            request.setAttribute( "title", "TeaStore Error" );
            request.setAttribute( "login", isLoggedIn( request ) );

            request.getRequestDispatcher( "WEB-INF/pages/error.jsp" ).forward( request, response );
        }
    }

    private String getErrorImage( )
    {
        ExistingImage errorImage = ExistingImage.ERROR;
        ImageSize iconSize = ImageSizePreset.ERROR.getImageSize( );

        return new GetWebImageRequest(
                errorImage.getFolderName( ),
                errorImage.getFileName( ),
                iconSize.getWidth( ),
                iconSize.getHeight( ) ).performRequest( ).getParsedResponseBody( );
    }
}