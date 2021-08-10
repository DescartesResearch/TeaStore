/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.image.rest;

import java.util.HashMap;
import java.util.stream.Collectors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.image.ImageProvider;
import tools.descartes.teastore.image.setup.SetupController;

/**
 * The image provider REST endpoints for querying and controlling the image provider service.
 * @author Norbert Schmitt
 */
@Path("image")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class ImageProviderEndpoint {

  /**
   * Queries the image provider for the given product IDs in the given size, provided as strings.
   * @param images Map of product IDs and the corresponding image size as string.
   * @return Map of product IDs and the image data as base64 encoded string.
   */
  @POST
  @Path("getProductImages")
  public Response getProductImages(HashMap<Long, String> images) {
    return Response.ok()
        .entity(ImageProvider.IP.getProductImages(images.entrySet().parallelStream().collect(
            Collectors.toMap(e -> e.getKey(), e -> ImageSize.parseImageSize(e.getValue())))))
        .build();
  }

  /**
   * Queries the image provider for the given web interface image names in the given size, provided as strings.
   * @param images Map of web interface image names and the corresponding image size as string.
   * @return Map of web interface image names and the image data as base64 encoded string.
   */
  @POST
  @Path("getWebImages")
  public Response getWebUIImages(HashMap<String, String> images) {
    return Response.ok()
        .entity(ImageProvider.IP.getWebUIImages(images.entrySet().parallelStream().collect(
            Collectors.toMap(e -> e.getKey(), e -> ImageSize.parseImageSize(e.getValue())))))
        .build();
  }

  /**
   * Signals the image provider to regenerate all product images. This is usually necessary if the product database
   * changed.
   * @return Returns status code 200.
   */
  @GET
  @Path("regenerateImages")
  public Response regenerateImages() {
    SetupController.SETUP.reconfiguration();
    return Response.ok().build();
  }

  /**
   * Checks if the setup of the image provider and image generation has finished.
   * @return Returns true if the setup is finished.
   */
  @GET
  @Path("finished")
  public Response isFinished() {
    if (SetupController.SETUP.isFinished()) {
      return Response.ok(true).build();
    } else {
      return Response.serverError().entity(false).build();
    }
  }

  /**
   * Checks the current state, configuration settings, number of images, cache size, etc., of the image provider.
   * @return Returns a string containing the current state and configuration.
   */
  @GET
  @Path("state")
  @Produces({ "text/plain" })
  public Response getState() {
    return Response.ok().entity(SetupController.SETUP.getState()).build();
  }

  /**
   * Sets the cache size to the given value.
   * @param cacheSize The new cache size in bytes. Cache size must be positive.
   * @return True if the cache size was set successfully, otherwise false.
   */
  @POST
  @Path("setCacheSize")
  public Response setCacheSize(long cacheSize) {
    return Response.ok().entity(SetupController.SETUP.setCacheSize(cacheSize)).build();
  }

}
