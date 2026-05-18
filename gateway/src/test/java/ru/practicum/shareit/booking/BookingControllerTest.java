package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void getBookings_shouldCallClient() throws Exception {
        Mockito.when(bookingClient.getBookings(1L, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).getBookings(1L, BookingState.ALL, 0, 10);
    }

    @Test
    void getOwnerBookings_shouldCallClient() throws Exception {
        Mockito.when(bookingClient.getOwnerBookings(1L, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).getOwnerBookings(1L, BookingState.ALL, 0, 10);
    }

    @Test
    void getBooking_shouldCallClient() throws Exception {
        Mockito.when(bookingClient.getBooking(1L, 10L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/10")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).getBooking(1L, 10L);
    }

    @Test
    void bookItem_shouldCallClient() throws Exception {
        Mockito.when(bookingClient.bookItem(Mockito.eq(1L), Mockito.any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        String json = "{"
                + "\"itemId\":1,"
                + "\"start\":\"2026-06-01T10:00:00\","
                + "\"end\":\"2026-06-02T10:00:00\""
                + "}";

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).bookItem(Mockito.eq(1L), Mockito.any(BookItemRequestDto.class));
    }

    @Test
    void approveBooking_shouldCallClient() throws Exception {
        Mockito.when(bookingClient.approveBooking(1L, 10L, true))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/10")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient).approveBooking(1L, 10L, true);
    }

    @Test
    void bookItem_whenStartIsInPast_thenBadRequest() throws Exception {
        String json = "{"
                + "\"itemId\":1,"
                + "\"start\":\"2020-01-01T10:00:00\","
                + "\"end\":\"2020-01-02T10:00:00\""
                + "}";

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoMoreInteractions(bookingClient);
    }
}