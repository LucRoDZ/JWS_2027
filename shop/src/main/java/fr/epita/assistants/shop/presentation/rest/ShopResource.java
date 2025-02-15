package fr.epita.assistants.shop.presentation.rest;

import fr.epita.assistants.common.api.request.ShopRequest;
import fr.epita.assistants.common.api.response.ShopResponse;
import fr.epita.assistants.common.api.response.ShopPriceResponse;
import fr.epita.assistants.common.command.SyncInventoryCommand;
import fr.epita.assistants.shop.converter.ShopConverter;
import fr.epita.assistants.shop.domain.entity.ShopEntity;
import fr.epita.assistants.shop.domain.service.ShopService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import java.util.List;
import java.util.Optional;

@Path("/")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShopResource {

    private final ShopService shopService;

    @Channel("sync-inventory-command")
    Emitter<SyncInventoryCommand> syncInventoryEmitter;

    @ConfigProperty(name = "JWS_SHOP_PRICE", defaultValue = "17.5")
    Float shopPrice;

    @ConfigProperty(name = "JWS_UPGRADE_PRICE_COST", defaultValue = "28.5")
    Float upgradePriceCost;

    @GET
    public Response getAllShops() {
        List<ShopResponse> responses = shopService.getAllShops().stream()
                .map(ShopConverter::entityToResponse)
                .toList();
        return Response.ok(responses).build();
    }

    @POST
    public Response addShop(ShopRequest request) {
        if (!shopService.canCreateShop()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Max shop limit reached or insufficient funds.")
                    .build();
        }
        ShopEntity shop = ShopConverter.requestToEntity(request);
        shopService.addShop(shop);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/price")
    public Response getShopPrice() {
        float currentPrice = shopService.getCurrentShopPrice();
        return Response.ok(new ShopPriceResponse(currentPrice)).build();
    }

    @POST
    @Path("/start")
    public Response startShop() {
        shopService.resetShopsAndCreateInitialShop(shopPrice, upgradePriceCost);
        syncInventoryEmitter.send(new SyncInventoryCommand());
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public Response getShopById(@PathParam("id") String idParam) {
        int id;
        try {
            id = Integer.parseInt(idParam);
            if (id <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The ID is not valid.")
                    .build();
        }

        return shopService.getShopById(id)
                .map(shop -> Response.ok(ShopConverter.entityToResponse(shop)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("The shop was not found.")
                        .build());
    }
}
