package com.peircean.glusterfs.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

/**
 * @author <a href="http://about.me/louiszuckerman">Louis Zuckerman</a>
 */
public class Example {
    public static FileSystemProvider getProvider(String scheme) {
        for (FileSystemProvider fsp : FileSystemProvider.installedProviders()) {
            if (fsp.getScheme().equals(scheme)) {
                return fsp;
            }
        }
        throw new IllegalArgumentException("No provider found for scheme: " + scheme);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println(getProvider("gluster").toString());

        String testUri = "gluster://127.0.2.1:foo/baz";

        FileSystem fileSystem = FileSystems.newFileSystem(new URI(testUri), null);
        FileStore store = fileSystem.getFileStores().iterator().next();
        System.out.println("TOTAL SPACE: " + store.getTotalSpace());
        System.out.println("USABLE SPACE: " + store.getUsableSpace());
        System.out.println("UNALLOCATED SPACE: " + store.getUnallocatedSpace());
        System.out.println(fileSystem.toString());

        Set<PosixFilePermission> posixFilePermissions = PosixFilePermissions.fromString("rw-rw-rw-");
        FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(posixFilePermissions);

        Path glusterPath = Paths.get(new URI(testUri)); //null
        System.out.println(glusterPath.getClass()); //NPE
        System.out.println(glusterPath);
        System.out.println(glusterPath.getFileSystem().toString()); //NPE

        try {
            Files.createFile(glusterPath, attrs);
        } catch (IOException e) {
            System.out.println("File exists");
        }
        String hello = "Hello, ";
        Files.write(glusterPath, hello.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        String world = "world!";
        Files.write(glusterPath, world.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        System.out.println("SIZE: " + Files.size(glusterPath));
        byte[] readBytes = Files.readAllBytes(glusterPath);
        System.out.println(hello + world + " == " + new String(readBytes));
        fileSystem.close();
    }
}