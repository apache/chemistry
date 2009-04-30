<%@ page import="org.apache.jackrabbit.cmis.*,
                 org.apache.jackrabbit.cmis.client.JCRRepositoryLocator" %><%

    response.setContentType("text/xml");
    response.setCharacterEncoding("UTF-8");

    String contextPath = request.getContextPath();
    
    Repository repository = JCRRepositoryLocator.getRepository(application);
    Capabilities caps = repository.getCapabilities();
    
%><?xml version="1.0" encoding="utf-8"?>
<service xmlns="http://www.w3.org/2007/app" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:cmis="http://www.cmis.org/2008/05">
  <workspace cmis:repositoryRelationship="self">
    <atom:title><%= repository.getName() %></atom:title>

    <cmis:repositoryInfo>
      <cmis:repositoryId><%= repository.getId() %></cmis:repositoryId>
      <cmis:repositoryName><%= repository.getName() %></cmis:repositoryName>
      <cmis:repositoryRelationship>self</cmis:repositoryRelationship>
      <cmis:repositoryDescription><%= repository.getDescription() %></cmis:repositoryDescription>
      <cmis:vendorName><%= repository.getVendorName() %></cmis:vendorName>
      <cmis:productName><%= repository.getProductName() %></cmis:productName>
      <cmis:productVersion><%= repository.getProductVersion() %></cmis:productVersion>
      <cmis:rootFolderId><%= repository.getRootFolderId() %></cmis:rootFolderId>
      <cmis:capabilities>
        <cmis:capabilityMultifiling><%= caps.hasMultifiling() %></cmis:capabilityMultifiling>
        <cmis:capabilityUnfiling><%= caps.hasUnfiling() %></cmis:capabilityUnfiling>
        <cmis:capabilityVersionSpecificFiling><%= caps.hasVersionSpecificFiling() %></cmis:capabilityVersionSpecificFiling>
        <cmis:capabilityPWCUpdateable><%= caps.isPWCUpdatable() %></cmis:capabilityPWCUpdateable>
        <cmis:capabilityPWCSearchable><%= caps.isPWCSearchable() %></cmis:capabilityPWCSearchable>
        <cmis:capabilityAllVersionsSearchable><%= caps.areAllVersionsSearchable() %></cmis:capabilityAllVersionsSearchable>
        <cmis:capabilityQuery><%= caps.getQuerySupport() %></cmis:capabilityQuery>
        <cmis:capabilityJoin><%= caps.getJoinSupport() %></cmis:capabilityJoin>
        <cmis:capabilityFullText><%= caps.getFullTextSupport() %></cmis:capabilityFullText>
      </cmis:capabilities>
      <cmis:cmisVersionsSupported><%= repository.getVersionsSupported() %></cmis:cmisVersionsSupported>
      <cmis:repositorySpecificInformation></cmis:repositorySpecificInformation>
    </cmis:repositoryInfo>

    <collection href="<%= contextPath %>/.getChildren.xml" cmis:collectionType="root-children">
      <atom:title>root collection</atom:title>
    </collection>
    <collection href="<%= contextPath %>/.getDescendants.xml" cmis:collectionType="root-descendants">
      <atom:title>root collection</atom:title>
    </collection>
    <collection href="<%= contextPath %>/.getCheckedOut.xml" cmis:collectionType="checkedout">
      <atom:title>checkedout collection</atom:title>
    </collection>
    <collection href="<%= contextPath %>/.getUnfiled.xml" cmis:collectionType="unfiled">
      <atom:title>unfiled collection</atom:title>
    </collection>
    <collection href="<%= contextPath %>/.getTypesChildren.xml" cmis:collectionType="types-children">
      <atom:title>type collection</atom:title>
    </collection>
    <collection href="<%= contextPath %>/.getTypesDescendants.xml" cmis:collectionType="types-descendants">
      <atom:title>type collection</atom:title>
    </collection>
    <collection href="<%= contextPath %>/.getQuery.xml" cmis:collectionType="query">
      <atom:title>query collection</atom:title>
    </collection>

  </workspace>
</service>
