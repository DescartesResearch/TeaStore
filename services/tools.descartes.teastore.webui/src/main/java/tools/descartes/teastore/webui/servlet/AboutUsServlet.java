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
 * Servlet implementation for the web view of "About us".
 *
 * @author Andre Bauer
 */
@WebServlet( "/about" )
public class AboutUsServlet extends AbstractUIServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AboutUsServlet( )
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

        request.setAttribute( "portraitAndre", getPortrait( ExistingImage.ANDRE_BAUER ) );
        request.setAttribute( "portraitJohannes", getPortrait( ExistingImage.JOHANNES_GROHMANN ) );
        request.setAttribute( "portraitJoakim", getPortrait( ExistingImage.JOAKIM_KISTOWSKI ) );
        request.setAttribute( "portraitSimon", getPortrait( ExistingImage.SIMON_EISMANN ) );
        request.setAttribute( "portraitNorbert", getPortrait( ExistingImage.NORBERT_SCHMITT ) );
        request.setAttribute( "portraitKounev", getPortrait( ExistingImage.SAMUEL_KOUNEV ) );

        request.setAttribute( "descartesLogo", getDescartesLogo( ) );
        request.setAttribute( "storeIcon", getStoreIcon( ) );

        request.setAttribute( "title", "TeaStore About Us" );
        request.setAttribute( "login", isLoggedIn( request ) );

        request.getRequestDispatcher( "WEB-INF/pages/about.jsp" ).forward( request, response );
    }

    private String getPortrait( final ExistingImage portrait )
    {
        ImageSize iconSize = ImageSizePreset.PORTRAIT.getImageSize( );

        return new GetWebImageRequest(
                portrait.getFolderName( ),
                portrait.getFileName( ),
                iconSize.getWidth( ),
                iconSize.getHeight( ) ).performRequest( ).getParsedResponseBody( );
    }

    private String getDescartesLogo( )
    {
        ExistingImage descartesLogo = ExistingImage.DESCARTES_LOGO;
        ImageSize iconSize = ImageSizePreset.LOGO.getImageSize( );

        return new GetWebImageRequest(
                descartesLogo.getFolderName( ),
                descartesLogo.getFileName( ),
                iconSize.getWidth( ),
                iconSize.getHeight( ) ).performRequest( ).getParsedResponseBody( );
    }
}