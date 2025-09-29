package com.djokic.responseserviceff;

import com.djokic.responseserviceff.dto.CreateResponseDTO;
import com.djokic.responseserviceff.dto.ResponseDTO;
import com.djokic.responseserviceff.entity.Response;
import com.djokic.responseserviceff.mapper.ResponseMapper;
import com.djokic.responseserviceff.repository.ResponseRepository;
import com.djokic.responseserviceff.service.ResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResponseServiceTest {

    private ResponseRepository repository;
    private ResponseMapper mapper;
    private ResponseService service;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        repository = mock(ResponseRepository.class);
        mapper = new ResponseMapper(objectMapper); // ako tvoj ResponseMapper ima default constructor, koristi mock ili pravi konstruktor
        service = new ResponseService(repository, mapper);
    }

    @Test
    void testCreateResponse() {
        CreateResponseDTO dto = CreateResponseDTO.builder()
                .formId(1L)
                .userId(123L)
                .userEmail("pera@example.com")
                .answers(Map.of("q1", "Da"))
                .build();

        Response savedResponse = Response.builder()
                .id(1L)
                .formId(1L)
                .userId(123L)
                .userEmail("pera@example.com")
                .answers("{\"q1\":\"Da\"}")
                .isAuthenticated(true)
                .build();

        when(repository.save(any(Response.class))).thenReturn(savedResponse);

        ResponseDTO result = service.createResponse(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getIsAuthenticated());
        assertEquals("Da", result.getAnswers().get("q1"));
    }

    @Test
    void testCreateAnonymousResponse() {
        CreateResponseDTO dto = CreateResponseDTO.builder()
                .formId(1L)
                .answers(Map.of("q1", "Ne"))
                .build();

        Response savedResponse = Response.builder()
                .id(2L)
                .formId(1L)
                .userId(0L)
                .answers("{\"q1\":\"Ne\"}")
                .isAuthenticated(false)
                .build();

        when(repository.save(any(Response.class))).thenReturn(savedResponse);

        ResponseDTO result = service.createAnonymousResponse(dto);

        assertNotNull(result);
        assertEquals(0L, result.getUserId());
        assertFalse(result.getIsAuthenticated());
        assertEquals("Ne", result.getAnswers().get("q1"));
    }

    @Test
    void testGetResponsesByFormId() {
        Response r1 = Response.builder().id(1L).formId(1L).userId(123L).answers("{\"q1\":\"Da\"}").isAuthenticated(true).build();
        Response r2 = Response.builder().id(2L).formId(1L).userId(0L).answers("{\"q1\":\"Ne\"}").isAuthenticated(false).build();

        when(repository.findByFormId(1L)).thenReturn(Arrays.asList(r1, r2));

        List<ResponseDTO> result = service.getResponsesByFormId(1L, 123L);

        assertEquals(2, result.size());
        assertEquals("Da", result.get(0).getAnswers().get("q1"));
        assertEquals("Ne", result.get(1).getAnswers().get("q1"));
    }

    @Test
    void testGetResponseByIdAllowed() {
        Response r = Response.builder().id(1L).formId(1L).userId(123L).answers("{\"q1\":\"Da\"}").isAuthenticated(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(r));

        ResponseDTO result = service.getResponseById(1L, 123L);
        assertEquals(1L, result.getId());
        assertEquals("Da", result.getAnswers().get("q1"));
    }

    @Test
    void testGetResponseByIdForbidden() {
        Response r = Response.builder().id(1L).formId(1L).userId(123L).answers("{\"q1\":\"Da\"}").isAuthenticated(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(r));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getResponseById(1L, 456L));
        assertEquals("Not allowed to view this response!", ex.getMessage());
    }

    @Test
    void testDeleteResponseAllowed() {
        Response r = Response.builder().id(1L).formId(1L).userId(123L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(r));

        service.deleteResponse(1L, 123L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteResponseForbidden() {
        Response r = Response.builder().id(1L).formId(1L).userId(123L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(r));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteResponse(1L, 456L));
        assertEquals("Not allowed to delete this response!", ex.getMessage());
    }
}
