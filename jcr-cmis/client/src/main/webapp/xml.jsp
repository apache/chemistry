<%@page session="false"%><%

    String servletPath = request.getServletPath();
    int ext = servletPath.lastIndexOf('.');
    if (ext == -1) {
        log("No extension found.");
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }
    String base = servletPath.substring(0, ext);
    
    int selector = base.lastIndexOf('.');
    if (selector == -1) {
        selector = 0;
    }
    String path = base.substring(0, selector);
    String jspFile = base.substring(selector + 1) + ".jsp";
    String uri = "/" + jspFile;
    String queryString = request.getQueryString();
    if (queryString != null) {
        uri += "?" + queryString;
    }
    request.setAttribute("path", path);
    request.getRequestDispatcher(uri).forward(request, response);
%>
