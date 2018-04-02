<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="/">
<MultiApi>
<xsl:for-each select="StoreList/Organization">
	<API Name="manageOrganizationHierarchy">
		<Input>
				<xsl:element name="Organization">
				<xsl:attribute name="CapacityOrganizationCode">
					<xsl:value-of select="@CapacityOrganizationCode"/>
				</xsl:attribute>
				<xsl:attribute name="CatalogOrganizationCode">
					<xsl:value-of select="@CatalogOrganizationCode"/>
				</xsl:attribute>
				<xsl:attribute name="InheritConfigFromEnterprise">
					<xsl:value-of select="@InheritConfigFromEnterprise"/>
				</xsl:attribute>
				<xsl:attribute name="InventoryOrganizationCode">
					<xsl:value-of select="@InventoryOrganizationCode"/>
				</xsl:attribute>
				<xsl:attribute name="InventoryPublished">
					<xsl:value-of select="@InventoryPublished"/>
				</xsl:attribute>
				<xsl:attribute name="IsHubOrganization">
					<xsl:value-of select="@IsHubOrganization"/>
				</xsl:attribute>
				<xsl:attribute name="IsSourcingKept">
					<xsl:value-of select="@IsSourcingKept"/>
				</xsl:attribute>
				<xsl:attribute name="IsLegalEntity">
					<xsl:value-of select="@IsLegalEntity"/>
				</xsl:attribute>
				<xsl:attribute name="LocaleCode">
					<xsl:value-of select="@LocaleCode"/>
				</xsl:attribute>
				<xsl:attribute name="Operation">
					<xsl:value-of select="@Operation"/>
				</xsl:attribute>
				<xsl:attribute name="OrganizationCode">
					<xsl:value-of select="@OrganizationCode"/>
				</xsl:attribute>
				<xsl:attribute name="OrganizationName">
					<xsl:value-of select="@OrganizationName"/>
				</xsl:attribute>
				<xsl:attribute name="ParentOrganizationCode">
					<xsl:value-of select="@ParentOrganizationCode"/>
				</xsl:attribute>
				<xsl:attribute name="PrimaryEnterpriseKey">
					<xsl:value-of select="@PrimaryEnterpriseKey"/>
				</xsl:attribute>
				<xsl:attribute name="RequiresChainedOrder">
					<xsl:value-of select="@RequiresChainedOrder"/>
				</xsl:attribute>
				<xsl:attribute name="Operation">
					<xsl:value-of select="@Operation"/>
				</xsl:attribute>
				
				
				<xsl:element name="Node">
				<xsl:attribute name="ActivateFlag">
					<xsl:value-of select="Node/@ActivateFlag"/>
				</xsl:attribute>
				<xsl:attribute name="InterfaceType">
					<xsl:value-of select="Node/@InterfaceType"/>
				</xsl:attribute>
				<xsl:attribute name="InventoryType">
					<xsl:value-of select="Node/@InventoryType"/>
					</xsl:attribute>
					<xsl:attribute name="InventoryTracked">
					<xsl:value-of select="Node/@InventoryTracked"/>
				</xsl:attribute>
				<xsl:attribute name="NodeType">
					<xsl:value-of select="Node/@NodeType"/>
				</xsl:attribute>
				<xsl:attribute name="ShipNode">
					<xsl:value-of select="Node/@ShipNode"/>
				</xsl:attribute>
				<xsl:attribute name="ProcureToShipAllowed">
					<xsl:value-of select="Node/@ProcureToShipAllowed"/>
				</xsl:attribute>
				</xsl:element>
				
				
				<xsl:element name="OrgRoleList">
				<xsl:for-each select="OrgRoleList/OrgRole">
				 <xsl:element name="OrgRole">
				 <xsl:attribute name="RoleKey">
					<xsl:value-of select="@RoleKey"/>
				</xsl:attribute>
				</xsl:element>
				</xsl:for-each> 
				</xsl:element>
				
				
				<xsl:element name="CorporatePersonInfo">
				<xsl:attribute name="AddressLine1">
					<xsl:value-of select="CorporatePersonInfo/@AddressLine1"/>
				</xsl:attribute>
				<xsl:attribute name="AddressLine2">
					<xsl:value-of select="CorporatePersonInfo/@AddressLine2"/>
				</xsl:attribute>
				<xsl:attribute name="AddressLine4">
					<xsl:value-of select="CorporatePersonInfo/@AddressLine4"/>
				</xsl:attribute>
				<xsl:attribute name="AddressLine5">
					<xsl:value-of select="CorporatePersonInfo/@AddressLine5"/>
				</xsl:attribute>
				<xsl:attribute name="City">
					<xsl:value-of select="CorporatePersonInfo/@City"/>
				</xsl:attribute>
				<xsl:attribute name="State">
					<xsl:value-of select="CorporatePersonInfo/@State"/>
				</xsl:attribute>
				<xsl:attribute name="ZipCode">
					<xsl:value-of select="CorporatePersonInfo/@ZipCode"/>
				</xsl:attribute>
				<xsl:attribute name="Country">
					<xsl:value-of select="CorporatePersonInfo/@Country"/>
				</xsl:attribute>
				<xsl:attribute name="DayPhone">
					<xsl:value-of select="CorporatePersonInfo/@DayPhone"/>
				</xsl:attribute>
				<xsl:attribute name="FirstName">
					<xsl:value-of select="CorporatePersonInfo/@FirstName"/>
				</xsl:attribute>
				<xsl:attribute name="LastName">
					<xsl:value-of select="CorporatePersonInfo/@LastName"/>
				</xsl:attribute>
				<xsl:attribute name="DayFaxNo">
					<xsl:value-of select="CorporatePersonInfo/@DayFaxNo"/>
				</xsl:attribute>
				<xsl:attribute name="EMailID">
					<xsl:value-of select="CorporatePersonInfo/@EMailID"/>
				</xsl:attribute>
				<xsl:attribute name="Latitude">
					<xsl:value-of select="CorporatePersonInfo/@Latitude"/>
				</xsl:attribute>
				<xsl:attribute name="Longitude">
					<xsl:value-of select="CorporatePersonInfo/@Longitude"/>
				</xsl:attribute>
				</xsl:element>
				
				<xsl:element name="BillingPersonInfo">
				<xsl:attribute name="AddressLine1">
					<xsl:value-of select="BillingPersonInfo/@AddressLine1"/>
				</xsl:attribute>
				<xsl:attribute name="AddressLine2">
					<xsl:value-of select="BillingPersonInfo/@AddressLine2"/>
				</xsl:attribute>
				<xsl:attribute name="AddressLine4">
					<xsl:value-of select="BillingPersonInfo/@AddressLine4"/>
				</xsl:attribute>
				<xsl:attribute name="AddressLine5">
					<xsl:value-of select="BillingPersonInfo/@AddressLine5"/>
				</xsl:attribute>
				<xsl:attribute name="City">
					<xsl:value-of select="BillingPersonInfo/@City"/>
				</xsl:attribute>
				<xsl:attribute name="State">
					<xsl:value-of select="BillingPersonInfo/@State"/>
				</xsl:attribute>
				<xsl:attribute name="ZipCode">
					<xsl:value-of select="BillingPersonInfo/@ZipCode"/>
				</xsl:attribute>
				<xsl:attribute name="Country">
					<xsl:value-of select="BillingPersonInfo/@Country"/>
				</xsl:attribute>
				<xsl:attribute name="DayPhone">
					<xsl:value-of select="BillingPersonInfo/@DayPhone"/>
				</xsl:attribute>
				<xsl:attribute name="FirstName">
					<xsl:value-of select="BillingPersonInfo/@FirstName"/>
				</xsl:attribute>
				<xsl:attribute name="LastName">
					<xsl:value-of select="BillingPersonInfo/@LastName"/>
				</xsl:attribute>
				<xsl:attribute name="DayFaxNo">
					<xsl:value-of select="BillingPersonInfo/@DayFaxNo"/>
				</xsl:attribute>
				<xsl:attribute name="EMailID">
					<xsl:value-of select="BillingPersonInfo/@EMailID"/>
				</xsl:attribute>
				<xsl:attribute name="Latitude">
					<xsl:value-of select="BillingPersonInfo/@Latitude"/>
				</xsl:attribute>
				<xsl:attribute name="Longitude">
					<xsl:value-of select="BillingPersonInfo/@Longitude"/>
				</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</Input>
	</API>
	</xsl:for-each>

</MultiApi>
</xsl:template>
</xsl:stylesheet>


