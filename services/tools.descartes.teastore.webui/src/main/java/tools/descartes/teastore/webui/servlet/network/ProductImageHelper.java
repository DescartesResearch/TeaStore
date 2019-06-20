package tools.descartes.teastore.webui.servlet.network;

import tools.descartes.research.faasteastorelibrary.interfaces.image.size.ImageSize;
import tools.descartes.research.faasteastorelibrary.interfaces.image.size.ImageSizePreset;
import tools.descartes.research.faasteastorelibrary.requests.image.GetProductImageByProductIdRequest;

import java.util.logging.Logger;

public class ProductImageHelper
{
    private final ImageSize imageSize;

    private final Logger logger = Logger.getLogger( "ProductImageHelper" );

    public ProductImageHelper( )
    {
        this.imageSize = ImageSizePreset.PREVIEW.getImageSize( );
    }

    public String getProductImageForProductId( final long productId )
    {
        String productImageAsBase64String = new GetProductImageByProductIdRequest( productId,
                this.imageSize.getWidth( ), this.imageSize.getHeight( ) ).performRequest( ).getParsedResponseBody( );

//        this.logger.info( "productId: " + productId );
//        this.logger.info( productImageAsBase64String );

        return productImageAsBase64String;
    }
}