package org.apache.chemistry.jcr;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.chemistry.Connection;
import org.apache.chemistry.repository.JoinCapability;
import org.apache.chemistry.repository.QueryCapability;
import org.apache.chemistry.repository.Repository;
import org.apache.chemistry.repository.RepositoryCapabilities;
import org.apache.chemistry.repository.RepositoryEntry;
import org.apache.chemistry.repository.RepositoryInfo;
import org.apache.chemistry.type.BaseType;
import org.apache.chemistry.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

public class JcrRepository implements Repository, RepositoryInfo,
        RepositoryCapabilities {

    private static final Log log = LogFactory.getLog(JcrRepository.class);
    private javax.jcr.Repository repository;
    private String workspace;

    public JcrRepository(javax.jcr.Repository repository, String workspace) {
        this.repository = repository;
        this.workspace = workspace;
    }

    public JcrRepository(javax.jcr.Repository repository) {
        this(repository, null);
    }
    
    public Connection getConnection(Map<String, Serializable> parameters) {
        // TODO pass credentials as parameters
        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());

        try {
            return new JcrConnection(repository.login(creds, workspace), this);
        } catch (RepositoryException e) {
            String msg = "Unable to open connection.";
            throw new RuntimeException(msg, e);
        }
    }

    public <T> T getExtension(Class<T> klass) {
        return null;
    }

    public RepositoryInfo getInfo() {
        return this;
    }

    public Type getType(String typeId) {
    	try {
	        // TODO pass credentials as parameters
	        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
	        
	    	Session session = repository.login(creds, workspace);
	    	
	    	// TODO fetch the types only once, include other types
	    	NodeTypeManager ntmgr = session.getWorkspace().getNodeTypeManager();
	    	NodeType nt = ntmgr.getNodeType(typeId);
	    	
	    	BaseType baseType = BaseType.FOLDER;
	    	if (nt.getName().equals("nt:file")) {
	    		baseType = BaseType.DOCUMENT;
	    	}
	    	return new JcrType(nt, baseType);
    	} catch (RepositoryException e) {
    		String msg = "Unable get type: " + typeId;
    		log.error(msg, e);
    	}
    	return null;
    }

    public Collection<Type> getTypes(String typeId,
                                     boolean returnPropertyDefinitions) {
    	boolean[] hasMoreItems = new boolean[1];
    	return getTypes(typeId, returnPropertyDefinitions, 0, 0, hasMoreItems);
    }

    public List<Type> getTypes(String typeId,
                               boolean returnPropertyDefinitions, int maxItems,
                               int skipCount, boolean[] hasMoreItems) {
    	
    	try {
	        // TODO pass credentials as parameters
	        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
	        
	        ArrayList<Type> result = new ArrayList<Type>();
	        
	    	Session session = repository.login(creds, workspace);
	    	
	    	// TODO fetch the types only once, include other types
	    	NodeTypeManager ntmgr = session.getWorkspace().getNodeTypeManager();
	    	result.add(new JcrType(ntmgr.getNodeType("rep:root"), BaseType.FOLDER));
	    	result.add(new JcrType(ntmgr.getNodeType("nt:folder"), BaseType.FOLDER));
	    	result.add(new JcrType(ntmgr.getNodeType("nt:file"), BaseType.DOCUMENT));
	    	return result;
    	} catch (RepositoryException e) {
    		String msg = "Unable to retrieve node types.";
    		log.error(msg, e);
    	}
        return null;
    }

    public String getId() {
        return getName();
    }

    public String getName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    public String getRelationshipName() {
        return null;
    }

    public URI getURI() {
        // TODO Auto-generated method stub
        return null;
    }

    // ---------------------------------------------------------- RepositoryInfo

    public RepositoryCapabilities getCapabilities() {
        return this;
    }

    public String getDescription() {
        return getName();
    }

    public String getProductName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_NAME_DESC);
    }

    public String getProductVersion() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VERSION_DESC);
    }

    public Collection<RepositoryEntry> getRelatedRepositories() {
        return Collections.emptySet();
    }

    public Document getRepositorySpecificInformation() {
        return null;
    }

    public String getRootFolderId() {
        return JcrObjectEntry.escape("/");
    }

    public String getVendorName() {
        return repository.getDescriptor(javax.jcr.Repository.REP_VENDOR_DESC);
    }

    public String getVersionSupported() {
        return "0.61";
    }

    // -------------------------------------------------- RepositoryCapabilities

    public JoinCapability getJoinCapability() {
        return JoinCapability.NO_JOIN;
    }

    public QueryCapability getQueryCapability() {
        return QueryCapability.BOTH_SEPARATE;
    }

    public boolean hasMultifiling() {
        return true;
    }

    public boolean hasUnfiling() {
        return true;
    }

    public boolean hasVersionSpecificFiling() {
        return false;
    }

    public boolean isAllVersionsSearchable() {
        return false;
    }

    public boolean isPWCSearchable() {
        return false;
    }

    public boolean isPWCUpdatable() {
        return false;
    }
}
