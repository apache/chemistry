package org.apache.chemistry.repository;

import java.util.Map;

public interface RepositoryFactory {

    public Repository create(Map<String, String> params) throws Exception;

    public Repository create(Object context, Map<String, String> params)
            throws Exception;
}

