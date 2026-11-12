package com.bloodconnect.storage;

import com.bloodconnect.exception.StorageException;

import java.util.List;

/**
 * =========================================================================
 * Unit 2 & Unit 3 Concept: Generic Interfaces & Persistence Contracts
 * Generic storage interface defining CRUD and batch persistence operations
 * for any domain entity in the BloodConnect system.
 *
 * @param <T>  entity type (e.g., Donor, BloodRequest)
 * @param <ID> entity identifier type (e.g., String)
 * =========================================================================
 */
public interface Storage<T, ID> {

    /**
     * Loads all persistent records of entity type T from disk.
     *
     * @return List of entities
     * @throws StorageException if file reading or deserialization fails
     */
    List<T> loadAll() throws StorageException;

    /**
     * Persists a complete collection of entities to disk via binary serialization.
     *
     * @param entities list of entities to serialize and save
     * @throws StorageException if file writing or serialization fails
     */
    void saveAll(List<T> entities) throws StorageException;

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id identifier to look up
     * @return entity if found, or null otherwise
     * @throws StorageException if retrieval encounters a storage error
     */
    T findById(ID id) throws StorageException;

    /**
     * Saves a single entity, appending or updating it in the persistent store.
     *
     * @param entity entity to save
     * @throws StorageException if saving fails
     */
    void save(T entity) throws StorageException;

    /**
     * Deletes an entity from storage by its ID.
     *
     * @param id identifier of entity to delete
     * @return true if found and removed, false otherwise
     * @throws StorageException if storage operation fails
     */
    boolean deleteById(ID id) throws StorageException;

    /**
     * Checks if a persistent data file exists and contains data.
     *
     * @return true if storage file exists and is non-empty
     */
    boolean exists();
}

