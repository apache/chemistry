<%@ page session="false" %><%
%><%@ page import="org.apache.jackrabbit.cmis.*,
                   org.apache.jackrabbit.cmis.client.JCRRepositoryLocator,
                 java.util.Iterator" %><%

    response.setContentType("application/atom+xml;type=feed");
    response.setCharacterEncoding("UTF-8");

    String contextPath = request.getContextPath();
    String query = (String) request.getParameter("query");
    
    Repository repository = JCRRepositoryLocator.getRepository(application);
    String authorName = "admin";
    
%><?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:app="http://www.w3.org/2007/app" xmlns:cmis="http://www.cmis.org/2008/05">
    <author><name><%= authorName %></name></author>
    <id></id>
    <link rel="self" href="<%= request.getRequestURI() %>"/>
    <link rel="cmis-source" href="<%= request.getRequestURI() %>"/>
    <title>Query result for <%= query %></title><%
    
    for (Entry child : repository.query(query)) {
        
    %>
    <entry><%
        String childId = child.getId();
        String childUrl = contextPath + "/" + childId;
    %>
        <author><name><%= authorName %></name></author>
        <content><%= childId %></content>
        <id>urn:uuid:<%= childId %></id>
        <link rel="self" href="<%= childUrl %>"/>
        <link rel="cmis-allowableactions" href="<%= contextPath %>/<%= childId %>.getAllowableActions.xml"/>
        <link rel="cmis-relationships" href="<%= contextPath %>/<%= childId %>.getRelationShips.xml"/>
        <link rel="cmis-parent" href="<%= contextPath %>/<%= childId %>.getParent.xml"/>
        <link rel="cmis-folderparent" href="<%= contextPath %>/<%= childId %>.getFolderParent.xml"/>
        <link rel="cmis-children" href="<%= contextPath %>/<%= childId %>.getChildren.xml"/>
        <link rel="cmis-descendants" href="<%= contextPath %>/<%= childId %>.getDescendants.xml"/>
        <link rel="cmis-type" href="<%= contextPath %>/<%= childId %>.getType.xml"/>
        <link rel="cmis-repository" href="<%= contextPath %>/<%= childId %>.getRepository.xml"/>
        <published><%= child.getLastModificationDate().getTime() %></published>
        <summary>Summary for <%= child.getName() %></summary>
        <title>Title for <%= child.getName() %></title>
        <updated><%= child.getLastModifiedBy() %></updated>
        <cmis:object>
            <cmis:properties>
                <cmis:propertyId cmis:name="ObjectId"><cmis:value><%= child.getObjectId() %></cmis:value></cmis:propertyId>
                <cmis:propertyString cmis:name="ObjectTypeId"><cmis:value><%= child.getObjectTypeId() %></cmis:value></cmis:propertyString>
                <cmis:propertyString cmis:name="CreatedBy"><cmis:value><%= child.getCreatedBy() %></cmis:value></cmis:propertyString>
                <cmis:propertyDateTime cmis:name="CreationDate"><cmis:value><%= child.getCreationDate().getTime() %></cmis:value></cmis:propertyDateTime>
                <cmis:propertyString cmis:name="LastModifiedBy"><cmis:value><%= child.getLastModifiedBy() %></cmis:value></cmis:propertyString>
                <cmis:propertyDateTime cmis:name="LastModificationDate"><cmis:value><%= child.getLastModificationDate().getTime() %></cmis:value></cmis:propertyDateTime>
                <cmis:propertyString cmis:name="Name"><cmis:value><%= child.getName() %></cmis:value></cmis:propertyString>
                <cmis:propertyId cmis:name="ParentId"><cmis:value><%= child.getParentId() %></cmis:value></cmis:propertyId>
            </cmis:properties>
        </cmis:object>
        <cmis:terminator/>
        <app:edited><%= child.getLastModifiedBy() %></app:edited>
    </entry>
    
    <% } %>
</feed>
