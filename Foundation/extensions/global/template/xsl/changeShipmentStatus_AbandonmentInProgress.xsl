<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="ShipmentLines">
		<Shipment>
			<xsl:attribute name="ShipmentKey">
				<xsl:value-of select="ShipmentLine/@ShipmentKey"/>
			</xsl:attribute>
			<xsl:attribute name="TransactionId">ABANDONMENT_IN_PROGRESS.0001.ex</xsl:attribute>
			<xsl:attribute name="BaseDropStatus">1100.70.500</xsl:attribute>
		</Shipment>
	</xsl:template>
</xsl:stylesheet>