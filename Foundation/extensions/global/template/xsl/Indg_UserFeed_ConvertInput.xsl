<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
    <xsl:output method="xml" indent="yes" />
    <xsl:template match="/">
	<xsl:element name="UserList">
		
		  <xsl:for-each select="/Objs/Obj/MS">
			<xsl:element name="User">
			   <xsl:for-each select="*">
				 <xsl:attribute name="{@N}" >
				   <xsl:value-of select="."/>
				 </xsl:attribute>
			   </xsl:for-each>
			</xsl:element>
			
			</xsl:for-each>
	</xsl:element>	
		
	</xsl:template>

</xsl:stylesheet>
