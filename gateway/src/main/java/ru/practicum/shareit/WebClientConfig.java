package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

@Configuration
public class WebClientConfig {
    private static final String API_BOOKING_PREFIX = "/bookings";
    private static final String API_ITEM_PREFIX = "/items";
    private static final String API_REQUEST_PREFIX = "/requests";
    private static final String API_USER_PREFIX = "/users";

    @Value("${shareit-server.url}")
    private String serverUrl;

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_BOOKING_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        BookingClient bookingClient = new BookingClient(restTemplate);
        return bookingClient;
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_ITEM_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        ItemClient itemClient = new ItemClient(restTemplate);
        return itemClient;
    }

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_REQUEST_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        ItemRequestClient itemRequestClient = new ItemRequestClient(restTemplate);
        return itemRequestClient;
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_USER_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        UserClient userClient = new UserClient(restTemplate);
        return userClient;
    }
}
