package tools.descartes.petstore.image.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.image.ImageProvider;
import tools.descartes.petstore.image.setup.SetupController;

@Path("image")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class ImageProviderEndpoint {

	@POST
	@Path("getProductImages")
	public Response getProductImages(Map<Long, ImageSize> images) {
		return Response.ok().entity(ImageProvider.getInstance().getProductImages(images)).build();
	}
	
	@POST
	@Path("getWebImages")
	public Response getWebUIImages(Map<String, ImageSize> images) {
		return Response.ok().entity(ImageProvider.getInstance().getWebUIImages(images)).build();
	}
	
	@POST
	@Path("regenerateImages")
	public Response regenerateImages() {
		SetupController.getInstance().reconfiguration();
		return Response.ok().build();
	}
}
