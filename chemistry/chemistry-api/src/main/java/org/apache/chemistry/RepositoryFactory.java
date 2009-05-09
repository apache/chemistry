package org.apache.chemistry;

import java.util.Map;

public interface RepositoryFactory {

    Repository create(Map<String, String> params) throws Exception;

    Repository create(Object context, Map<String, String> params)
            throws Exception;
}

