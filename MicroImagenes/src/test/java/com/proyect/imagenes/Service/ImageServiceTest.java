package com.proyect.imagenes.Service;

import com.proyect.imagenes.dto.ImageRequestDTO;
import com.proyect.imagenes.dto.ImageResponseDTO;
import com.proyect.imagenes.Exceptions.ImageAlreadyExistsException;
import com.proyect.imagenes.Exceptions.ImageNotFoundException;
import com.proyect.imagenes.model.Image;
import com.proyect.imagenes.repository.ImageRepository;
import com.proyect.imagenes.service.ImageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    private Image image;
    private MultipartFile multipartFileMock;
    private ImageRequestDTO requestMock;

    @BeforeEach
    void setUp() {
        // Preparamos la imagen de prueba
        image = Image.builder()
                .id("db-id-1")
                .imageId("img-123")
                .fileName("perrito.jpg")
                .contentType("image/jpeg")
                .data(new byte[]{1, 2, 3})
                .size(1024L)
                .uploadDate(LocalDateTime.now())
                .build();

        // Preparamos los mocks para la subida de archivos
        multipartFileMock = mock(MultipartFile.class);
        requestMock = mock(ImageRequestDTO.class);
    }

    @SuppressWarnings("null")
    @Test
    void saveImage_Exitoso_RetornaDTO() throws IOException {
        // Simulamos el request
        when(requestMock.getImageId()).thenReturn("img-123");
        when(requestMock.getFile()).thenReturn(multipartFileMock);
        
        // Simulamos las propiedades del archivo
        when(multipartFileMock.isEmpty()).thenReturn(false);
        when(multipartFileMock.getContentType()).thenReturn("image/jpeg");
        when(multipartFileMock.getOriginalFilename()).thenReturn("perrito.jpg");
        when(multipartFileMock.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(multipartFileMock.getSize()).thenReturn(1024L);

        // Simulamos BD
        when(imageRepository.existsByImageId("img-123")).thenReturn(false);
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        // Ejecutamos
        ImageResponseDTO resultado = imageService.saveImage(requestMock);

        // Verificamos
        assertNotNull(resultado);
        assertEquals("img-123", resultado.getImageId());
        assertEquals("perrito.jpg", resultado.getFileName());
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @SuppressWarnings("null")
    @Test
    void saveImage_CuandoYaExiste_LanzaExcepcion() {
        when(requestMock.getImageId()).thenReturn("img-123");
        when(imageRepository.existsByImageId("img-123")).thenReturn(true);

        assertThrows(ImageAlreadyExistsException.class, () -> imageService.saveImage(requestMock));
        verify(imageRepository, never()).save(any(Image.class));
    }

    @Test
    void saveImage_CuandoNoEsImagen_LanzaIllegalArgument() {
        when(requestMock.getImageId()).thenReturn("img-123");
        when(requestMock.getFile()).thenReturn(multipartFileMock);
        when(multipartFileMock.isEmpty()).thenReturn(false);
        
        // Simulamos que alguien intenta subir un PDF
        when(multipartFileMock.getContentType()).thenReturn("application/pdf");

        when(imageRepository.existsByImageId("img-123")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> imageService.saveImage(requestMock));
    }

    @Test
    void downloadImage_CuandoExiste_RetornaBytes() {
        when(imageRepository.findByImageId("img-123")).thenReturn(Optional.of(image));

        byte[] resultado = imageService.downloadImage("img-123");

        assertNotNull(resultado);
        assertEquals(3, resultado.length, "Debe retornar el arreglo de bytes correcto");
    }

    @Test
    void deleteImage_CuandoNoExiste_LanzaExcepcion() {
        when(imageRepository.existsByImageId("img-999")).thenReturn(false);

        assertThrows(ImageNotFoundException.class, () -> imageService.deleteImage("img-999"));
        // Verificamos que no intente borrar si no existe
        verify(imageRepository, never()).deleteByImageId(anyString());
    }

    @Test
    void deleteImage_CuandoExiste_EjecutaDelete() {
        when(imageRepository.existsByImageId("img-123")).thenReturn(true);

        imageService.deleteImage("img-123");

        verify(imageRepository, times(1)).deleteByImageId("img-123");
    }
}