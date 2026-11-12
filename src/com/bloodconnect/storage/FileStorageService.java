package com.bloodconnect.storage;

import com.bloodconnect.exception.StorageException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * =========================================================================
 * Unit 3 Concept: File I/O Streams, Serialization, Deserialization & Generics
 * Generic file storage implementation that serializes domain objects to disk
 * using Java Object Streams (ObjectOutputStream and ObjectInputStream).
 *
 * Implements Requirement 4: File handling and binary serialization without databases.
 * =========================================================================
 *
 * @param <T>  serializable entity type (e.g., Donor, BloodRequest)
 * @param <ID> entity identifier type (e.g., String)
 */
public class FileStorageService<T extends Serializable, ID> implements Storage<T, ID> {

    private final File storageFile;
    private final Function<T, ID> idExtractor;

    /**
     * Constructs a FileStorageService targeting a specific file path on disk.
     *
     * @param filePath    relative or absolute path to the data file (e.g., "data/donors.dat")
     * @param idExtractor lambda function to extract the unique ID from an entity
     * @throws IllegalArgumentException if filePath or idExtractor is null or blank
     */
    public FileStorageService(String filePath, Function<T, ID> idExtractor) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or blank.");
        }
        if (idExtractor == null) {
            throw new IllegalArgumentException("ID extractor function cannot be null.");
        }
        this.storageFile = new File(filePath.trim());
        this.idExtractor = idExtractor;
        ensureParentDirectoryExists();
    }

    /**
     * Reads and deserializes all entities from the binary storage file.
     * Uses try-with-resources to guarantee stream closure.
     *
     * @return List of deserialized entities, or an empty list if file is absent/empty
     * @throws StorageException if I/O or class deserialization fails
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<T> loadAll() throws StorageException {
        if (!storageFile.exists() || storageFile.length() == 0) {
            return new ArrayList<>();
        }

        try (FileInputStream fis = new FileInputStream(storageFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            Object readObject = ois.readObject();
            if (readObject instanceof List<?>) {
                return new ArrayList<>((List<T>) readObject);
            }
            return new ArrayList<>();

        } catch (EOFException e) {
            // Empty or truncated file; return safe empty list
            return new ArrayList<>();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Failed to read and deserialize data from: " + storageFile.getPath(), e);
        }
    }

    /**
     * Serializes and writes a complete list of entities to disk.
     *
     * @param entities collection of entities to persist
     * @throws StorageException if serialization or file writing fails
     */
    @Override
    public synchronized void saveAll(List<T> entities) throws StorageException {
        ensureParentDirectoryExists();
        List<T> dataToWrite = (entities != null) ? new ArrayList<>(entities) : new ArrayList<>();

        try (FileOutputStream fos = new FileOutputStream(storageFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(dataToWrite);
            oos.flush();

        } catch (IOException e) {
            throw new StorageException("Failed to serialize and write data to: " + storageFile.getPath(), e);
        }
    }

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id identifier to query
     * @return matching entity, or null if not found
     * @throws StorageException on storage read error
     */
    @Override
    public synchronized T findById(ID id) throws StorageException {
        if (id == null) {
            return null;
        }
        List<T> all = loadAll();
        for (T item : all) {
            if (Objects.equals(idExtractor.apply(item), id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Saves a single entity (inserts new record or updates existing record).
     *
     * @param entity entity to save
     * @throws StorageException on storage write error
     */
    @Override
    public synchronized void save(T entity) throws StorageException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save cannot be null.");
        }
        List<T> all = loadAll();
        ID targetId = idExtractor.apply(entity);

        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            if (Objects.equals(idExtractor.apply(all.get(i)), targetId)) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            all.set(index, entity); // Update existing
        } else {
            all.add(entity); // Insert new
        }

        saveAll(all);
    }

    /**
     * Deletes an entity from storage by ID.
     *
     * @param id identifier of entity to delete
     * @return true if deleted, false if record was not present
     * @throws StorageException on storage write error
     */
    @Override
    public synchronized boolean deleteById(ID id) throws StorageException {
        if (id == null) {
            return false;
        }
        List<T> all = loadAll();
        boolean removed = all.removeIf(item -> Objects.equals(idExtractor.apply(item), id));
        if (removed) {
            saveAll(all);
        }
        return removed;
    }

    /**
     * Checks if the storage file exists and contains binary data.
     */
    @Override
    public boolean exists() {
        return storageFile.exists() && storageFile.length() > 0;
    }

    public File getStorageFile() {
        return storageFile;
    }

    /**
     * Helper to verify and create parent directories if missing.
     */
    private void ensureParentDirectoryExists() {
        File parent = storageFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}

