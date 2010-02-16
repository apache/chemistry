<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:app="http://www.w3.org/2007/app"
	xmlns:atom="http://www.w3.org/2005/Atom" xmlns:cmis="http://docs.oasis-open.org/ns/cmis/core/200908/"
	xmlns:cmisra="http://docs.oasis-open.org/ns/cmis/restatom/200908/">

	<xsl:output method="html" />

	<xsl:param name="browseUrl"/>
	<xsl:param name="auxRoot"/>

	<xsl:template match="/">
		<html>
			<head>
				<title>ACL</title>
				<link rel="stylesheet" type="text/css" href="{$auxRoot}browser.css" />
			</head>
			<body>
				<h1>ACL</h1>
				<table class="entrytable">
				<tr>
					<th>Principal Id</th>
					<th>Permissions</th>
					<th>Direct</th>
				</tr>
				<xsl:for-each select="cmis:acl/cmis:permission">
					<tr>
						<td><xsl:value-of select="cmis:principal/cmis:principalId" /></td>
						<td><xsl:for-each select="cmis:permission"><xsl:value-of select="current()" /><br/></xsl:for-each></td>
						<td><xsl:value-of select="cmis:direct" /></td>
					</tr>
				</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>