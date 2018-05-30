<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="Node">
		<NodeCapacityList>
			<NodeCapacity>
				<xsl:attribute name="Node">
					<xsl:value-of select="@Node" />
				</xsl:attribute>
				<xsl:attribute name="ResourcePoolId">
					<xsl:value-of select="ResourcePools/ResourcePool/@ResourcePoolId" />
				</xsl:attribute>
				<xsl:attribute name="ResourcePoolType">
					<xsl:value-of select="ResourcePools/ResourcePool/ResourcePoolAttributeList/ResourcePoolAttribute/@DeliveryMethod" />
				</xsl:attribute>
				<Dates>
					<xsl:for-each select="ResourcePools/ResourcePool/ServiceSlots/ServiceSlot/Dates/Date">
						<Date>
							<xsl:attribute name="Date">
								<xsl:value-of select="@Date" />
							</xsl:attribute>
							<xsl:attribute name="IsWorkingDay">
								<xsl:value-of select="@IsWorkingDay" />
							</xsl:attribute>								
							<xsl:attribute name="LastUpdatedTime">
								<xsl:value-of select="@Date" />
							</xsl:attribute>
							<xsl:attribute name="SlotId">
								<xsl:value-of select="/Node/ResourcePools/ResourcePool/@ServiceSlotGroupId" />
							</xsl:attribute>
							<xsl:attribute name="ShiftId">
								<xsl:value-of select="concat(@Date,'-',/Node/ResourcePools/ResourcePool/@ServiceSlotGroupId)" />
							</xsl:attribute>
							<xsl:attribute name="ShiftStartTime">
								<xsl:value-of select="../../@StartTime" />
							</xsl:attribute>
							<xsl:attribute name="ShiftEndTime">
								<xsl:value-of select="../../@EndTime" />
							</xsl:attribute>
							<xsl:attribute name="AvailableCapacity">
								<xsl:value-of select="@Availability" />
							</xsl:attribute>
							<xsl:attribute name="ConsumedCapacity">
								<xsl:value-of select="@AllocatedConsumption" />
							</xsl:attribute>
							<xsl:attribute name="TotalCapacity">
								<xsl:value-of select="@StandardCapacity" />
							</xsl:attribute>
							<xsl:attribute name="CapacityUOM">
								<xsl:value-of select="@CapacityUnitOfMeasure" />
							</xsl:attribute>
						</Date>
					</xsl:for-each>
				</Dates>
			</NodeCapacity>
		</NodeCapacityList>
	</xsl:template>
</xsl:stylesheet>