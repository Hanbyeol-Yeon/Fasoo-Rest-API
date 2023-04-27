package com.fasoo.prac.events;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasoo.prac.accounts.Account;
import com.fasoo.prac.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
		
	@Autowired
	EventRepository eventRepository;
	
	@Test
	@TestDescription("정상적으로 이벤트 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 29, 17, 10))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 30, 17, 10))
                .beginEventDateTime(LocalDateTime.of(2023, 03, 31, 17, 10))
                .endEventDateTime(LocalDateTime.of(2023, 04, 01, 17, 10))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("누리꿈스퀘어 6층 파수")
				.build();
				
		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event)))
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 29, 17, 10))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 30, 17, 10))
                .beginEventDateTime(LocalDateTime.of(2023, 03, 31, 17, 10))
                .endEventDateTime(LocalDateTime.of(2023, 04, 01, 17, 10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("누리꿈스퀘어 6층 파수")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
	
    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
	
    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 03, 31, 17, 10))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 03, 30, 17, 10))
                .beginEventDateTime(LocalDateTime.of(2023, 03, 25, 17, 10))
                .endEventDateTime(LocalDateTime.of(2023, 03, 01, 17, 10))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("누리꿈스퀘어 6층 파수")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    @TestDescription("특정 가격 내의 이벤트를 조회하기")
    public void queryEventsByBasePrice() throws Exception {
        // Given
    	IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events/base-price")
                .param("startBasePrice", "100")
                .param("endBasePrice", "200")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk());
    }    
    
    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }

    private Event buildEvent(int index) {
        return Event.builder()
                    .name("event " + index)
                    .description("test event")
                    .beginEnrollmentDateTime(LocalDateTime.of(2023, 04, 22, 10, 10))
                    .closeEnrollmentDateTime(LocalDateTime.of(2023, 04, 23, 10, 10))
                    .beginEventDateTime(LocalDateTime.of(2023, 04, 24, 10, 10))
                    .endEventDateTime(LocalDateTime.of(2023, 04, 25, 10, 10))
                    .basePrice(150)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("누리꿈스퀘어 6층 파수")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DRAFT)
                    .build();
    }
}
