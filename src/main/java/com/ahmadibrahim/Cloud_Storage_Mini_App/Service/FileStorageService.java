package com.ahmadibrahim.Cloud_Storage_Mini_App.Service;

import com.ahmadibrahim.Cloud_Storage_Mini_App.Entity.FileMetadata;
import com.ahmadibrahim.Cloud_Storage_Mini_App.Repository.FileMetadataRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileStorageService {
    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    //service layer for upload the file
    @Transactional
    public FileMetadata saveFile(MultipartFile file){
        try{
            //save physycal file to localstorage
            Path path = Paths.get("/home/ahmadibrahim/uploads/"+ file.getOriginalFilename());
            Files.write(path, file.getBytes());

            //save metadata to database
            FileMetadata metadata = new FileMetadata();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(file.getContentType());
            metadata.setFileSize(file.getSize());
            metadata.setFilePath(path.toString());
            metadata.setUpluadDate(LocalDateTime.now());

            return fileMetadataRepository.save(metadata);

        }catch (Exception e){
            throw new RuntimeException("Eror while uploading", e);
        }
    }

    //function for view all files metadata
    public List<FileMetadata> getAllFiles(){
        return fileMetadataRepository.findAll();
    }

    //function for get metadata by name
    public FileMetadata getFileMetadataByName(String fileName){
        return fileMetadataRepository.findByFileName(fileName)
                .orElseThrow(()-> new RuntimeException("File with name : " + fileName + "not found"));
    }

    //function for get metadata by Id
    public FileMetadata getFileMetadataById(Long id){
        return fileMetadataRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("File with name : " + id + "not found"));
    }

    //function for get file from local storage
    public ResponseEntity<Resource> downloadFile(String filePath, String fileName){
        final String storagePath = "/home/ahmadibrahim/uploads/";
        try {

            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("File not found or not readable");
            }

        }catch (MalformedURLException e){
            throw new RuntimeException("Error while downloading file " + e.getMessage());
        }catch (IOException e){
            throw new RuntimeException("File content detection failed" + e.getMessage());
        }
    }
}
