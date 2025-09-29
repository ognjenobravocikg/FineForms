package com.fineforms.backend.controller;

import com.fineforms.backend.entity.Collaborator;
import com.fineforms.backend.enums.CollaboratorRole;
import com.fineforms.backend.service.CollaboratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fineforms.backend.service.FormService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CollaboratorController.class)
@Import(CollaboratorControllerTest.CollaboratorTestConfig.class)
class CollaboratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollaboratorService collaboratorService;

    @TestConfiguration
    static class CollaboratorTestConfig {
        @Bean
        public CollaboratorService collaboratorService() {
            return mock(CollaboratorService.class);
        }
        @Bean
        public FormService formService() {
            return mock(FormService.class);
        }
    }

    @Test
    void addCollaborator_ShouldReturnCollaborator() throws Exception {
        Collaborator collaborator = Collaborator.builder()
                .id(1L)
                .userId(2L)
                .role(CollaboratorRole.EDITOR)
                .build();

        when(collaboratorService.addCollaborator(eq(1L), eq(2L), eq(CollaboratorRole.EDITOR), eq(1L)))
                .thenReturn(collaborator);

        mockMvc.perform(post("/form/1/collab")
                        .param("userId", "2")
                        .param("role", "EDITOR")
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void getCollaborators_ShouldReturnCollaboratorList() throws Exception {
        List<Collaborator> collaborators = List.of(
                Collaborator.builder().id(1L).userId(2L).role(CollaboratorRole.EDITOR).build()
        );

        when(collaboratorService.getCollaboratorsByForm(1L)).thenReturn(collaborators);

        mockMvc.perform(get("/form/1/collab"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
    }

    @Test
    void updateRole_ShouldReturnUpdatedCollaborator() throws Exception {
        Collaborator collaborator = Collaborator.builder()
                .id(1L)
                .userId(2L)
                .role(CollaboratorRole.VIEWER)
                .build();

        when(collaboratorService.updateCollaboratorRole(eq(1L), eq(2L), eq(CollaboratorRole.VIEWER), eq(1L)))
                .thenReturn(collaborator);

        mockMvc.perform(post("/form/1/collab/2/update-role")
                        .param("role", "VIEWER")
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("VIEWER"));
    }

    @Test
    void removeCollaborator_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/form/1/collab/2")
                        .param("currentUserId", "1"))
                .andExpect(status().isNoContent());
    }
}
