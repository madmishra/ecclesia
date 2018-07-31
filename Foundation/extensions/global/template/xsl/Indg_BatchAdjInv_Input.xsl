<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="yes"  />
	<xsl:template match="/">
		<ItemList>
		<xsl:for-each select="Items/Item">

				<xsl:choose>
					<xsl:when test="position() mod 25 = 1">

						<Items>
							
								<xsl:apply-templates mode="copy" select= ". | following-sibling::*[not(position() > 24)]"/>
							
						</Items>
					</xsl:when>
				</xsl:choose>					
			</xsl:for-each>
		</ItemList>		
	</xsl:template>

	<xsl:template match="*" mode="copy">
		<xsl:copy-of select="../@* | ."/>
	</xsl:template> 
</xsl:stylesheet>