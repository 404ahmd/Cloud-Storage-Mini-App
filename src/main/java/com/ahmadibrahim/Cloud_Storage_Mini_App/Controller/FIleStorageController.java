package com.ahmadibrahim.Cloud_Storage_Mini_App.Controller;

import com.ahmadibrahim.Cloud_Storage_Mini_App.Entity.FileMetadata;
import com.ahmadibrahim.Cloud_Storage_Mini_App.Repository.FileMetadataRepository;
import com.ahmadibrahim.Cloud_Storage_Mini_App.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping(
        "/api/files"
)
public class FIleStorageController {
    @Autowired
    private FileStorageService fileStorageService;
    private final String storagePath = "/home/ahmadibrahim/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file){
        try {

            Path path = Paths.get(storagePath + "/" + file.getOriginalFilename());
            Files.write(path, file.getBytes());
            fileStorageService.saveFile(file);
            return ResponseEntity.ok("Upload Successfully " + file.getOriginalFilename());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public ResponseEntity<List<FileMetadata>> getAllFilesMetadata(){
        List<FileMetadata> metadataList = fileStorageService.getAllFiles();
        return ResponseEntity.ok(metadataList);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> donwloadFile(@PathVariable Long id){
        FileMetadata metadata = fileStorageService.getFileMetadataById(id);
        return fileStorageService.downloadFile(metadata.getFilePath(), metadata.getFileName());
    }

    @GetMapping("/download/byname")
    public ResponseEntity<Resource> downloadFileByName(@RequestParam String fileName){
        FileMetadata metadata = fileStorageService.getFileMetadataByName(fileName);
        return fileStorageService.downloadFile(metadata.getFilePath(), metadata.getFileName());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFileById(@PathVariable Long id){
        fileStorageService.deleteFile(id);
        return ResponseEntity.ok("Delete succesfully");
    }

    @DeleteMapping("/delete/byname/{fileName}")
    public ResponseEntity<String> deleteFileByName(@PathVariable String fileName){
        fileStorageService.deleteFileWithName(fileName);
        return ResponseEntity.ok("Delete succesfully");
    }
}
