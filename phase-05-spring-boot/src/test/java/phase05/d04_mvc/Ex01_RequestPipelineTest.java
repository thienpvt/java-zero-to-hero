package phase05.d04_mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Ex01_RequestPipeline.OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = Ex01_RequestPipeline.MvcConfiguration.class)
class Ex01_RequestPipelineTest {
    @Autowired MockMvc mvc;
    @MockitoBean Ex01_RequestPipeline.OrderService service;

    @Test
    @DisplayName("B04: request DTO được deserialize rồi gọi service và trả JSON")
    void validJsonReachesServiceAndReturnsJson() throws Exception {
        when(service.place(any())).thenReturn("accepted");

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reference\":\"order-1\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"result\":\"accepted\"}"));
        verify(service).place(new Ex01_RequestPipeline.CreateOrderRequest("order-1"));
    }

    @Test
    @DisplayName("B04: JSON sai cú pháp bị từ chối trước service")
    void malformedJsonFailsBeforeServiceCall() throws Exception {
        when(service.place(any())).thenReturn("accepted");
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reference\":\"control\"}"))
                .andExpect(status().isOk());
        clearInvocations(service);

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        verify(service, never()).place(any());
    }

    @Test
    @DisplayName("B04: media type không hỗ trợ bị từ chối trước service")
    void unsupportedMediaTypeFailsBeforeServiceCall() throws Exception {
        when(service.place(any())).thenReturn("accepted");
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reference\":\"control\"}"))
                .andExpect(status().isOk());
        clearInvocations(service);

        mvc.perform(post("/api/orders").contentType(MediaType.TEXT_PLAIN).content("order-1"))
                .andExpect(status().isUnsupportedMediaType());
        verify(service, never()).place(any());
    }
}
