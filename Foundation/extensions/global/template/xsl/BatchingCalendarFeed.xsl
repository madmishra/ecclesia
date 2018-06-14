<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="yes"  />
	<xsl:template match="/">
		<Calendars>
		<xsl:for-each select="CalendarList/Calendar">

				<xsl:choose>
					<xsl:when test="position() mod 364 = 1">

						<CalendarList>
							
								<xsl:apply-templates mode="copy" select= ". | following-sibling::*[not(position() > 363)]"/>
							
						</CalendarList>
					</xsl:when>
				</xsl:choose>					
			</xsl:for-each>
		</Calendars>		
	</xsl:template>

	<xsl:template match="*" mode="copy">
		<xsl:copy-of select="../@* | ."/>
	</xsl:template> 
</xsl:stylesheet>