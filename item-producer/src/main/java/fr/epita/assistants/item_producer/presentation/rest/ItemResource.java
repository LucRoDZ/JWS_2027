package fr.epita.assistants.item_producer.presentation.rest;

import fr.epita.assistants.common.api.response.ItemResponse;
import fr.epita.assistants.item_producer.converter.ItemConverter;
import fr.epita.assistants.item_producer.domain.service.ItemService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/items")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemService itemService;

    @GET
    public Response getAllItems() {
        return Response.ok(
                itemService.getAllItems().stream()
                        .map(ItemConverter::entityToResponse)
                        .toList()).build();
    }
}
