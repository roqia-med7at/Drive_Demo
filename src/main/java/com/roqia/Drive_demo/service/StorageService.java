package com.roqia.Drive_demo.service;

import com.roqia.Drive_demo.error.customExceptions.StorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class StorageService {

    public String saveFileToStorage( String pathString , String parentPathString,MultipartFile multipartFile){

        try {
            Path parentPath = Paths.get(parentPathString);
            Files.createDirectories(parentPath);
            Path path = Paths.get(pathString);
            try(InputStream input = multipartFile.getInputStream()) {
                Files.copy(input,path, StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (IOException e){
            throw new StorageException("Failed to save file",e);
        }
        return pathString;
    }
    public  byte[]readFile(String filePath){
        Path path = Paths.get(filePath);
        byte[] fileData;
        try {
            fileData = Files.readAllBytes(path);
            return fileData;
        } catch (IOException e) {
            throw new StorageException("Failed to read file found at "+filePath,e);
        }
    }
    public Path createOrGetFolder(String folderName,String parentPath){
        try {
            Path root = Paths.get(parentPath);
            Path userPath = root.resolve(folderName);
            if (!Files.exists(userPath)) {
                Files.createDirectories(userPath);
            }
            return userPath;
        } catch (IOException e) {
            throw new StorageException("Failed to create folder : "+folderName,e);
        }
    }
    public void moveItem(String oldPathStr, String newPathStr){
        try {
            Path oldPath = Paths.get(oldPathStr);
            Path newPath = Paths.get(newPathStr);
            if (Files.exists(oldPath)) {
                Files.move(oldPath, newPath,StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (IOException e) {
            throw new StorageException("Failed to move item from " + oldPathStr + " to " + newPathStr, e);
        }
    }
    public void deleteFolder(String folderPathStr){
        try {
            Path folderPath = Paths.get(folderPathStr);
            if (Files.exists(folderPath)) {
                Files.walkFileTree(folderPath, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                });

            }
        } catch (IOException e) {
            throw new StorageException("Failed to delete folder found at : "+folderPathStr,e);
        }
    }
    public void deleteFile(String filePathStr){
        Path path = Paths.get(filePathStr);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }catch (IOException e){
            throw new StorageException("Failed to delete file found at : "+filePathStr,e);
        }
    }
    public void copyFolder (String oldPathStr, String newPathStr){
        try {
            Path new_path = Paths.get(newPathStr);
            Path old_path = Paths.get(oldPathStr);

            Files.walkFileTree(old_path,new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relative = old_path.relativize(dir);
                    Path target = new_path.resolve(relative);
                    Files.createDirectories(target);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relative = old_path.relativize(file);
                    Path target = new_path.resolve(relative);
                    Files.copy(file,target,StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

            });
        }catch (IOException e) {
            throw new StorageException("Failed to copy folder from "+oldPathStr+"to "+newPathStr,e);
        }
    }
    public void copyFile(String oldPathStr, String newPathStr){
        Path oldPath = Paths.get(oldPathStr);
        Path newPath = Paths.get(newPathStr);

        try {

            if (Files.exists(oldPath)){
                Files.createDirectories(newPath.getParent());
                Files.copy(oldPath,newPath,StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (IOException e){
            throw new StorageException("Failed to copy file from "+oldPathStr+"to "+newPathStr,e);
        }
    }
    public void downloadFolder(String folderPathStr, OutputStream output){
        try {
            Path folderPath = Paths.get(folderPathStr);
            try (ZipOutputStream zos = new ZipOutputStream(output)) {
                if (Files.exists(folderPath)) {
                    Files.walkFileTree(folderPath, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                            zos.putNextEntry(new ZipEntry(folderPath.relativize(file).toString()));
                            Files.copy(file, zos);
                            zos.closeEntry();
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    zos.finish();
                    zos.close();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to download folderfound at  "+folderPathStr,e);
        }
    }
    public Map<String, List<String>> getFolderContents(String folderPathStr){
        Path folderPath = Paths.get(folderPathStr);

        Map<String,List<String>>map = new HashMap<>();
        try(Stream<Path> pathStream = Files.list(folderPath)) {
            pathStream.forEach(path -> {
                if (Files.isDirectory(path)){
                    map.computeIfAbsent("folders",ls->new ArrayList<>()).add(path.getFileName().toString());

                }else {
                    map.computeIfAbsent("files",ls->new ArrayList<>()).add(path.getFileName().toString());
                }
            });
        } catch (IOException e) {
            throw new StorageException("Failed to retrieve folder contents found at "+folderPathStr ,e);
        }
        return map;
    }
}
