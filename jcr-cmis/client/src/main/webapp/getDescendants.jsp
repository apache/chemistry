<%@ page session="false" %><%
%><%@ page import="org.apache.jackrabbit.cmis.*,
                   org.apache.jackrabbit.cmis.client.JCRRepositoryLocator,
                   java.util.Iterator" %><%

    response.setContentType("application/atom+xml;type=feed");
    response.setCharacterEncoding("UTF-8");

    String contextPath = request.getContextPath();
    String path = (String) request.getAttribute("path");
    
    String id = "";
    if (path != null && path.startsWith("/")) {
        id = path.substring(1);
    }
    
    Repository repository = JCRRepositoryLocator.getRepository(application);
    Entry entry = repository.getEntry(id);
    if (entry == null) {
        %>Unable to return entry for id: <%= id %><%
        return;
    }
    
    String authorName = "admin";
    
%><?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:app="http://www.w3.org/2007/app" xmlns:cmis="http://www.cmis.org/2008/05">
    <author><name><%= authorName %></name></author>
    <id><%= entry.getObjectId() %></id>
    <link rel="self" href="<%= request.getRequestURI() %>"/>
    <link rel="cmis-source" href="<%= request.getRequestURI() %>"/>
    <title>Descendants for <%= entry.getName() %></title>
    <updated><%= entry.getLastModificationDate().getTime() %></updated><%
    
    for (Entry descendant : entry.getDescendants()) {
        
    %>
    <entry><%
        String childId = descendant.getId();
        String childUrl = contextPath + "/" + childId;
    %>
        <author><name><%= authorName %></name></author>
        <content><%= childId %></content>
        <id>urn:uuid:<%= childId %></id>
        <link rel="self" href="<%= childUrl %>"/>
        <link rel="cmis-allowableactions" href="<%= childUrl %>?allowableactions"/>
        <link rel="cmis-relationships" href="<%= childUrl %>?relationships"/>
        <link rel="cmis-parent" href="<%= childUrl %>?parent"/>
        <link rel="cmis-folderparent" href="<%= childUrl %>?folderparent"/>
        <link rel="cmis-children" href="<%= childUrl %>?children"/>
        <link rel="cmis-descendants" href="<%= childUrl %>?descendants"/>
        <link rel="cmis-type" href="<%= childUrl %>?type"/>
        <link rel="cmis-repository" href="<%= childUrl %>?repository"/>
        <published><%= descendant.getLastModificationDate().getTime() %></published>
        <summary>Summary for <%= descendant.getName() %></summary>
        <title>Title for <%= descendant.getName() %></title>
        <updated><%= descendant.getLastModifiedBy() %></updated>
        <cmis:object>
            <cmis:properties>
                <cmis:propertyId cmis:name="ObjectId"><cmis:value><%= descendant.getObjectId() %></cmis:value></cmis:propertyId>
                <cmis:propertyString cmis:name="ObjectTypeId"><cmis:value><%= descendant.getObjectTypeId() %></cmis:value></cmis:propertyString>
                <cmis:propertyString cmis:name="CreatedBy"><cmis:value><%= descendant.getCreatedBy() %></cmis:value></cmis:propertyString>
                <cmis:propertyDateTime cmis:name="CreationDate"><cmis:value><%= descendant.getCreationDate().getTime() %></cmis:value></cmis:propertyDateTime>
                <cmis:propertyString cmis:name="LastModifiedBy"><cmis:value><%= descendant.getLastModifiedBy() %></cmis:value></cmis:propertyString>
                <cmis:propertyDateTime cmis:name="LastModificationDate"><cmis:value><%= descendant.getLastModificationDate().getTime() %></cmis:value></cmis:propertyDateTime>
                <cmis:propertyString cmis:name="Name"><cmis:value><%= descendant.getName() %></cmis:value></cmis:propertyString>
                <cmis:propertyId cmis:name="ParentId"><cmis:value><%= descendant.getParentId() %></cmis:value></cmis:propertyId>
            </cmis:properties>
        </cmis:object>
        <cmis:terminator/>
        <app:edited><%= descendant.getLastModifiedBy() %></app:edited>
    </entry>
    
    <% } %>
</feed>
